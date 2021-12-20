package xyz.minecat;

import org.bukkit.Server;
import org.json.JSONObject;
import java.net.http.WebSocket;
import java.util.logging.Logger;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;

public class AdvancementListener implements Listener {
    private Minecat plugin;
    private Logger logger;
    private Server mcServer;
    private WebSocket client;

    public AdvancementListener(Minecat plugin, WebSocket client) {
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
        String title = null;
        String url = null;
        String desc = null;
        try {
            title = getTitle(key);
            url = getUrl(key);
            desc = getDesc(key);
        }
        catch (IndexOutOfBoundsException e) {
            return;
        }
        if (title == null) {
            return;
        }
        JSONObject req = new JSONObject();
        req.put("type", "adv");
        req.put("adv", title);
        req.put("url", url);
        req.put("desc", desc);
        this.plugin.putPlayer(event, req);
        client.sendText(req.toString(), true);
        logger.info(title);
    }

    private String getTitle(String raw) throws IndexOutOfBoundsException {
        return this.plugin.getConfig().getStringList(raw).get(0);
    }

    private String getDesc(String raw) throws IndexOutOfBoundsException {
        return this.plugin.getConfig().getStringList(raw).get(1);
    }

    private String getUrl(String raw) throws IndexOutOfBoundsException {
        return this.plugin.getConfig().getStringList(raw).get(2);
    }
}
