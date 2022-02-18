// SPDX-License-Identifier: GPL-3.0-or-later

package xyz.minecat;

import java.io.File;
import java.util.Random;
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

    public float getWsReloadTime() {
        switch (this.reloads) {
            case 0:
                return 0.25F;
            case 1:
                return 0.5F;
            case 2:
                return 1F;
            case 3:
                return 2F;
            case 4:
                return 4F;
            default:
                return 8F;
        }
    }

    public void reloadWs() {
        BukkitScheduler scheduler = getServer().getScheduler();
        Random random = new Random();
        long time = (long) ((random.nextFloat() * 2) + this.getWsReloadTime()) * 20;
        scheduler.runTaskLater(this, () -> {
            this.reloads++;
            this.client = new WebSocketClient(this).getClient();

            this.messageListener.reload(this.client);
            this.advancementListener.reload(this.client);
            this.joinLeaveListeners.reload(this.client);
            this.linkCommand.reload(this.client);
        }, time);
    }
}
