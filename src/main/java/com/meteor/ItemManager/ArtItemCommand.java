package com.meteor.ItemManager;

import com.meteor.ArtMailBox;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

public class ArtItemCommand implements CommandExecutor {
    private ArtMailBox plugin;
    public ArtItemCommand(ArtMailBox plugin){this.plugin=plugin;}
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!sender.isOp()||!sender.hasPermission("artmailbox.admin")){
            sender.sendMessage("§f[§aArtMailBox§f] §c你没有足够的权限");
            return true;
        }
        if(args.length==0||args[0].equalsIgnoreCase("help")){
            sender.sendMessage("§f ");
            sender.sendMessage("§f       §8 [ §aArtItemManager §8]       ");
            sender.sendMessage("§e§o/artitem save §3[ID] §f保存手中物品至物品库中");
            sender.sendMessage("§e§o/artitem send §c[player] §3[ID] §f将手中物品发送至指定玩家手中");
            sender.sendMessage("§e§o/artitem get §3[ID] §f获得指定物品");
            sender.sendMessage("§f ");
            return true;
        }
        if(args.length==2&&args[0].equalsIgnoreCase("save")){
                if(sender instanceof ConsoleCommandSender){
                    sender.sendMessage("[ArtMailBox] 你不是一名玩家。");
                    return true;
                }
            Player player = (Player)sender;
            if(player.getItemInHand()==null||player.getItemInHand().getType()== Material.AIR){
                player.sendMessage("§f[§aArtMailBox§f] §3你想保存什么?脑袋里的空气吗?");
                return true;
            }
            YamlConfiguration yml = new YamlConfiguration();
            yml.set(args[1],player.getItemInHand());
            player.sendMessage("§f[§aArtMailBox§f] §3已保存名为 §e "+args[1] + " §3的物品进入物品库");
            plugin.getMySql().addItem(args[1], new ByteArrayInputStream(yml.saveToString().getBytes(StandardCharsets.UTF_8)));
            return true;
        }
        if(args.length>=2&&args[0].equalsIgnoreCase("get")){
            if(sender instanceof ConsoleCommandSender){
                sender.sendMessage("[ArtMailBox] 你不是一名玩家。");
                return true;
            }
            Player player = (Player)sender;
            if(plugin.getMySql().getItem(args[1]).getType()==Material.AIR){
                player.sendMessage("§f[§aArtMailBox§f] §3ID为§c "+args[1] + " §3的物品未找到,请确认关键词");
                return true;
            }
            ItemStack item = plugin.getMySql().getItem(args[1]);
            item.setAmount(1);
            player.getInventory().addItem(item);
            player.sendMessage("§f[§aArtMailBox§f] §3已获取ID为§c "+args[1] + " §3的物品");
            return true;
        }
        if(args.length==4&&args[0].equalsIgnoreCase("send")){
            if(Bukkit.getPlayerExact(args[1])==null||plugin.getMySql().getItem(args[2]).getType()==Material.AIR){
                sender.sendMessage("§f[§aArtMailBox§f] §3物品不存在或玩家已离线");
                return true;
            }
            Player player = Bukkit.getPlayer(args[1]);
            ItemStack item = plugin.getMySql().getItem(args[2]);
            int i = Integer.valueOf(args[3]);
            item.setAmount(i);
            player.getInventory().addItem(item);
            sender.sendMessage("§f[§aArtMailBox§f] §3已给予 §c"+args[1]+" §3指定数量的物品");
            return true;
        }
        return false;
    }
}
