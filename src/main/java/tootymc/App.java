package tootymc;

import org.bukkit.plugin.java.JavaPlugin;

public class App extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("Tooty is enabling...");
    }

    @Override
    public void onDisable() {
        getLogger().info("Tooty is disabling...");
    }

}