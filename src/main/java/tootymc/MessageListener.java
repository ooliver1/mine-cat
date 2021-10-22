package tootymc;

import org.bukkit.Server;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class MessageListener implements Listener {
    private Server mcServer;
    private Socket socket;

    public MessageListener(Tooty plugin, Socket socket) {
        this.mcServer = plugin.getServer();
        this.socket = socket;
        mcServer.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onMessage(AsyncPlayerChatEvent event) {
        String msg = event.getMessage();
        mcServer.getLogger().info(String.format("Message: %s", msg));
    }
}
