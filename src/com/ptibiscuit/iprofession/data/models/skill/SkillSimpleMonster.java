/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.ptibiscuit.iprofession.data.models.skill;

import java.util.ArrayList;
import java.util.Map;
import org.bukkit.entity.EntityType;

/**
 * 
 * @author ANNA
 */
public class SkillSimpleMonster extends Skill {

    private ArrayList<EntityType> types = new ArrayList<EntityType>();

    @Override
    public void onEnable(Map<?, ?> config) {
        String[] typesName = config.get("type").toString().split(",");
        for (String typeName : typesName) {
            types.add(EntityType.fromName(typeName));
        }
    }

    public ArrayList<EntityType> getTypes() {
        return this.types;
    }

    public boolean containsType(EntityType type) {
        return types.contains(type);
    }
}
