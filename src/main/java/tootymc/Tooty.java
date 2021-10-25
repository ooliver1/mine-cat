package tootymc;

import java.io.File;
import java.util.Scanner;
import java.net.http.WebSocket;
import java.util.logging.Logger;
import java.io.FileNotFoundException;
import org.bukkit.plugin.java.JavaPlugin;

public class Tooty extends JavaPlugin {
    private String uuid = null;
    private WebSocket client;
    private Logger logger = getServer().getLogger();

    @Override
    public void onEnable() {
        logger.info("Tooty is enabling...");
        uuid = getUuid();
        logger.info("Your uuid is: '" + uuid + "'");
        WebSocketClient wsClient = new WebSocketClient(this);
        logger.info("Websocket client created!");
        this.client = wsClient.getClient();
        Thread thread = new Thread(new PaceMaker(this.client));
        thread.start();
        logger.info("PaceMaker started!");
        new MessageListener(this);
        logger.info("MessageListener is enabled!");
    }

    @Override
    public void onDisable() {
        logger.info("Tooty is disabling...");
    }

    public String getUuid() {
        if (this.uuid != null) {
            return this.uuid;
        } else {
            try {
                File file = new File(this.getDataFolder().getPath() + "/" + "uuid.txt");
                Scanner myReader = new Scanner(file);
                if (myReader.hasNextLine()) {
                    String data = myReader.nextLine();
                    myReader.close();
                    return data;
                }
                myReader.close();
            } catch (FileNotFoundException e) {
                return "unknown";
            }
            return "unknown";
        }
    }
}
