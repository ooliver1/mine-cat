package tootymc;

import java.io.File;
import java.util.Scanner;
import java.io.IOException;
import java.util.logging.Logger;
import java.io.FileNotFoundException;
import org.bukkit.plugin.java.JavaPlugin;

public class Tooty extends JavaPlugin {
    public String uuid = null;
    // private WebSocketClient wsClient;
    private Logger logger = getServer().getLogger();

    @Override
    public void onEnable() {
        logger.info("Tooty is enabling...");
        uuid = getUuid();
        logger.info("Your uuid is: '" + uuid + "'");
        new WebSocketClient(this);
        logger.info("Websocket client created!");
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
                File file = new File("./Tooty/uuid.txt");
                Scanner myReader = new Scanner(file);
                if (myReader.hasNextLine()) {
                    String data = myReader.nextLine();
                    myReader.close();
                    return data;
                }
                myReader.close();
            } catch (FileNotFoundException e) {
                try {
                    File file = new File("./Tooty/uuid.txt");
                    file.createNewFile();
                } catch (IOException e1) {
                    logger.warning("Could not create/read uuid.txt file ;(");
                }
            }
            return null;
        }
    }
}
