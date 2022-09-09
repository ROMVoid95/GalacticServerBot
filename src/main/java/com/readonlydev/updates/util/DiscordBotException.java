package com.readonlydev.updates.util;

public class DiscordBotException extends RuntimeException
{

    private static final long serialVersionUID = 7532472787467902055L;

    public DiscordBotException(String message)
    {
        super(message);
    }
    
    public DiscordBotException(String message, Throwable e)
    {
        super(message, e);
    }
}
