package com.ptibiscuit.iprofession.listeners;

import com.ptibiscuit.iprofession.ProfessionsPlugin;
import com.ptibiscuit.iprofession.data.Skill;
import com.ptibiscuit.iprofession.data.TypeSkill;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.event.inventory.InventoryCraftEvent;
import org.getspout.spoutapi.event.inventory.InventoryListener;

public class InventoryShiftManager extends InventoryListener {

	@Override
	public void onInventoryCraft(InventoryCraftEvent e) {
		if (e.getResult() != null)
		{
			Skill s = ProfessionsPlugin.getData().getSkill(e.getResult().getTypeId(), TypeSkill.CRAFT);
			if (s != null)
			{
				if (!ProfessionsPlugin.getData().hasSkill(e.getPlayer(), s))
				{
					e.setCancelled(true);
					e.setResult(new ItemStack(0, 0));
					ProfessionsPlugin.getInstance().sendMessage(e.getPlayer(), s.getNotHave());
				}
			}
		}
	}

}
