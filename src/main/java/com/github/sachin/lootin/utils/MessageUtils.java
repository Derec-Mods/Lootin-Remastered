package com.github.sachin.lootin.utils;

import com.github.sachin.lootin.Lootin;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class MessageUtils {

    private final Lootin plugin;

    public MessageUtils(Lootin plugin){
        this.plugin = plugin;
    }

    public String getMessage(String key, Player player){
        String message = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.prefix") + plugin.getConfig().getString(key, key));
        if(plugin.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI") && player != null){
            return PlaceholderAPI.setPlaceholders(player, message);
        }
        return message;
    }

    public void sendPlayerMessage(String message, Player player){
        sendMessageTo(player, getMessage(message, player), false);
    }

    public MessageMode getMessageMode(){
        String val = plugin.getConfig().getString("messages.mode", "ACTIONBAR");
        try{
            return MessageMode.valueOf(val.toUpperCase());
        }catch (Exception e){
            return MessageMode.ACTIONBAR;
        }
    }

    public void sendMessageTo(Player player, String message, boolean forceChat){
        if(player == null || message == null) return;
        if(forceChat){
            player.sendMessage(message);
            return;
        }
        if(getMessageMode() == MessageMode.ACTIONBAR){
            try{
                player.sendActionBar(message);
                return;
            }catch (NoClassDefFoundError | NoSuchMethodError ignored){
                // fallthrough to chat fallback
            }
        }
        player.sendMessage(message);
    }

    public String getPrefix(){
        return ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.prefix"));
    }

    public String getTitle(String key){
        return ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString(key,"Error"));
    }
}
