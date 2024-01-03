package io.github.romvoid95.commands.staff.suggestions;

import java.util.Collection;
import java.util.EnumSet;

import io.github.readonly.command.event.SlashCommandEvent;
import io.github.romvoid95.BotData;
import io.github.romvoid95.commands.core.GalacticSlashCommand;
import io.github.romvoid95.database.entity.DBGalacticBot;
import io.github.romvoid95.util.Check;
import io.github.romvoid95.util.discord.Reply;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class ChannelUnlock extends GalacticSlashCommand
{

    public ChannelUnlock()
    {
        super("unlock-channel", "Unlocks and sllows new Suggestions in the current Suggestions channel");
    }

    @Override
    protected void onExecute(SlashCommandEvent event)
    {

        boolean canRun = Check.adminRoles(event);

        if (!canRun)
        {
            Reply.InvalidPermissions(event);
            return;
        }

        final DBGalacticBot db                = BotData.database().galacticBot();
        boolean             suggestionsLocked = db.getSuggestionOptions().isSuggestionsLocked();

        Role memberRole = event.getGuild().getRolesByName("Astronaut", false).get(0);

        Collection<Permission> toAllow = EnumSet.of(Permission.MESSAGE_SEND, Permission.USE_APPLICATION_COMMANDS, Permission.MESSAGE_HISTORY, Permission.MESSAGE_SEND_IN_THREADS);

        TextChannel suggestionChannel = db.getSuggestionOptions().getSuggestionChannel();
        suggestionChannel.getManager().putPermissionOverride(memberRole, toAllow, null).queue(s ->
        {
            if (!suggestionsLocked)
            {
                Reply.Success(event, "No operations performed: Suggestions channel is already UNLOCKED");
                return;
            }

            db.getSuggestionOptions().setSuggestionsLocked(false);
            db.saveUpdating();
            Reply.Success(event, "Sucessfully UNLOCKED Suggestions Channel");
        }, f ->
        {
            Reply.Error(event, "An Error occured while setting Role Permission overrides");
        });
    }
}
