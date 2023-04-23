package io.github.romvoid95.core;

import io.github.romvoid95.BotData;
import io.github.romvoid95.GalacticBot;
import io.github.romvoid95.Servers;
import io.github.romvoid95.database.impl.options.SuggestionOptions;
import io.github.romvoid95.server.Server;
import io.github.romvoid95.util.discord.SuggestionsHelper;
import net.dv8tion.jda.api.entities.MessageType;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class GalacticEventListener extends ListenerAdapter
{
	@Override
	public void onMessageReceived(MessageReceivedEvent event)
	{
		if(event.getChannelType().isGuild())
		{
			if (!GalacticBot.instance().isDevBot())
			{
				if (Server.of(event.getGuild()).equals(Servers.galacticraftCentral))
				{
					SuggestionOptions	options	= BotData.database().galacticBot().getSuggestionOptions();
					MessageChannel		channel	= event.getChannel();

					if (options.getSuggestionsChannelId().equals(channel.getId()))
					{
						MessageType type = event.getMessage().getType();
						if (type != MessageType.SLASH_COMMAND)
						{
							event.getMessage().delete().queue();
						}
					}
				}
			}
		}
	}

	@Override
	public void onMessageReactionAdd(MessageReactionAddEvent event)
	{
		if (!GalacticBot.instance().isDevBot())
		{
			if (!event.getUser().isBot())
			{
				SuggestionsHelper.handleSuggestionUpvoteEvent(event);
			}
		}
	}
}
