// SPDX-License-Identifier: MIT

package xyz.minecat

import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI

class WsClient(plugin: Minecat) {
    private val url = URI("wss://minecat.ws")

    internal val ws = object : WebSocketClient(url, mapOf("X-Guild" to plugin.guild)) {
        override fun onOpen(handshake: ServerHandshake) {
            //
        }

        override fun onMessage(message: String) {
            //
        }

        override fun onClose(code: Int, reason: String?, remote: Boolean) {
            //
        }

        override fun onError(ex: Exception?) {
            //
        }
    }
}