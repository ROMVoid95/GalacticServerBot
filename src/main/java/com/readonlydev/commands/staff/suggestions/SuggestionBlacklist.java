package com.readonlydev.commands.staff.suggestions;

import java.util.Arrays;

import com.readonlydev.BotData;
import com.readonlydev.command.slash.SlashCommand;
import com.readonlydev.command.slash.SlashCommandEvent;
import com.readonlydev.core.guildlogger.RootLogChannel;
import com.readonlydev.core.guildlogger.ServerSettings;
import com.readonlydev.database.entity.DBBlacklist;
import com.readonlydev.util.Check;
import com.readonlydev.util.discord.Reply;

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
            new OptionData(OptionType.STRING, "user_id", "the Users ID", true),
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

        RootLogChannel logChannel = ((ServerSettings) event.getClient().getSettingsFor(event.getGuild())).getRootLogger();
        final String userId = event.getOption("user_id").getAsString();
        final String action = event.getOption("action").getAsString();
        final String reason = event.getOption("reason").getAsString();
        
        if(action.equals("add"))
        {
            boolean wasAdded = this.add(userId);
            if(wasAdded)
            {
                logChannel.sendBlacklistedLog(event.getJDA(), event.getMember(), action, userId, reason);
                User user = event.getJDA().getUserById(userId);
                if(user != null)
                {
                    Reply.Success(event, "User %s is now blacklisted".formatted(user.getAsMention()));
                    return;
                } else {
                    Reply.Success(event, "User with ID `%s` is now blacklisted".formatted(userId));
                    return;
                }
            } else {
                User user = event.getJDA().getUserById(userId);
                if(user != null)
                {
                    Reply.Error(event, "User %s is already blacklisted".formatted(user.getAsMention()));
                    return;
                } else {
                    Reply.Error(event, "User with ID `%s`  is already blacklisted".formatted(userId));
                    return;
                }
            }
        }
        
        if(action.equals("remove"))
        {
            boolean wasRemoved = this.remove(userId);
            if(wasRemoved)
            {
                logChannel.sendBlacklistedLog(event.getJDA(), event.getMember(), action, userId, reason);
                User user = event.getJDA().getUserById(userId);
                if(user != null)
                {
                    Reply.Success(event, "User %s is no longer blacklisted".formatted(user.getAsMention()));
                    return;
                } else {
                    Reply.Success(event, "User with ID `%s` is no longer blacklisted".formatted(userId));
                    return;
                }
            } else {
                User user = event.getJDA().getUserById(userId);
                if(user != null)
                {
                    Reply.Error(event, "User %s is not blacklisted".formatted(user.getAsMention()));
                    return;
                } else {
                    Reply.Error(event, "User with ID `%s`  is not blacklisted".formatted(userId));
                    return;
                }
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
