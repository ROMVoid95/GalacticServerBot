package com.readonlydev.core;

import javax.annotation.Nonnull;

import com.readonlydev.BotData;
import com.readonlydev.database.impl.options.ServerOptions;
import com.readonlydev.database.impl.options.SuggestionOptions;
import com.readonlydev.util.discord.DiscordUtils;
import com.readonlydev.util.discord.SuggestionsHelper;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageType;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@Slf4j
public class GalacticEventListener extends ListenerAdapter
{

    @Override
    public void onReady(@Nonnull ReadyEvent event)
    {
        event.getJDA().getGuilds().forEach(guild ->
        {
            ServerOptions options = BotData.database().botDatabase().createServerOptionsIfMissing(guild);
            if (options == null)
            {
                log.error("An error occured initializing ServerOptions for guild %s [%s]".formatted(guild.getName(), guild.getId()));
            } else
            {
                log.info("ServerOptions initialized for guild %s [%s]".formatted(guild.getName(), guild.getId()));
            }
        });
    }

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event)
    {
        if (DiscordUtils.areGuildsTheSame(event.getGuild(), BotData.communityServer()))
        {
            SuggestionOptions options = BotData.database().botDatabase().getSuggestionOptions();
            MessageChannel channel = event.getChannel();
            
            if(options.getSuggestionsChannelId().equals(channel.getId()))
            {
                MessageType type = event.getMessage().getType();
                if(type != MessageType.SLASH_COMMAND)
                {
                    event.getMessage().delete().queue();
                }
            }
        }
    }

    @Override
    public void onMessageReactionAdd(@Nonnull MessageReactionAddEvent event)
    {
        if (!event.getUser().isBot())
        {
            SuggestionsHelper.handleSuggestionUpvoteEvent(event);
        }
    }
}
