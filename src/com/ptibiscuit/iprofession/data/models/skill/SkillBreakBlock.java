/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.ptibiscuit.iprofession.data.models.skill;

import com.ptibiscuit.iprofession.Plugin;
import java.util.ArrayList;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 * 
 * @author ANNA
 */
public class SkillBreakBlock extends SkillSimpleId {
    private String hasnot;
    private ArrayList<Location> ignoredBlocks = new ArrayList<Location>();

    @Override
    public void onEnable(Map<?, ?> config) {
        super.onEnable(config);
        this.hasnot = config.get("hasnot").toString();
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockDestroy(BlockBreakEvent e) {
        Plugin plugin = Plugin.getInstance();
        if (!Plugin.getInstance().hasSkill(e.getPlayer(), this) && this.hasToAct(e.getPlayer())) {
            if (this.hasId(e.getBlock().getTypeId(), e.getBlock().getData()) && (!this.ignoredBlocks.contains(e.getBlock().getLocation()) || !plugin.getConfig().getBoolean("config.allow_break_placed_blocks", false))) {
                e.setCancelled(true);
                plugin.sendMessage(e.getPlayer(), this.hasnot);
            }
        }
        if (plugin.getConfig().getBoolean("config.allow_break_placed_blocks", false))
            this.ignoredBlocks.remove(e.getBlock().getLocation());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockPlace(BlockPlaceEvent e) {
        Plugin plugin = Plugin.getInstance();
        if (plugin.getConfig().getBoolean("config.allow_break_placed_blocks", false)) {
            if (!Plugin.getInstance().hasSkill(e.getPlayer(), this) && this.hasToAct(e.getPlayer())) {
                if (this.hasId(e.getBlock().getTypeId(), e.getBlock().getData())) {
                    this.ignoredBlocks.add(e.getBlock().getLocation());
                }
            }
        }
    }

}
