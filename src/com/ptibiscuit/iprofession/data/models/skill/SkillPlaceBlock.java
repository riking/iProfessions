/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.ptibiscuit.iprofession.data.models.skill;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import com.ptibiscuit.iprofession.Plugin;

/**
 *
 * @author ANNA
 */
public class SkillPlaceBlock extends SkillSimpleId implements Listener {
    private String hasnot;

    @Override
    public String getKey() {
        return "placeBlock";
    }

    @Override
    public void onEnable(ConfigurationSection config) {
        super.onEnable(config);
        hasnot = config.get("hasnot").toString();
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockPlace(BlockPlaceEvent e) {
        if (!Plugin.getInstance().hasSkill(e.getPlayer(), this) && hasToAct(e.getPlayer())) {
            if (hasId(e.getBlock().getTypeId(), e.getBlock().getData())) {
                e.setCancelled(true);
                Plugin.getInstance().sendMessage(e.getPlayer(), hasnot);
            }
        }
    }
}
