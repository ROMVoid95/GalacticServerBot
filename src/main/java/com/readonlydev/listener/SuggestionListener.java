package com.readonlydev.listener;

import javax.annotation.Nonnull;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class SuggestionListener extends ListenerAdapter {
	
	@Override
	public void onMessageReceived(@Nonnull MessageReceivedEvent event)
	{
		if(event.getChannel().getId().equals("946063816284848210")) {
			if(event.getMessage().getAuthor().equals(event.getJDA().getSelfUser())) {
				event.getMessage().createThreadChannel(event.getMessage().getEmbeds().get(0).getTitle()).queue();
			}
		}
	}
}
