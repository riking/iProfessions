package com.ptibiscuit.iprofession.ressources;

import org.bukkit.Server;
import org.bukkit.entity.Player;

import com.nidefawl.Stats.Stats;

public class StatsHandler {
	private Stats stats;
	
	public boolean setupStats(Server sv)
	{
		stats = (Stats) sv.getPluginManager().getPlugin("Stats");
		if (stats == null)
		{
			return false;
		}
		return true;
	}
	
	public int getStat(Player p, String cat, String key)
	{
		return stats.get(p.getName(), cat, key);
	}
}
