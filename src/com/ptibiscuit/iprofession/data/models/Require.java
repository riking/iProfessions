package com.ptibiscuit.iprofession.data.models;

import org.bukkit.entity.Player;

import com.ptibiscuit.iprofession.Plugin;

public class Require {
    private String category;
    private String key;
    private int required;

    private String hasnot;

    public Require(String category, String key, int required) {
        super();
        this.category = category;
        this.key = key;
        this.required = required;
    }

    public String getCategory() {
        return category;
    }

    public String getKey() {
        return key;
    }

    public int getRequired() {
        return required;
    }


    public String getHasnot() {
        return hasnot;
    }

    public void setHasnot(String hasnot) {
        this.hasnot = hasnot;
    }
}
