/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.ptibiscuit.iprofession.data.models.skill;

import java.util.HashMap;
import java.util.HashSet;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import com.ptibiscuit.iprofession.Plugin;

/**
 *
 * @author ANNA
 */
public abstract class Skill implements Listener {
    @Deprecated
    public static HashMap<String, String> skillTypes = new HashMap<String, String>();
    public static HashSet<Class<? extends Skill>> skills = new HashSet<Class<? extends Skill>>();

    public abstract void onEnable(ConfigurationSection config);

    public abstract String getKey();

    private boolean isGod(Player p) {
        return p.hasPermission(Plugin.getInstance().godperm);
    }

    public boolean hasToAct(Player p) {
        if (isGod(p) || !Plugin.getInstance().isWorldActivated(p.getWorld())) {
            return false;
        }
        return true;
    }

    // Just to tag Skill
    static {
        skills.add(SkillBreakBlock.class);
        skills.add(SkillPlaceBlock.class);
        skills.add(SkillCraftItem.class);
        skills.add(SkillSmeltItem.class);
        skills.add(SkillWearItem.class);
        skills.add(SkillGainMoneyOnBreakBlock.class);
        skills.add(SkillGainMoneyOnKillCreature.class);
        skills.add(SkillDropItem.class);
        String baseUrl = "com.ptibiscuit.iprofession.data.models.skill.";
        // Pure Skill, like the first ones :3
        // XXX This is a horrible thing to do
        skillTypes.put("breakBlock", baseUrl + "SkillBreakBlock");
        skillTypes.put("placeBlock", baseUrl + "SkillPlaceBlock");
        skillTypes.put("craftItem", baseUrl + "SkillCraftItem");
        skillTypes.put("smeltItem", baseUrl + "SkillSmeltItem");
        skillTypes.put("wearItem", baseUrl + "SkillWearItem");
        skillTypes.put("useItem", baseUrl + "SkillUseItem");
        // Skill about winning money.
        skillTypes.put("gainMoneyOnBreakBlock", baseUrl + "SkillGainMoneyOnBreakBlock");
        skillTypes.put("gainMoneyOnKillCreature", baseUrl + "SkillGainMoneyOnKillCreature");
        // About drop
        skillTypes.put("dropItem", baseUrl + "SkillDropItem");
    }
}
