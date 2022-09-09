package com.readonlydev.util.rec;

import java.util.Optional;

import com.readonlydev.util.discord.SuggestionEmbed;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

public record LinkedMessagesRecord(
    Optional<Message> postMsg, 
    Optional<Message> communityMsg, 
    Optional<Message> devMsg
)
{

    public MessageAction editMessages(SuggestionEmbed embed)
    {
        
        if(communityMsg().isPresent())
        {
            communityMsg().get().editMessageEmbeds(embed.toEmbedBuilder().build()).queue();
        }
        if(devMsg().isPresent())
        {
            devMsg().get().editMessageEmbeds(embed.toEmbedBuilder().build()).queue();
        }
        
        return postMsg().get().editMessageEmbeds(embed.toEmbedBuilder().build());
    }
    
    public AuditableRestAction<Void> deleteMessages()
    {
        if(communityMsg().isPresent())
        {
            communityMsg().get().delete().queue();
        }
        if(devMsg().isPresent())
        {
            devMsg().get().delete().queue();
        }
        
        return postMsg.get().delete();
    }
}
