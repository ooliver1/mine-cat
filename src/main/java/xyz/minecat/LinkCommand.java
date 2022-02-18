/*
Minecat: a Minecraft plugin for connecting to Discord!
Copyright (C) 2021-present ooliver1

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

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

    public void reload(WebSocket client) {
        this.client = client;
    }
}
