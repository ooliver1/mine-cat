package tootymc;

import org.bukkit.Server;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatMessage implements Listener {

    Server server = null;

    public ChatMessage(Tooty plugin) {
        server = plugin.getServer();
        server.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onMessage(AsyncPlayerChatEvent event) {
        String msg = event.getMessage();
        String fmt = event.getFormat();
        server.getLogger().info(String.format("Message: %s, format: %s", msg, fmt));
    }
}
