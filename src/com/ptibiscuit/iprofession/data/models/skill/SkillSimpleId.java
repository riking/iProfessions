package com.ptibiscuit.iprofession.data.models.skill;

import java.util.HashMap;
import java.util.Map;

/**
 * A base class that holds a collection of block / item IDs with damage
 * values.
 */
public class SkillSimpleId extends Skill {
    private Map<Integer, Integer> ids = new HashMap<Integer, Integer>();

    @Override
    public void onEnable(Map<?, ?> config) {
        /// XXX Should be YAML parse
        for (String idString : config.get("id").toString().split(",")) {
            String[] dataIdString = idString.split("-");
            if (dataIdString.length == 1) {
                // New wildcard is Short.MAX_VALUE, not -1
                this.ids.put(Integer.valueOf(dataIdString[0]), Integer.valueOf(Short.MAX_VALUE));
            } else {
                this.ids.put(Integer.valueOf(dataIdString[0]), Integer.valueOf(dataIdString[1]));
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
