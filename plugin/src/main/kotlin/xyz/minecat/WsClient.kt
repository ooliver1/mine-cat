// SPDX-License-Identifier: MIT

package xyz.minecat

import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI

class WsClient(plugin: Minecat) {
    private val url = URI("wss://minecat.ws?${plugin.guild}")
    private val logger = plugin.logger

    internal val ws = object : WebSocketClient(url) {
        override fun onOpen(handshake: ServerHandshake) {
            logger.info("[webs] Connected to Minecat with status ${handshake.httpStatus}")
        }

        override fun onMessage(message: String) {
            logger.fine("[webs] Received message: $message")
        }

        override fun onClose(code: Int, reason: String?, remote: Boolean) {
            logger.warning("[webs] Disconnected from Minecat with status $code reason $reason")
        }

        override fun onError(ex: Exception?) {
            logger.severe("[webs] Error while connecting to Minecat ${ex?.message}\n${ex?.stackTrace}")
        }
    }
}