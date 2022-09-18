package com.readonlydev.util.rec;

import java.util.Optional;

import com.readonlydev.util.discord.entity.SuggestionEmbed;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import net.dv8tion.jda.api.requests.restaction.MessageEditAction;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

public record LinkedMessagesRecord(
    Optional<Message> postMsg, 
    Optional<Message> communityMsg, 
    Optional<Message> devMsg
)
{

    public MessageEditAction editMessages(SuggestionEmbed embed)
    {
        MessageEditBuilder builder = new MessageEditBuilder()
            .setEmbeds(embed.toEmbedBuilder().build());
        
        MessageEditData editData = builder.build();
        
        if(communityMsg().isPresent())
        {
            communityMsg().get().editMessage(editData).queue();
        }
        if(devMsg().isPresent())
        {
            devMsg().get().editMessage(editData).queue();
        }
        
        return postMsg().get().editMessage(editData);
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
