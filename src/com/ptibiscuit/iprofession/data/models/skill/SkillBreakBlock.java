/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
		if (!Plugin.getInstance().hasSkill(e.getPlayer(), this) && !this.isGod(e.getPlayer()))
		{
			if (this.hasId(e.getBlock().getTypeId(), e.getBlock().getData()) && !this.ignoredBlocks.contains(e.getBlock().getLocation()))
			{
				e.setCancelled(true);
				Plugin.getInstance().sendMessage(e.getPlayer(), this.hasnot);
			}
		}
		this.ignoredBlocks.remove(e.getBlock().getLocation());
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockPlace(BlockPlaceEvent e) {
		if (!Plugin.getInstance().hasSkill(e.getPlayer(), this) && !this.isGod(e.getPlayer()))
		{
			if (this.hasId(e.getBlock().getTypeId(), e.getBlock().getData()))
			{
				this.ignoredBlocks.add(e.getBlock().getLocation());
			}
		}
	}
	
}
