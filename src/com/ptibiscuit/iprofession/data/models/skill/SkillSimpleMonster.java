/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.ptibiscuit.iprofession.data.models.skill;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;

/**
 *
 * @author ANNA
 */
public abstract class SkillSimpleMonster extends Skill {
    private ArrayList<EntityType> types = new ArrayList<EntityType>();

    @Override
    public void onEnable(ConfigurationSection config) {
        List<String> typesName = config.getStringList("type");
        for (String typeName : typesName) {
            EntityType type = EntityType.fromName(typeName);
            if (type == null) {
                Logger.getLogger("iProfessions").log(Level.SEVERE, "Bad entity name: '" + typeName + "' (expected an EntityType string)");
            }
        }
    }

    public ArrayList<EntityType> getTypes() {
        return types;
    }

    public boolean containsType(EntityType type) {
        return types.contains(type);
    }
}
