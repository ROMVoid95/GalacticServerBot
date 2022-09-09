package com.readonlydev.commands.member;

import com.readonlydev.command.slash.SlashCommand;
import com.readonlydev.command.slash.SlashCommandEvent;
import com.readonlydev.common.utils.ResultLevel;
import com.readonlydev.util.Check;
import com.readonlydev.util.discord.Reply;

import net.dv8tion.jda.api.entities.ChannelType;

public class CloseDiscussionThread extends SlashCommand
{
    
    public CloseDiscussionThread()
    {
        this.name = "close";
        this.help = "Closes the Discussion of the suggestion Thread called in";
    }

    @Override
    protected void execute(SlashCommandEvent event)
    {
        if(!event.getChannel().getType().equals(ChannelType.GUILD_PUBLIC_THREAD))
        {
            Reply.EphemeralReply(event, ResultLevel.ERROR, "This can only be run in Suggestion Discussion Threads!");
            return;
        }
        
        boolean canRun = Check.staffRoles(event);
        if(canRun == false)
        {
            canRun = Check.forSuggestionAuthor(event);
        }

        if (!canRun)
        {
            Reply.InvalidPermissions(event);
            return;
        }
        
        Reply.EphemeralReplyCallback(event, "Discussion Thread has been closed").queue(hook -> {
            event.getChannel().asThreadChannel().getManager().setName("Discussion (CLOSED)").setArchived(true).setLocked(true).queue();
        });;
        
        
    }
}
