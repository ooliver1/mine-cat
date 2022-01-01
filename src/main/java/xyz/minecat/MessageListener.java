package xyz.minecat;

import org.bukkit.Server;
import org.json.JSONObject;
import java.net.http.WebSocket;
import java.util.logging.Logger;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class MessageListener implements Listener {
    private Minecat plugin;
    private Logger logger;
    private Server mcServer;
    private WebSocket client;

    public MessageListener(Minecat plugin, WebSocket client) {
        this.client = client;
        this.plugin = plugin;
        logger = plugin.getLogger();
        mcServer = plugin.getServer();
        mcServer.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onMessage(AsyncPlayerChatEvent event) {
        String msg = event.getMessage();
        logger.info("Message: " + msg);
        JSONObject req = new JSONObject();
        req.put("type", "msg");
        req.put("msg", msg);
        this.plugin.putPlayer(event, req);
        client.sendText(req.toString(), true);
    }

    public void reload(WebSocket client) {
        this.client = client;
    }
}
