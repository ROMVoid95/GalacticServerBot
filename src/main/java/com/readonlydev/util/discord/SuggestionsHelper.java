package com.readonlydev.util.discord;

import java.util.List;
import java.util.stream.Collectors;

import com.readonlydev.BotData;
import com.readonlydev.GalacticBot;
import com.readonlydev.database.entity.DBGalacticBot;
import com.readonlydev.database.impl.Suggestion;
import com.readonlydev.database.impl.Suggestion.LinkedMessages;
import com.readonlydev.database.impl.options.SuggestionOptions;

import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

@UtilityClass
public class SuggestionsHelper
{
    
    public List<String> getAllAuthors() 
    {
        DBGalacticBot db = BotData.database().botDatabase();
        return db.getManager().getList().stream().map(Suggestion::getAuthorId).collect(Collectors.toList());
    }

    public static Message setStatus(SuggestionStatus status, Suggestion suggestion)
    {
        LinkedMessages messages = suggestion.getMessages();
        Message populatPostMsg = null;
        
        if(messages.postMsg().isPresent())
        {
            setStatusOnMessage(messages.postMsg().get(), status);
            
            if(messages.communityPopularMsg().isPresent())
            {
                setStatusOnMessage(messages.communityPopularMsg().get(), status);
                
                if(messages.devPopularMsg().isPresent())
                {
                    setStatusOnMessage(messages.devPopularMsg().get(), status);
                    populatPostMsg = messages.communityPopularMsg().get();
                }
            }
        }
        
        return populatPostMsg;
    }
    
    private static void setStatusOnMessage(Message message, SuggestionStatus status)
    {
        MessageEmbed suggestionEmbed = message.getEmbeds().get(0);
        //@noformat
        EmbedBuilder builder = new EmbedBuilder()
            .setTitle(suggestionEmbed.getTitle())
            .setAuthor(suggestionEmbed.getAuthor().getName())
            .setDescription(suggestionEmbed.getDescription())
            .setColor(status.getColor())
            .addField(suggestionEmbed.getFields().get(0))
            .addField(status.getStatusEmbedField());
        //@format
        
        message.editMessageEmbeds(builder.build()).queue();
    }

    public static void handleSuggestionUpvoteEvent(MessageReactionAddEvent event)
    {
       DBGalacticBot suggestions = BotData.database().botDatabase();

        if (suggestions.getAllSuggestionMessageIds().contains(event.getMessageId()))
        {
            Suggestion suggestion = suggestions.getSuggestionFromMessageId(event.getMessageId());
            Message message = DiscordUtils.getMessageOrNull(event);

            if (message != null)
            {
                int reactionCount = message.getReactions().get(0).getCount() - 1;
                suggestion.setUpvotes(reactionCount);
                suggestions.saveUpdateAsync();

                SuggestionOptions options = BotData.database().botDatabase().getSuggestionOptions();

                if (suggestion.getUpvotes() == options.getStarRequirement())
                {
                    TextChannel popularChannel = GalacticBot.getJda().getTextChannelById(options.getPopularChannelId());
                    TextChannel devPopularChannel = GalacticBot.getJda().getTextChannelById(options.getDevServerPopularChannelId());
                    
                    popularChannel.sendMessageEmbeds(message.getEmbeds().get(0)).queue(s1 -> {
                        String communityServerMsgId = s1.getId();
                        devPopularChannel.sendMessageEmbeds(message.getEmbeds().get(0)).queue(s2 -> {
                            String devServerMsgId = s2.getId();
                            suggestions.addNewPopularLinkedMessages(communityServerMsgId, devServerMsgId, suggestion);
                        });
                    });
                }
            }
        }
    }
}
