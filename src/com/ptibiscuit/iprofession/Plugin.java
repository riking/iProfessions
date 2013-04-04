package com.ptibiscuit.iprofession;

import com.ptibiscuit.framework.JavaPluginEnhancer;
import com.ptibiscuit.iprofession.data.IData;
import com.ptibiscuit.iprofession.data.YamlData;
import com.ptibiscuit.iprofession.data.models.*;
import com.ptibiscuit.iprofession.data.models.skill.Skill;
import com.ptibiscuit.iprofession.listeners.LearnManagerSign;
import java.util.*;
import me.tehbeard.BeardStat.BeardStat;
import me.tehbeard.BeardStat.containers.PlayerStatBlob;
import me.tehbeard.BeardStat.containers.PlayerStatManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;


public class Plugin extends JavaPluginEnhancer {

    private static Plugin instance;
    private LearnManagerSign lms = new LearnManagerSign();
    private static IData data;
    private ArrayList<ProfessionGroup> professionGroups = new ArrayList<ProfessionGroup>();
    
    // TODO: Move to plugin.yml
    Permission learn = new Permission("iprofession.learn", "Learn a new profession", PermissionDefault.TRUE);
    Permission forget = new Permission("iprofession.forget", "Forget your profession", PermissionDefault.TRUE);
    Permission learn_op = new Permission("iprofession.learn.other", "Cause another user to learn a profession", PermissionDefault.OP);
    Permission forget_op = new Permission("iprofession.learn.other", "Cause another user to forget a profession", PermissionDefault.OP);
    Permission list = new Permission("iprofession.list", "List available professions", PermissionDefault.TRUE);
    Permission showplayers_own = new Permission("iprofession.showplayers.own", "Show all online players with a certain profession that you also have", PermissionDefault.FALSE);
    Permission showplayers_all = new Permission("iprofession.showplayers.all", "Show all online players with a certain profession", PermissionDefault.OP);
    Permission whois = new Permission("iprofession.whois.self", "Get profession information about yourself", PermissionDefault.TRUE);
    Permission whois_other = new Permission("iprofession.whois.other", "Get profession information about someone else", PermissionDefault.TRUE);
    
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
        
        data = new YamlData(this);
        data.loadProfessions();
        myLog.addInFrame(data.getProfessions().size() + " professions loaded !");
        data.loadPlayersProfessions();
        data.loadActivatedWorlds();
        // On doit rendre autonome chaque skill
        for (Profession p : data.getProfessions()) {
            for (Skill s : p.getSkills()) {
                this.getServer().getPluginManager().registerEvents(s, this);
            }
        }
        
