package tootymc;

import java.util.Map;
import java.io.IOException;
import java.util.logging.Logger;
import java.net.InetSocketAddress;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;

public class HTTPServer {
    private Logger logger;
    private HttpServer httpServer;

    public HTTPServer(Tooty plugin, int port, Map<String, HttpHandler> mapping) {
        logger = plugin.getServer().getLogger();
        try {
            httpServer = HttpServer.create(new InetSocketAddress(port), 0);
            for (var entry : mapping.entrySet()) {
                httpServer.createContext(entry.getKey(), entry.getValue());
            }
            httpServer.setExecutor(null);
            logger.info(String.format("Server listening on port %s", port));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        this.httpServer.start();
    }
}
