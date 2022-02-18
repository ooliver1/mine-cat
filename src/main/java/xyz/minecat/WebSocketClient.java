// SPDX-License-Identifier: GPL-3.0-or-later

package xyz.minecat;

import java.net.URI;
import java.io.File;
import java.awt.Color;
import java.util.UUID;
import java.util.Vector;
import org.bukkit.Server;
import java.util.HashMap;
import java.io.FileWriter;
import java.io.IOException;
import org.json.JSONObject;
import org.bukkit.ChatColor;
import java.util.Collection;
import org.json.JSONException;
import java.net.http.WebSocket;
import java.net.http.HttpClient;
import java.util.logging.Logger;
import org.bukkit.entity.Player;
import java.net.http.WebSocket.Listener;
import java.net.http.HttpTimeoutException;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CompletionException;
import java.net.http.WebSocketHandshakeException;

public class WebSocketClient {
    private Logger logger;
    private Minecat plugin;
    public WebSocket webSocket;

    public WebSocketClient(Minecat plugin) {
        this.plugin = plugin;
        logger = plugin.getLogger();
        HttpClient httpClient = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
        try {
            WebSocket webSocket = httpClient.newWebSocketBuilder()
                    .buildAsync(URI.create("wss://tooty.xyz/ws/"),
                            new WsClient(plugin))
                    .join();
            logger.info("The WebSocket was created and ran asynchronously.");
            this.webSocket = webSocket;
        }
        catch (CompletionException e) {
            logger.warning("Failed to connect to minecat ;(");
            if (e.getCause() instanceof WebSocketHandshakeException
                    || e.getCause() instanceof HttpTimeoutException) {
                logger.info(
                        "Attempting reload in " + String.valueOf(this.plugin.getWsReloadTime())
                                + "s");
                this.plugin.reloadWs();
            }
            else {
                e.printStackTrace();
                logger.warning("Caused by " + e.getCause().toString());
                logger.warning("Caused by " + e.getCause().getMessage());
            }
            this.plugin.reloads = 0;
        }
    }

    public WebSocket getClient() {
        return this.webSocket;
    }

    private static class WsClient implements WebSocket.Listener {
        private String uuid;
        private Logger logger;
        private Server server;
        private Minecat plugin;
        private File dataFolder;
        private long lastNotify = 0;
        private static final String version = "2.2.0";

        public WsClient(Minecat plugin) {
            this.plugin = plugin;
            this.uuid = plugin.getUuid();
            this.server = plugin.getServer();
            this.logger = plugin.getLogger();
            this.dataFolder = plugin.getDataFolder();
        }

        @Override
        public void onOpen(WebSocket webSocket) {
            logger.info("Connection established.");
            JSONObject req = new JSONObject();
            req.put("uuid", uuid);
            req.put("type", "login");
            req.put("version", version);
            webSocket.sendText(req.toString(), true);
            Listener.super.onOpen(webSocket);
        }

        private CompletionStage<?> handleMsg(WebSocket webSocket, CharSequence data, boolean last,
                JSONObject res) {
            try {
                Object msg = res.get("msg");
                Object uuid = res.get("uuid");
                Object color = res.get("color");
                if (uuid != null && uuid.toString() != "null") {
                    String playerName = this.server.getOfflinePlayer(
                            UUID.fromString(uuid.toString())).getName();
                    if (color != null && color.toString() != "null") {
                        this.server.broadcastMessage(String.format("<%s%s%s> %s",
                                net.md_5.bungee.api.ChatColor.of(Color.decode(color.toString())),
                                playerName, net.md_5.bungee.api.ChatColor.RESET, msg.toString()));
                    }
                    else {
                        this.server
                                .broadcastMessage(
                                        String.format("<%s> %s", playerName, msg.toString()));
                    }
                }
                else {
                    String playerName = res.get("name").toString();
                    logger.info(String.format(
                            "<%s%s%s> %s", ChatColor.BLUE, playerName, ChatColor.RESET,
                            msg.toString()));
                    this.server.broadcastMessage(String.format(
                            "<%s%s%s> %s", ChatColor.BLUE, playerName, ChatColor.RESET,
                            msg.toString()));
                }
            }
            catch (JSONException e) {
                this.sendError(webSocket, e, "Message, uuid or name not in JSON");
            }
            return Listener.super.onText(webSocket, data, last);
        }

