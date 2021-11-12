package tootymc;

import org.bukkit.Server;
// import org.json.JSONObject;
import java.net.http.WebSocket;
import java.util.logging.Logger;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;

public class AdvancementListener implements Listener {
    private Tooty plugin;
    private Logger logger;
    private Server mcServer;
    // private WebSocket client;

    public AdvancementListener(Tooty plugin, WebSocket client) {
        // this.client = client;
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
        logger.info(getTitle(event.getAdvancement().getKey().getKey()));
    }

    private String getTitle(String raw) {
        return this.plugin.getConfig().getString(raw);
    }
}
