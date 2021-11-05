package tootymc;

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

    public WebSocketClient(Tooty plugin) {
        logger = plugin.getLogger();
        HttpClient httpClient = HttpClient.newHttpClient();
        try {
            WebSocket webSocket = httpClient.newWebSocketBuilder()
                    .buildAsync(URI.create("ws://65.21.247.55:6969"),
                            new WsClient(plugin))
                    .join();
            logger.info("The WebSocket was created and ran asynchronously.");
            this.webSocket = webSocket;
        } catch (CompletionException e) {
            logger.warning("Failed to connect to tooty ;(");
        }
    }

    public WebSocket getClient() {
        return this.webSocket;
    }

    private static class WsClient implements WebSocket.Listener {
        private String uuid;
        private Tooty plugin;
        private Logger logger;
        private Server server;
        private File dataFolder;
        private static final String version = "0.0.0-a26";

        public WsClient(Tooty plugin) {
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
            JSONObject res = new JSONObject(data.toString());
            Object type = res.get("type");
            if (type instanceof String) {
                String reqType = (String) type;
                switch (reqType) {
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
                                            new FileWriter(dataFolder.getPath() + "/" + "uuid.txt");
                                    myWriter.write(uuid.toString());
                                    myWriter.close();
                                    logger.info("Your uuid is in TootyMC/uuid.txt");
                                } catch (IOException e) {
                                    logger.warning("An error occurred.");
                                    e.printStackTrace();
                                }
                            } else {
                                logger.info("Logged in successfully!");
                            }
                        } catch (JSONException e) {
                            logger.warning("Uuid not in payload?" + e);
                        }
                        break;
                    }
                    case "player": {
                        try {
                            Object id = res.get("id");
                            Object uuid = res.get("uuid");
                            this.plugin.addPlayer(uuid.toString(), id.toString());
                        } catch (JSONException e) {
                            logger.warning("Id or uuid not in payload?" + e);
                            e.printStackTrace();
                        }
                        break;
                    }
                    case "msg": {
                        try {
                            Object msg = res.get("msg");
                            Object id = res.get("id");
                            String uuid = this.plugin.getUuid(id.toString());
                            if (uuid != null && uuid.toString() != "null") {
                                String playerName = this.server.getOfflinePlayer(
                                        UUID.fromString(uuid)).getName();
                                this.server.broadcastMessage(String.format(
                                        "<%s> %s", playerName, msg.toString()));
                            } else {
                                String playerName = res.get("name").toString();
                                logger.info(String.format(
                                        "<%s%s%s> %s", ChatColor.BLUE,
                                        playerName, ChatColor.RESET, msg.toString()));
                                this.server.broadcastMessage(String.format(
                                        "<%s%s%s> %s", ChatColor.BLUE,
                                        playerName, ChatColor.RESET, msg.toString()));
                            }
                        } catch (JSONException e) {
                            logger.warning("Message or id not in payload?");
                            e.printStackTrace();
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
    }
}
