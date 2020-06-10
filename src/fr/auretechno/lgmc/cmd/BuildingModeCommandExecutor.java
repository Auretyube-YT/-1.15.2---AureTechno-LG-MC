package fr.auretechno.lgmc.cmd;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import fr.auretechno.lgmc.LGMain;

public class BuildingModeCommandExecutor implements CommandExecutor {

	private LGMain main;
	
	public BuildingModeCommandExecutor(LGMain main) {
		super();
		this.main = main;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(label.equalsIgnoreCase("building-mode")) {
			if(sender instanceof Player) {
				Player player = (Player) sender;
				if(player.isOp()) {
					if(args.length == 0 || args.length >= 2) {
						player.sendMessage(
								"§2[§cAure§eTechno§2] §4La commande s'utilise comme ça /building-mode <on|off> !");
					} else if(args.length == 1) {
						if(args[0] == null) {
							player.sendMessage("§2[§cAure§eTechno§2] §4La commande s'utilise comme ça /building-mode <on|off> !");
						} else if(args[0].equalsIgnoreCase("on")) {
							if(main.playersProprieties.getConfig().getConfigurationSection(player.getUniqueId().toString()) != null) {
								main.playersProprieties.getConfig().set(player.getUniqueId().toString() + ".builder", true);
								main.getBuilders().put(player.getUniqueId(), true);
								main.playersProprieties.saveConfig();
								main.playersProprieties.reloadConfig();
								player.sendMessage("§2[§cAure§eTechno§2] §6Building-mod activer !");
							} else {
								main.playersProprieties.getConfig().createSection(player.getUniqueId().toString());
								main.playersProprieties.getConfig().set(player.getUniqueId().toString() + ".builder", true);
								main.getBuilders().put(player.getUniqueId(), true);
								main.playersProprieties.saveConfig();
								main.playersProprieties.reloadConfig();
								player.sendMessage("§2[§cAure§eTechno§2] §6Building-mod activer !");
							}
						} else if(args[0].equalsIgnoreCase("off")) {
							if(main.playersProprieties.getConfig().getConfigurationSection(player.getUniqueId().toString()) != null) {
								main.playersProprieties.getConfig().set(player.getUniqueId().toString() + ".builder", false);
								main.getBuilders().put(player.getUniqueId(), false);
								main.playersProprieties.saveConfig();
								main.playersProprieties.reloadConfig();
								player.sendMessage("§2[§cAure§eTechno§2] §6Building-mod desactiver !");
	
							} else {
								main.playersProprieties.getConfig().createSection(player.getUniqueId().toString());
								main.playersProprieties.getConfig().set(player.getUniqueId().toString() + ".builder", false);
								main.getBuilders().put(player.getUniqueId(), false);
								main.playersProprieties.saveConfig();
								main.playersProprieties.reloadConfig();
								player.sendMessage("§2[§cAure§eTechno§2] §6Building-mod desactiver !");
							}
						}
					}	
				} else {
					player.sendMessage("§2[§cAure§eTechno§2] §4Tu n'as pas la permission espèce de chlag !");
				}
			}	else {
				sender.sendMessage("§2[§cAure§eTechno§2] §4Juste un joueur peut executer cette commande !");
				return false;
			}
		}
		return false;
	}
	
	public static class BuildingModeCommandTabCompleter implements TabCompleter {
		
		List<String> arguments = new ArrayList<String>();
		
		@Override
		public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
			if(arguments.isEmpty()) {
				arguments.add("on");
				arguments.add("off");
			}
			
			List<String> result = new ArrayList<String>();
			if(sender.isOp()) {
				if(args.length == 1) {
					for(String s : arguments) {
						if(s.toLowerCase().startsWith(args[0].toLowerCase())) {
							result.add(s);
						}
					}
					return result;
				}
			}
			return null;
		}
	}

}