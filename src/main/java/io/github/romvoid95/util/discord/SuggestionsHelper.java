package io.github.romvoid95.util.discord;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.github.readonly.common.util.ResultLevel;
import io.github.romvoid95.BotData;
import io.github.romvoid95.BusListener;
import io.github.romvoid95.GalacticBot;
import io.github.romvoid95.database.entity.DBGalacticBot;
import io.github.romvoid95.database.impl.Suggestion;
import io.github.romvoid95.database.impl.Suggestion.LinkedMessages;
import io.github.romvoid95.database.impl.options.SuggestionOptions;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;

@Slf4j
@UtilityClass
public class SuggestionsHelper
{

    public List<String> getAllAuthors()
    {
        DBGalacticBot db = BotData.database().galacticBot();
        return db.getManager().getList().stream().map(Suggestion::getAuthorId).collect(Collectors.toList());
    }

    public static Message setStatus(SuggestionStatus status, Suggestion suggestion)
    {
        LinkedMessages messages = suggestion.getMessages();
        Message        postMsg  = null;

        boolean isRevert = suggestion.getStatus() != SuggestionStatus.NONE && status == SuggestionStatus.NONE;

        log.info("isRevert: " + isRevert);
        log.info("suggestion.getStatus() != SuggestionStatus.NONE: " + (suggestion.getStatus() != SuggestionStatus.NONE));
        log.info("suggestion.getStatus(): " + suggestion.getStatus().getName());
        log.info("status == SuggestionStatus.NONE: " + (status == SuggestionStatus.NONE));
        
        if (messages.postMsg().isPresent())
        {
            setStatusOnMessage(messages.postMsg().get(), status, isRevert);
            postMsg = messages.postMsg().get();

            if (messages.communityPopularMsg().isPresent())
            {
                setStatusOnMessage(messages.communityPopularMsg().get(), status, isRevert);

                if (messages.devPopularMsg().isPresent())
                {
                    setStatusOnMessage(messages.devPopularMsg().get(), status, isRevert);
                }
            }
        }

        return postMsg;
    }

    private static void setStatusOnMessage(Message message, SuggestionStatus status, boolean isRevert)
    {
        List<MessageEmbed> embeds      = new ArrayList<>();
        int                statusColor = status.getRGB().getColor();

        EmbedBuilder infoBuilder = new EmbedBuilder(message.getEmbeds().get(0));
        if (isRevert)
        {
            infoBuilder.clearFields();
        } else
        {
            infoBuilder.addField(status.getStatusEmbedField());
        }
        infoBuilder.setColor(statusColor);
        embeds.add(infoBuilder.build());

        for (MessageEmbed description : message.getEmbeds().stream().skip(1).toList())
        {
            EmbedBuilder descriptionBuilder = new EmbedBuilder(description);
            descriptionBuilder.setColor(statusColor);
            embeds.add(descriptionBuilder.build());
        }

        message.editMessageEmbeds(embeds).queue();
    }

    public static void handleSuggestionDownvoteEvent(MessageReactionRemoveEvent event)
    {
        DBGalacticBot suggestions = BotData.database().galacticBot();
        if (suggestions.getAllSuggestionMessageIds().contains(event.getMessageId()))
        {
            Suggestion suggestion = suggestions.getSuggestionFromMessageId(event.getMessageId());
            Message    message    = DiscordUtils.getMessageOrNull(event);

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
        DBGalacticBot database = BotData.database().galacticBot();

        if (database.getAllSuggestionMessageIds().contains(event.getMessageId()))
        {
            Suggestion suggestion = database.getSuggestionFromMessageId(event.getMessageId());
            Message    message    = DiscordUtils.getMessageOrNull(event);

            if (event.getUser().getId().equals(suggestion.getAuthorId()))
            {
                event.retrieveMessage().flatMap((m) -> m.removeReaction(event.getEmoji(), event.getUser())).queue();
            }

            if (BotData.database().galacticBot().isMaintenanceMode())
            {
                if (message != null)
                {
                    suggestionUpvote(suggestion, message, database);
                }
            } else
            {
                if (message != null && !event.getUser().getId().equals(suggestion.getAuthorId()))
                {
                    suggestionUpvote(suggestion, message, database);
                }
            }
        }
    }

    private void suggestionUpvote(Suggestion suggestion, Message message, DBGalacticBot database)
    {
        int reactionCount = message.getReactions().get(0).getCount() - 1;
        suggestion.setUpvotes(reactionCount);
        database.saveUpdating();

        SuggestionOptions options = database.getSuggestionOptions();

        if (suggestion.getUpvotes() >= options.getStarRequirement())
        {
            if (suggestion.getMessages().getCommunityPopularMsgId().isEmpty())
            {
                TextChannel popularChannel = GalacticBot.instance().getJda().getTextChannelById(options.getPopularChannelId());
                popularChannel.sendMessageEmbeds(message.getEmbeds()).queue(success ->
                {
                    database.addNewCommunityPopularMessage(success.getId(), suggestion);
                }, failure ->
                {
                    String conent = "Error occured sending new Popular Message for Suggestion " + suggestion.get_id();
                    SettingsHelper.getRootLogChannel(popularChannel.getGuild()).sendMessage(conent, ResultLevel.ERROR);
                });
            }

            if (suggestion.getMessages().getDevPopularMsgId().isEmpty())
            {
                TextChannel devPopularChannel = GalacticBot.instance().getJda().getTextChannelById(options.getDevServerPopularChannelId());
                devPopularChannel.sendMessageEmbeds(message.getEmbeds()).queue(success ->
                {
                    database.addNewDevServerPopularMessage(success.getId(), suggestion);
                }, failure ->
                {
                    String conent = "Error occured sending new Popular Message for Suggestion #" + suggestion.get_id();
                    SettingsHelper.getRootLogChannel(devPopularChannel.getGuild()).sendMessage(conent, ResultLevel.ERROR);
                });
            }
        }
    }
}
