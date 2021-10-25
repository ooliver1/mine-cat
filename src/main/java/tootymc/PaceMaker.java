package tootymc;

import java.lang.Runnable;
import java.nio.ByteBuffer;
import java.net.http.WebSocket;
import java.nio.charset.StandardCharsets;

public class PaceMaker implements Runnable {
    private WebSocket client;

    public PaceMaker(WebSocket wsClient) {
        client = wsClient;
    }

    public void run() {
        String str = new String("ping!");
        ByteBuffer bb = ByteBuffer.wrap(str.getBytes(StandardCharsets.UTF_8));
        try {
            while (true) {
                client.sendPing(bb);
                Thread.sleep(30 * 1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
