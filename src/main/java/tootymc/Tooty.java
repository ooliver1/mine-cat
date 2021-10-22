package tootymc;

import java.util.logging.Logger;
import org.bukkit.plugin.java.JavaPlugin;

public class Tooty extends JavaPlugin {
    private Socket socket;
    private Logger logger = getServer().getLogger();

    @Override
    public void onEnable() {
        logger.info("Tooty is enabling...");
        this.socket = new Socket(this);
        this.socket.enable();
        logger.info("Socket is enabled!");
        new MessageListener(this, socket);
        logger.info("MessageListener is enabled!");
    }

    @Override
    public void onDisable() {
        logger.info("Tooty is disabling...");
        this.socket.disable();
        logger.info("Socket is disabled!");
    }
}