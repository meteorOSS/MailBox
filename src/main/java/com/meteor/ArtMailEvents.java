package com.meteor;

import com.meteor.Data.MailData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ArtMailEvents implements Listener {
    private ArtMailBox plugin;
    public ArtMailEvents(ArtMailBox plugin){this.plugin = plugin;}
    public String getItemKey(String player , String name){
        for(int i = 0; i< ArtMailBox.mail.get(player).size(); i++){
            MailData mdata = ArtMailBox.mail.get(player).get(i);
            if(name.contains(mdata.getKey())){
                return mdata.getKey();
            }
        }
        return "no";
    }
    @EventHandler
    public void Join(PlayerJoinEvent event){
        if(plugin.getMySql().checkPlayer(event.getPlayer().getName())==false){
            plugin.getMySql().addPlayer(event.getPlayer().getName());
        }
        YamlConfiguration yml = plugin.getMySql().getMailList(event.getPlayer().getName());
        if(!yml.saveToString().equalsIgnoreCase("")){
            event.getPlayer().sendMessage(plugin.getConfig().getString("Message.new_mail_nopapi").replace("&","§"));
            return;
        }
    }
    @EventHandler
    public void close(InventoryCloseEvent event){
        if(event.getView().getTitle().equalsIgnoreCase(plugin.getConfig().getString("Mail_Setting.Info.Title").replace("&","§"))){
            ArtMailBox.mail.remove(event.getPlayer().getName());
        }
    }
    @EventHandler
    public void click(InventoryClickEvent event){
        if(event.getView().getTitle().equalsIgnoreCase(plugin.getConfig().getString("Mail_Setting.Info.Title").replace("&","§"))){
            event.setCancelled(true);
            ItemStack itemStack = event.getCurrentItem();
                Player player = (Player) event.getWhoClicked();
                if(itemStack!=null&&itemStack.getType()!=Material.AIR){
                    if(itemStack.getItemMeta().hasDisplayName()&&itemStack.getItemMeta().getDisplayName().equalsIgnoreCase(plugin.getConfig().getString("item.return.name").replace("&","§"))){
                        player.closeInventory();
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),plugin.getConfig().getString("item.return.commands").replace("@player@",player.getName()));
                        return;
                    }
                    if(itemStack.getItemMeta().hasDisplayName()&&itemStack.getItemMeta().getDisplayName().equalsIgnoreCase(plugin.getConfig().getString("item.flag.name").replace("&","§"))){
                        return;
                    }
                    String key = getItemKey(player.getName(),itemStack.getItemMeta().getLore().get(plugin.getConfig().getInt("Mail_Setting.Info.int")));
                    if(!key.equalsIgnoreCase("no")){
                        Inventory pinv = player.getInventory();
                        for(MailData temp : ArtMailBox.mail.get(player.getName())){
                            if(temp.getKey().equalsIgnoreCase(key)){
                                for(String item :temp.getItem()){
                                    ItemStack items = plugin.getMySql().getItem(item.substring(0,item.indexOf(":")));
                                    items.setAmount(Integer.valueOf(item.substring(item.indexOf(":")).replace(":","")).intValue());
                                    pinv.addItem(items);
                                    plugin.getMySql().delMail(player.getName(),key);
                                    player.closeInventory();
                                }
                            }
                        }
                        player.sendMessage(plugin.getConfig().getString("Message.use_mail").replace("@key@",key).replace("&","§"));
                    }
                }
            }
        }
    }

