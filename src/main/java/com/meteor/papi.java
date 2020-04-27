package com.meteor;

import me.clip.placeholderapi.PlaceholderHook;
import org.bukkit.Art;
import org.bukkit.entity.Player;

public class papi extends PlaceholderHook {
    private ArtMailBox plugin;
    public papi(ArtMailBox plugin){this.plugin = plugin;}
    @Override
    public String onPlaceholderRequest(Player player, String string) {
        if(string.equalsIgnoreCase("newmail")){
            if(plugin.getMySql().checkNewMail(player.getName())){
                return plugin.getConfig().getString("Message.new_mail").replace("&","§");
            }
        }
        return plugin.getConfig().getString("Message.no_mail").replace("&","§");
    }
}