        // Loading group of professions.
        for (Map<?, ?> dataGroup : this.getConfig().getMapList("config.profession_groups")) {
            int count = (Integer) dataGroup.get("max_profession");
            @SuppressWarnings("unchecked")
            ArrayList<String> professionsTag = (ArrayList<String>) dataGroup.get("professions");
            ArrayList<Profession> professions = new ArrayList<Profession>();
            for (String pTag : professionsTag) {
                professions.add(data.getProfession(pTag));
            }
            this.professionGroups.add(new ProfessionGroup(professions, count));
        }
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
        pManager.registerEvents(this.lms, this);
        myLog.displayFrame(false);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        // TODO restrict to player later
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
                    if (!sender.hasPermission("iprofession.learn." + args[0])) {
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
                if (!sender.hasPermission("iprofession.forget"))
                {
                    this.sendPreMessage(writer, "have_perm"); //TODO disambiguate message
                    return true;
                }
                
                Profession p = data.getProfession(args[0]);
                if (p != null) {
                    // TODO player.hasProfession
                    ArrayList<Profession> playerProfs = data.getProfessionByPlayer(writer.getName());
                    if (playerProfs.contains(p)) {
                        this.removeProfessionToPlayer(p, writer);
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
                if (!sender.hasPermission("iprofession.learn.other"))
                {
                    this.sendPreMessage(writer, "have_perm");
                    return true;
                }
                Profession p = data.getProfession(args[1]);
                if (p != null) {
                    Player player = this.getServer().getPlayer(args[0]);
                    if (player != null)
                    {
                        this.addProfessionToPlayer(p, player);
                        this.sendMessage(sender, this.getSentence("profession_added").replace("{PLAYER}", player.getName()));
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
            else if (label.equalsIgnoreCase("plist")) {
                if (!sender.hasPermission("iprofession.list"))
                {
                    this.sendPreMessage(writer, "have_perm");
                    return true;
                }
                // TODO
                this.sendMessage(writer, "");
            } else if (label.equalsIgnoreCase("showusers")) {
                Profession p = data.getProfession(args[0]);
                if (p != null) {
                    ArrayList<Profession> playerProfs = data.getProfessionByPlayer(writer.getName());
                    if ((sender.hasPermission(showplayers_all)) || (playerProfs.contains(p) && sender.hasPermission(showplayers_own))) {
                        
                        ArrayList<Player> players = new ArrayList<Player>();
                        for (Player playerFoc : this.getServer().getOnlinePlayers()) {
                            if (data.getProfessionByPlayer(playerFoc.getName()).contains(p)) {
                                players.add(playerFoc);
                            }
                        }
                        if (players.size() > 0) {
                            this.sendMessage(sender, this.getSentence("list_online_head").replace("{PROF}", args[0]));
                            StringBuilder sb = new StringBuilder();
                            for (Player playerFoc : players) {
                                sb.append(playerFoc.getName()).append(' ');
                            }
                            this.sendMessage(sender, sb.toString().trim());
                        } else {
                            this.sendMessage(sender, this.getSentence("no_player_online").replace("{PROF}", args[0]));
                        }
                    } else {
                        this.sendPreMessage(writer, "have_perm");
                    }
                } else {
                    ArrayList<Player> players = new ArrayList<Player>();
                    for (Player playerFoc : this.getServer().getOnlinePlayers()) {
                        if (data.getProfessionPlayers().containsKey(playerFoc.getName())) {
                            players.add(playerFoc);
                        }
                    }
                    if (players.size() > 0) {
                        // TODO make localized string
                        this.sendMessage(sender, "Players with no profession:");
                        StringBuilder sb = new StringBuilder();
                        for (Player playerFoc : players) {
                            sb.append(playerFoc.getName()).append(' ');
                        }
                        this.sendMessage(sender, sb.toString().trim());
                    } else {
                        // TODO make localized string
                        this.sendMessage(sender, "No professionless players online.");
                    }
                }
            }
            else if (label.equalsIgnoreCase("premprofuser")) {
                if (!sender.hasPermission("iprofession.forget.other"))
                {
                    this.sendPreMessage(writer, "have_perm");
                    return true;
                }
                
                Profession p = data.getProfession(args[1]);
                if (p != null) {
                    Player player = this.getServer().getPlayer(args[0]);
                    if (player != null)
                    {
                        ArrayList<Profession> playerProfs = data.getProfessionByPlayer(args[0]);
                        if (playerProfs.contains(p)) {
                            this.removeProfessionToPlayer(p, player);
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
            else if (label.equalsIgnoreCase("pwhois"))
            {
                OfflinePlayer pFocus;
                if (args.length == 0)
                {
                    if (!sender.hasPermission(whois))
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
                    ArrayList<Profession> prof = data.getProfessionByPlayer(pFocus.getName());
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

    public boolean tryToLearn(Player writer, Profession newprof)
    {
        ArrayList<Profession> playerActualProfessions = data.getProfessionByPlayer(writer.getName());

        for (Require r : newprof.getRequired())
        {
            int requiredLeft = this.hasRequire(writer.getName(), r);
            if (requiredLeft > 0)
            {
                // On affiche le problème
                this.sendMessage(writer, r.getHasnot().replace("{LEFT}", String.valueOf(requiredLeft)));
                return false;
            }
        }
        // On vérifie si notre métier n'appartient pas à un groupe
        ProfessionGroup pg = this.getGroupByProfession(newprof);
        if (pg != null) {
            // On regarde le nombre actuelle de métier qu'il a, il faut qu'il soit strictement
            // inférieur au nombre maximal de métier dans cette catégorie.
            if (!(pg.getContains(playerActualProfessions) < pg.getMaxProfessionsPerPlayer())) {
                this.sendPreMessage(writer, "maximal_group_count_reached");
                return false;
            }
        }
        
        // On vérifie qu'il n'a pas déjà un métier dans cet arbre. A ne faire seulement
        // Seulement si c'est une profession première
        if (newprof.getParent() == null) {
            for (Profession curprof : data.getProfessionByPlayer(writer.getName())) {
                if (newprof.isInTheSameTree(curprof)) {
                    this.sendPreMessage(writer, "already_prof_in_tree");
                    return false;
                }
            }
        }
        // On vérifie qu'il n'a pas déjà atteint le max de profession dans ce groupe.
        //ProfessionGroup professionGroup = 
        
        // On regarde si il a l'argent
        if (newprof.getPrice() != 0 && this.isEconomyEnabled())
        {
            double moneyPlayer = this.economy.getBalance(writer.getName());
            if (moneyPlayer < newprof.getPrice()) {
                this.sendMessage(writer, getSentence("cant_afford").replace("{PRICE}", this.economy.format(newprof.getPrice())));
                return false;
            }
        }
        if (newprof.getParent() == null) {
            // Il ne lui faut qu'une place de libre dans ses professions !
            if (playerActualProfessions.size() >= this.getConfig().getInt("config.max_profession")) {
                this.sendPreMessage(writer, "cant_learn_more_prof");
                return false;
            } else {
                this.addProfessionToPlayer(newprof, writer);
                this.sendPreMessage(writer, "profession_learnt");
                if (this.isEconomyEnabled())
                    this.economy.withdrawPlayer(writer.getName(), newprof.getPrice());
                return true;
            }
        } else {
            // On vérifie qu'il possède la profession parente !
            if (!playerActualProfessions.contains(newprof.getParent())) {
                this.sendPreMessage(writer, "need_to_learn_parent_profession");
                return false;
            } else {
                // Ok, on enlève la profession parent
                this.removeProfessionToPlayer(newprof.getParent(), writer);
                this.addProfessionToPlayer(newprof, writer);
                this.sendPreMessage(writer, "profession_learnt");
                if (this.isEconomyEnabled())
                    this.economy.withdrawPlayer(writer.getName(), newprof.getPrice());
                return true;
            }
        }
    }
    @Override
    public void onConfigurationDefault(FileConfiguration c) {
        c.set("config.max_profession", 1);
        ArrayList<MemorySection> nodeGroups = new ArrayList<MemorySection>();
        MemoryConfiguration defaultGroup = new MemoryConfiguration();
        defaultGroup.set("max_profession", 1);
        ArrayList<String> professions = new ArrayList<String>();
        professions.add("farmer");
        professions.add("miner");
        professions.add("hunter");
        defaultGroup.set("professions", professions);
        nodeGroups.add(defaultGroup);
        c.set("config.profession_groups", nodeGroups);
        c.set("config.activated_worlds", new ArrayList<String>());
        c.set("config.allow_break_placed_blocks", false);
        c.set("players", new HashMap<String, String>());
        c.set("professions", new HashMap<String, String>());
    }

    @Override
    public void onLangDefault(Properties p) {
        /*p.put("fail_prof", "Can't load professions' file.");
        p.put("new_file_prof", "Creatig a default professions' file.");
        p.put("succ_prof", "Professions' file loaded !");
        p.put("need_config", "You can now create your professions in config.yml file");
        p.put("new_file_lang", "Creating a default lang file.");
        p.put("fail_lang", "Can't load lang file.");
        p.put("succ_lang", "Lang file loaded !");
        p.put("fail_config", "Problème dans le chargement des fichiers de configurations.");
        p.put("new_file_pprof", "Création d'un fichier de professions de joueurs par défaut.");
        p.put("fail_pprof", "Impossible de trouver le fichier de joueur.");
        p.put("succ_pprof", "Fichier de professions de joueurs correctement chargé !");*/
        p.put("get_prof", "Your have learn't the profession of {NAME}");
        p.put("forget_succ", "You have forgotten your job !");
        p.put("have_perm", "You're not able to do that.");
        p.put("run_as_player", "This command has to be executed in game.");
        p.put("unknown_tag", "Unknown profession's tag.");
        //p.put("fake_tag_prof", "Un tag de profession du fichier professions.yml n'existe pas.");
        p.put("player_unknown", "This player doesn't exist.");
        p.put("profession_learnt", "You have learn't a new profession !");
        p.put("need_to_learn_parent_profession", "You first have to learn the parent profession of this profession.");
        p.put("whois_entete", ChatColor.GOLD + "Informations about {PLAYER}:" + ChatColor.WHITE);
        p.put("whois_first", " Profession: {PROFESSION}");
        p.put("cant_afford", "You havn't enough money to learn this profession. You need {PRICE}.");
        p.put("user_havnt_profession", "{PLAYER} doesn't have this profession.");
        p.put("profession_removed", "{PLAYER} has forget this profession !");
        p.put("profession_added", "{PLAYER} has learn't this profession !");
        p.put("cant_learn_more_prof", "You can't learn more profession.");
        p.put("havnt_profession", "You havn't this profession.");
        p.put("already_prof_in_tree", "You have already learn't a profession in the same tree.");
        p.put("maximal_group_count_reached", "You can't learn more profession of this group.");
        p.put("no_player_online", "There's no {PROF} online.");
        p.put("list_online_head", "List of {PROF}");
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
    
    public void addProfessionToPlayer(Profession prof, Player p) {
        ArrayList<Profession> actualProfession = data.getProfessionByPlayer(p.getName());
        actualProfession.add(prof);
        data.setPlayerProfession(p.getName(), actualProfession);
        prof.applyPermissions(p);
        // If profession has a linked-group
        String groupTag = prof.getGroup();
        if (groupTag != null) {
            net.milkbowl.vault.permission.Permission perm = this.getPermissionHandler().getPermission();
            perm.playerAddGroup(p, groupTag);
        }
    }
    
    public void removeProfessionToPlayer(Profession prof, Player p) {
        ArrayList<Profession> actualProfession = data.getProfessionByPlayer(p.getName());
        actualProfession.remove(prof);
        data.setPlayerProfession(p.getName(), actualProfession);
        // If profession has a linked-group
        String groupTag = prof.getGroup();
        if (groupTag != null) {
            net.milkbowl.vault.permission.Permission perm = this.getPermissionHandler().getPermission();
            perm.playerRemoveGroup(p, groupTag);
        }
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
    
    public boolean isWorldActivated(World w) {
        return this.isWorldActivated(w.getName());
    }
    
    public boolean isWorldActivated(String s) {
        List<String> activeWorlds = data.getActivatedWorlds();
        if (!activeWorlds.isEmpty()) {
            return activeWorlds.contains(s);
        } else {
            return true;
        }
    }
    
    public ProfessionGroup getGroupByProfession(Profession p) {
        for (ProfessionGroup pg : this.professionGroups) {
            if (pg.isInGroup(p))
                return pg;
        }
        return null;
    }
    
    public boolean isEconomyEnabled()
    {
        return (this.economy != null);
    }
}
