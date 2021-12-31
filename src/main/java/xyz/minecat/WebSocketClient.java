package xyz.minecat;

import java.net.URI;
import java.io.File;
import java.util.UUID;
import org.bukkit.Server;
import java.io.FileWriter;
import java.io.IOException;
import org.json.JSONObject;
import org.bukkit.ChatColor;
import org.json.JSONException;
import java.net.http.WebSocket;
import java.net.http.HttpClient;
import java.util.logging.Logger;
import java.net.http.WebSocket.Listener;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CompletionException;

public class WebSocketClient {
    private Logger logger;
    public WebSocket webSocket;

    public WebSocketClient(Minecat plugin) {
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
            e.printStackTrace();
            logger.warning("Caused by " + e.getCause().toString());
            logger.warning("Caused by " + e.getCause().getMessage());
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
        private static final String version = "1.3.1-rc";

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
                        try {
                            Object uuid = res.get("uuid");
                            if (uuid != null && uuid != "null" && uuid.toString() != "null") {
                                try {
                                    if (!dataFolder.exists()) {
                                        dataFolder.mkdir();
                                    }
                                    File myObj = new File(dataFolder, "uuid.txt");
                                    myObj.createNewFile();
                                    FileWriter myWriter =
                                            new FileWriter(
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
                            }
                        }
                        catch (JSONException e) {
                            this.sendError(webSocket, e, "Uuid not found in JSON");
                            return Listener.super.onText(webSocket, data, last);
                        }
                        break;
                    }
                    case "msg": {
                        try {
                            Object msg = res.get("msg");
                            Object uuid = res.get("uuid");
                            if (uuid != null && uuid.toString() != "null") {
                                String playerName = this.server.getOfflinePlayer(
                                        UUID.fromString(uuid.toString())).getName();
                                this.server.broadcastMessage(String.format(
                                        "<%s> %s", playerName, msg.toString()));
                            }
                            else {
                                String playerName = res.get("name").toString();
                                logger.info(String.format(
                                        "<%s%s%s> %s", ChatColor.BLUE,
                                        playerName, ChatColor.RESET, msg.toString()));
                                this.server.broadcastMessage(String.format(
                                        "<%s%s%s> %s", ChatColor.BLUE,
                                        playerName, ChatColor.RESET, msg.toString()));
                            }
                        }
                        catch (JSONException e) {
                            this.sendError(webSocket, e, "Message, uuid or name not in JSON");
                            return Listener.super.onText(webSocket, data, last);
                        }
                        break;
                    }
                }
            }
            return Listener.super.onText(webSocket, data, last);
        }

        @Override
        public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
            logger.warning("Websocket closed with status "
                    + statusCode + ", reason: " + reason);
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
