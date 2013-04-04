/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.ptibiscuit.iprofession.data.models.skill;

import com.ptibiscuit.iprofession.Plugin;
import java.util.ArrayList;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 * 
 * @author ANNA
 */
public class SkillGainMoneyOnBreakBlock extends SkillSimpleId implements Listener {
    private ArrayList<Location> ignoredBlocks = new ArrayList<Location>();
    private double reward;

    @Override
    public void onEnable(Map<?, ?> config) {
        super.onEnable(config);
        this.reward = Double.parseDouble(config.get("reward").toString());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockBreak(BlockBreakEvent e) {
        Block b = e.getBlock();
        if (!this.hasToAct(e.getPlayer()))
            return;
        if (Plugin.getInstance().hasSkill(e.getPlayer(), this)) {
            if (this.hasId(b.getTypeId(), b.getData()) && !this.ignoredBlocks.contains(b.getLocation())) {
                Plugin.getInstance().getEconomy().depositPlayer(e.getPlayer().getName(), this.reward);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockPlace(BlockPlaceEvent e) {
        if (!this.hasToAct(e.getPlayer()))
            return;
        if (Plugin.getInstance().hasSkill(e.getPlayer(), this)) {
            if (this.hasId(e.getBlock().getTypeId(), e.getBlock().getData())) {
                this.ignoredBlocks.add(e.getBlock().getLocation());
            }
        }
    }
}
