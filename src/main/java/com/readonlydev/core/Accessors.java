package com.readonlydev.core;

import com.readonlydev.core.config.Config;
import com.readonlydev.util.data.JsonDataManager;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Accessors {

	private static JsonDataManager<Config> configInstance = getOrMake();
	
    private static JsonDataManager<Config> getOrMake() {
        if (configInstance == null) {
        	configInstance = new JsonDataManager<>(Config.class, "config.json", Config::new);
        }
        return configInstance;
    }
    
    public static JsonDataManager<Config> configManager() {
    	return configInstance;
    }
    
    public static Config config() {
    	return configManager().get();
    }
    
    public static Config.BotInfo botInfo() {
    	return config().getBotInfo();
    }
    
    public static Config.Secrets secrets() {
    	return config().getSecrets();
    }
    
    public static Config.StaffRoles staffRoles() {
    	return config().getStaffRoles();
    }
    
    public static Config.RethinkCredentials rethinkCredentials() {
    	return config().getRethink();
    }
}
