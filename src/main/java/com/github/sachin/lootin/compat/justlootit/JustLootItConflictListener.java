package com.github.sachin.lootin.compat.justlootit;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JustLootItConflictListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.isOp() || player.hasPermission("lootin.admin") || player.hasPermission("lootin.command.*")) {
            player.sendMessage(ChatColor.DARK_RED + "====================================================");
            player.sendMessage(ChatColor.RED + "[Lootin] Lootin has disabled itself for safety reasons!");
            player.sendMessage(ChatColor.RED + "[Lootin] JustLootIt was detected running on this server.");
            player.sendMessage(ChatColor.RED + "[Lootin] Both plugins cannot run at the same time.");
            player.sendMessage(ChatColor.RED + "[Lootin] Lootin includes automatic, lossless JustLootIt conversion.");
            player.sendMessage(ChatColor.RED
                    + "[Lootin] To complete migration: stop the server, remove JustLootIt.jar, and start the server. (It's still an awesome plugin!!)");
            player.sendMessage(ChatColor.DARK_RED + "====================================================");
        }
    }
}
