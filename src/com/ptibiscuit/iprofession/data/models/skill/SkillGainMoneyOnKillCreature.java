/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.ptibiscuit.iprofession.data.models.skill;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import com.ptibiscuit.iprofession.Plugin;

public class SkillGainMoneyOnKillCreature extends SkillSimpleMonster implements Listener {
    private double reward;

    @Override
    public String getKey() {
        return "gainMoneyOnKillCreature";
    }

    @Override
    public void onEnable(ConfigurationSection config) {
        super.onEnable(config);
        reward = config.getDouble("reward");
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityKilled(EntityDeathEvent e) {
        if (e.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent eBis = (EntityDamageByEntityEvent) e.getEntity().getLastDamageCause();
            if (eBis.getDamager() instanceof Player) {
                Player p = (Player) eBis.getDamager();
                if (!hasToAct(p)) {
                    return;
                }
                if (Plugin.getInstance().hasSkill(p, this)) {
                    if (containsType(e.getEntityType())) {
                        Plugin.getInstance().getEconomy().depositPlayer(p.getName(), reward);
                    }
                }
            }
        }
    }
}
