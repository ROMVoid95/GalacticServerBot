package io.github.romvoid95.util.discord;

import java.util.List;
import java.util.stream.Collectors;

import com.github.readonlydevelopment.common.utils.ResultLevel;

import io.github.romvoid95.BotData;
import io.github.romvoid95.GalacticBot;
import io.github.romvoid95.database.entity.DBGalacticBot;
import io.github.romvoid95.database.impl.Suggestion;
import io.github.romvoid95.database.impl.Suggestion.LinkedMessages;
import io.github.romvoid95.database.impl.options.SuggestionOptions;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;

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

        if (messages.postMsg().isPresent())
        {
            setStatusOnMessage(messages.postMsg().get(), status);

            if (messages.communityPopularMsg().isPresent())
            {
                setStatusOnMessage(messages.communityPopularMsg().get(), status);

                if (messages.devPopularMsg().isPresent())
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

    public static void handleSuggestionDownvoteEvent(MessageReactionRemoveEvent event)
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
                suggestions.saveUpdating();
            }
        }
    }

    public static void handleSuggestionUpvoteEvent(MessageReactionAddEvent event)
    {
        DBGalacticBot database = BotData.database().botDatabase();

        if (database.getAllSuggestionMessageIds().contains(event.getMessageId()))
        {
            Suggestion suggestion = database.getSuggestionFromMessageId(event.getMessageId());
            Message message = DiscordUtils.getMessageOrNull(event);

            if (event.getUser().getId().equals(suggestion.getAuthorId()))
            {
                event.retrieveMessage().flatMap((m) -> m.removeReaction(event.getEmoji(), event.getUser())).queue();
            }

            if (message != null && !event.getUser().getId().equals(suggestion.getAuthorId()))
            {
                int reactionCount = message.getReactions().get(0).getCount() - 1;
                suggestion.setUpvotes(reactionCount);
                database.saveUpdating();

                SuggestionOptions options = database.getSuggestionOptions();

                if (suggestion.getUpvotes() >= options.getStarRequirement())
                {
                    if (suggestion.getMessages().getCommunityPopularMsgId().isEmpty())
                    {
                        TextChannel popularChannel = GalacticBot.getJda().getTextChannelById(options.getPopularChannelId());
                        popularChannel.sendMessageEmbeds(message.getEmbeds().get(0)).queue(
                            success -> {
                                database.addNewCommunityPopularMessage(success.getId(), suggestion);
                            }, 
                            failure -> {
                                String conent = "Error occured sending new Popular Message for Suggestion #" + database.getSuggestionNumber(suggestion);
                                SettingsHelper.getRootLogChannel(popularChannel.getGuild()).sendMessage(conent, ResultLevel.ERROR);
                            });
                    }
                    
                    if (suggestion.getMessages().getDevPopularMsgId().isEmpty())
                    {
                        TextChannel devPopularChannel = GalacticBot.getJda().getTextChannelById(options.getDevServerPopularChannel());
                        devPopularChannel.sendMessageEmbeds(message.getEmbeds().get(0)).queue(
                            success -> {
                                database.addNewDevServerPopularMessage(success.getId(), suggestion);
                            }, 
                            failure -> {
                                String conent = "Error occured sending new Popular Message for Suggestion #" + database.getSuggestionNumber(suggestion);
                                SettingsHelper.getRootLogChannel(devPopularChannel.getGuild()).sendMessage(conent, ResultLevel.ERROR);
                            });
                    }
                }
            }
        }
    }
}
