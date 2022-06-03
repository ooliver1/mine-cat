package xyz.minecat

import net.md_5.bungee.api.ChatColor
import org.bukkit.entity.Player
import org.java_websocket.client.WebSocketClient
import org.json.JSONObject

class Sender(private val plugin: Minecat, private val ws: WebSocketClient) {
    fun login() {
        val data = JSONObject()
        data.put("uuid", plugin.uuid)
        data.put("v", plugin.version)
        sendPayload(ws, Opcode.LOGIN, data)
    }

    fun sendMessage(message: String, player: Player) {
        val data = JSONObject()
        data.put("msg", message)
        data.put("auth", player.uniqueId.toString())
        data.put("name", ChatColor.stripColor(player.displayName))
        sendPayload(ws, Opcode.MESSAGE, data)
    }
}
