package fr.auretechno.lgmc.cmd;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import fr.auretechno.lgmc.LGMain;

public class ReloadConfigCommand implements CommandExecutor {
	
	private LGMain main;
	
	public ReloadConfigCommand(LGMain main) {
		this.main = main;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(sender.isOp()) {
			main.reloadConfig();
			main.playersProprieties.reloadConfig();
			sender.sendMessage("§2[§cAure§eTechno§2] §aLa configuration du plugin est rechargée !");
			return false;
		} else {
			sender.sendMessage("§2[§cAure§eTechno§2] §4Tu n'as pas la permission espèce de chlag !");
			return true;
		}
	}

}