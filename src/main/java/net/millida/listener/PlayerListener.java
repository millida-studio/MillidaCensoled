package net.millida.listener;

import net.millida.CensurePlugin;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (!player.isOp()) {
            return;
        }

        if (!CensurePlugin.INSTANCE.isHasUpdate()) {
            return;
        }

        player.sendMessage("");
        player.sendMessage(ChatColor.GOLD + "It seems that a new version of the Censure plugin has been released!");
        player.sendMessage(ChatColor.GOLD + "Find out more - https://www.spigotmc.org/resources/censure.100546/");
        player.sendMessage("");
    }
}
