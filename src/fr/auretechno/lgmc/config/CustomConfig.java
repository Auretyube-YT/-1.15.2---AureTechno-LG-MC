package fr.auretechno.lgmc.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import fr.auretechno.lgmc.LGMain;

public class CustomConfig {

	private String configName;
	private LGMain main;
	private FileConfiguration config;
	private File file = null;

	public CustomConfig(LGMain main, String configName) {
		this.configName = configName;
		this.main = main;
	}

	public void reloadConfig() {
		if (this.file == null) {
			file = new File(main.getDataFolder(), configName + ".yml");
		}

		config = YamlConfiguration.loadConfiguration(file);

		InputStream defaultReader = main.getResource(configName + ".yml");
		if (defaultReader != null) {
			YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultReader));
			config.setDefaults(defaultConfig);
		}
	}

	public void saveConfig() {
		if (config == null || file == null)
			return;

		try {
			getConfig().save(file);
		} catch (IOException e) {
			main.sendError(e);
		}
	}

	public void saveDefaultConfig() {
		if(file == null) file = new File(main.getDataFolder(), configName + ".yml");
		
		if(!file.exists()) {
			main.saveResource(configName + ".yml", false);
		}
	}

	public FileConfiguration getConfig() {
		if (config == null) {
			reloadConfig();
		}
		return config;
	}

	public String getConfigName() {
		return configName;
	}

}