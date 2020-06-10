package fr.auretechno.lgmc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import fr.auretechno.lgmc.RoleManager.Role;
import fr.auretechno.lgmc.cmd.BuildingModeCommandExecutor;
import fr.auretechno.lgmc.cmd.LoupGarouCommand;
import fr.auretechno.lgmc.cmd.ReloadConfigCommand;
import fr.auretechno.lgmc.config.CustomConfig;
import fr.auretechno.lgmc.game.GameState;
import fr.auretechno.lgmc.tasks.StartGameTask;

public class LGMain extends JavaPlugin implements Listener {

	private Map<UUID, Boolean> builders = new HashMap<UUID, Boolean>();
	public List<Player> players = new ArrayList<Player>();
	public CustomConfig playersProprieties = new CustomConfig(this, "players");
	private GameState currentState = GameState.WAITING;
	private boolean isThiefPass = false;
	private boolean isCupidPass;
	private boolean isLoversPass;
	
	public void setState(GameState state) {
		if(state == GameState.THIEF_TURN && !isThiefPass) {
			this.currentState = state;
			isThiefPass = true;
		} else if(state == GameState.CUPID_TURN && !isCupidPass) {
			this.currentState = state;
			isCupidPass = true;
		} else if(state == GameState.LOVERS_TURN && !isLoversPass) {
			this.currentState = state;
			isLoversPass = true;
		} else {
			this.currentState = state;
		}
	}

	public GameState getState() {
		return currentState;
	}

	public boolean isState(GameState state) {
		return this.currentState == state;
	}

	@Override
	public void onEnable() {
		sendInfo("Plugin State - Enabling");
		saveDefaultConfig();
		playersProprieties.saveDefaultConfig();

		setState(GameState.WAITING);

		Bukkit.getWorld("world").setTime(18000);

		PluginManager manager = getServer().getPluginManager();
		manager.registerEvents(this, this);

		getCommand("building-mode").setExecutor(new BuildingModeCommandExecutor(this));
		getCommand("building-mode").setTabCompleter(new BuildingModeCommandExecutor.BuildingModeCommandTabCompleter());
		getCommand("reload-lgconfig").setExecutor(new ReloadConfigCommand(this));
		getCommand("lg").setExecutor(new LoupGarouCommand(this));

		int i = 1;
		for (String section : playersProprieties.getConfig().getKeys(false)) {
			builders.put(UUID.fromString(section), playersProprieties.getConfig().getBoolean(section + ".builder"));
			sendInfo("PlayerUUID " + i + ": " + section);
			i++;
		}
	}

	@Override
	public void onLoad() {
		sendInfo("Plugin State - Loading");
	}

	@Override
	public void onDisable() {
		sendInfo("Plugin State - Disabling");
	}
	
	@EventHandler
	public void onPlayerFood(FoodLevelChangeEvent e) {
		e.setFoodLevel(20);
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player player = e.getPlayer();

		setupScoreboard(player);
		if (isState(GameState.WAITING)) {
			if (!getBuilders().containsKey(player.getUniqueId())) {
				playersProprieties.getConfig().createSection(player.getUniqueId().toString());
				playersProprieties.getConfig().set(player.getUniqueId().toString() + ".builder", false);
				playersProprieties.saveConfig();
				continueJoinEvent(e);
			} else if (getBuilders().containsKey(player.getUniqueId())) {
				if (!getBuilders().get(player.getUniqueId())) {
					continueJoinEvent(e);
				} else if (getBuilders().get(player.getUniqueId())) {
					player.teleport(parseStringToLoc(getConfig().getString("locations.spawn")));
					player.setGameMode(GameMode.SPECTATOR);
					e.setJoinMessage("");
					return;
				}
			}
		} else {
			player.teleport(parseStringToLoc(getConfig().getString("locations.spawn")));
		}
	}

