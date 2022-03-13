package com.readonlydev;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.readonlydev.config.Config;
import com.readonlydev.database.ManagedDatabase;
import com.readonlydev.database.wrapper.Rethink;
import com.readonlydev.util.data.JsonDataManager;

public class BotData {

    private static final ScheduledExecutorService exec = Executors.newScheduledThreadPool(
            1, new ThreadFactoryBuilder().setNameFormat("GCBot-Executor Thread-%d").build()
    );
	private static JsonDataManager<Config> config;
	private static ManagedDatabase db;

    private static JsonDataManager<Config> conf() {
        if (config == null) {
            config = new JsonDataManager<>(Config.class, "config.json", Config::new);
        }

        return config;
    }
    
    public static Config config() {
    	return conf().get();
    }
    
    public static JsonDataManager<Config> configManager() {
    	return conf();
    }
    
    public static ManagedDatabase database() {
        if (db == null) {
            db = new ManagedDatabase(Rethink.connect());
        }

        return db;
    }

    public static ScheduledExecutorService getExecutor() {
        return exec;
    }

    public static void queue(Callable<?> action) {
        getExecutor().submit(action);
    }

    public static void queue(Runnable runnable) {
        getExecutor().submit(runnable);
    }
}
