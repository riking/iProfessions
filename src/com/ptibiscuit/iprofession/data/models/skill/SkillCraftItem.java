/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ptibiscuit.iprofession.data.models.skill;

import com.ptibiscuit.iprofession.Plugin;
import java.util.Map;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.FurnaceInventory;

public class SkillCraftItem extends SkillSimpleId implements Listener {
	private String hasNot;
	@Override
	public void onEnable(Map<?, ?> config) {
		super.onEnable(config);
		this.hasNot = config.get("hasnot").toString();
	}
	
	
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onWearItem(InventoryClickEvent e) {
		System.out.println(e.getSlotType() + "|" + e.getSlot());
		/*
		if (e.getWhoClicked() != null && e.getWhoClicked() instanceof Player)
		{
			Player p = (Player) e.getWhoClicked();
			if (this.isGod(p))
				return;
			if (e.getInventory() instanceof FurnaceInventory && e.getSlotType() == InventoryType.SlotType.CONTAINER && e.getSlot() == 0)
			{
				// On a affaire à quelqu'un qui veut mettre un objet dans le four pour le fondre. On s'en occupe, cap'tain
				if (this.hasId(e.getCursor().getTypeId(), e.getCursor().getData().getData())) {
					if (!Plugin.getInstance().hasSkill(p, this)) {
						e.setCancelled(true);
						Plugin.getInstance().sendMessage(p, this.hasNot);
					}
				}
			}
		}
		*/
	}
}
