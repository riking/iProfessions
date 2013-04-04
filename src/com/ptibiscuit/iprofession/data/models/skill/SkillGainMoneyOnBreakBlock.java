/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.ptibiscuit.iprofession.data.models.skill;

import java.util.ArrayList;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import com.ptibiscuit.iprofession.Plugin;

/**
 *
 * @author ANNA
 */
public class SkillGainMoneyOnBreakBlock extends SkillSimpleId implements Listener {
    private ArrayList<Location> ignoredBlocks = new ArrayList<Location>();
    private double reward;

    @Override
    public String getKey() {
        return "gainMoneyOnBreakBlock";
    }

    @Override
    public void onEnable(ConfigurationSection config) {
        super.onEnable(config);
        reward = Double.parseDouble(config.get("reward").toString());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockBreak(BlockBreakEvent e) {
        Block b = e.getBlock();
        if (!hasToAct(e.getPlayer())) {
            return;
        }
        if (Plugin.getInstance().hasSkill(e.getPlayer(), this)) {
            if (hasId(b.getTypeId(), b.getData()) && !ignoredBlocks.contains(b.getLocation())) {
                Plugin.getInstance().getEconomy().depositPlayer(e.getPlayer().getName(), reward);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockPlace(BlockPlaceEvent e) {
        if (!hasToAct(e.getPlayer())) {
            return;
        }
        if (Plugin.getInstance().hasSkill(e.getPlayer(), this)) {
            if (hasId(e.getBlock().getTypeId(), e.getBlock().getData())) {
                ignoredBlocks.add(e.getBlock().getLocation());
            }
        }
    }
}
