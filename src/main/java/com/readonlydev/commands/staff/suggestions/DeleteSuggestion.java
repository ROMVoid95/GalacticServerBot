package com.readonlydev.commands.staff.suggestions;

import java.awt.Color;
import java.util.Arrays;

import com.readonlydev.BotData;
import com.readonlydev.command.slash.SlashCommand;
import com.readonlydev.command.slash.SlashCommandEvent;
import com.readonlydev.core.guildlogger.ServerSettings;
import com.readonlydev.database.impl.Suggestion;
import com.readonlydev.util.Check;
import com.readonlydev.util.discord.Reply;
import com.readonlydev.util.rec.LinkedMessagesRecord;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class DeleteSuggestion extends SlashCommand
{

    public DeleteSuggestion()
    {
        this.name = "delete";
        this.help = "Deletes the given Suggestion";
        this.options = Arrays.asList(
            new OptionData(OptionType.INTEGER, "number", "The Suggestion #", true), 
            new OptionData(OptionType.STRING, "reason", "The reason for deleting this suggestion", true)
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

        Member invoker = event.getMember();
        int number = event.getOption("number").getAsInt();
        String reason = event.getOption("reason").getAsString();

        Suggestion toDelete = BotData.database().botDatabase().getSuggestionFromNumber(number);
        User suggestionAuthor = event.getJDA().getUserById(toDelete.getAuthorId());

        LinkedMessagesRecord lmr = toDelete.getMessages().getLinkedMessagesRecord();

        lmr.deleteMessages().queue(s ->
        {
            ((ServerSettings) event.getClient().getSettingsFor(event.getGuild())).getRootLogger().sendDeletedLog(invoker, toDelete, reason);
            this.sendMessageToAuthor(suggestionAuthor, invoker, toDelete, reason);

            BotData.database().botDatabase().clearMessageIdsFromSuggestion(number);

            Reply.Success(event, "Suggestion #" + number + " Deleted");
        }, e ->
        {
            Reply.Error(event, "There was an error when attempting to delete Suggestion #" + number);
        });
    }

    private void sendMessageToAuthor(User author, Member member, Suggestion suggestion, String reason)
    {
        EmbedBuilder builder = new EmbedBuilder();
        StringBuilder sb = new StringBuilder();
        sb.append("Title: %s".formatted(suggestion.getTitle()));

        builder.setTitle("Suggestion Deleted");
        builder.setDescription(sb.toString());
        builder.setColor(Color.RED);
        builder.addField("Deleted By", member.getUser().getAsTag(), false);
        builder.addField("Reason", reason, false);
        builder.setFooter("If you feel this was done in error please contact the staff member listed");

        author.openPrivateChannel().flatMap(channel -> channel.sendMessageEmbeds(builder.build())).queue();
    }
}
