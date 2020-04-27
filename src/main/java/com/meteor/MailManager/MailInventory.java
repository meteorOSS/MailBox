package com.meteor.MailManager;

import com.meteor.ArtMailBox;
import com.meteor.Data.MailData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class MailInventory {
    public static void addFlag(Inventory inv){
        ArtMailBox plugin = ArtMailBox.getPlugin(ArtMailBox.class);
        int flag[] = {53, 52, 51, 50,48, 47, 46, 45 };
        ItemStack itemStack = new ItemStack(Material.valueOf(plugin.getConfig().getString("item.flag.ID")),1,(short)plugin.getConfig().getInt("item.flag.data"));
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(plugin.getConfig().getString("item.flag.name").replace("&","§"));
        itemStack.setItemMeta(itemMeta);
        for(int i:flag){
            inv.setItem(i,itemStack);
        }
        ItemStack back = new ItemStack(Material.valueOf(plugin.getConfig().getString("item.return.ID")),1,(short)plugin.getConfig().getInt("item.return.data"));
        ItemMeta im = back.getItemMeta();
        im.setDisplayName(plugin.getConfig().getString("item.return.name").replace("&","§"));
        List<String> lore = new ArrayList<>();
        for(String str : plugin.getConfig().getStringList("item.return.lore")){
            str = str.replace("&","§");
            lore.add(str);
        }
        back.setItemMeta(im);
        inv.setItem(49,back);
    }
    public static void openMail(Player player){
        ArtMailBox plugin = ArtMailBox.getPlugin(ArtMailBox.class);
        Inventory inv = Bukkit.createInventory(null,54,plugin.getConfig().getString("Mail_Setting.Info.Title").replace("&","§"));
        addFlag(inv);
        for(int i =0;i<ArtMailBox.mail.get(player.getName()).size();i++){
            ItemStack itemStack = new ItemStack(Material.valueOf(plugin.getConfig().getString("Mail_Setting.Info.ID")),1,(short)plugin.getConfig().getInt("Mail_Setting.Info.data"));
            ItemMeta itemMeta = itemStack.getItemMeta();
            MailData temp = ArtMailBox.mail.get(player.getName()).get(i);
            itemMeta.setDisplayName(ArtMailBox.plugin.getConfig().getString("Mail_Setting.Info.name")
                    .replace("@mailname@",temp.getName())
                    .replace("&","§"));
            List<String> lore = new ArrayList<>();
            for(String str : ArtMailBox.plugin.getConfig().getStringList("Mail_Setting.Info.prefix")){
                str = str
                        .replace("@sender@","§c 系统")
                        .replace("@time@",temp.getDate())
                        .replace("@key@",temp.getKey())
                        .replace("&","§");
                lore.add(str);
            }

            for(String item : temp.getItem()){
                String name;
                if(plugin.getMySql().getItem(item.substring(0,item.indexOf(":"))).getItemMeta().hasDisplayName()){
                    name = plugin.getMySql().getItem(item.substring(0,item.indexOf(":"))).getItemMeta().getDisplayName();
                }else {
                    name = plugin.getMySql().getItem(item.substring(0,item.indexOf(":"))).getType().toString();
                }
                lore.add(ArtMailBox.plugin.getConfig().getString("Mail_Setting.Info.suffix")
                .replace("@item@",name)
                        .replace("@amount@",item.substring(item.indexOf(":")+1))
                .replace("&","§"));
            }
            itemMeta.setLore(lore);
            itemStack.setItemMeta(itemMeta);
            inv.addItem(itemStack);
        }
        player.openInventory(inv);
        return;
    }
}
