package io.github.romvoid95.commands.staff.suggestions;

import java.awt.Color;

import io.github.readonly.command.event.SlashCommandEvent;
import io.github.readonly.command.option.RequiredOption;
import io.github.romvoid95.BotData;
import io.github.romvoid95.commands.core.GalacticSlashCommand;
import io.github.romvoid95.core.guildlogger.ServerSettings;
import io.github.romvoid95.database.impl.Suggestion;
import io.github.romvoid95.util.Check;
import io.github.romvoid95.util.discord.Reply;
import io.github.romvoid95.util.rec.LinkedMessagesRecord;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

public class StaffDeleteSuggestion extends GalacticSlashCommand
{

    public StaffDeleteSuggestion()
    {
        super("delete", "Deletes the given Suggestion");
        options(RequiredOption.integer("number", "The Suggestion #"), RequiredOption.text("reason", "The reason for deleting this suggestion"));
    }

    @Override
    protected void onExecute(SlashCommandEvent event)
    {
        boolean canRun = Check.staffRoles(event);

        if (!canRun)
        {
            Reply.InvalidPermissions(event);
            return;
        }

        Member invoker = event.getMember();
        int    number  = event.getOption("number").getAsInt();
        String reason  = event.getOption("reason").getAsString();

        Suggestion toDelete         = BotData.database().galacticBot().getSuggestionFromNumber(number);
        User       suggestionAuthor = event.getJDA().getUserById(toDelete.getAuthorId());

        LinkedMessagesRecord lmr = toDelete.getMessages().getLinkedMessagesRecord();

        lmr.deleteMessages().queue(s ->
        {
            ((ServerSettings) event.getClient().getSettingsFor(event.getGuild())).getRootLogger().sendDeletedLog(invoker, toDelete, reason);
            this.sendMessageToAuthor(suggestionAuthor, invoker, toDelete, reason);

            BotData.database().galacticBot().clearMessageIdsFromSuggestion(number);

            Reply.Success(event, "Suggestion #" + number + " Deleted");
        }, e ->
        {
            Reply.Error(event, "There was an error when attempting to delete Suggestion #" + number);
        });
    }

    private void sendMessageToAuthor(User author, Member member, Suggestion suggestion, String reason)
    {
        EmbedBuilder  builder = new EmbedBuilder();
        StringBuilder sb      = new StringBuilder();
        sb.append("Title: %s".formatted(suggestion.getTitle()));

        builder.setTitle("Suggestion Deleted");
        builder.setDescription(sb.toString());
        builder.setColor(Color.RED);
        builder.addField("Deleted By", member.getUser().getGlobalName(), false);
        builder.addField("Reason", reason, false);
        builder.setFooter("If you feel this was done in error please contact the staff member listed");

        author.openPrivateChannel().flatMap(channel -> channel.sendMessageEmbeds(builder.build())).queue();
    }
}
