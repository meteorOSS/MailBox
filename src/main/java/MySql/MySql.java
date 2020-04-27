package MySql;

import com.meteor.ArtMailBox;
import com.meteor.Data.MailData;
import com.meteor.Data.PlayerData;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MySql {
    private Connection mysql;
    private ArtMailBox plugin;

    public MySql(ArtMailBox plugin){
        this.plugin = plugin;
        try {
            this.mysql = DriverManager.getConnection(plugin.getConfig().getString("mysql.url"),plugin.getConfig().getString("mysql.user")
            ,plugin.getConfig().getString("mysql.password"));
            plugin.getServer().getLogger().info("[ArtMailBox] 已成功链接数据库");
            PreparedStatement ps;
            ps = mysql.prepareStatement(MySqlCommand.CREATE_ITEM.getCommand());
            ps.execute();
            ps = mysql.prepareStatement(MySqlCommand.CREATE_MAIL.getCommand());
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addItem(String id , ByteArrayInputStream item){
        PreparedStatement ps;
        if(!checkItem(id).equalsIgnoreCase("no")){
            try {
                ps = getMysql().prepareStatement(MySqlCommand.UPDATE_ITEM.getCommand());
                ps.setString(1,id);
                ps.setBinaryStream(2,item);
                ps.setString(3,id);
                start(ps);
                return;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        try {
            ps = getMysql().prepareStatement(MySqlCommand.ADD_ITEM.getCommand());
            ps.setString(1,id);
            ps.setBinaryStream(2,item);
            start(ps);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public String checkItem(String id){
        try {
            PreparedStatement ps = getMysql().prepareStatement(MySqlCommand.CHECK_ITEM.getCommand());
            ps.setString(1,id);
            ResultSet set = ps.executeQuery();
            if(set.next()){
                return String.valueOf(set.getBinaryStream("item"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "no";
    }

    public Connection getMysql() {
        return mysql;
    }

    public ItemStack getItem(String id){
        try {
            PreparedStatement ps = getMysql().prepareStatement(MySqlCommand.CHECK_ITEM.getCommand());
            ps.setString(1,id);
            ResultSet set = ps.executeQuery();
            if(set.next()){
                ByteArrayInputStream inputStream = (ByteArrayInputStream)set.getBinaryStream("item");
                byte[] bytes = new byte[inputStream.available()];
                inputStream.read(bytes);
                String str = new String(bytes, StandardCharsets.UTF_8);
                YamlConfiguration yml = new YamlConfiguration();
                try {
                    yml.load(new StringReader(str));
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InvalidConfigurationException e) {
                    e.printStackTrace();
                }
                return yml.getItemStack(id);
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
        return new ItemStack(Material.AIR);
    }

    //邮件操作
    public YamlConfiguration getMailList(String player){
        YamlConfiguration yml = new YamlConfiguration();
        try {
            PreparedStatement ps = getMysql().prepareStatement(MySqlCommand.CHECK_MAIL.getCommand());
            ps.setString(1,player);
            ResultSet set = ps.executeQuery();
            if(set.next()){
                ByteArrayInputStream inputStream = (ByteArrayInputStream)set.getBinaryStream("mail");
                byte[] bytes = new byte[inputStream.available()];
                inputStream.read(bytes);
                try {
                    yml.load(new StringReader(new String(bytes,StandardCharsets.UTF_8)));
                    inputStream.close();
                } catch (InvalidConfigurationException e) {
                    e.printStackTrace();
                }
                return yml;
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
        return yml;
    }

    public List<String> getPlayerList(){
        List<String> playerlist = new ArrayList<>();
        try {
            PreparedStatement ps = getMysql().prepareStatement("select * FROM player_mail");
            ResultSet temp = ps.executeQuery();
            while (temp.next()){
                playerlist.add(temp.getString("player"));
            }
            return playerlist;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return playerlist;
    }

    public Boolean checkPlayer(String player){
        try {
            PreparedStatement ps = getMysql().prepareStatement(MySqlCommand.CHECK_PLAYER.getCommand());
            ps.setString(1,player);
            ResultSet temp = ps.executeQuery();
            if(temp.next()){
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void addPlayer(String player){
        try {
            PreparedStatement ps = getMysql().prepareStatement(MySqlCommand.ADD_PLAYER.getCommand());
            ps.setString(1,player);
            String str = "";
            ps.setBinaryStream(2,new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8)));
            ps.setString(3, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd")));
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public Boolean checkNewMail(String player){
        YamlConfiguration yml = getMailList(player);
        if(!yml.saveToString().equalsIgnoreCase("")){
            return true;
        }
        return false;
    }

    public void addMail(String player,ByteArrayInputStream mail){
        try {
            PreparedStatement ps = getMysql().prepareStatement(MySqlCommand.UPDATE_MAIL.getCommand());
            ps.setString(1,player);
            ps.setBinaryStream(2,mail);
            ps.setString(3,player);
            start(ps);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadMail(String player){
        YamlConfiguration yml = getMailList(player);
        List<MailData> mail = new ArrayList<>();
        for(String key : yml.getKeys(false)){
            mail.add(new MailData(key,yml.getString(key+".date"),yml.getString(key+".name"), yml.getStringList(key+".item")));
        }
        ArtMailBox.mail.put(player,mail);
    }

    public void delMail(String player,String mail){
        YamlConfiguration yml = getMailList(player);
        yml.set(mail,null);
        addMail(player,new ByteArrayInputStream(yml.saveToString().getBytes(StandardCharsets.UTF_8)));
    }

    public void start(PreparedStatement ps){
        try {
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
