/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.ptibiscuit.iprofession.data.models.skill;

import com.ptibiscuit.iprofession.Plugin;
import java.util.ArrayList;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 * 
 * @author ANNA
 */
public class SkillPlaceBlock extends SkillSimpleId implements Listener {
    private String hasnot;

    @Override
    public void onEnable(Map<?, ?> config) {
        super.onEnable(config);
        this.hasnot = config.get("hasnot").toString();
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockPlace(BlockPlaceEvent e) {
        if (!Plugin.getInstance().hasSkill(e.getPlayer(), this) && this.hasToAct(e.getPlayer())) {
            if (this.hasId(e.getBlock().getTypeId(), e.getBlock().getData())) {
                e.setCancelled(true);
                Plugin.getInstance().sendMessage(e.getPlayer(), this.hasnot);
            }
        }
    }
}
