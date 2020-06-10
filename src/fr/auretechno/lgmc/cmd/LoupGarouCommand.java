package fr.auretechno.lgmc.cmd;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.auretechno.lgmc.LGMain;
import fr.auretechno.lgmc.RoleManager;
import fr.auretechno.lgmc.RoleManager.Role;

public class LoupGarouCommand implements CommandExecutor {

	private LGMain main;

	public LoupGarouCommand(LGMain main) {
		this.main = main;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (player.isOp()) {
				if (args.length == 0) {
					player.sendMessage("§2[§cAure§eTechno§2] §4La commande est incorect ! \n"
							+ "§r- §6/lg roles set <playerName> <role> \n"
							+ "§r- §6/lg roles inverse <playerName> <secPlayerName>");
				}
				if(args.length > 0) {
					if(args[0].equalsIgnoreCase("roles")) {
						if(args[1].equalsIgnoreCase("set")) {
							if(args[2] != null && args[3] != null) {
								 RoleManager.roles.replace(Bukkit.getPlayer(args[2]), Role.valueOf(args[3]));
							} else {
								player.sendMessage("§2[§cAure§eTechno§2] §4La commande est incorect ! \n"
										+ "§r- §6/lg roles set <playerName> <role> \n"
										+ "§r- §6/lg roles inverse <playerName> <secPlayerName>");
							}
						} else if(args[1].equalsIgnoreCase("inverse")) {
							if(args[2] != null && args[3] != null) {
								 RoleManager.inverseRole(Bukkit.getPlayer(args[2]), Bukkit.getPlayer(args[3]));
							} else {
								player.sendMessage("§2[§cAure§eTechno§2] §4La commande est incorect ! \n"
										+ "§r- §6/lg roles set <playerName> <role> \n"
										+ "§r- §6/lg roles inverse <playerName> <secPlayerName>");
							}
						}
						else {
							player.sendMessage("§2[§cAure§eTechno§2] §4La commande est incorect ! \n"
									+ "§r- §6/lg roles set <playerName> <role> \n"
									+ "§r- §6/lg roles inverse <playerName> <secPlayerName>");
						}
					} else {
						player.sendMessage("§2[§cAure§eTechno§2] §4La commande est incorect ! \n"
								+ "§r- §6/lg roles set <playerName> <role> \n"
								+ "§r- §6/lg roles inverse <playerName> <secPlayerName>");
					}
				}
			} else {
				sender.sendMessage("§2[§cAure§eTechno§2] §4Tu n'as pas la permission espèce de chlag !");
				return true;
			}
		} else {
			sender.sendMessage("§2[§cAure§eTechno§2] §4Juste un joueur peut executer cette commande !");
			return true;
		}
		return false;
	}

}
