package tootymc;

import org.bukkit.Server;
import org.json.JSONObject;
import java.net.http.WebSocket;
import java.util.logging.Logger;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitevent;

public class JoinLeaveListeners implements Listener {
    private Tooty plugin;
    private Logger logger;
    private Server mcServer;
    private WebSocket client;

    public JoinLeaveListeners(Tooty plugin, WebSocket client) {
        this.client = client;
        this.plugin = plugin;
        mcServer = plugin.getServer();
        logger = plugin.getLogger();
        mcServer.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        sendWs("join", event);
    }

    @EventHandler
    public void onPlayerJoin(PlayerQuitEvent event) {
        sendWs("leave", event);
    }

    private void sendWs(String type, PlayerEvent event) {
        JSONObject req = new JSONObject();
        req.put("type", type);
        this.plugin.putPlayer(event, req);
        client.sendText(req.toString(), true);
        logger.info("Player" + type);
    }
}
