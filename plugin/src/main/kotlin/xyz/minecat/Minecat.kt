// SPDX-License-Identifier: MIT

package xyz.minecat

import org.bukkit.plugin.java.JavaPlugin
import org.java_websocket.client.WebSocketClient
import xyz.minecat.listeners.MessageListener

@Suppress("unused")
class Minecat : JavaPlugin() {
    private lateinit var wsClient: WsClient
    private lateinit var ws: WebSocketClient
    internal lateinit var sender: Sender
    internal lateinit var handler: Handler
    val version = "0.0.1"

    private lateinit var messageListener: MessageListener

    internal var guild
        get() = config.getString("guild", "unknown")
        set(value) {
            config.set("guild", value)
            saveConfig()
        }
    internal var uuid
        get() = config.getString("uuid", "unknown")
        set(value) {
            config.set("uuid", value)
            saveConfig()
        }

    override fun onEnable() {
        logger.info("[mine] Minecat is enabling")

        logger.fine("[mine] Creating ws client")
        wsClient = WsClient(this)
        ws = wsClient.ws

        logger.fine("[mine] Creating sender")
        sender = Sender(this, ws)

        logger.fine("[mine] Creating ws handler")
        handler = Handler(this)

        logger.fine("[mine] Creating message listener")
        messageListener = MessageListener(this)

        logger.fine("[mine] starting ws client")
        ws.connect()
    }
}
