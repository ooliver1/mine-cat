// SPDX-License-Identifier: MIT

package xyz.minecat

import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import org.json.JSONObject
import java.net.URI

class WsClient(private val plugin: Minecat) {
    private val logger = plugin.logger

    internal val ws = object : WebSocketClient(URI("wss://minecat.ws?${plugin.guild}")) {
        override fun onOpen(handshake: ServerHandshake) {
            logger.info("[webs] Connected to Minecat with status ${handshake.httpStatus}")

            logger.fine("[webs] Sending login")
            login()
        }

        override fun onMessage(message: String) {
            logger.finer("[webs] Received message: $message")
        }

        override fun onClose(code: Int, reason: String?, remote: Boolean) {
            logger.warning("[webs] Disconnected from Minecat with status $code reason $reason")
        }

        override fun onError(ex: Exception?) {
            logger.severe("[webs] Error while connecting to Minecat ${ex?.message}\n${ex?.stackTrace}")
        }
    }

    private fun login() {
        val data = JSONObject()
        data.put("uuid", plugin.uuid)
        sendPayload(ws, Opcode.LOGIN, data)
    }
}