package com.readonlydev.util.entity;

import com.readonlydev.GalacticBot;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.requests.RestAction;

@Data
@NoArgsConstructor
public class ReferenceData {
	String addonChannelId;
	String messageId;
	
	public MessageChannel referencedChannel() {
		return GalacticBot.getJda().getTextChannelById(addonChannelId);
	}
	
	public RestAction<Message> getMessage() {
		return referencedChannel().retrieveMessageById(messageId);
	}
}
