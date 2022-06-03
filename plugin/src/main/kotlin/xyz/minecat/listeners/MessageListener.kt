package xyz.minecat.listeners

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import xyz.minecat.Minecat

class MessageListener(private val plugin: Minecat) : Listener {
    private val logger = plugin.logger

    init {
        logger.finest("[msgl] Registering events")
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    @EventHandler
    fun onMessage(event: AsyncPlayerChatEvent) {
        val msg = event.message
        val player = event.player

        logger.finer("[msgl] Received $msg from $player")

        logger.finest("[msgl] Sending $msg to sender")
        plugin.sender.sendMessage(msg, player)
    }
}