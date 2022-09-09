package com.readonlydev.commands.owner;

import com.readonlydev.BotData;
import com.readonlydev.api.annotation.BotCommand;
import com.readonlydev.command.event.CommandEvent;
import com.readonlydev.commands.core.AbstractCommand;

import net.dv8tion.jda.api.entities.Message;

@BotCommand
public class ShutdownCommand extends AbstractCommand
{
    Message message;

    public ShutdownCommand()
    {
        super("shutdown");
        aliases("abort");
        this.help = "safely shuts off the bot";
        this.guildOnly = false;
        this.ownerCommand = true;
    }

    @Override
    public void onExecute(CommandEvent event)
    {
        BotData.database().getConnection().close();
        event.getJDA().shutdown();
        System.exit(0);
    }

}
