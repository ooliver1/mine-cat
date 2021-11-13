package tootymc;

import java.io.File;
import java.util.Scanner;
import org.json.JSONObject;
import java.sql.SQLException;
import java.net.http.WebSocket;
import org.bukkit.entity.Player;
import java.util.logging.Logger;
import java.io.FileNotFoundException;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.player.PlayerEvent;

public class Tooty extends JavaPlugin {
    private Players players;
    private WebSocket client;
    private String uuid = null;
    private Logger logger = getLogger();

    @Override
    public void onEnable() {
        logger.info("Tooty is enabling...");
        this.saveDefaultConfig();
        logger.info("Config saved!");
        uuid = getUuid();
        logger.info("Your uuid is: '" + uuid + "'");
        WebSocketClient wsClient = new WebSocketClient(this);
        logger.info("Websocket client created!");
        this.client = wsClient.getClient();
        new MessageListener(this, this.client);
        logger.info("MessageListener is enabled!");
        new AdvancementListener(this, this.client);
        logger.info("AdvancementListener is enabled!");
        this.players = new Players(this);
        logger.info("Players manager is enabled!");
    }

    public void addPlayer(String uuid, String id) {
        players.addPlayer(uuid, id);
    }

    public String getDiscordId(String uuid) {
        return this.players.getDiscordId(uuid);
    }

    public String getUuid(String id) {
        return this.players.getUuid(id);
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

    public void putPlayer(PlayerEvent event, JSONObject req) {
        Player player = event.getPlayer();
        String uuid = player.getUniqueId().toString();
        String id = this.getDiscordId(uuid);
        String name = player.getDisplayName();
        if (id != null) {
            req.put("id", id);
            req.put("dc", true);
        } else {
            req.put("id", uuid);
            req.put("dc", false);
            req.put("name", name);
        }
    }
}
