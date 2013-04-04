/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.ptibiscuit.iprofession.data.models.skill;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;


/**
 * 
 * @author ANNA
 */
public class SkillSimpleId extends Skill {

    private ArrayList<Entry<Integer, Integer>> ids = new ArrayList<Entry<Integer, Integer>>();

    @Override
    public void onEnable(Map<?, ?> config) {
        for (String idString : config.get("id").toString().split(",")) {
            String[] dataIdString = idString.split("-");
            if (dataIdString.length == 1) {
                this.ids.add(new SimpleEntry(new Integer(dataIdString[0]), -1));
            } else {
                this.ids.add(new SimpleEntry(new Integer(dataIdString[0]), new Integer(dataIdString[1])));
            }
        }
    }

    public ArrayList<Entry<Integer, Integer>> getIds() {
        return ids;
    }

    public boolean hasId(int id, int meta_data) {
        for (Entry<Integer, Integer> idFocus : this.ids) {
            if (idFocus.getKey() == id) {
                if (meta_data == idFocus.getValue() || idFocus.getValue() == -1)
                    return true;
            }
        }

        return false;
    }

    public boolean containsId(int id) {
        for (Entry<Integer, Integer> idFoc : this.ids) {
            if (idFoc.getKey() == id)
                return true;
        }
        return false;
    }
}
