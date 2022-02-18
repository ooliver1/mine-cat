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
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.event.player.PlayerEvent;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.hover.content.Text;

public class JoinLeaveListeners implements Listener {
    private Minecat plugin;
    private Logger logger;
    private Server mcServer;
    private WebSocket client;

    public JoinLeaveListeners(Minecat plugin, WebSocket client) {
        this.client = client;
        this.plugin = plugin;
        mcServer = plugin.getServer();
        logger = plugin.getLogger();
        mcServer.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        sendWs("join", event);
        if (this.plugin.note) {
            logger.info("Telling player you are not set with discord");
            BaseComponent[] comp = new ComponentBuilder("<")
                    .append("minecat").color(ChatColor.BLUE)
                    .append("> Hey! "
                            + "Nice that you have set this plugin up, however"
                            + "this plugin is useless without ")
                    .color(ChatColor.RESET)
                    .append("the discord bot.")
                    .color(ChatColor.LIGHT_PURPLE)
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                            new Text("Visit the discord link for the bot!")))
                    .event(new ClickEvent(ClickEvent.Action.OPEN_URL,
                            "https://discord.gg/GhTu4GcKxX"))
                    .append(" If you already have the bot added, please follow ")
                    .color(ChatColor.RESET)
                    .append("this guide")
                    .color(ChatColor.YELLOW)
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                            new Text("Visit the SpigotMC resources page for minecat!")))
                    .event(new ClickEvent(ClickEvent.Action.OPEN_URL,
                            "https://www.spigotmc.org/resources/minecat.98640/"))
                    .append(" if you have access to this server's files "
                            + "or ask the owner to follow this guide!")
                    .color(ChatColor.RESET)
                    .create();
            this.mcServer.spigot().broadcast(comp);
            this.plugin.note = false;
        }
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

    public void reload(WebSocket client) {
        this.client = client;
    }
}
