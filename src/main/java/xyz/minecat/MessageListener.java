/*
Minecat: a Minecraft plugin for connecting to Discord!
Copyright (C) 2021-present ooliver1

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package xyz.minecat;

import org.bukkit.Server;
import org.json.JSONObject;
import java.net.http.WebSocket;
import java.util.logging.Logger;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class MessageListener implements Listener {
    private Minecat plugin;
    private Logger logger;
    private Server mcServer;
    private WebSocket client;

    public MessageListener(Minecat plugin, WebSocket client) {
        this.client = client;
        this.plugin = plugin;
        logger = plugin.getLogger();
        mcServer = plugin.getServer();
        mcServer.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onMessage(AsyncPlayerChatEvent event) {
        String msg = event.getMessage();
        logger.info("Message: " + msg);
        JSONObject req = new JSONObject();
        req.put("type", "msg");
        req.put("msg", msg);
        this.plugin.putPlayer(event, req);
        client.sendText(req.toString(), true);
    }

    public void reload(WebSocket client) {
        this.client = client;
    }
}
