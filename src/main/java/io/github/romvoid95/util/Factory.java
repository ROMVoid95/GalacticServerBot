package io.github.romvoid95.util;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Factory
{
    
    public static ScheduledExecutorService newScheduledThreadPool(int poolSize, String threadName, boolean isDaemon)
    {
        return Executors.newScheduledThreadPool(poolSize, newThreadFactory(threadName, isDaemon));
    }
    
    public static ScheduledExecutorService newUpdateThread()
    {
    	return Executors.newScheduledThreadPool(1, r -> setThreadDaemon(new Thread(r, "UpdateChecker"), true));
    }

    public static ThreadFactory newThreadFactory(String threadName, boolean isdaemon)
    {
        return (r) ->
        {
            Thread t = new Thread(r, threadName);
            t.setDaemon(isdaemon);
            t.setUncaughtExceptionHandler((final Thread thread, final Throwable throwable) ->
                    log.error("There was a uncaught exception in the {} threadpool", thread.getName(), throwable));
            return t;
        };
    }
    
    private static Thread setThreadDaemon(final Thread thread, final boolean isDaemon) {
        thread.setDaemon(isDaemon);
        return thread;
    }
}
