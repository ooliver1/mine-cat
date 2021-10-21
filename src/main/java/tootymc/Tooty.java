package tootymc;

import org.bukkit.plugin.java.JavaPlugin;

public class Tooty extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("Tooty is enabling...");
        new ChatMessage(this);
        getLogger().info("ChatMessage is enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Tooty is disabling...");
    }
}