	public void setupScoreboard(Player player) {
		Scoreboard scoreboard = getServer().getScoreboardManager().getNewScoreboard();
		Objective objective = scoreboard.registerNewObjective("objective", "dummy", "§c§lLoup §8§lGarou");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		Team stateLine = scoreboard.registerNewTeam("state");
		stateLine.addEntry("§dStatue: ");
		stateLine.setPrefix("");
		stateLine.setSuffix("");
		Team playersLine = scoreboard.registerNewTeam("players");
		playersLine.addEntry("§6Players: §a§l");
		playersLine.setPrefix("");
		playersLine.setSuffix("");
		Team roleLine = scoreboard.registerNewTeam("role");
		roleLine.addEntry("§9Role: ");
		roleLine.setPrefix("");
		roleLine.setSuffix("§cNo");
		objective.getScore("--------------------").setScore(8);
		objective.getScore("   ").setScore(7);
		objective.getScore("§dStatue: ").setScore(6);
		objective.getScore("§9Role: ").setScore(5);
		objective.getScore("§6Players: §a§l").setScore(4);
		objective.getScore(" ").setScore(3);
		objective.getScore("§8--------------------").setScore(2);
		objective.getScore("").setScore(1);
		objective.getScore("§eplay.auretech.ml").setScore(0);

		new BukkitRunnable() {

			int counter = 0;

			@Override
			public void run() {
				counter += 1;
				stateLine.setSuffix(getState().getName());
				
				if(isState(GameState.WAITING) || isState(GameState.STARTING)) {
					playersLine.setSuffix(players.size() + "§r§a/§a§l" + getConfig().getInt("game.playerSize"));
				} else {
					playersLine.setSuffix(RoleManager.roles.size() + "§r§a/§a§l" + getConfig().getInt("game.playerSize"));
					roleLine.setSuffix(RoleManager.roles.get(player).getName());
				}
			}
		}.runTaskTimer(this, 0, 5);

		player.setScoreboard(scoreboard);
	}

	public Location parseStringToLoc(String string) {
		String[] loc = string.replace(" ", "").split(",");
		String w = loc[0];
		Double x = Double.valueOf(loc[1]);
		Double y = Double.valueOf(loc[2]);
		Double z = Double.valueOf(loc[3]);
		return new Location(Bukkit.getWorld(w), x, y, z);
	}

