package fr.auretechno.lgmc.tasks;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import fr.auretechno.lgmc.LGMain;
import fr.auretechno.lgmc.game.GameState;

public class StartGameTask extends BukkitRunnable {

	private LGMain main;

	public StartGameTask(LGMain main) {
		this.main = main;
	}

	private int timer = 15;

	@Override
	public void run() {
		if (main.isState(GameState.STARTING)) {

			if (timer == 15 || timer == 10 || timer == 5 || timer < 5 && timer > 0) {
				Bukkit.broadcastMessage(
						"§2[§cAure§eTechno§2] §6La partie commence dans " + timer + " §r§6seconde(s) !");
				for (Player player : main.players) {
					if (timer == 15 || timer == 10) {
						player.sendTitle("§6" + timer, "", 10, 20, 10);
						player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 4, 0);
					}
					if(timer == 5 || timer < 5 && timer > 0) {
						player.sendTitle("§c" + timer, "", 10, 20, 10);
						player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 4, 0);
					}
				}
			}
			if (timer == 0) {
				Bukkit.broadcastMessage("§2[§cAure§eTechno§2] §6Let's go !");
				main.setState(GameState.PLAYING);
				main.setGamePlaying();
			}

			timer--;
		}
		if (!main.isState(GameState.STARTING)) {
			cancel();
		}
	}

}