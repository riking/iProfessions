package com.ptibiscuit.iprofession.data.models.skill;

import com.ptibiscuit.iprofession.Plugin;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

/**
 * 
 * @author ANNA
 */
public class SkillDropItem extends SkillSimpleId implements Listener {

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockBreak(BlockBreakEvent e) {
        if (!Plugin.getInstance().hasSkill(e.getPlayer(), this) && this.hasToAct(e.getPlayer())) {
            if (this.hasId(e.getBlock().getTypeId(), e.getBlock().getData())) {
                e.getBlock().setType(Material.AIR);
            }
        }
    }
}
