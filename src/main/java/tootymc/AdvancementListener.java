package tootymc;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.advancement.Advancement;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;

public class AdvancementListener implements Listener {
    private Tooty plugin;
    private Logger logger;
    private Server mcServer;
    private WebSocket client;

    public AdvancementListener(Tooty plugin, WebSocket client) {
        this.client = client;
        this.plugin = plugin;
        logger = plugin.getLogger();
        mcServer = plugin.getServer();
        mcServer.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onAdvancement(PlayerAdvancementDoneEvent event) {
        Advancement advancement = event.getAdvancement();
    }
}