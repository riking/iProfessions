package com.ptibiscuit.iprofession.listeners;

import com.ptibiscuit.iprofession.Plugin;
import com.ptibiscuit.iprofession.data.models.Skill;
import com.ptibiscuit.iprofession.data.models.TypeSkill;
import java.util.ArrayList;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.ItemStack;

public class SkillManager implements Listener {

	private ArrayList<Location> ignoredBlocks = new ArrayList<Location>();
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockBreak(BlockBreakEvent e) {
		if (Plugin.getInstance().getPermissionHandler().has(e.getPlayer(), "god", true))
			return;
		Skill s = Plugin.getInstance().getSkill(e.getBlock().getTypeId(), e.getBlock().getData(), TypeSkill.BREAK);
		System.out.println(e.getBlock().getData());
		if (s != null)
		{
			System.out.println(s.getMetaData());
			if (!Plugin.getInstance().hasSkill(e.getPlayer(), s))
			{
				if (!this.ignoredBlocks.contains(e.getBlock().getLocation()))
				{
					e.setCancelled(true);
					Plugin.getInstance().sendMessage(e.getPlayer(), s.getNotHave());
				}
			}
		}
		this.ignoredBlocks.remove(e.getBlock().getLocation());
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockPlace(BlockPlaceEvent e) {
		if (Plugin.getInstance().getPermissionHandler().has(e.getPlayer(), "god", true))
			return;
		
		Skill s = Plugin.getInstance().getSkill(e.getBlock().getTypeId(), e.getBlock().getData(), TypeSkill.BREAK);
		if (s != null)
		{
			if (!Plugin.getInstance().hasSkill(e.getPlayer(), s))
			{
				// Si il n'a pas ce skill, ça veut dire qu'il est potentiellement possible qu'il veuille
				// retirer ce bloc par après.
				this.ignoredBlocks.add(e.getBlock().getLocation());
			}
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onCraftItem(PrepareItemCraftEvent e) {
		if (e.getView().getPlayer() != null && e.getView().getPlayer() instanceof Player)
		{
			Player p = (Player) e.getView().getPlayer();
			if (Plugin.getInstance().getPermissionHandler().has(p, "god", true))
				return;
			ItemStack item = e.getRecipe().getResult();
			Skill s = Plugin.getInstance().getSkill(item.getTypeId(), item.getData().getData(), TypeSkill.CRAFT);
			if (s != null)
			{
				if (!Plugin.getInstance().hasSkill(p, s))
				{
					e.getInventory().setItem(0, new ItemStack(0, 0));
					Plugin.getInstance().sendMessage(p, s.getNotHave());
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerInteract(PlayerInteractEvent e)
	{
		if (Plugin.getInstance().getPermissionHandler().has(e.getPlayer(), "god", true))
			return;
		if (e.isCancelled())
			return;
		if (e.getItem() != null)
		{
			Skill s = Plugin.getInstance().getSkill(e.getItem().getTypeId(), e.getItem().getData().getData(), TypeSkill.USE);
			if (s != null)
			{
				
				if (!Plugin.getInstance().hasSkill(e.getPlayer(), s))
				{
					Plugin.getInstance().sendMessage(e.getPlayer(), s.getNotHave());
					e.setCancelled(true);
				}
			}
			
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onInventoryClick(InventoryClickEvent e)
	{
		if (e.getWhoClicked() != null && e.getWhoClicked() instanceof Player)
		{
			Player p = (Player) e.getWhoClicked();
			if (Plugin.getInstance().getPermissionHandler().has(p, "god", true))
				return;
			if (e.getInventory() instanceof FurnaceInventory && e.getSlotType() == SlotType.CONTAINER && e.getSlot() == 0)
			{
				// On a affaire à quelqu'un qui veut mettre un objet dans le four pour le fondre. On s'en occupe, cap'tain
				Skill s = Plugin.getInstance().getSkill(e.getCursor().getTypeId(), e.getCursor().getData().getData(), TypeSkill.SMELT);
				if (s != null)
				{
					if (!Plugin.getInstance().hasSkill(p, s))
					{
						e.setCancelled(true);
						Plugin.getInstance().sendMessage(p, s.getNotHave());
					}
				}
			}
		}
	}
}