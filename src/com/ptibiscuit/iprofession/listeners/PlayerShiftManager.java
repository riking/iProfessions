package com.ptibiscuit.iprofession.listeners;

import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;

import com.ptibiscuit.iprofession.ProfessionsPlugin;
import com.ptibiscuit.iprofession.data.Skill;
import com.ptibiscuit.iprofession.data.TypeSkill;

public class PlayerShiftManager extends PlayerListener {

	@Override
	public void onPlayerInteract(PlayerInteractEvent e)
	{
		if (e.isCancelled())
			return;
		
		if (e.getItem() != null)
		{
			Skill s = ProfessionsPlugin.getData().getSkill(e.getItem().getTypeId(), TypeSkill.USE);
			if (s != null)
			{
				
				if (!ProfessionsPlugin.getData().hasSkill(e.getPlayer(), s))
				{
					ProfessionsPlugin.getInstance().sendMessage(e.getPlayer(), s.getNotHave());
					e.setCancelled(true);
				}
			}
			
		}
	}
	
}
