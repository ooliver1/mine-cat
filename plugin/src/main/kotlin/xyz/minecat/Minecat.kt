package xyz.minecat

import org.bukkit.plugin.java.JavaPlugin

@Suppress("unused")
class Minecat : JavaPlugin() {
    override fun onEnable() {
        logger.info("Minecat is enabling")
    }
}
