package fr.auretechno.lgmc;

import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.google.common.collect.Maps;

public class RoleManager {

	public static Map<Player, Role> roles = Maps.newHashMap();

	public static void randomRole(List<Player> players) {
		Player thief = players.get(new Random().nextInt(players.size()));
		players.remove(thief);
		roles.put(thief, Role.THIEF);

		Player cupid = players.get(new Random().nextInt(players.size()));
		players.remove(cupid);
		roles.put(cupid, Role.CUPID);

		Player clairvoyant = players.get(new Random().nextInt(players.size()));
		players.remove(clairvoyant);
		roles.put(clairvoyant, Role.CLAIRVOYANT);

		Player WEREWOLF = players.get(new Random().nextInt(players.size()));
		players.remove(WEREWOLF);
		roles.put(WEREWOLF, Role.WEREWOLF);
		Player WEREWOLF1 = players.get(new Random().nextInt(players.size()));
		players.remove(WEREWOLF1);
		roles.put(WEREWOLF1, Role.WEREWOLF);

		Player witch = players.get(new Random().nextInt(players.size()));
		players.remove(witch);
		roles.put(witch, Role.WITCH);

		Player villager = players.get(new Random().nextInt(players.size()));
		players.remove(villager);
		roles.put(villager, Role.VILLAGER);

		Player hunter = players.get(new Random().nextInt(players.size()));
		players.remove(hunter);
		roles.put(hunter, Role.HUNTER);
	}

	public static enum Role {

		THIEF("§3Voleur"), CUPID("§5Cupidon"), CLAIRVOYANT("§dVoyante"), WEREWOLF("§4Loup-Garou"), WITCH("§2Sorcière"),
		HUNTER("§aChasseur"), VILLAGER("§eVillageois");

		private String name;

		private Role(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}

	private static int headTarget = 10;
	public static Inventory thiefInventory = Bukkit.createInventory(null, 54, "§8Choisis une personne >> Voleur");

	
	@SuppressWarnings("deprecation")
	public static void openThiefInventory(Player player) {
		ItemStack glass_air = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
		glass_air.getItemMeta().setDisplayName("");
		roles.forEach((p, role) -> {
			ItemStack head = new ItemStack(Material.PLAYER_HEAD);
			SkullMeta skull = (SkullMeta) head.getItemMeta();
			skull.setOwner(p.getDisplayName());
			skull.setDisplayName(p.getDisplayName());
			head.setItemMeta(skull);
			thiefInventory.setItem(headTarget, head);
			headTarget++;
		});
		for(int i = 0; i < 9; i++) {
			thiefInventory.setItem(i, glass_air);
		}
		for(int i = 1; i < 5; i++) {
			thiefInventory.setItem(i * 9, glass_air);
		}
		for(int i = 1; i < 5; i++) {
			thiefInventory.setItem(i * 9 + 8, glass_air);
		}
		for(int i = 45; i < thiefInventory.getSize(); i++) {
			thiefInventory.setItem(i, glass_air);
		}
		player.openInventory(thiefInventory);
	}


	public static void inverseRole(Player player, Player player2) {
		Role role1 = roles.get(player);
		Role role2 = roles.get(player2);
		
		roles.replace(player, role2);
		roles.replace(player2, role1);
	}
}