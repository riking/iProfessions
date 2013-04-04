package com.ptibiscuit.iprofession.data.models.skill;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.configuration.ConfigurationSection;

/**
 * A base class that holds a collection of block / item IDs with damage
 * values.
 */
public abstract class SkillSimpleId extends Skill {
    private Map<Integer, Integer> ids = new HashMap<Integer, Integer>();

    @Override
    public void onEnable(ConfigurationSection config) {
        for (String idString : config.getStringList("")) {
            try {
                if (idString.contains("-")) {
                    String[] data = idString.split("-");
                    ids.put(Integer.valueOf(data[0]), Integer.valueOf(data[1]));
                } else {
                    ids.put(Integer.valueOf(idString), Integer.valueOf(Short.MAX_VALUE));
                }
            } catch (NumberFormatException e) {
                Logger.getLogger("iProfessions").log(Level.SEVERE, "Malformed block/item id: '" + idString + "' (expected ## or ##-##)", e);
            }
        }
    }

    public Map<Integer, Integer> getIds() {
        return ids;
    }

    public boolean hasId(int id, int meta_data) {
        if (ids.containsKey(id)) {
            Integer val = ids.get(Integer.valueOf(id));
            return (val == meta_data) || (val == Short.MAX_VALUE);
        }
        return false;
    }

    public boolean containsId(int id) {
        return ids.containsKey(Integer.valueOf(id));
    }
}
