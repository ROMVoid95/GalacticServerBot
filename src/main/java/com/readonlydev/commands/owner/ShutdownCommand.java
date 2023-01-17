package com.readonlydev.commands.owner;

import com.readonlydev.command.slash.SlashCommandEvent;
import com.readonlydev.commands.core.GalacticSlashCommand;
import com.readonlydev.util.Check;
import com.readonlydev.util.discord.Reply;

public class ShutdownCommand extends GalacticSlashCommand
{
    public ShutdownCommand()
    {
        this.name = "shutdown";
        this.help = "safely shuts off the bot";
    }
    
    @Override
    protected void execute(SlashCommandEvent event)
    {
        if(!Check.isOwner(event))
        {
            Reply.InvalidPermissions(event);
            return;
        }
        super.execute(event);
    }

    @Override
    public void onExecute(SlashCommandEvent event)
    {
        System.exit(0);
    }

}
