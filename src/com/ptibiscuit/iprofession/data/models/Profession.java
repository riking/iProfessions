package com.ptibiscuit.iprofession.data.models;

import com.ptibiscuit.iprofession.data.models.skill.Skill;
import java.util.ArrayList;

public class Profession {
    private String name;
    private String tag;
    private ArrayList<Skill> skills = new ArrayList<Skill>();
    private double price;
    private Profession parent;
    private String group;

    private ArrayList<Require> prerequis = new ArrayList<Require>();

    public Profession(String tag, String name, ArrayList<Skill> skills, ArrayList<Require> prerequis, Profession parent, double price, String group) {
        this.name = name;
        this.skills = skills;
        this.tag = tag;
        this.price = price;
        this.prerequis = prerequis;
        this.parent = parent;
        this.group = group;
    }

    public String getGroup() {
        return group;
    }

    public boolean hasSkill(Skill sk) {
        if (parent != null)
            if (parent.hasSkill(sk))
                return true;

        return (this.skills.contains(sk));
    }

    public String getName() {
        return name;
    }

    public String getTag() {
        return tag;
    }

    public boolean isInTheSameTree(Profession other) {
        // On regarde si en faisant l'iteration des parents de l'un des 2, on tombe sur l'autre.
        Profession localParent = other;
        while (localParent != null) {
            if (localParent == this)
                return true;
            localParent = localParent.getParent();
        }

        // Maintenant, en partant de this
        localParent = this.getParent();
        while (localParent != null) {
            if (localParent == other)
                return true;
            localParent = localParent.getParent();
        }

        return false;
    }

    public ArrayList<Skill> getSkills() {
        return skills;
    }

    public ArrayList<Require> getRequired() {
        return prerequis;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return tag;
    }

    public Profession getParent() {
        return parent;
    }
}
