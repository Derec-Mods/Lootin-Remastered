package com.github.sachin.lootin.utils;

import com.github.sachin.lootin.Lootin;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MessageUtils {
    private static Lootin plugin() { return Lootin.getPlugin(); }

    /* Backwards-compatible static wrappers (aliases) */
    public static String getMessageStatic(String key, Player player){
        return getMessage(key, player);
    }

    public static void sendActionBarStatic(Player player, String message){
        sendActionBar(player, message);
    }

    public static void sendStatic(CommandSender sender, String key){
        send(sender, key);
    }

    public static void sendStatic(CommandSender sender, String key, boolean force){
        send(sender, key, force);
    }

    public static String getMessage(String key, Player player) {
        Lootin plugin = plugin();
        String message = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.prefix") + plugin.getConfig().getString(key, key));
        if (plugin.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI") && player != null) {
            return PlaceholderAPI.setPlaceholders(player, message);
        }
        return message;
    }

    public static void sendPlayerMessage(String message, Player player) {
        sendMessageTo(player, getMessage(message, player), false);
    }

    public static void sendMessageTo(Player player, String message, boolean forceChat) {
        if (player == null || message == null) {
            return;
        }
        if (forceChat) {
            player.sendMessage(message);
            return;
        }
        // Default behaviour for sendMessageTo is to send chat messages.
        player.sendMessage(message);
    }

    public static String getPrefix() {
        return ChatColor.translateAlternateColorCodes('&', plugin().getConfig().getString("messages.prefix"));
    }

    public static String getTitle(String key) {
        return ChatColor.translateAlternateColorCodes('&', plugin().getConfig().getString(key, "Error"));
    }

    /**
     * Send a configured message (by key or raw text) to any CommandSender.
     * If the sender is a Player, respects message mode and `forceChat`.
     */
    public static void send(CommandSender sender, String key){
        send(sender, key, false);
    }

    public static void send(CommandSender sender, String key, boolean forceChat){
        if(sender == null || key == null) return;
        Player player = sender instanceof Player ? (Player) sender : null;
        String message = getMessage(key, player);
        if(player != null){
            sendMessageTo(player, message, forceChat);
        } else {
            sender.sendMessage(message);
        }
    }

    /**
     * Send an action bar message to a player. Falls back to chat if action bar is not available.
     */
    public static void sendActionBar(Player player, String message){
        if(player == null || message == null) return;
        try{
            player.sendActionBar(message);
            return;
        }catch (NoClassDefFoundError | NoSuchMethodError ignored){
            // try spigot text component as fallback
        }
        try{
            net.md_5.bungee.api.chat.TextComponent tc = new net.md_5.bungee.api.chat.TextComponent(message);
            player.spigot().sendMessage(net.md_5.bungee.api.ChatMessageType.ACTION_BAR, tc);
            return;
        }catch (Throwable ignored){
            // final fallback to chat
        }
        player.sendMessage(message);
    }
}
