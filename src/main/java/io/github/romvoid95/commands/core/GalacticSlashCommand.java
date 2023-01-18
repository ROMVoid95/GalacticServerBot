package io.github.romvoid95.commands.core;

import com.github.readonlydevelopment.command.SlashCommand;
import com.github.readonlydevelopment.command.event.SlashCommandEvent;
import com.github.readonlydevelopment.common.utils.ResultLevel;

import io.github.romvoid95.BotData;
import io.github.romvoid95.util.discord.Reply;

public abstract class GalacticSlashCommand extends SlashCommand
{

    @Override
    protected void execute(SlashCommandEvent event)
    {
        if(!event.getUser().getId().equals(event.getClient().getOwnerId()))
        {
            if(BotData.database().botDatabase().isMaintenanceMode())
            {
                Reply.EphemeralReply(event, ResultLevel.WARNING, "**Maintenance Mode Active**\n\nOnly the bot owner can issue commands at this time!");
                return;
            }
        }
        
        onExecute(event);
    }
    
    protected abstract void onExecute(SlashCommandEvent event);
}
