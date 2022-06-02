package xyz.minecat

import org.json.JSONObject

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
        }
    }

    private fun handleLogin(json: JSONObject) {
        val uuid = json.getString("uuid")

        if (uuid != null) {
            logger.info("[hand] Your uuid is $uuid")
            plugin.uuid = uuid
        }
    }
}