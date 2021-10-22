package tootymc;

import org.bukkit.Server;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class MessageListener implements Listener {
    private Server mcServer;

    public MessageListener(Tooty plugin) {
        this.mcServer = plugin.getServer();
        mcServer.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onMessage(AsyncPlayerChatEvent event) {
        String msg = event.getMessage();
        mcServer.getLogger().info(String.format("Message: %s", msg));
    }
}
