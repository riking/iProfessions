package com.ptibiscuit.iprofession;

import com.ptibiscuit.framework.JavaPluginEnhancer;
import com.ptibiscuit.iprofession.data.IData;
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
import me.tehbeard.BeardStat.BeardStat;
import me.tehbeard.BeardStat.containers.PlayerStatBlob;
import me.tehbeard.BeardStat.containers.PlayerStatManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class Plugin extends JavaPluginEnhancer {

	private static Plugin instance;
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
		this.setup(ChatColor.BLUE + "[iProfessions]", "iprofessions", true);
		Plugin.instance = this;

		myLog.startFrame();
		myLog.addInFrame("iProfessions by Ptibiscuit");
		
		data = new YamlData();
		data.loadProfessions();
		myLog.addInFrame(data.getProfessions().size() + " professions loaded !");
		data.loadPlayersProfessions();
		
		if (this.setupStats())
			myLog.addInFrame("Stats detected, you can use the required field !");
		else
			myLog.addInFrame("Stats not detected.");
		
		if (this.setupEconomy())
			myLog.addInFrame("Economy detected, you can use the price field !");
		else
			myLog.addInFrame("Economy not detected, too bad !");
		
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
			sender.sendMessage("[iProfessions] " + this.getSentence("run_as_player"));
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
					if (!this.getPermissionHandler().has(writer, "learn." + args[0], false))
					{
						this.sendPreMessage(writer, "have_perm");
						return true;
					}
					this.tryToLearn(writer, p);
				}
				else if (true)
				{
					this.sendPreMessage(writer, "unknown_tag");
				}
			}
			else if (label.equalsIgnoreCase("pforget"))
			{
				
				
				Profession p = data.getProfession(args[0]);
				if (p != null) {
					ArrayList<Profession> playerProfs = this.data.getProfessionByPlayer(writer.getName());
					if (playerProfs.contains(p)) {
						playerProfs.remove(p);
						this.data.setPlayerProfession(writer.getName(), playerProfs);
						this.sendPreMessage(writer, "forget_succ");
					} else {
						this.sendMessage(sender, this.getSentence("havnt_profession"));
					}
				} else {
					this.sendPreMessage(writer, "unknown_tag");
				}
			}
			else if (label.equalsIgnoreCase("paddprofuser"))
			{
				if (!this.getPermissionHandler().has(writer, "user.profession.add", true))
				{
					this.sendPreMessage(writer, "have_perm");
					return true;
				}
				Profession p = data.getProfession(args[1]);
				if (p != null) {
					OfflinePlayer player = this.getServer().getOfflinePlayer(args[0]);
					if (player != null)
					{
						ArrayList<Profession> playerProfs = this.data.getProfessionByPlayer(args[0]);
						playerProfs.add(p);
						this.data.setPlayerProfession(args[0], playerProfs);
					}
					else
					{
						this.sendPreMessage(sender, "player_unknown");
					}
				} else
				{
					this.sendPreMessage(writer, "unknown_tag");
				}
			}
			else if (label.equalsIgnoreCase("premprofuser")) {
				if (!this.getPermissionHandler().has(writer, "user.profession.remove", true))
				{
					this.sendPreMessage(writer, "have_perm");
					return true;
				}
				
				Profession p = data.getProfession(args[1]);
				if (p != null) {
					OfflinePlayer player = this.getServer().getOfflinePlayer(args[0]);
					if (player != null)
					{
						ArrayList<Profession> playerProfs = this.data.getProfessionByPlayer(args[0]);
						if (playerProfs.contains(p)) {
							playerProfs.remove(p);
							this.data.setPlayerProfession(args[0], playerProfs);
							this.sendMessage(sender, this.getSentence("profession_removed")
									  .replace("{PLAYER}", player.getName()));
						} else {
							this.sendMessage(sender, this.getSentence("user_havnt_profession")
									  .replace("{PLAYER}", player.getName()));
						}
					}
					else
					{
						this.sendPreMessage(sender, "player_unknown");
					}
				} else
				{
					this.sendPreMessage(writer, "unknown_tag");
				}
			}
			/*
			else if (label.equalsIgnoreCase("psetuser"))
			{
				if (!this.getPermissionHandler().has(writer, "setuser", true))
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
			}*/
			else if (label.equalsIgnoreCase("pwhois"))
			{
				OfflinePlayer pFocus;
				if (args.length == 0)
				{
					if (!this.getPermissionHandler().has(writer, "whois.self", false))
					{
						this.sendPreMessage(writer, "have_perm");
						return true;
					}
					pFocus = writer;
				}
				else
				{
					if (!this.getPermissionHandler().has(writer, "whois.other", false))
					{
						this.sendPreMessage(writer, "have_perm");
						return true;
					}
					
					pFocus = this.getServer().getOfflinePlayer(args[0]);
				}
				
				if (pFocus != null)
				{
					this.sendMessage(writer, this.getSentence("whois_entete").replace("{PLAYER}", pFocus.getName()));
					ArrayList<Profession> prof = this.data.getProfessionByPlayer(pFocus.getName());
					if (prof != null) {
						String professions = "";
						for (Profession p : prof) {
							professions = professions + " " + p.getTag();
						}
						this.sendMessage(writer, this.getSentence("whois_first").replace("{PROFESSION}", professions));
					} else
						this.sendMessage(writer, this.getSentence("whois_first").replace("{PROFESSION}", "null"));
				}
				else
				{
					this.sendPreMessage(sender, "player_unknown");
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
		// problem est passé à true quand un des required n'est pas bon.
		boolean problem = false;
		for (Require r : p.getRequired())
		{
			int requiredLeft = this.hasRequire(writer.getName(), r);
			if (requiredLeft > 0)
			{
				// On affiche le problème
				this.sendMessage(writer, r.getHasnot().replace("{LEFT}", String.valueOf(requiredLeft)));
				problem = true;
			}
		}
		if (!problem) {
			// On regarde si il a l'argent
			if (p.getPrice() != 0 && this.isEconomyEnabled())
			{
				double moneyPlayer = this.economy.getBalance(writer.getName());
				if (moneyPlayer >= p.getPrice()) {
					this.economy.withdrawPlayer(writer.getName(), p.getPrice());
				} else {
					this.sendMessage(writer, getSentence("cant_afford").replace("{PRICE}", this.economy.format(p.getPrice())));
					return false;
				}
				
			}
			ArrayList<Profession> actualProfession = data.getProfessionByPlayer(writer.getName());
			if (p.getParent() == null) {
				// Il ne lui faut qu'une place de libre dans ses professions !
				if (actualProfession.size() < this.getConfig().getInt("config.max_profession")) {
					actualProfession.add(p);
					data.setPlayerProfession(writer.getName(), actualProfession);
					return true;
				} else {
					this.sendPreMessage(writer, "cant_learn_more_prof");
				}
			} else {
				// On vérifie qu'il possède la profession parente !
				if (actualProfession.contains(p.getParent())) {
					// Ok, on enlève la profession parent
					actualProfession.remove(p.getParent());
					actualProfession.add(p);
					data.setPlayerProfession(writer.getName(), actualProfession);
					return true;
				} else {
					this.sendPreMessage(writer, "need_to_learn_parent_profession");
				}
			}
		}
		return false;
	}

	@Override
	public void onConfigurationDefault(FileConfiguration c) {
		c.set("config.max_profession", 1);
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
		p.put("forget_succ", "Vous avez oublié votre métier !");
		p.put("have_perm", "Vous n'avez pas la permission de faire ceci.");
		p.put("run_as_player", "Cette commande doit être faites en jeu.");
		p.put("unknown_tag", "Tag de profession inconnue.");
		p.put("fake_tag_prof", "Un tag de profession du fichier professions.yml n'existe pas.");
		p.put("player_unknown", "Ce joueur n'existe pas.");
		p.put("need_to_learn_parent_profession", "Vous devez d'abord apprendre une autre profession pour accéder à celle-ci");
		p.put("whois_entete", ChatColor.GOLD + "Informations sur {PLAYER}:" + ChatColor.WHITE);
		p.put("whois_first", " Profession: {PROFESSION}");
		p.put("cant_afford", "You havn't enough money to learn this profession. You need {PRICE}.");
		p.put("user_havnt_profession", "{PLAYER} doesn't have this profession.");
		p.put("profession_removed", "{PLAYER} has forget this profession !");
		p.put("cant_learn_more_prof", "You can't learn more profession.");
		p.put("havnt_profession", "You havn't this profession.");
	}
	
	public Skill getSkill(int id, int metaData, TypeSkill ts)
	{
		Skill fakeSk = new Skill(id, metaData, ts);
		for (Profession p : data.getProfessions())
			for (Skill s : p.getSkills())
				if (s.equals(fakeSk))
					return s;
		// Maintenant, on regarde si on n'a pas la compétence en globale
		Skill fakeGlobalSk = new Skill(id, -1, ts);
		for (Profession p : data.getProfessions())
			for (Skill s : p.getSkills())
				if (s.equals(fakeGlobalSk))
					return s;
		return null;
	}
	
	public boolean hasSkill(Player pl, Skill s)
	{
		ArrayList<Profession> profs = data.getProfessionByPlayer(pl.getName());
		for (Profession p : profs) {
			if (p.hasSkill(s))
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
	
	/*
	 * All about BeardStat handling
	 */
	private PlayerStatManager statManager;
	public boolean setupStats()
	{
		JavaPlugin plug = (JavaPlugin) this.getServer().getPluginManager().getPlugin("BeardStat");
		if (plug == null)
			return false;
		this.statManager = ((BeardStat) plug).getStatManager();
		return true;
	}
	/*
	 * It seems weird, but this function returned the difference between the required and the actual stat. If it's positive,
	 * it means that it hasn't the required (Cause required would be higher than the actual)
	 */
	public int hasRequire(String playername, Require req)
	{
		return req.getRequired() - this.getStatValue(playername, req.getCategory(), req.getKey());
	}
	
	public int getStatValue(String playerName, String cat, String stat)
	{
		if (isUsingStat())
		{
			PlayerStatBlob playerStat = this.statManager.findPlayerBlob(playerName);
			if (playerStat != null) {
				if (playerStat.hasStat(cat, stat)) {
					return playerStat.getStat(cat, stat).getValue();
				} else {
					this.myLog.warning("You're using a stat that doesn't exist: " + cat + "-" + stat);
				}
			}
		}
		return 0;
	}
	
	public boolean isUsingStat()
	{
		return (this.statManager != null);
	}
	
	/*
	 * iConomySupport
	 */
	private Economy economy;
	public boolean setupEconomy()
	{
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		this.economy = rsp.getProvider();
		return economy != null;
	}
	
	public Economy getEconomy()
	{
		return this.economy;
	}
	
	public boolean isEconomyEnabled()
	{
		return (this.economy != null);
	}
}
