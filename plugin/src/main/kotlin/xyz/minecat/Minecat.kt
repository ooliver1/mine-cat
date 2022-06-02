// SPDX-License-Identifier: MIT

package xyz.minecat

import org.bukkit.plugin.java.JavaPlugin
import org.java_websocket.client.WebSocketClient

@Suppress("unused")
class Minecat : JavaPlugin() {
    private lateinit var wsClient: WsClient
    internal lateinit var ws: WebSocketClient
    val version = "0.0.1"
    internal var guild
        get() = config.getString("guild", "unknown")
        set(value) = config.set("guild", value)
    internal var uuid
        get() = config.getString("uuid", "unknown")
        set(value) = config.set("uuid", value)

    override fun onEnable() {
        logger.info("[mine] Minecat is enabling")

        logger.info("[mine] creating ws client")
        wsClient = WsClient(this)
        ws = wsClient.ws

        logger.info("[mine] starting ws client")
        ws.connect()
    }
}
