package com.ptibiscuit.iprofession.data.models;

import java.util.ArrayList;
import java.util.Map;

import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.PermissionAttachment;

import com.ptibiscuit.iprofession.Plugin;
import com.ptibiscuit.iprofession.data.models.skill.Skill;

public class Profession {
    private Plugin p;
    private String name;
    private String tag;
    private ArrayList<Skill> skills = new ArrayList<Skill>();
    private double price;
    private Profession parent;
    private String group;

    private ArrayList<Require> prerequis = new ArrayList<Require>();
    private Map<String, Boolean> permissions;

    public Profession(Plugin p, String tag, String name, ArrayList<Skill> skills, Map<String, Boolean> permissions, ArrayList<Require> prerequis, Profession parent, double price, String group) {
        this.p = p;
        this.name = name;
        this.skills = skills;
        this.tag = tag;
        this.price = price;
        this.prerequis = prerequis;
        this.parent = parent;
        this.permissions = permissions;
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

    public Map<String, Boolean> getGrantedPermissions() {
        return permissions;
    }

    public PermissionAttachment applyPermissions(Permissible target) {
        PermissionAttachment ret = new PermissionAttachment(p, target);
        for (Map.Entry<String, Boolean> e : permissions.entrySet()) {
            ret.setPermission(e.getKey(), e.getValue().booleanValue());
        }
        return ret;
    }
}
