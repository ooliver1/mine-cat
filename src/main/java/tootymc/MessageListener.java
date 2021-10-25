package tootymc;

import org.bukkit.Server;
import java.util.logging.Logger;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class MessageListener implements Listener {
    private Logger logger;
    private Server mcServer;

    public MessageListener(Tooty plugin) {
        logger = plugin.getLogger();
        mcServer = plugin.getServer();
        mcServer.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onMessage(AsyncPlayerChatEvent event) {
        String msg = event.getMessage();
        logger.info(String.format("Message: %s", msg));
    }
}
