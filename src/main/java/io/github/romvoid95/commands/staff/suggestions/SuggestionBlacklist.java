package io.github.romvoid95.commands.staff.suggestions;

import io.github.readonly.command.event.SlashCommandEvent;
import io.github.readonly.command.lists.ChoiceList;
import io.github.readonly.command.option.Choice;
import io.github.readonly.command.option.RequiredOption;
import io.github.readonly.common.util.ResultLevel;
import io.github.romvoid95.BotData;
import io.github.romvoid95.commands.core.GalacticSlashCommand;
import io.github.romvoid95.core.guildlogger.RootLogChannel;
import io.github.romvoid95.core.guildlogger.ServerSettings;
import io.github.romvoid95.database.entity.DBBlacklist;
import io.github.romvoid95.util.Check;
import io.github.romvoid95.util.discord.Reply;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.commands.OptionType;

public class SuggestionBlacklist extends GalacticSlashCommand
{

    public SuggestionBlacklist()
    {
        super("blacklist", "Add or remove a member from suggestions blacklist");
        options(RequiredOption.text("action", "What action to take", ChoiceList.of(Choice.add("Add"), Choice.add("Remove"))), RequiredOption.user("user", "User"), RequiredOption.text("reason", "reason for the action taken"));
    }

    @Override
    protected void onExecute(SlashCommandEvent event)
    {

        boolean canRun = Check.staffRoles(event);

        if (!canRun)
        {
            Reply.InvalidPermissions(event);
            return;
        }

        String      channelId  = BotData.database().galacticBot().getSuggestionOptions().getSuggestionsChannelId();
        TextChannel txtChannel = event.getGuild().getTextChannelById(channelId);

        if (!event.getChannel().asTextChannel().equals(txtChannel))
        {
            Reply.EphemeralReply(event, ResultLevel.ERROR, "command must be performed in " + txtChannel.getAsMention());
            return;
        }

        RootLogChannel logChannel = ((ServerSettings) event.getClient().getSettingsFor(event.getGuild())).getRootLogger();
        final User     user       = event.getOptionsByType(OptionType.USER).get(0).getAsUser();
        final String   action     = event.getOption("action").getAsString();
        final String   reason     = event.getOption("reason").getAsString();

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
