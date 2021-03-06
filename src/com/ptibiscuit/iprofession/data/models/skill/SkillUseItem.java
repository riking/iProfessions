/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.ptibiscuit.iprofession.data.models.skill;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.inventory.ItemStack;

import com.ptibiscuit.iprofession.Plugin;

/**
 *
 * @author ANNA
 */
public class SkillUseItem extends SkillSimpleId {
    private String hasNot;

    @Override
    public String getKey() {
        return "useItem";
    }

    @Override
    public void onEnable(ConfigurationSection config) {
        super.onEnable(config);
        hasNot = config.get("hasnot").toString();
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onCraftItem(PlayerAnimationEvent e) {
        if (!Plugin.getInstance().hasSkill(e.getPlayer(), this) && hasToAct(e.getPlayer())) {
            ItemStack item = e.getPlayer().getItemInHand();
            if (hasId(item.getTypeId(), item.getData().getData())) {
                e.setCancelled(true);
                Plugin.getInstance().sendMessage(e.getPlayer(), hasNot);
            }
        }
    }
}
