package com.readonlydev.database;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import lombok.experimental.UtilityClass;

@UtilityClass
public class DatabaseAccessor {
	private static ManagedDatabase managedDatabase;
	
    public static ManagedDatabase database() {
        if (managedDatabase == null) {
        	managedDatabase = new ManagedDatabase();
        }
        return managedDatabase;
    }
    
    @UtilityClass
    public static class Scheduled {
        private static final ScheduledExecutorService exec = Executors.newScheduledThreadPool(
                1, new ThreadFactoryBuilder().setNameFormat("JDABot-Executor Thread-%d").build()
        );
    
        public static void queue(Callable<?> action) {
        	exec.submit(action);
        }

        public static void queue(Runnable runnable) {
        	exec.submit(runnable);
        }
    }
}
