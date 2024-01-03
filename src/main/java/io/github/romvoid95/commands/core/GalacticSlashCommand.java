package io.github.romvoid95.commands.core;

import io.github.readonly.command.SlashCommand;
import io.github.readonly.command.event.SlashCommandEvent;
import io.github.readonly.common.util.ResultLevel;
import io.github.romvoid95.BotData;
import io.github.romvoid95.GalacticBot;
import io.github.romvoid95.util.Check;
import io.github.romvoid95.util.discord.Reply;

public abstract class GalacticSlashCommand extends SlashCommand
{

    protected GalacticSlashCommand(String name)
    {
        super(name);
    }
    
    protected GalacticSlashCommand(String name, String description)
    {
        super(name, description);
    }

    @Override
    protected void execute(SlashCommandEvent event)
    {
    	if(GalacticBot.instance().isDevBot() && !Check.isOwner(event))
    	{
    		Reply.EphemeralReply(event, ResultLevel.WARNING, "Only the Bot Developer can use the Dev instance of the bot");
    		return;
    	}
    	
    	if(!GalacticBot.instance().isDevBot() && BotData.database().galacticBot().isMaintenanceMode() && !Check.isOwner(event))
    	{
    		Reply.EphemeralReply(event, ResultLevel.WARNING, "**Maintenance Mode Active**\n\nOnly the bot owner can issue commands at this time!");
            return;
    	}

        onExecute(event);
    }
    
    protected abstract void onExecute(SlashCommandEvent event);
}
