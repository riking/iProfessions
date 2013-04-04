/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.ptibiscuit.iprofession.data.models.skill;

import com.ptibiscuit.iprofession.Plugin;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

/**
 * 
 * @author ANNA
 */
public abstract class Skill implements Listener {
    public static HashMap<String, String> skillTypes = new HashMap<String, String>();

    public abstract void onEnable(Map<?, ?> config);

    private boolean isGod(Player p) {
        return (Plugin.getInstance().getPermissionHandler().has(p, "god", true));
    }

    public boolean hasToAct(Player p) {
        if (this.isGod(p) || !Plugin.getInstance().isWorldActivated(p.getWorld()))
            return false;
        return true;
    }

    // Just to tag Skill
    static {
        String baseUrl = "com.ptibiscuit.iprofession.data.models.skill.";
        // Pure Skill, like the first ones :3
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
