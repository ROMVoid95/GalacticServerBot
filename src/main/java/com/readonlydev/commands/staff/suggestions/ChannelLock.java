package com.readonlydev.commands.staff.suggestions;

import java.util.Collection;
import java.util.EnumSet;

import com.readonlydev.BotData;
import com.readonlydev.command.slash.SlashCommandEvent;
import com.readonlydev.commands.core.GalacticSlashCommand;
import com.readonlydev.database.entity.DBGalacticBot;
import com.readonlydev.util.Check;
import com.readonlydev.util.discord.Reply;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class ChannelLock extends GalacticSlashCommand
{

    public ChannelLock()
    {
        this.name = "lock-channel";
        this.help = "Locks and prevents new Suggestions in the current Suggestions channel";
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

        final DBGalacticBot db = BotData.database().botDatabase();
        boolean suggestionsLocked = db.getSuggestionOptions().isSuggestionsLocked();

        Role memberRole = event.getGuild().getRolesByName("Astronaut", false).get(0);

        Collection<Permission> toDeny = EnumSet.of(Permission.MESSAGE_SEND, Permission.USE_APPLICATION_COMMANDS);
        Collection<Permission> alwaysAllow = EnumSet.of(Permission.MESSAGE_HISTORY, Permission.MESSAGE_SEND_IN_THREADS);

        TextChannel suggestionChannel = event.getJDA().getTextChannelById(db.getSuggestionOptions().getSuggestionChannel());
        suggestionChannel.getManager().putPermissionOverride(memberRole, alwaysAllow, toDeny).queue(s ->
        {
            if (suggestionsLocked)
            {
                Reply.Success(event,"No operations performed: Suggestions channel is already LOCKED");
                return;
            }

            db.getSuggestionOptions().setSuggestionsLocked(true);
            db.saveUpdating();
            Reply.Success(event, "Sucessfully LOCKED Suggestions Channel");
        }, f ->
        {
            Reply.Error(event, "An Error occured while setting Role Permission overrides");
        });
    }
}