        private CompletionStage<?> handleLink(WebSocket webSocket, CharSequence data, boolean last,
                JSONObject res) {
            try {
                Object linked = res.get("linked");
                if (linked.toString() == "false") {
                    Object uuid = res.get("uuid");
                    Player player = this.server.getPlayer(
                            UUID.fromString(uuid.toString()));
                    if ((System.currentTimeMillis() - this.lastNotify) > 1200000) {
                        player.sendMessage(ChatColor.RED
                                + "You have not linked your discord account!");
                        player.sendMessage("If you want your discord account linked to minecat, "
                                + "please type /link and use your code with `mc linkuser` in discord.");
                        this.lastNotify = System.currentTimeMillis();
                    }
                }
            }
            catch (JSONException e) {
                this.sendError(webSocket, e, "Linked or uuid not found in JSON");
            }
            return Listener.super.onText(webSocket, data, last);
        }

        private CompletionStage<?> handleAll(WebSocket webSocket, CharSequence data, boolean last,
                JSONObject res) {
            try {
                Object id = res.get("id");
                Collection<? extends Player> players = this.server.getOnlinePlayers();
                Vector<HashMap<String, String>> playerList = new Vector<>();
                for (Player player : players) {
                    HashMap<String, String> playerMap = new HashMap<>();
                    playerMap.put("name", player.getName());
                    playerMap.put("uuid", player.getUniqueId().toString());
                    playerList.add(playerMap);
                }
                JSONObject req = new JSONObject();
                req.put("type", "all");
                req.put("id", id.toString());
                req.put("players", playerList);
                webSocket.sendText(req.toString(), true);
            }
            catch (JSONException e) {
                this.sendError(webSocket, e, "Id not in JSON");
            }
            return Listener.super.onText(webSocket, data, last);
        }

        private CompletionStage<?> handleLogin(WebSocket webSocket, CharSequence data, boolean last,
                JSONObject res) {
            try {
                Object uuid = res.get("uuid");
                if (uuid != null && uuid != "null" && uuid.toString() != "null") {
                    try {
                        if (!dataFolder.exists()) {
                            dataFolder.mkdir();
                        }
                        File myObj = new File(dataFolder, "uuid.txt");
                        myObj.createNewFile();
                        FileWriter myWriter = new FileWriter(
                                dataFolder.getPath() + "/" + "uuid.txt", false);
                        myWriter.write(uuid.toString());
                        myWriter.close();
                        logger.info("Your uuid is in minecat/uuid.txt");
                    }
                    catch (IOException e) {
                        this.sendError(webSocket, e, "IOException when getting uuid");
                        return Listener.super.onText(webSocket, data, last);
                    }
                }
                else {
                    logger.info("Logged in successfully!");
                }
                try {
                    Object set = res.get("set");
                    if (set.toString() == "false") {
                        this.plugin.note = true;
                        logger.info("You have not set up with discord!");
                    }
                }
                catch (JSONException e) {
                    this.sendError(webSocket, e, "No set found in JSON");
                }
            }
            catch (JSONException e) {
                this.sendError(webSocket, e, "Uuid not found in JSON");
            }
            return Listener.super.onText(webSocket, data, last);
        }

        @Override
        public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
            logger.info("Method onText() got data: " + data);
            JSONObject res = null;
            try {
                res = new JSONObject(data.toString());
            }
            catch (JSONException e) {
                this.sendError(webSocket, e, "Failed to parse JSON");
                return Listener.super.onText(webSocket, data, last);
            }
            Object type = null;
            try {
                type = res.get("type");
            }
            catch (JSONException e) {
                this.sendError(webSocket, e, "No type found in JSON");
                return Listener.super.onText(webSocket, data, last);
            }
            if (type instanceof String) {
                String reqType = (String) type;
                switch (reqType) {
                    case "invalidate":
                    case "login": {
                        return this.handleLogin(webSocket, data, last, res);
                    }
                    case "msg": {
                        return this.handleMsg(webSocket, data, last, res);
                    }
                    case "linked": {
                        return this.handleLink(webSocket, data, last, res);
                    }
                    case "all": {
                        return this.handleAll(webSocket, data, last, res);
                    }
                }
            }
            return Listener.super.onText(webSocket, data, last);
        }

        @Override
        public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
            logger.warning("Websocket closed with status " + statusCode + ": " + reason);
            logger.info(
                    "Attempting reload in " + String.valueOf(this.plugin.getWsReloadTime()) + "s");
            this.plugin.reloadWs();
            return Listener.super.onClose(webSocket, statusCode, reason);
        }

        private void sendError(WebSocket webSocket, Exception e, String msg) {
            logger.warning(msg + e);
            JSONObject req = new JSONObject();
            req.put("type", "exc");
            req.put("msg", e.getMessage());
            req.put("exc", e.getClass().getName());
            webSocket.sendText(req.toString(), true);
        }
    }
}
