/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.ptibiscuit.iprofession.data.models.skill;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

import com.ptibiscuit.iprofession.Plugin;

public class SkillCraftItem extends SkillSimpleId implements Listener {
    private String hasNot;

    @Override
    public String getKey() {
        return "craftItem";
    }

    @Override
    public void onEnable(ConfigurationSection config) {
        super.onEnable(config);
        hasNot = config.get("hasnot").toString();
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onCraftItem(CraftItemEvent e) {
        if (e.getView().getPlayer() != null && e.getView().getPlayer() instanceof Player) {
            Player p = (Player) e.getView().getPlayer();
            if (!hasToAct(p)) {
                return;
            }
            ItemStack item = e.getRecipe().getResult();
            if (hasId(item.getTypeId(), item.getData().getData())) {
                if (!Plugin.getInstance().hasSkill(p, this)) {
                    e.setCancelled(true);
                    Plugin.getInstance().sendMessage(p, hasNot);
                }
            }
        }
    }
}
