package com.ptibiscuit.iprofession.listeners;

import com.ptibiscuit.iprofession.Plugin;
import com.ptibiscuit.iprofession.data.models.Profession;
import java.util.ArrayList;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class LearnManagerSign implements Listener {
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerInteract(PlayerInteractEvent e) {
        Plugin plug = Plugin.getInstance();
        Player player = e.getPlayer();
        Block b = e.getClickedBlock();
        if (b != null && (b.getType() == Material.SIGN_POST || b.getType() == Material.WALL_SIGN)) {
            Sign sign = (Sign) b.getState();
            if (sign.getLine(0).contains("[iProfessions]")) {
                String[] data = sign.getLine(1).split(" ");
                // On ajoute des arguments qui pourrait etre sur la deuxième ligne
                if (data[0].equalsIgnoreCase("learn")) {
                    Profession p = Plugin.getData().getProfession(data[1]);
                    if (p != null) {
                        if (!plug.getPermissionHandler().has(player, "sign.learn." + p.getTag(), true) && !plug.getPermissionHandler().has(player, "sign.learn", true)) {
                            plug.sendPreMessage(player, "have_perm");
                            e.setCancelled(true);
                            return;
                        }
                        plug.tryToLearn(player, p);
                    } else {
                        plug.sendPreMessage(player, "unknown_tag");
                    }
                } else if (data[0].equalsIgnoreCase("forget")) {
                    Profession p = Plugin.getData().getProfession(data[1]);
                    if (p != null) {
                        if (!plug.getPermissionHandler().has(player, "sign.forget." + p.getTag(), true) && !plug.getPermissionHandler().has(player, "sign.forget", true)) {
                            Plugin.getInstance().sendPreMessage(player, "have_perm");
                            e.setCancelled(true);
                            return;
                        }
                        ArrayList<Profession> playerProfs = Plugin.getData().getProfessionByPlayer(player.getName());
                        if (playerProfs.contains(p)) {
                            plug.removeProfessionToPlayer(p, player);
                            plug.sendPreMessage(player, "forget_succ");
                        } else {
                            plug.sendMessage(player, plug.getSentence("havnt_profession"));
                        }
                    } else {
                        Plugin.getInstance().sendPreMessage(player, "unknown_tag");
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onSignChange(SignChangeEvent e) {
        if (e.getLine(0).contains("[iProfessions]") && !Plugin.getInstance().getPermissionHandler().has(e.getPlayer(), "sign.create", true)) {
            Plugin.getInstance().sendPreMessage(e.getPlayer(), "have_perm");
            e.setCancelled(true);
        }
    }
}
