/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.ptibiscuit.iprofession.data.models.skill;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.CraftingInventory;

import com.ptibiscuit.iprofession.Plugin;

/**
 *
 * @author ANNA
 */
public class SkillWearItem extends SkillSimpleId {
    private String hasNot;

    @Override
    public String getKey() {
        return "wearItem";
    }

    @Override
    public void onEnable(ConfigurationSection config) {
        super.onEnable(config);
        hasNot = config.get("hasnot").toString();
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onWearItem(InventoryClickEvent e) {
        if (e.getWhoClicked() != null && e.getWhoClicked() instanceof Player) {
            Player p = (Player) e.getWhoClicked();
            if (!hasToAct(p)) {
                return;
            }
            if (e.getInventory() instanceof CraftingInventory && e.getSlotType() == SlotType.ARMOR) {
                // On a affaire à quelqu'un qui veut mettre un objet dans le four pour le fondre. On s'en occupe, cap'tain
                if (hasId(e.getCursor().getTypeId(), e.getCursor().getData().getData())) {
                    if (!Plugin.getInstance().hasSkill(p, this)) {
                        e.setCancelled(true);
                        Plugin.getInstance().sendMessage(p, hasNot);
                    }
                }
            }
        }
    }
}
