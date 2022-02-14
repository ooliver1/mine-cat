package xyz.minecat;

import java.io.File;
import java.util.Scanner;
import org.json.JSONObject;
import java.net.http.WebSocket;
import org.bukkit.entity.Player;
import java.util.logging.Logger;
import java.io.FileNotFoundException;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.scheduler.BukkitScheduler;

public class Minecat extends JavaPlugin {
    public int reloads = 0;
    private WebSocket client;
    private String uuid = null;
    public boolean note = false;
    private Logger logger = getLogger();

    private MessageListener messageListener;
    private AdvancementListener advancementListener;
    private JoinLeaveListeners joinLeaveListeners;
    private LinkCommand linkCommand;

    @Override
    public void onEnable() {
        logger.info("Minecat is enabling...");
        this.saveDefaultConfig();
        logger.info("Config saved!");
        uuid = getUuid();
        logger.info("Your uuid is: '" + uuid + "'");

        WebSocketClient wsClient = new WebSocketClient(this);
        logger.info("Websocket client created!");
        this.client = wsClient.getClient();

        this.messageListener = new MessageListener(this, this.client);
        logger.info("MessageListener is enabled!");

        this.advancementListener = new AdvancementListener(this, this.client);
        logger.info("AdvancementListener is enabled!");

        this.joinLeaveListeners = new JoinLeaveListeners(this, this.client);
        logger.info("JoinLeaveListeners is enabled!");

        this.linkCommand = new LinkCommand(this, this.client);
        logger.info("LinkCommand is enabled!");
    }

    @Override
    public void onDisable() {
        logger.info("Minecat is disabling...");
        try {
            client.sendClose(1000, "shutting down mc server...");
        }
        catch (NullPointerException e) {
            logger.warning("Cannot close as client is null ;(");
        }
        logger.info("Websocket closed with status 1000 as the plugin is disabled");
    }

    public String getUuid() {
        if (this.uuid != null) {
            return this.uuid;
        }
        else {
            try {
                File file = new File(this.getDataFolder().getPath() + "/" + "uuid.txt");
                Scanner myReader = new Scanner(file);
                if (myReader.hasNextLine()) {
                    String data = myReader.nextLine();
                    myReader.close();
                    return data;
                }
                myReader.close();
            }
            catch (FileNotFoundException e) {
                return "unknown";
            }
            return "unknown";
        }
    }

    public void putPlayer(PlayerEvent event, JSONObject req) {
        Player player = event.getPlayer();
        String uuid = player.getUniqueId().toString();
        String name = player.getDisplayName();
        req.put("uuid", uuid);
        req.put("name", ChatColor.stripColor(name));
    }

    public int getWsReloadTime() {
        switch (this.reloads) {
            case 0:
                return 1;
            case 1:
                return 2;
            case 2:
                return 4;
            case 3:
                return 8;
            case 4:
                return 16;
            default:
                return 32;
        }
    }

    public void reloadWs() {
        BukkitScheduler scheduler = getServer().getScheduler();
        scheduler.runTaskLater(this, () -> {
            this.reloads++;
            this.client = new WebSocketClient(this).getClient();

            this.messageListener.reload(this.client);
            this.advancementListener.reload(this.client);
            this.joinLeaveListeners.reload(this.client);
            this.linkCommand.reload(this.client);
        }, 20L * this.getWsReloadTime());
    }
}
