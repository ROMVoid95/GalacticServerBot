package io.github.romvoid95.commands.owner;

import io.github.readonly.command.event.SlashCommandEvent;
import io.github.romvoid95.commands.core.GalacticSlashCommand;
import io.github.romvoid95.util.Check;
import io.github.romvoid95.util.discord.Reply;

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
