package io.github.romvoid95.commands.member;

import java.util.function.Consumer;

import io.github.readonly.command.event.SlashCommandEvent;
import io.github.readonly.common.util.ResultLevel;
import io.github.romvoid95.commands.core.GalacticSlashCommand;
import io.github.romvoid95.util.Check;
import io.github.romvoid95.util.discord.Reply;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.interactions.InteractionHook;

public class CloseDiscussionThread extends GalacticSlashCommand
{

    public CloseDiscussionThread()
    {
        name("close");
        description("Closes the Discussion of the suggestion Thread called in");
    }

    @Override
    protected void onExecute(SlashCommandEvent event)
    {
        if (!event.getChannel().getType().equals(ChannelType.GUILD_PUBLIC_THREAD))
        {
            Reply.EphemeralReply(event, ResultLevel.ERROR, "This can only be run in Suggestion Discussion Threads!");
            return;
        }

        boolean canRun = Check.staffRoles(event);
        if (canRun == false)
        {
            canRun = Check.forSuggestionAuthor(event);
        }

        if (!canRun)
        {
            Reply.InvalidPermissions(event);
            return;
        }

        Consumer<InteractionHook> consumerLambda = (close) -> event.getChannel().asThreadChannel().getManager().setName("(CLOSED)").setArchived(true).setLocked(true).queue();

        Reply.EphemeralReply(event, "Discussion Thread has been closed", consumerLambda);
    }
}
