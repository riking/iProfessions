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
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.inventory.ItemStack;

/**
 * 
 * @author ANNA
 */
public class SkillUseItem extends SkillSimpleId {

    private String hasNot;

    @Override
    public void onEnable(Map<?, ?> config) {
        super.onEnable(config);
        this.hasNot = config.get("hasnot").toString();
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onCraftItem(PlayerAnimationEvent e) {
        if (!Plugin.getInstance().hasSkill(e.getPlayer(), this) && this.hasToAct(e.getPlayer())) {
            ItemStack item = e.getPlayer().getItemInHand();
            if (this.hasId(item.getTypeId(), item.getData().getData())) {
                e.setCancelled(true);
                Plugin.getInstance().sendMessage(e.getPlayer(), this.hasNot);
            }
        }
    }
}
