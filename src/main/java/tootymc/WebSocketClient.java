package tootymc;

import java.net.URI;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.json.JSONObject;
import org.json.JSONException;
import java.net.http.WebSocket;
import java.net.http.HttpClient;
import java.util.logging.Logger;
import java.net.http.WebSocket.Listener;
import java.util.concurrent.CompletionStage;

public class WebSocketClient {
    private Logger logger;
    public WebSocket webSocket;

    public WebSocketClient(Tooty plugin) {
        logger = plugin.getLogger();
        HttpClient httpClient = HttpClient.newHttpClient();
        WebSocket webSocket = httpClient.newWebSocketBuilder()
                .buildAsync(URI.create("ws://65.21.247.55:6969"), new WsClient(this.logger, plugin))
                .join();

        logger.info("The WebSocket was created and ran asynchronously.");
        this.webSocket = webSocket;
    }

    public WebSocket getClient() {
        return this.webSocket;
    }

    private static class WsClient implements WebSocket.Listener {
        private String uuid;
        private Logger logger;
        private File dataFolder;

        public WsClient(Logger logger, Tooty plugin) {
            this.logger = logger;
            this.uuid = plugin.getUuid();
            this.dataFolder = plugin.getDataFolder();
        }

        @Override
        public void onOpen(WebSocket webSocket) {
            logger.info("Connection established.");
            JSONObject req = new JSONObject();
            req.put("uuid", uuid);
            req.put("type", "login");
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
                            if (uuid != null) {
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
                                    JSONObject req = new JSONObject();
                                    req.put("uuid", uuid.toString());
                                    req.put("type", "login");
                                    webSocket.sendText(req.toString(), true);
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
