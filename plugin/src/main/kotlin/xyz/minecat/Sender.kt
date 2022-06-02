package xyz.minecat

import org.java_websocket.client.WebSocketClient
import org.json.JSONObject

class Sender(private val plugin: Minecat, private val ws: WebSocketClient) {
    fun login() {
        val data = JSONObject()
        data.put("uuid", plugin.uuid)
        data.put("v", plugin.version)
        sendPayload(ws, Opcode.LOGIN, data)
    }
}
