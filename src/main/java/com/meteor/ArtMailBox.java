package com.meteor;

import MySql.MySql;
import com.meteor.Data.MailData;
import com.meteor.Data.PlayerData;
import com.meteor.ItemManager.ArtItemCommand;
import com.meteor.MailManager.MailCommand;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;

public final class ArtMailBox extends JavaPlugin {
    private static MySql mySql;
    public static HashMap<String, List<MailData>> mail = new HashMap<>();
    public static JavaPlugin plugin;
    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();
        plugin = this;
        getServer().getLogger().info("[ArtMailBox] Art物品及邮箱管理已加载。");
        mySql = new MySql(this);
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            getServer().getLogger().info("[ArtMailBox] 已关联PlaceholderAPI");
            PlaceholderAPI.registerPlaceholderHook(plugin,new papi(this));
        }
        getServer().getPluginCommand("artitem").setExecutor(new ArtItemCommand(this));
        getServer().getPluginCommand("artmail").setExecutor(new MailCommand(this));
        getServer().getPluginManager().registerEvents(new ArtMailEvents(this),this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public MySql getMySql() {
        return mySql;
    }
    public static MySql getMail(){
        return mySql;
    }
}
