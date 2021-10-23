package tootymc;

import java.util.Map;
import java.util.HashMap;
import java.util.logging.Logger;
import org.bukkit.plugin.java.JavaPlugin;
import com.sun.net.httpserver.HttpHandler;

public class Tooty extends JavaPlugin {
    private HTTPClient client;
    private Logger logger = getServer().getLogger();

    @Override
    public void onEnable() {
        logger.info("Tooty is enabling...");
        client = new HTTPClient(this);
        logger.info("HTTP is enabled!");
        new MessageListener(this);
        logger.info("MessageListener is enabled!");
        logger.info("Initializing HTTP...");
        Map<String, String> json = new HashMap<String, String>();
        json.put("message", "test");
        client.postJson("test", json);
        logger.info("HTTP tested!");
        Map<String, HttpHandler> handlers = new HashMap<String, HttpHandler>();
        handlers.put("/test", new TestHandler(this));
        new HTTPServer(this, 42000, handlers);
    }

    @Override
    public void onDisable() {
        logger.info("Tooty is disabling...");
    }
}
