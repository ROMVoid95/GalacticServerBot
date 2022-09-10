package com.readonlydev.commands.staff.suggestions;

import java.util.Arrays;

import com.readonlydev.BotData;
import com.readonlydev.command.slash.SlashCommand;
import com.readonlydev.command.slash.SlashCommandEvent;
import com.readonlydev.common.utils.ResultLevel;
import com.readonlydev.core.guildlogger.RootLogChannel;
import com.readonlydev.core.guildlogger.ServerSettings;
import com.readonlydev.database.entity.DBBlacklist;
import com.readonlydev.util.Check;
import com.readonlydev.util.discord.Reply;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class SuggestionBlacklist extends SlashCommand
{

    public SuggestionBlacklist()
    {
        this.name = "blacklist";
        this.help = "Add or remove a member from suggestions blacklist";
        this.options = Arrays.asList(
            new OptionData(OptionType.STRING, "action", "What action to take", true).addChoices(
                new Command.Choice("Add", "add"), 
                new Command.Choice("Remove", "remove")), 
            new OptionData(OptionType.USER, "user", "User", true), 
            new OptionData(OptionType.STRING, "reason", "reason for the action taken", true)
        );
    }

    @Override
    protected void execute(SlashCommandEvent event)
    {

        boolean canRun = Check.staffRoles(event);

        if (!canRun)
        {
            Reply.InvalidPermissions(event);
            return;
        }

        String channelId = BotData.database().botDatabase().getSuggestionOptions().getSuggestionsChannelId();
        TextChannel txtChannel = event.getGuild().getTextChannelById(channelId);

        if (!event.getChannel().asTextChannel().equals(txtChannel))
        {
            Reply.EphemeralReply(event, ResultLevel.ERROR, "command must be performed in " + txtChannel.getAsMention());
            return;
        }

        RootLogChannel logChannel = ((ServerSettings) event.getClient().getSettingsFor(event.getGuild())).getRootLogger();
        final User user = event.getOptionsByType(OptionType.USER).get(0).getAsUser();
        final String action = event.getOption("action").getAsString();
        final String reason = event.getOption("reason").getAsString();

        if (action.equals("add"))
        {
            boolean wasAdded = this.add(user.getId());
            if (wasAdded)
            {
                logChannel.sendBlacklistedLog(event.getJDA(), event.getMember(), action, user, reason);
                Reply.EphemeralReply(event, ResultLevel.SUCCESS, "User %s is now blacklisted".formatted(user.getAsMention()));
                return;
            } else
            {
                Reply.EphemeralReply(event, ResultLevel.ERROR, "User %s is already blacklisted".formatted(user.getAsMention()));
                return;
            }
        } else
        {
            boolean wasRemoved = this.remove(user.getId());
            if (wasRemoved)
            {
                logChannel.sendBlacklistedLog(event.getJDA(), event.getMember(), action, user, reason);
                Reply.EphemeralReply(event, ResultLevel.SUCCESS, "User %s is no longer blacklisted".formatted(user.getAsMention()));
                return;
            } else
            {
                Reply.EphemeralReply(event, ResultLevel.ERROR, "User %s is not blacklisted".formatted(user.getAsMention()));
                return;
            }
        }
    }

    private boolean add(String userId)
    {
        final DBBlacklist blackList = BotData.database().blacklist();
        return blackList.addToBlacklist(userId);
    }

    private boolean remove(String userId)
    {
        final DBBlacklist blackList = BotData.database().blacklist();
        return blackList.removeFromBlacklist(userId);
    }
}
