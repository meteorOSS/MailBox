package com.meteor.Data;

import java.util.List;

public class PlayerData {
    private String player_name;
    private List<MailData> mail_list;
    public PlayerData(String player_name,List<MailData> mail_list){
        this.player_name = player_name;
        this.mail_list = mail_list;
    }

    public String getPlayer_name() {
        return player_name;
    }

    public List<MailData> getMail_list() {
        return mail_list;
    }
}
