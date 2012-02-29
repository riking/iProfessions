package com.ptibiscuit.iprofession;

import java.util.ArrayList;
import java.util.Properties;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginManager;
import org.bukkit.util.config.Configuration;

import com.ptibiscuit.framework.JavaPluginEnhancer;
import com.ptibiscuit.framework.PermissionHelper;
import com.ptibiscuit.iprofession.data.Profession;
import com.ptibiscuit.iprofession.data.Require;
import com.ptibiscuit.iprofession.listeners.BlockShiftManager;
import com.ptibiscuit.iprofession.listeners.InventoryShiftManager;
import com.ptibiscuit.iprofession.listeners.PlayerShiftManager;
import com.ptibiscuit.iprofession.ressources.ProfessionsHandler;
import com.ptibiscuit.iprofession.ressources.StatsHandler;
import java.util.HashMap;

public class ProfessionsPlugin extends JavaPluginEnhancer {

	private static ProfessionsPlugin instance;
	private static StatsHandler stats = new StatsHandler();
	
	private PlayerShiftManager pm = new PlayerShiftManager();
	private InventoryShiftManager im;
	private BlockShiftManager bm = new BlockShiftManager();
	
	
	private static ProfessionsHandler ph;
	
	/*
	 * Liste des permissions:
	 * => iprofessions.[...]
	 * - learn.[Tag de metier]
	 */
	
	@Override
	public void onDisable() 
	{
	}

	@Override
	public void onEnable()
	{
		this.setup("iProfessions", ChatColor.BLUE + "[iProfessions]", "iprofessions", true);
		
		ProfessionsPlugin.instance = this;
		ph = new ProfessionsHandler(this.getDataFolder());
		im = new InventoryShiftManager();
		stats.setupStats(this.getServer());
		
		myLog.startFrame();
		myLog.addInFrame(this.name + " by Ptibiscuit");
			ph.loadProfessions();
			ph.loadPlayersProfessions();
			
			// On active nos listeners
			PluginManager pManager = this.getServer().getPluginManager();
			pManager.registerEvent(Type.PLAYER_INTERACT, pm, Priority.Normal, this);
			pManager.registerEvent(Type.CUSTOM_EVENT, im, Priority.Normal, this);
			pManager.registerEvent(Type.BLOCK_BREAK, bm, Priority.Normal, this);
		
		myLog.displayFrame();
	}
	
	public static ProfessionsPlugin getInstance() {
		return instance;
	}

	public static ProfessionsHandler getData() {
		return ph;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		if (!(sender instanceof Player))
		{
			sender.sendMessage("[iPermissions] " + this.getSentence("run_as_player"));
			return true;
		}
		
		Player writer = (Player) sender;
		
		try
		{
			if (label.equalsIgnoreCase("plearn"))
			{
				// D'abord le tag !
				Profession p = ph.getProfession(args[0]);
				if (p != null)
				{
					if (!PermissionHelper.has(writer, this.prefixPermissions + ".learn." + args[0], false))
					{
						this.sendPreMessage(writer, "have_perm");
						return true;
					}
					
					// Peut-il rejoindre ce métier ?
					ArrayList<Require> rqire = p.canLearn(writer);
					if (rqire.isEmpty())
					{
						if (ph.getProfessionByPlayer(writer.getName()) == null)
						{
							// Okey !
							String m = this.getSentence("get_prof").replace("+prof", p.getName());
							ph.setPlayersProfession(writer.getName(), p);
							this.sendMessage(writer, m);
						}
						else
						{
							this.sendPreMessage(writer, "already_profession");
						}
					}
					else
					{
						for (Require r : rqire)
						{
							String message = r.getHasnot();
							this.sendMessage(writer, message.replace("+need", String.valueOf(r.getHowManyPointNeedToComplete(writer))));
						}
					}
				}
				else
				{
					this.sendPreMessage(writer, "unknown_tag");
				}
			}
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			return false;
		}
		
		return true;
	}

	public static StatsHandler getStatsHandler() {
		return stats;
	}

	@Override
	public void onConfigurationDefault(Configuration c) {
		c.setProperty("players", new HashMap<String, String>());
		c.setProperty("professions", new HashMap<String, String>());
		
	}

	@Override
	public void onLangDefault(Properties p) {
		p.put("fail_prof", "Impossible de charger le fichier de professions.");
		p.put("new_file_prof", "Création d'un fichier de professions par défaut.");
		p.put("succ_prof", "Fichier de professions correctement chargés !");
		p.put("need_config", "Veuillez configurez vos classes avec le fichier professions.yml.");
		
		p.put("new_file_lang", "Création d'un fichier de langues par défaut.");
		p.put("fail_lang", "Impossible de trouver le fichier de langue.");
		p.put("succ_lang", "Fichier de langue correctement chargé !");
		
		p.put("fail_config", "Problème dans le chargement des fichiers de configurations.");
		p.put("new_file_pprof", "Création d'un fichier de professions de joueurs par défaut.");
		p.put("fail_pprof", "Impossible de trouver le fichier de joueur.");
		p.put("succ_pprof", "Fichier de professions de joueurs correctement chargé !");
		p.put("get_prof", "Vous êtes dorénavent un +prof");
		p.put("have_perm", "Vous n'avez pas la permission de faire ceci.");
		
		p.put("run_as_player", "Cette commande doit être faites en jeu.");
		p.put("unknown_tag", "Tag de profession inconnue.");
		p.put("already_profession", "Vous avez déjà une spécialisation !");
		
		p.put("fake_tag_prof", "Un tag de profession du fichier professions.yml n'existe pas.");
		
	}
}
