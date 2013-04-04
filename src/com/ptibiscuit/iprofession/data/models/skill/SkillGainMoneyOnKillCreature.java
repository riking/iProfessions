/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.ptibiscuit.iprofession.data.models.skill;

import com.ptibiscuit.iprofession.Plugin;
import java.util.Map;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;

public class SkillGainMoneyOnKillCreature extends SkillSimpleMonster implements Listener {
    private double reward;

    @Override
    public void onEnable(Map<?, ?> config) {
        super.onEnable(config);
        this.reward = Double.parseDouble(config.get("reward").toString());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityKilled(EntityDeathEvent e) {
        if (e.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent eBis = (EntityDamageByEntityEvent) e.getEntity().getLastDamageCause();
            if (eBis.getDamager() instanceof Player) {
                Player p = (Player) eBis.getDamager();
                if (!this.hasToAct(p))
                    return;
                if (Plugin.getInstance().hasSkill(p, this)) {
                    if (this.containsType(e.getEntityType())) {
                        Plugin.getInstance().getEconomy().depositPlayer(p.getName(), this.reward);
                    }
                }
            }
        }
    }

}
