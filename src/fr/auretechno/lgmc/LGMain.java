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
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import fr.auretechno.lgmc.cmd.BuildingModeCommandExecutor;
import fr.auretechno.lgmc.cmd.ReloadConfigCommand;
import fr.auretechno.lgmc.config.CustomConfig;
import fr.auretechno.lgmc.game.GameState;

public class LGMain extends JavaPlugin implements Listener {
	
	private Map<UUID, Boolean> builders = new HashMap<UUID, Boolean>();
	private List<Player> players = new ArrayList<Player>();
	public CustomConfig playersProprieties = new CustomConfig(this, "players");
	private GameState currentState = GameState.WAITING;
	
	@Override
	public void onEnable() {
		sendInfo("Plugin State - Enabling");
		saveDefaultConfig();
		playersProprieties.saveDefaultConfig();
		
		PluginManager manager = getServer().getPluginManager();
		manager.registerEvents(this, this);
		
		getCommand("building-mode").setExecutor(new BuildingModeCommandExecutor(this));
		getCommand("building-mode").setTabCompleter(new BuildingModeCommandExecutor.BuildingModeCommandTabCompleter());
		
		getCommand("reload-lgconfig").setExecutor(new ReloadConfigCommand(this));
		
		int i = 1;
		for(String section : playersProprieties.getConfig().getKeys(false)) {
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
		if(!getBuilders().containsKey(player.getUniqueId())) {
			playersProprieties.getConfig().createSection(player.getUniqueId().toString());
			playersProprieties.getConfig().set(player.getUniqueId().toString() + ".builder", false);
			playersProprieties.saveConfig();
			continueJoinEvent(e);
		} else if(getBuilders().containsKey(player.getUniqueId())) {
			if(!getBuilders().get(player.getUniqueId())) {
				continueJoinEvent(e);
			} else if(getBuilders().get(player.getUniqueId())) {
				player.teleport(parseStringToLoc(getConfig().getString("locations.spawn")));
				player.setGameMode(GameMode.SPECTATOR);
				e.setJoinMessage("");
				return;
			}
		}
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
		if(players.size() < getConfig().getInt("game.playerSize")) {
			players.add(player);
			event.setJoinMessage("§2[§cAure§eTechno§2] §l§a" + player.getDisplayName() + " §r§6viens de d'arriver dans la partie " + "(§l§a" + players.size() + "§r§a/" + getConfig().getInt("game.playerSize") + "§r§6) !");
			player.setGameMode(GameMode.SURVIVAL);
			player.getInventory().clear();
			player.updateInventory();
		}
	}
	
	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		if(players.contains(player)) {
			players.remove(player);
			event.setQuitMessage("§2[§cAure§eTechno§2] §l§a" + player.getDisplayName() + " §r§6viens de quitter la partie (§l§a" + players.size() + "§r§a/" + getConfig().getInt("game.playerSize") + "§r§6) !");
		}
	}
	
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		event.setFormat("%1$s §7>> §6%2$s");
	}


	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if(!getBuilders().containsKey(event.getPlayer().getUniqueId())) {
			playersProprieties.getConfig().createSection(event.getPlayer().getUniqueId().toString());
			playersProprieties.getConfig().set(event.getPlayer().getUniqueId().toString() + ".builder", false);
			playersProprieties.saveConfig();
			event.setCancelled(true);
		} else if(getBuilders().containsKey(event.getPlayer().getUniqueId())) {
			if(!getBuilders().get(event.getPlayer().getUniqueId())) {
				event.setCancelled(true);
			} else if(getBuilders().get(event.getPlayer().getUniqueId())) {
				event.setCancelled(false);
			}
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockPlaceEvent event) {
		if(!getBuilders().containsKey(event.getPlayer().getUniqueId())) {
			playersProprieties.getConfig().createSection(event.getPlayer().getUniqueId().toString());
			playersProprieties.getConfig().set(event.getPlayer().getUniqueId().toString() + ".builder", false);
			playersProprieties.saveConfig();
			event.setCancelled(true);
		} else if(getBuilders().containsKey(event.getPlayer().getUniqueId())) {
			if(!getBuilders().get(event.getPlayer().getUniqueId())) {
				event.setCancelled(true);
			} else if(getBuilders().get(event.getPlayer().getUniqueId())) {
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

	
}