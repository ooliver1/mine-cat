package xyz.minecat

import org.java_websocket.client.WebSocketClient
import org.json.JSONObject

fun sendPayload(ws: WebSocketClient, opcode: Opcode, payload: JSONObject) {
    val json = JSONObject()
    json.put("o", opcode.ordinal)
    json.put("d", payload)
    ws.send(json.toString())
}
