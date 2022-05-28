package xyz.minecat

import org.bukkit.plugin.java.JavaPlugin
import org.java_websocket.client.WebSocketClient

@Suppress("unused")
class Minecat : JavaPlugin() {
    lateinit var wsClient: WsClient
    lateinit var ws: WebSocketClient
    var guild = config.getString("guild", "unknown")

    override fun onEnable() {
        logger.info("Minecat is enabling")

        logger.info("[ws] creating ws client")
        wsClient = WsClient(this)
        ws = wsClient.ws
    }
}
