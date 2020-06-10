package fr.auretechno.lgmc.game;

public enum GameState {
	
	WAITING("§6Attente", false),
	STARTING("§aLancement", false),
	PLAYING("§aJouer", false),
	DAY("§eJour", false),
	START_NIGHT("§cNuit", false),
	THIEF_TURN("§cNuit §3Voleur", true),
	CUPID_TURN("§cNuit §5Cupidon", true),
	LOVERS_TURN("§cNuit §cAmoureux", true),
	CLAIRVOYANT_TURN("§cNuit §dVoyante", false),
	WEREWOLF_TURN("§cNuit §4Loup-Garou", false),
	WITCH_TURN("§cNuit §2Sorcière", false),
	FINISH("§cFin", false);
	
	private String name;
	private boolean onlyOne;
	
	private GameState(String name, boolean onlyOne) {
		this.name = name;
		this.onlyOne = onlyOne;
	}
	
	public boolean isOnlyOne() {
		return onlyOne;
	}
	
	public String getName() {
		return name;
	}
}