package com.readonlydev;

import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;

import com.readonlydev.database.DatabaseManager;
import com.readonlydev.database.Rethink;
import com.readonlydev.util.Factory;

import net.dv8tion.jda.api.entities.Guild;

public class BotData
{

    private static final ScheduledExecutorService galacticExec = Factory.newScheduledThreadPool(1, "Galactic-Thread-%d", false);
    private static final ScheduledExecutorService updateExec   = Factory.newScheduledThreadPool(1, "Galactic-Update-Thread-%d", false);
    private static DatabaseManager                db;

    public static Guild botDevServer()
    {
        return GalacticBot.getJda().getGuildById(538530739017220107L);
    }

    public static Guild botDevDevServer()
    {
        return GalacticBot.getJda().getGuildById(695532548410834964L);
    }

    public static Guild communityServer()
    {
        return GalacticBot.getJda().getGuildById(449966345665249290L);
    }

    public static Guild devServer()
    {
        return GalacticBot.getJda().getGuildById(775251052517523467L);
    }

    public static DatabaseManager database()
    {
        if (db == null)
        {
            db = new DatabaseManager(Rethink.connect());
        }

        return db;
    }

    public static ScheduledExecutorService updateExecutor()
    {
        return updateExec;
    }

    public static ScheduledExecutorService galacticExecutor()
    {
        return galacticExec;
    }

    public static void queue(Callable<?> action)
    {
        galacticExecutor().submit(action);
    }

    public static void queue(Runnable runnable)
    {
        galacticExecutor().submit(runnable);
    }
}
