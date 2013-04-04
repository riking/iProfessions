/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.ptibiscuit.iprofession.data.models.skill;

import com.ptibiscuit.iprofession.Plugin;
import java.util.Map;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

public class SkillCraftItem extends SkillSimpleId implements Listener {
    private String hasNot;

    @Override
    public void onEnable(Map<?, ?> config) {
        super.onEnable(config);
        this.hasNot = config.get("hasnot").toString();
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onCraftItem(CraftItemEvent e) {
        if (e.getView().getPlayer() != null && e.getView().getPlayer() instanceof Player) {
            Player p = (Player) e.getView().getPlayer();
            if (!this.hasToAct(p))
                return;
            ItemStack item = e.getRecipe().getResult();
            if (this.hasId(item.getTypeId(), item.getData().getData())) {
                if (!Plugin.getInstance().hasSkill(p, this)) {
                    e.setCancelled(true);
                    Plugin.getInstance().sendMessage(p, this.hasNot);
                }
            }
        }
    }
}
