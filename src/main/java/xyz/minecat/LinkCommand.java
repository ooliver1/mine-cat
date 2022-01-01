package xyz.minecat;

import java.util.Random;
import org.json.JSONObject;
import java.net.http.WebSocket;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;

public class LinkCommand implements CommandExecutor {
    private WebSocket client;

    public LinkCommand(Minecat plugin, WebSocket client) {
        this.client = client;
        plugin.getCommand("link").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            JSONObject req = new JSONObject();
            req.put("type", "link");
            req.put("uuid", player.getUniqueId().toString());

            int leftLimit = 97; // letter 'a'
            int rightLimit = 122; // letter 'z'
            int targetStringLength = 8;
            Random random = new Random();
            StringBuilder buffer = new StringBuilder(targetStringLength);
            for (int i = 0; i < targetStringLength; i++) {
                int randomLimitedInt = leftLimit + (int) 
                (random.nextFloat() * (rightLimit - leftLimit + 1));
                buffer.append((char) randomLimitedInt);
            }
            String generatedString = buffer.toString();
            req.put("code", generatedString);
            this.client.sendText(req.toString(), true);
            player.sendMessage("Your code is §9" + generatedString + "§r.");
        }

        return true;
    }
}
