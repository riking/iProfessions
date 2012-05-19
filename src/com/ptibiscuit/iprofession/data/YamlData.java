package com.ptibiscuit.iprofession.data;

import com.ptibiscuit.iprofession.Plugin;
import com.ptibiscuit.iprofession.data.models.Profession;
import com.ptibiscuit.iprofession.data.models.Require;
import com.ptibiscuit.iprofession.data.models.Skill;
import com.ptibiscuit.iprofession.data.models.TypeSkill;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;

public class YamlData implements IData {
	private ArrayList<Profession> professions = new ArrayList<Profession>();
	private HashMap<String, ArrayList<Profession>> playersProfessions = new HashMap<String, ArrayList<Profession>>();
	
	@Override
	public void loadProfessions()
	{
		FileConfiguration c = Plugin.getInstance().getConfig();
		if (c.getConfigurationSection("professions") == null)
			return;
		for (Entry<String, Object> listProfs : c.getConfigurationSection("professions").getValues(false).entrySet())
		{
			/* Voici donc l'architecture d'une classe
			 * players:
			 *   [player]:
			 *     profession:
			 *     - miner
			 *     - girl_thing
			 * professions:
				* [tag]:
				*   name: [name]
				*   required:
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
			MemorySection data = (MemorySection) listProfs.getValue();
			String name = data.getString("name");
			ArrayList<Skill> skills = new ArrayList<Skill>();
			ArrayList<Require> prerequis = new ArrayList<Require>();
			
			// On va chercher les pré-requis
			List<Map<?, ?>> dataRequired = data.getMapList("required");
			if (dataRequired != null)
			{
				for (Map<?, ?> lhm : dataRequired)
				{
					Require r = new Require(lhm.get("category").toString(), lhm.get("key").toString(), Integer.parseInt(lhm.get("require").toString()));
					r.setHasnot(lhm.get("hasnot").toString());
					prerequis.add(r);
				}
			}
			// Pour le prix
			int price = 0;
			if (data.get("price") != null)
				price = data.getInt("price");
			// On va cherche la liste des skills
			ConfigurationSection dataSkills = data.getConfigurationSection("skills");
			
			String[] types = new String[]{"useItem", "breakBlock", "craftItem", "smeltItem"};
			TypeSkill[] typesSkill = new TypeSkill[]{TypeSkill.USE, TypeSkill.BREAK, TypeSkill.CRAFT, TypeSkill.SMELT};
			
			for (int i = 0;i < types.length;i++)
			{
				List<Map<?, ?>> dataTypeSkill = dataSkills.getMapList(types[i]);
				if (dataTypeSkill != null)
				{
					
					for (Map<?, ?> dataSkill : dataTypeSkill)
					{
						String exply = dataSkill.get("hasnot").toString();
						TypeSkill type = typesSkill[i];
						String[] ids = dataSkill.get("id").toString().split(","); 
						for (String Sid : ids)
						{
							String[] dataIdSkill = Sid.split("-");
							int id = new Integer(dataIdSkill[0]);
							int metaData = -1;
							if (dataIdSkill.length > 1)
								metaData = new Integer(dataIdSkill[1]);
							Skill sk = new Skill(id, metaData, type);
							sk.setNotHave(exply);
							skills.add(sk);
						}
					}
				}
			}
			// On va prendre la profession parent, ou null
			Profession parent = null;
			String tagParent = data.getString("parent");
			if (tagParent != null)
			{
				Profession possibleParent = this.getProfession(tagParent);
				if (possibleParent != null)
					parent = possibleParent;
				else
					Plugin.getInstance().getMyLogger().warning("The parent of " + tag + ", " + tagParent + " is unkwown; Are you sure of you ?");
			}
			professions.add(new Profession(tag, name, skills, prerequis, parent, price));
		}
	}
	
	@Override
	public void loadPlayersProfessions()
	{
		Plugin p = Plugin.getInstance();
		ConfigurationSection cs = p.getConfig().getConfigurationSection("players");
		if (cs == null)
			return;
		for (Entry<String, Object> e : cs.getValues(false).entrySet())
		{
			ConfigurationSection csPlayer = (ConfigurationSection) e.getValue();
			String playerName = e.getKey();
			List<String> listProfessionsTag = csPlayer.getStringList("professions");
			ArrayList listProfessions = new ArrayList<Profession>();
			for (String professionsTag : listProfessionsTag) {
				
				Profession prof = this.getProfession(professionsTag);
				if (prof != null) {
					listProfessions.add(prof);
				} else {
					Plugin.getInstance().getMyLogger().warning(e.getKey() + " has a non existing profession. (" + professionsTag + ")");
				}
			}
			this.playersProfessions.put(playerName, listProfessions);
			
		}
	}
	
	@Override
	public ArrayList<Profession> getProfessionByPlayer(String s)
	{
		ArrayList<Profession> pps = playersProfessions.get(s);
		return (pps != null) ? pps : new ArrayList<Profession>();
	}
	
	@Override
	public void save()
	{
		for (Entry<String, ArrayList<Profession>> e : this.playersProfessions.entrySet()) {
			ArrayList<String> professionsTagList = new ArrayList<String>();
			for (Profession p : e.getValue()) {
				professionsTagList.add(p.getTag());
			}
			Plugin.getInstance().getConfig().set("players." + e.getKey() + "professions", professionsTagList);
		}
		Plugin.getInstance().saveConfig();
	}
	
	@Override
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

	@Override
	public ArrayList<Profession> getProfessions() {
		return this.professions;
	}

	@Override
	public HashMap<String, ArrayList<Profession>> getProfessionPlayers() {
		return this.playersProfessions;
	}

	@Override
	public void setPlayerProfession(String player, ArrayList<Profession> profession) {
		if (profession != null)
			playersProfessions.put(player, profession);
		else
			playersProfessions.remove(player);
		this.save();
	}
}