	public void continueJoinEvent(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		player.teleport(parseStringToLoc(getConfig().getString("locations.spawn")));
		if (players.size() < getConfig().getInt("game.playerSize")) {
			players.add(player);
			event.setJoinMessage(
					"§2[§cAure§eTechno§2] §l§a" + player.getDisplayName() + " §r§6viens de d'arriver dans la partie "
							+ "(§l§a" + players.size() + "§r§a/" + getConfig().getInt("game.playerSize") + "§r§6) !");
			player.setGameMode(GameMode.SURVIVAL);
			player.getInventory().clear();
			player.updateInventory();
			if (players.size() >= getConfig().getInt("game.playerSize")) {
				setState(GameState.STARTING);
				StartGameTask start = new StartGameTask(this);
				start.runTaskTimer(this, 0, 20);
			}
		}
	}

	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		if (players.contains(player)) {
			players.remove(player);
			event.setQuitMessage(
					"§2[§cAure§eTechno§2] §l§a" + player.getDisplayName() + " §r§6viens de quitter la partie (§l§a"
							+ players.size() + "§r§a/" + getConfig().getInt("game.playerSize") + "§r§6) !");
		}
	}

	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		event.setFormat("%1$s §7>> §6%2$s");
	}
	
	@EventHandler
	public void onPlayerClickInventory(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		if(event.getInventory() != null && event.getClickedInventory() == RoleManager.thiefInventory) {
			event.setCancelled(true);
			if(RoleManager.roles.containsKey(player)) {
				if(isState(GameState.THIEF_TURN)) {
					if(RoleManager.roles.get(player).equals(Role.THIEF)) {
						if(event.getCurrentItem() != null) {
							if(event.getCurrentItem().getType() == Material.PLAYER_HEAD && event.getCurrentItem().hasItemMeta()) {
								RoleManager.inverseRole(player, Bukkit.getPlayer(event.getCurrentItem().getItemMeta().getDisplayName()));
							} else {
								return;
							}
						} else {
							return;
						}
					}
				}
			} else {
				return;
			}
		} else {
			return;
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if (!getBuilders().containsKey(event.getPlayer().getUniqueId())) {
			playersProprieties.getConfig().createSection(event.getPlayer().getUniqueId().toString());
			playersProprieties.getConfig().set(event.getPlayer().getUniqueId().toString() + ".builder", false);
			playersProprieties.saveConfig();
			event.setCancelled(true);
		} else if (getBuilders().containsKey(event.getPlayer().getUniqueId())) {
			if (!getBuilders().get(event.getPlayer().getUniqueId())) {
				event.setCancelled(true);
			} else if (getBuilders().get(event.getPlayer().getUniqueId())) {
				event.setCancelled(false);
			}
		}
	}

	@EventHandler
	public void onBlockBreak(BlockPlaceEvent event) {
		if (!getBuilders().containsKey(event.getPlayer().getUniqueId())) {
			playersProprieties.getConfig().createSection(event.getPlayer().getUniqueId().toString());
			playersProprieties.getConfig().set(event.getPlayer().getUniqueId().toString() + ".builder", false);
			playersProprieties.saveConfig();
			event.setCancelled(true);
		} else if (getBuilders().containsKey(event.getPlayer().getUniqueId())) {
			if (!getBuilders().get(event.getPlayer().getUniqueId())) {
				event.setCancelled(true);
			} else if (getBuilders().get(event.getPlayer().getUniqueId())) {
				event.setCancelled(false);
			}
		}
	}

	public void sendInfo(String txt) {
		getLogger().info(txt);
	}

	public void sendError(Exception e) {
		getLogger().log(Level.SEVERE, "Plugin Error", e);
	}

	public void sendError(String errorMessage) {
		getLogger().log(Level.SEVERE, "Plugin Error\n" + errorMessage);
	}

	public Map<UUID, Boolean> getBuilders() {
		return builders;
	}

	public void setGamePlaying() {
		for (String section : getConfig().getConfigurationSection("locations.playingPlaces").getKeys(false)) {
			System.out.println(section);
			if (players.get(Integer.valueOf(section.replace("place", "")) - 1) != null) {
				System.out.println("locations.playingPlaces." + section);
				Player player = players.get(Integer.valueOf(section.replace("place", "")) - 1);
				player.teleport(parseStringToLoc(getConfig().getString("locations.playingPlaces." + section)));
			}
		}
		for(Player player : players) {
			player.setGameMode(GameMode.ADVENTURE);
//			player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 255, true, false));
		}
		RoleManager.randomRole(players);
		RoleManager.roles.forEach((p, r) -> {
			p.sendMessage("§2[§cAure§eTechno§2] §6Tu es " + r.getName() + " §r§6!");
		});
		Bukkit.getWorld("world").setTime(18000);
		setState(GameState.START_NIGHT);
		new BukkitRunnable() {
			
			int time = 0;
			int night = 0;
			
			@Override
			public void run() {
				if(time == 5) {
					if(isState(GameState.START_NIGHT)) {
						RoleManager.roles.forEach((player, role) -> {
							if(night == 0) {
								if(role != Role.THIEF) {
									player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 255, true, false));
								}
								if(role.equals(Role.THIEF)) {
									RoleManager.openThiefInventory(player);
								}
							} else {
								if(role != Role.CLAIRVOYANT) {
									player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 255, true, false));
								}
								if(role.equals(Role.CLAIRVOYANT)) {
									
								}
							}
						});
						night++;
						if(night == 1) {
							setState(GameState.THIEF_TURN);
						} else {
							setState(GameState.CLAIRVOYANT_TURN);	
						}
					}
					time = 0;
				}
				time++;
			}
		}.runTaskTimer(this, 0, 10);
	}
}