package xyz.minecat

import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.ComponentBuilder
import org.json.JSONObject
import java.util.UUID

class Handler(private val plugin: Minecat) {
    private val logger = plugin.logger

    fun handle(json: JSONObject) {
        val opcode = Opcode.from(json.getInt("o"))

        if (opcode == null) {
            logger.warning("[hand] Unknown opcode: ${json.getInt("o")}")
            return
        }

        when (opcode) {
            Opcode.LOGIN -> handleLogin(json)
            Opcode.MESSAGE -> handleMessage(json)
        }
    }

    private fun handleLogin(json: JSONObject) {
        val uuid = json.getString("uuid")

        if (uuid != null) {
            logger.info("[hand] Your uuid is $uuid")
            plugin.uuid = uuid
        }
    }

    private fun handleMessage(json: JSONObject) {
        val message = json.getString("msg")
        val discord = json.getBoolean("dc")

        logger.finest("[hand] ($discord) $message")

        if (discord) {
            val name = json.getString("name")

            val msg = ComponentBuilder()
                .append("<")
                .append(name).color(ChatColor.BLUE)
                .append("> ")
                .append(message)

            plugin.server.spigot().broadcast(*msg.create())
        } else {
            val author = json.getString("uuid")

            val player = plugin.server.getPlayer(UUID.fromString(author))
            plugin.server.broadcastMessage("<$player>: $message")
        }
    }
}
