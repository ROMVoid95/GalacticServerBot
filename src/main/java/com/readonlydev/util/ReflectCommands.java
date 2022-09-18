package com.readonlydev.util;

import java.util.HashSet;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import com.google.common.collect.Sets;
import com.readonlydev.Conf;
import com.readonlydev.api.annotation.BotCommand;
import com.readonlydev.command.Command;
import com.readonlydev.command.CommandType;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ReflectCommands
{

    private static Reflections       reflections;
    private static HashSet<Class<?>> commandClasses;

    private static HashSet<Class<?>> commandClasses()
    {
        if (commandClasses == null)
        {
            commandClasses = Sets.newHashSet(reflections.getTypesAnnotatedWith(BotCommand.class));
        }
        return commandClasses;
    }

    public static final Set<Command> getConventionalCommands()
    {
        Set<Command> commandSet = new HashSet<>();
        for (Class<?> clazz : commandClasses())
        {
            BotCommand cmd = clazz.getAnnotation(BotCommand.class);
            if (cmd.value().equals(CommandType.CONVENTIONAL))
            {
                commandSet.add(newCommand(clazz));
            }
        }
        return commandSet;
    }

    private static final Command newCommand(Class<?> clazz)
    {
        try
        {
            return (Command) clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    static
    {
        reflections = new Reflections(Conf.Bot().getCommandPackages(), Scanners.values());
    }
}
