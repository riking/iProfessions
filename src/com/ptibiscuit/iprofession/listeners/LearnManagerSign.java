package com.ptibiscuit.iprofession.listeners;

import com.ptibiscuit.iprofession.Plugin;
import com.ptibiscuit.iprofession.data.models.Profession;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class LearnManagerSign implements Listener {
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerInteract(PlayerInteractEvent e)
	{
		Block b = e.getClickedBlock();
		if (b != null && (b.getType() == Material.SIGN_POST || b.getType() == Material.WALL_SIGN))
		{
			Sign sign = (Sign) b.getState();
			if (sign.getLine(0).contains("[iProfessions]"))
			{
				String[] data = sign.getLine(1).split(" ");
				if (data[0].equalsIgnoreCase("learn"))
				{
					Profession p = Plugin.getData().getProfession(data[1]);
					if (p != null)
					{
						if (!Plugin.getInstance().getPermissionHandler().has(e.getPlayer(), "sign.learn." + p.getTag(), true))
						{
							Plugin.getInstance().sendPreMessage(e.getPlayer(), "have_perm");
							e.setCancelled(true);
							return;
						}
						Plugin.getInstance().tryToLearn(e.getPlayer(), p);
					}
					else
					{
						Plugin.getInstance().sendPreMessage(e.getPlayer(), "unknown_tag");
					}
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onSignChange(SignChangeEvent e)
	{
		if (e.getLine(0).contains("[iProfessions]") && !Plugin.getInstance().getPermissionHandler().has(e.getPlayer(), "sign.create", true))
		{
			Plugin.getInstance().sendPreMessage(e.getPlayer(), "have_perm");
			e.setCancelled(true);
		}
	}
}
