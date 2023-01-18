package io.github.romvoid95.commands.member;

import com.github.readonlydevelopment.command.event.SlashCommandEvent;
import com.github.readonlydevelopment.common.utils.ResultLevel;

import io.github.romvoid95.commands.core.GalacticSlashCommand;
import io.github.romvoid95.util.Check;
import io.github.romvoid95.util.discord.Reply;
import net.dv8tion.jda.api.entities.channel.ChannelType;

public class CloseDiscussionThread extends GalacticSlashCommand
{
    
    public CloseDiscussionThread()
    {
        this.name = "close";
        this.help = "Closes the Discussion of the suggestion Thread called in";
    }

    @Override
    protected void onExecute(SlashCommandEvent event)
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
