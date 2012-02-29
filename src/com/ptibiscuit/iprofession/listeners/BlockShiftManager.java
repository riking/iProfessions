package com.ptibiscuit.iprofession.listeners;

import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;

import com.ptibiscuit.iprofession.ProfessionsPlugin;
import com.ptibiscuit.iprofession.data.Skill;
import com.ptibiscuit.iprofession.data.TypeSkill;

public class BlockShiftManager extends BlockListener {

	@Override
	public void onBlockBreak(BlockBreakEvent e) {
		Skill s = ProfessionsPlugin.getData().getSkill(e.getBlock().getTypeId(), TypeSkill.BREAK);
		if (s != null)
		{
			if (!ProfessionsPlugin.getData().hasSkill(e.getPlayer(), s))
			{
				e.setCancelled(true);
				ProfessionsPlugin.getInstance().sendMessage(e.getPlayer(), s.getNotHave());
			}
		}
	}

}
