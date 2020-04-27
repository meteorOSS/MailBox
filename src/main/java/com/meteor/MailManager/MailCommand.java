package com.meteor.MailManager;

import com.meteor.ArtMailBox;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MailCommand implements CommandExecutor {
    private ArtMailBox plugin;
    public MailCommand(ArtMailBox plugin){this.plugin = plugin;}
    @Override
    public boolean onCommand(CommandSender sender, Command command, String arg, String[] args) {
        if(args.length==0||args[0].equalsIgnoreCase("open")){
            if(!(sender instanceof Player)){
                sender.sendMessage("§f[§aArtMailBox§f] §c控制台查看帮助请输入/artmail help。");
                return true;
            }
            plugin.getMySql().loadMail(sender.getName());
            MailInventory.openMail((Player)sender);
            return true;
        }
        if(args[0].equalsIgnoreCase("reload")){
            if(!sender.isOp()||!sender.hasPermission("artmail.admin")){
                sender.sendMessage("§f[§aArtMailBox§f] §c你没有足够的权限");
                return true;
            }
            plugin.reloadConfig();
            sender.sendMessage("§f[§aArtMailBox§f] §c已重载配置文件");
            return true;
        }
        if(args[0].equalsIgnoreCase("help")){
            if(!sender.isOp()||!sender.hasPermission("artmail.admin")){
                sender.sendMessage("§f[§aArtMailBox§f] §c你没有足够的权限");
                return true;
            }
            sender.sendMessage("§f ");
            sender.sendMessage("§f       §8 [ §aArtMailManager §8]       ");
            sender.sendMessage("§e§o/artmail §f打开邮箱");
            sender.sendMessage("§e§o/artmail send §c[player] §3[邮件名] §3[物品ID...] §f向指定玩家发送一封邮件");
            sender.sendMessage("§e§o/artmail all §3[邮件名] §3[物品ID...] §f向所有玩家发送一封邮件");
            sender.sendMessage("§e§o/artmail reload §f重载配置文件");
            sender.sendMessage("§f ");
        }
        if(args.length>=3&&args[0].equalsIgnoreCase("send")){
            if(!sender.isOp()||!sender.hasPermission("artmail.admin")){
                sender.sendMessage("§f[§aArtMailBox§f] §c你没有足够的权限");
                return true;
            }
            List<String> mail = new ArrayList<>();
            for(int i = 3;i<args.length;i++){
                mail.add(args[i]);
            }
            for(String item :mail){
                if(plugin.getMySql().checkItem(item.substring(0,item.indexOf(":"))).equalsIgnoreCase("no")){
                    sender.sendMessage("§f[§aArtMailBox§f] §3ID为 §e" + item.substring(0,item.indexOf(":")) +" §3的物品不存在,请检查指令");
                    return true;
                }
            }
            YamlConfiguration yml = plugin.getMySql().getMailList(args[1]);
            String key = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 5);
            yml.set(key+".name",args[2]);
            yml.set(key+".date",LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd")));
            yml.set(key+".item",mail);
            plugin.getMySql().addMail(args[1],new ByteArrayInputStream(yml.saveToString().getBytes(StandardCharsets.UTF_8)));
            sender.sendMessage("§f[§aArtMailBox§f] §3已向 §e" +args[1] + " §3发送一封邮件");
            return true;
        }
        if(args.length>=2&&args[0].equalsIgnoreCase("all")){
            if(!sender.isOp()||!sender.hasPermission("artmail.admin")){
                sender.sendMessage("§f[§aArtMailBox§f] §c你没有足够的权限");
                return true;
            }
            List<String> mail = new ArrayList<>();
            for(int i = 2;i<args.length;i++){
                mail.add(args[i]);
            }
            for(String item :mail){
                if(plugin.getMySql().checkItem(item.substring(0,item.indexOf(":"))).equalsIgnoreCase("no")){
                    sender.sendMessage("§f[§aArtMailBox§f] §3ID为 §e" + item.substring(0,item.indexOf(":")) +" §3的物品不存在,请检查指令");
                    return true;
                }
            }
            List<String> player = plugin.getMySql().getPlayerList();
            for(String str : player){
                YamlConfiguration yml = plugin.getMySql().getMailList(str);
                String key = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 5);
                yml.set(key+".name",args[1]);
                yml.set(key+".date",LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd")));
                yml.set(key+".item",mail);
                plugin.getMySql().addMail(str,new ByteArrayInputStream(yml.saveToString().getBytes(StandardCharsets.UTF_8)));
            }
            sender.sendMessage("§f[§aArtMailBox§f] §3已向全体玩家发送一封邮件");
            return true;
        }
        return false;

    }
}
