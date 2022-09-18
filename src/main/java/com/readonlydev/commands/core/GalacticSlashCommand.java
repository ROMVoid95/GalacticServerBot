package com.readonlydev.commands.core;

import com.readonlydev.BotData;
import com.readonlydev.command.slash.SlashCommand;
import com.readonlydev.command.slash.SlashCommandEvent;
import com.readonlydev.common.utils.ResultLevel;
import com.readonlydev.util.discord.Reply;

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
