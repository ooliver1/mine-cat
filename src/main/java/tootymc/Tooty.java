package tootymc;

import java.io.File;
import java.util.Scanner;
import java.sql.SQLException;
import java.net.http.WebSocket;
import java.util.logging.Logger;
import java.io.FileNotFoundException;
import org.bukkit.plugin.java.JavaPlugin;

public class Tooty extends JavaPlugin {
    private Players players;
    private WebSocket client;
    private String uuid = null;
    private Logger logger = getLogger();

    @Override
    public void onEnable() {
        logger.info("Tooty is enabling...");
        uuid = getUuid();
        logger.info("Your uuid is: '" + uuid + "'");
        WebSocketClient wsClient = new WebSocketClient(this);
        logger.info("Websocket client created!");
        this.client = wsClient.getClient();
        new MessageListener(this, this.client);
        logger.info("MessageListener is enabled!");
        this.players = new Players(this);
        logger.info("Players manager is enabled!");
    }

    public void addPlayer(String uuid, String id) {
        players.addPlayer(uuid, id);
    }

    public String getDiscordId(String uuid) {
        return this.players.getDiscordId(uuid);
    }

    @Override
    public void onDisable() {
        logger.info("Tooty is disabling...");
        try {
            client.sendClose(1000, "shutting down mc server...");
        } catch (NullPointerException e) {
            logger.warning("Cannot close as client is null ;(");
        }
        logger.info("Websocket closed with status 1000 as the plugin is disabled");
        try {
            players.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
