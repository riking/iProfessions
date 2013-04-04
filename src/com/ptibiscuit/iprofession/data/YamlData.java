package com.ptibiscuit.iprofession.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;

import com.ptibiscuit.iprofession.Plugin;
import com.ptibiscuit.iprofession.data.models.Profession;
import com.ptibiscuit.iprofession.data.models.Require;
import com.ptibiscuit.iprofession.data.models.skill.Skill;

public class YamlData implements IData {
    private ArrayList<Profession> professions = new ArrayList<Profession>();
    private List<String> activatedWorlds = new ArrayList<String>();
    private HashMap<String, ArrayList<Profession>> playersProfessions = new HashMap<String, ArrayList<Profession>>();
    private Plugin plugin;

    public YamlData(Plugin p) {
        plugin = p;
    }

    public void loadProfessions() {
        FileConfiguration c = Plugin.getInstance().getConfig();
        if (c.getConfigurationSection("professions") == null) {
            return;
        }
        for (Entry<String, Object> listProfs : c.getConfigurationSection("professions").getValues(false).entrySet()) {
            /* Voici donc l'architecture d'une classe
             * players:
             *   [player]:
             *     profession:
             *     - miner
             *     - girl_thing
             * professions:
             * [tag]:
             *   name: [name]
             *   linked_group: [group]
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
             *   permissions:
             *     - [perm]
             *     - [perm]
             */

            String tag = listProfs.getKey();
            MemorySection data = (MemorySection) listProfs.getValue();
            String name = data.getString("name");
            ArrayList<Skill> skills = new ArrayList<Skill>();
            ArrayList<Require> prerequis = new ArrayList<Require>();
            Map<String, Boolean> permissions = new LinkedHashMap<String, Boolean>();
            for (Object obj : data.getList("permissions")) {
                try {
                    String perm = (String) obj;
                    boolean grant = true;
                    if (perm.startsWith("-")) {
                        grant = false;
                        perm = perm.substring(1);
                    }
                    permissions.put(perm, grant);
                } catch (ClassCastException e) {
                    // pass
                }
            }

            // On va chercher les pré-requis
            List<Map<?, ?>> dataRequired = data.getMapList("required");
            if (dataRequired != null) {
                for (Map<?, ?> lhm : dataRequired) {
                    Require r = new Require(lhm.get("category").toString(), lhm.get("key").toString(), Integer.parseInt(lhm.get("require").toString()));
                    r.setHasnot(lhm.get("hasnot").toString());
                    prerequis.add(r);
                }
            }
            // Pour le groupe
            String group = data.getString("linked_group");
            // Pour le prix
            double price = 0;
            if (data.get("price") != null) {
                price = data.getDouble("price");
            }
            // On va cherche la liste des skills
            ConfigurationSection dataSkills = data.getConfigurationSection("skills");

            // On va faire une boucle pour chaque type de skill qui existe
            for (String sk : dataSkills.getKeys(false)) {
                if (Skill.skills.containsKey(sk)) {
                    Skill skill;
                    try {
                        skill = Skill.skills.get(sk).newInstance();
                    } catch (InstantiationException e) {
                        Logger.getLogger(YamlData.class.getName()).log(Level.SEVERE, "Failed to load skill: " + sk, e);
                        continue;
                    } catch (IllegalAccessException e) {
                        Logger.getLogger(YamlData.class.getName()).log(Level.SEVERE, "Failed to load skill: " + sk, e);
                        continue;
                    }
                    ConfigurationSection skillsec = dataSkills.getConfigurationSection(skill.getKey());
                    skill.onEnable(skillsec);
                    skills.add(skill);
                } else {
                    Logger.getLogger(YamlData.class.getName()).log(Level.SEVERE, "Failed to find skill class: " + sk);
                }
            }

            // On va prendre la profession parent, ou null
            Profession parent = null;
            String tagParent = data.getString("parent");
            if (tagParent != null) {
                Profession possibleParent = getProfession(tagParent);
                if (possibleParent != null) {
                    parent = possibleParent;
                } else {
                    Plugin.getInstance().getMyLogger().warning("The parent of " + tag + ", " + tagParent + " is unkwown; Are you sure of you ?");
                }
            }
            professions.add(new Profession(plugin, tag, name, skills, permissions, prerequis, parent, price, group));
        }
    }

    public void loadPlayersProfessions() {
        Plugin p = Plugin.getInstance();
        ConfigurationSection cs = p.getConfig().getConfigurationSection("players");
        if (cs == null) {
            return;
        }
        for (Entry<String, Object> e : cs.getValues(false).entrySet()) {
            ConfigurationSection csPlayer = (ConfigurationSection) e.getValue();
            String playerName = e.getKey();
            List<String> listProfessionsTag = csPlayer.getStringList("professions");
            ArrayList<Profession> listProfessions = new ArrayList<Profession>();
            for (String professionsTag : listProfessionsTag) {

                Profession prof = getProfession(professionsTag);
                if (prof != null) {
                    listProfessions.add(prof);
                } else {
                    Plugin.getInstance().getMyLogger().warning(e.getKey() + " has a non existing profession. (" + professionsTag + ")");
                }
            }
            playersProfessions.put(playerName, listProfessions);

        }
    }


    public ArrayList<Profession> getProfessionByPlayer(String s) {
        ArrayList<Profession> pps = playersProfessions.get(s);
        return (pps != null) ? pps : new ArrayList<Profession>();
    }

    public void save() {
        for (Entry<String, ArrayList<Profession>> e : playersProfessions.entrySet()) {
            ArrayList<String> professionsTagList = new ArrayList<String>();
            for (Profession p : e.getValue()) {
                professionsTagList.add(p.getTag());
            }
            Plugin.getInstance().getConfig().set("players." + e.getKey() + ".professions", professionsTagList);
        }
        Plugin.getInstance().saveConfig();
    }

    public Profession getProfession(String tag) {
        for (Profession p : professions) {
            if (p.getTag().equalsIgnoreCase(tag)) {
                return p;
            }
        }
        return null;
    }

    public ArrayList<Profession> getProfessions() {
        return professions;
    }

    public HashMap<String, ArrayList<Profession>> getProfessionPlayers() {
        return playersProfessions;
    }

    public void setPlayerProfession(String player, ArrayList<Profession> profession) {
        if (profession != null) {
            playersProfessions.put(player, profession);
        } else {
            playersProfessions.remove(player);
        }
        save();
    }

    public List<String> getActivatedWorlds() {
        return activatedWorlds;
    }

    public void loadActivatedWorlds() {
        if (Plugin.getInstance().getConfig().getStringList("config.activated_worlds") != null) {
            activatedWorlds = Plugin.getInstance().getConfig().getStringList("config.activated_worlds");
        }
    }
}
