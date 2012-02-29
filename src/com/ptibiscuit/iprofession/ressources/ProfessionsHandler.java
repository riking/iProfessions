package com.ptibiscuit.iprofession.ressources;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.bukkit.entity.Player;

import com.ptibiscuit.iprofession.ProfessionsPlugin;
import com.ptibiscuit.iprofession.data.Profession;
import com.ptibiscuit.iprofession.data.Require;
import com.ptibiscuit.iprofession.data.Skill;
import com.ptibiscuit.iprofession.data.TypeSkill;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;

public class ProfessionsHandler {
	private ArrayList<Profession> professions = new ArrayList<Profession>();
	private LinkedHashMap<String, Profession> playersProfessions = new LinkedHashMap<String, Profession>();
	
	private File folder;
	private String path = "professions.yml";
	private String pathPlayersProfessions = "players.yml";
	
	
	public ProfessionsHandler(File datafolder)
	{
		this.folder = datafolder;
	}
	
	public Skill getSkill(int id, TypeSkill ts)
	{
		Skill fakeSk = new Skill(id, ts);
		for (Profession p : professions)
		{
			
			for (Skill s : p.getSkills())
			{
				if (s.equals(fakeSk))
				{
					return s;
				}
			}
		}
		return null;
	}
	
	public boolean loadProfessions()
	{
		Configuration c = ProfessionsPlugin.getInstance().getConfiguration();
		for (Entry<String, ConfigurationNode> listProfs : c.getNodes("professions").entrySet())
		{
			/* Voici donc l'architecture d'une classe
			 * players:
			 *   [player]: [profession]
			 * professions:
				 * [tag]:
				 *   name: [name]
				 *   required:
				 *     - category: [Category]
				 *       key: [Key]
				 *       require: [Number]
				 *       hasnot: [Has Not Message]
				 *     - category: [Category]
				 *       key: [Key]
				 *       require: [Number]
				 *       hasnot: [Has Not Message]
				 *   skills:
				 *     craftItem:
				 *       - id: [Id]
				 *         hasnot: [Has Not Message]
				 *     useItem:
				 *       - id: [Id]
				 *         hasnot: [Has Not Message]
				 *     breakBlock:
				 *       - id: [Id]
				 *         hasnot: [Has Not Message]
			 */
			
			String tag = listProfs.getKey();
			ConfigurationNode data = listProfs.getValue();
			String name = data.getString("name");
			ArrayList<Skill> skills = new ArrayList<Skill>();
			ArrayList<Require> prerequis = new ArrayList<Require>();
			
			// On va chercher les pré-requis
			List<ConfigurationNode> dataRequired = data.getNodeList("required", null);
			if (dataRequired != null)
			{
				for (ConfigurationNode lhm : dataRequired)
				{
					Require r = new Require(lhm.getString("category"), lhm.getString("key"), lhm.getInt("require", 0));
					r.setHasnot(data.getString("hasnot"));
					prerequis.add(r);
				}
			}
			
			// On va cherche la liste des skills
			ConfigurationNode dataSkills = data.getNode("skills");
			
			
			List<ConfigurationNode> dataUseSkills = dataSkills.getNodeList("useItem", null);
			if (dataUseSkills != null)
			{
				for (ConfigurationNode dataUseSkill : dataUseSkills)
				{
					
					String exply = dataUseSkill.getString("hasnot");
					int id = dataUseSkill.getInt("id", 0);
					TypeSkill type = TypeSkill.USE;
					
					Skill sk = new Skill(id, type);
					sk.setNotHave(exply);
					skills.add(sk);
				}
			}
			
			List<ConfigurationNode> dataBreakSkills = dataSkills.getNodeList("breakBlock", null);
			if (dataBreakSkills != null)
			{
				for (ConfigurationNode dataBreakSkill : dataBreakSkills)
				{
					String exply = dataBreakSkill.getString("hasnot");
					int id = dataBreakSkill.getInt("id", 0);
					TypeSkill type = TypeSkill.BREAK;
					
					Skill sk = new Skill(id, type);
					sk.setNotHave(exply);
					skills.add(sk);
				}
			}
			
			List<ConfigurationNode> dataCraftSkills = dataSkills.getNodeList("craftItem", null);
			if (dataCraftSkills != null)
			{
				for (ConfigurationNode dataCraftSkill : dataBreakSkills)
				{
					
					String exply = dataCraftSkill.getString("hasnot");
					int id = dataCraftSkill.getInt("id", 0);
					TypeSkill type = TypeSkill.CRAFT;
					
					Skill sk = new Skill(id, type);
					sk.setNotHave(exply);
					skills.add(sk);
				}
			}
			
			professions.add(new Profession(tag, name, skills, prerequis));
		}
		return true;
	}
	
	public boolean loadPlayersProfessions()
	{
		ProfessionsPlugin p = ProfessionsPlugin.getInstance();
		for (Entry<String, ConfigurationNode> e : p.getConfiguration().getNodes("players").entrySet())
		{
			Profession prof = this.getProfession(e.getValue().getString("profession"));
			if (prof != null)
			{
				this.playersProfessions.put(e.getKey(), prof);
			}
		}
		return true;
	}
	
	public boolean hasSkill(Player pl, Skill s)
	{
		Profession p = this.getProfessionByPlayer(pl.getName());
		if ((p != null && p.hasSkill(s)))
		{
			return true;
		}
		return false;
	}
	
	public boolean isALearnableSkill(Skill s)
	{
		for (Profession p : professions)
		{
			if (p.hasSkill(s))
			{
				return true;
			}
		}
		return false;
	}
	
	public Profession getProfessionByPlayer(String s)
	{
		return playersProfessions.get(s);
	}
	
	public void setPlayersProfession(String p, Profession s)
	{
		playersProfessions.put(p, s);
		this.savePlayersProfession();
	}
	
	public void savePlayersProfession()
	{
		HashMap<String, String> transition = new HashMap<String, String>();
		for (Entry<String, Profession> e : this.playersProfessions.entrySet())
			transition.put(e.getKey(), e.getValue().getTag());
		ProfessionsPlugin.getInstance().getConfiguration().setProperty("players", transition);
		ProfessionsPlugin.getInstance().getConfiguration().save();
	}
	
	public Profession getProfession(String tag)
	{
		for (Profession p : professions)
		{
			if (p.getTag().equalsIgnoreCase(tag))
			{
				return p;
			}
		}
		return null;
	}
}
