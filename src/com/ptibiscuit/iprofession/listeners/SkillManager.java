package com.ptibiscuit.iprofession.listeners;

import com.ptibiscuit.iprofession.Plugin;
import com.ptibiscuit.iprofession.data.models.Skill;
import com.ptibiscuit.iprofession.data.models.TypeSkill;
import java.util.Map.Entry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.ItemStack;

public class SkillManager implements Listener {

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockBreak(BlockBreakEvent e) {
		Skill s = Plugin.getInstance().getSkill(e.getBlock().getTypeId(), TypeSkill.BREAK);
		if (s != null)
		{
			if (!Plugin.getInstance().hasSkill(e.getPlayer(), s))
			{
				e.setCancelled(true);
				Plugin.getInstance().sendMessage(e.getPlayer(), s.getNotHave());
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onCraftItem(PrepareItemCraftEvent e) {
		
		if (e.getView().getPlayer() != null && e.getView().getPlayer() instanceof Player)
		{
			Player p = (Player) e.getView().getPlayer();
			Skill s = Plugin.getInstance().getSkill(e.getRecipe().getResult().getTypeId(), TypeSkill.CRAFT);
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
		if (e.isCancelled())
			return;
		
		if (e.getItem() != null)
		{
			Skill s = Plugin.getInstance().getSkill(e.getItem().getTypeId(), TypeSkill.USE);
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
			if (e.getInventory() instanceof FurnaceInventory && e.getSlotType() == SlotType.CONTAINER && e.getSlot() == 0)
			{
				// On a affaire à quelqu'un qui veut mettre un objet dans le four pour le fondre. On s'en occupe, cap'tain
				Skill s = Plugin.getInstance().getSkill(e.getCursor().getTypeId(), TypeSkill.SMELT);
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