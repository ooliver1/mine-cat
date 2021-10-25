package tootymc;

import java.net.URI;
import org.json.JSONObject;
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

    private static class WsClient implements WebSocket.Listener {
        private String uuid;
        private Logger logger;

        public WsClient(Logger logger, Tooty plugin) {
            this.logger = logger;
            this.uuid = plugin.getUuid();
        }

        @Override
        public void onOpen(WebSocket webSocket) {
            logger.info("Connection established.");
            JSONObject o1 = new JSONObject();
            o1.put("uuid", uuid);
            webSocket.sendText(o1.toString(), true);
            Listener.super.onOpen(webSocket);
        }

        @Override
        public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
            logger.info("Method onText() got data: " + data);
            return Listener.super.onText(webSocket, data, last);
        }

        @Override
        public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
            logger.info("Closed with status " + statusCode + ", reason: " + reason);
            return Listener.super.onClose(webSocket, statusCode, reason);
        }
    }
}
