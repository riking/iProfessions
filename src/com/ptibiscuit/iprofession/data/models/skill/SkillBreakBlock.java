/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.ptibiscuit.iprofession.data.models.skill;

import java.util.ArrayList;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import com.ptibiscuit.iprofession.Plugin;

/**
 *
 * @author ANNA
 */
public class SkillBreakBlock extends SkillSimpleId {
    private String hasnot;
    private ArrayList<Location> ignoredBlocks = new ArrayList<Location>();

    @Override
    public void onEnable(ConfigurationSection config) {
        super.onEnable(config);
        hasnot = config.get("hasnot").toString();
    }

    @Override
    public String getKey() {
        return "breakBlock";
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockDestroy(BlockBreakEvent e) {
        Plugin plugin = Plugin.getInstance();
        if (!Plugin.getInstance().hasSkill(e.getPlayer(), this) && hasToAct(e.getPlayer())) {
            if (hasId(e.getBlock().getTypeId(), e.getBlock().getData()) && (!ignoredBlocks.contains(e.getBlock().getLocation()) || !plugin.getConfig().getBoolean("config.allow_break_placed_blocks", false))) {
                e.setCancelled(true);
                plugin.sendMessage(e.getPlayer(), hasnot);
            }
        }
        if (plugin.getConfig().getBoolean("config.allow_break_placed_blocks", false)) {
            ignoredBlocks.remove(e.getBlock().getLocation());
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockPlace(BlockPlaceEvent e) {
        Plugin plugin = Plugin.getInstance();
        if (plugin.getConfig().getBoolean("config.allow_break_placed_blocks", false)) {
            if (!Plugin.getInstance().hasSkill(e.getPlayer(), this) && hasToAct(e.getPlayer())) {
                if (hasId(e.getBlock().getTypeId(), e.getBlock().getData())) {
                    ignoredBlocks.add(e.getBlock().getLocation());
                }
            }
        }
    }
}
