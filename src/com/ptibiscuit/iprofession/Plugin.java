package com.ptibiscuit.iprofession;

import com.ptibiscuit.framework.JavaPluginEnhancer;
import com.ptibiscuit.framework.PermissionHelper;
import com.ptibiscuit.iprofession.data.IData;
import com.ptibiscuit.iprofession.data.StatsHandler;
import com.ptibiscuit.iprofession.data.YamlData;
import com.ptibiscuit.iprofession.data.models.Profession;
import com.ptibiscuit.iprofession.data.models.Require;
import com.ptibiscuit.iprofession.data.models.Skill;
import com.ptibiscuit.iprofession.data.models.TypeSkill;
import com.ptibiscuit.iprofession.listeners.LearnManagerSign;
import com.ptibiscuit.iprofession.listeners.SkillManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

public class Plugin extends JavaPluginEnhancer {

	private static Plugin instance;
	private static StatsHandler stats = new StatsHandler();
	private SkillManager sm = new SkillManager();
	private LearnManagerSign lms = new LearnManagerSign();
	private static IData data;
	
	@Override
	public void onDisable() 
	{
	}

	@Override
	public void onEnable()
	{
		this.setup("iProfessions", ChatColor.BLUE + "[iProfessions]", "iprofessions", true);
		Plugin.instance = this;

		myLog.startFrame();
		myLog.addInFrame(this.name + " by Ptibiscuit");
		
		data = new YamlData();
		data.loadProfessions();
		myLog.addInFrame(data.getProfessions().size() + " professions loaded !");
		data.loadPlayersProfessions();
		
		if (stats.setupStats(this.getServer()))
		{
			myLog.addInFrame("Stats detected, you can use the required field !");
		}
		else
		{
			myLog.addInFrame("Stats not detected.");
		}
		
		// On active nos listeners
		PluginManager pManager = this.getServer().getPluginManager();
		pManager.registerEvents(this.sm, this);
		pManager.registerEvents(this.lms, this);
		
		myLog.displayFrame(false);
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
				Profession p = data.getProfession(args[0]);
				if (p != null)
				{
					if (!PermissionHelper.has(writer, this.prefixPermissions + ".learn." + args[0], false))
					{
						this.sendPreMessage(writer, "have_perm");
						return true;
					}
					this.tryToLearn(writer, p);
				}
				else
				{
					this.sendPreMessage(writer, "unknown_tag");
				}
			}
			else if (label.equalsIgnoreCase("pforget"))
			{
				if (!PermissionHelper.has(writer, this.prefixPermissions + ".forget", false))
				{
					this.sendPreMessage(writer, "have_perm");
					return true;
				}

				this.data.setPlayerProfession(writer.getName(), null);
				this.sendPreMessage(writer, "forget_succ");
			}
			else if (label.equalsIgnoreCase("psetuser"))
			{
				if (!PermissionHelper.has(writer, this.prefixPermissions + ".setuser", false))
				{
					this.sendPreMessage(writer, "have_perm");
					return true;
				}
				
				Profession p = data.getProfession(args[1]);
				if (p != null  || args[1].equalsIgnoreCase("null"))
				{
					OfflinePlayer player = this.getServer().getOfflinePlayer(args[0]);
					if (player != null)
					{
						this.data.setPlayerProfession(player.getName(), p);
						this.sendMessage(sender, this.getSentence("setuser_succ").replace("{PLAYER}", player.getName()).replace("{PROFESSION}", p.getName()));
					}
					else
					{
						this.sendPreMessage(sender, "player_unknown");
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

	public boolean tryToLearn(Player writer, Profession p)
	{
		ArrayList<Require> rqire = p.canLearn(writer);
		if (rqire.isEmpty())
		{
			Profession actualProfession = data.getProfessionByPlayer(writer.getName());
			if (actualProfession == p.getParent())
			{
				// Okey !
				String m = this.getSentence("get_prof").replace("{NAME}", p.getName());
				data.setPlayerProfession(writer.getName(), p);
				this.sendMessage(writer, m);
				return true;
			}
			else
			{
				if (actualProfession != null)
					this.sendPreMessage(writer, "already_profession");
				else
					this.sendPreMessage(writer, "need_to_learn_parent_profession");
			}
		}
		else
		{
			for (Require r : rqire)
			{
				String message = r.getHasnot();
				this.sendMessage(writer, message.replace("{LEFT}", String.valueOf(r.getHowManyPointNeedToComplete(writer))));
			}
		}
		return false;
	}
	
	public static StatsHandler getStatsHandler() {
		return stats;
	}

	@Override
	public void onConfigurationDefault(FileConfiguration c) {
		c.set("players", new HashMap<String, String>());
		c.set("professions", new HashMap<String, String>());
		
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
		p.put("get_prof", "Vous êtes dorénavent un {NAME}");
		p.put("have_perm", "Vous n'avez pas la permission de faire ceci.");
		p.put("run_as_player", "Cette commande doit être faites en jeu.");
		p.put("unknown_tag", "Tag de profession inconnue.");
		p.put("already_profession", "Vous avez déjà une spécialisation !");
		p.put("fake_tag_prof", "Un tag de profession du fichier professions.yml n'existe pas.");
		p.put("player_unknown", "Ce joueur n'existe pas.");
		p.put("setuser_succ", "Vous avez bourré le crane de {PLAYER} en lui apprenant \"{PROFESSION}\"");
		p.put("need_to_learn_parent_profession", "Vous devez d'abord apprendre une autre profession pour accéder à celle-ci");
	}
	
	public Skill getSkill(int id, TypeSkill ts)
	{
		Skill fakeSk = new Skill(id, ts);
		for (Profession p : data.getProfessions())
			for (Skill s : p.getSkills())
				if (s.equals(fakeSk))
					return s;
		return null;
	}
	
	public boolean hasSkill(Player pl, Skill s)
	{
		Profession p = data.getProfessionByPlayer(pl.getName());
		if ((p != null && p.hasSkill(s)))
		{
			return true;
		}
		return false;
	}
	
	public boolean isALearnableSkill(Skill s)
	{
		for (Profession p : data.getProfessions())
		{
			if (p.hasSkill(s))
			{
				return true;
			}
		}
		return false;
	}
	
	public static Plugin getInstance() {
		return instance;
	}

	public static IData getData() {
		return data;
	}
}
