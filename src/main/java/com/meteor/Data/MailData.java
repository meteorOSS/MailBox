package com.meteor.Data;

import java.util.List;

public class MailData {
    private String key;
    private String date;
    private String name;
    private List<String> item;
    public MailData(String key,String date,String name,List<String> item){
        this.key = key;
        this.date = date;
        this.name = name;
        this.item = item;
    }

    public String getKey() {
        return key;
    }

    public String getDate() {
        return date;
    }

    public String getName() {
        return name;
    }

    public List<String> getItem() {
        return item;
    }
}
