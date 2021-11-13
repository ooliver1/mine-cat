package tootymc;

import org.bukkit.Server;
import org.json.JSONObject;
import java.net.http.WebSocket;
import java.util.logging.Logger;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;

public class AdvancementListener implements Listener {
    private Tooty plugin;
    private Logger logger;
    private Server mcServer;
    private WebSocket client;

    public AdvancementListener(Tooty plugin, WebSocket client) {
        this.client = client;
        this.plugin = plugin;
        mcServer = plugin.getServer();
        logger = plugin.getLogger();
        mcServer.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onAdvancement(PlayerAdvancementDoneEvent event) {
        if (event.getAdvancement() == null
                || event.getAdvancement().getKey().getKey().contains("recipe/")
                || event.getPlayer() == null) {
            return;
        }
        String key = event.getAdvancement().getKey().getKey();
        String title = getTitle(key);
        String url = getUrl(key);
        logger.info(key);
        if (title == null) {
            return;
        }
        JSONObject req = new JSONObject();
        req.put("type", "adv");
        req.put("adv", title);
        req.put("url", url);
        this.plugin.putPlayer(event, req);
        client.sendText(req.toString(), true);
    }

    private String getTitle(String raw) {
        return this.plugin.getConfig().getStringList(raw).get(0);
    }

    private String getUrl(String raw) {
        return this.plugin.getConfig().getStringList(raw).get(1);
    }
}
