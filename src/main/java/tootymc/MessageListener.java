package tootymc;

import org.bukkit.Server;
import org.json.JSONObject;
import java.net.http.WebSocket;
import java.util.logging.Logger;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class MessageListener implements Listener {
    private Tooty plugin;
    private Logger logger;
    private Server mcServer;
    private WebSocket client;

    public MessageListener(Tooty plugin, WebSocket client) {
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
        String uuid = event.getPlayer().getUniqueId().toString();
        String id = this.plugin.getDiscordId(uuid);
        if (id != null) {
            req.put("id", id);
            req.put("dc", true);
        } else {
            req.put("id", uuid);
            req.put("dc", false);
        }
        client.sendText(req.toString(), true);
    }
}
