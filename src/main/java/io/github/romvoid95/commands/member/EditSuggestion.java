package io.github.romvoid95.commands.member;

import java.util.Optional;

import io.github.readonly.command.event.SlashCommandEvent;
import io.github.readonly.command.lists.ChoiceList;
import io.github.readonly.command.option.RequiredOption;
import io.github.readonly.common.util.KeyValueSupplier;
import io.github.readonly.common.util.ResultLevel;
import io.github.romvoid95.BotData;
import io.github.romvoid95.Servers;
import io.github.romvoid95.commands.core.EditType;
import io.github.romvoid95.commands.core.GalacticSlashCommand;
import io.github.romvoid95.database.impl.Suggestion;
import io.github.romvoid95.util.StringUtils;
import io.github.romvoid95.util.discord.Reply;
import io.github.romvoid95.util.discord.SuggestionStatus;
import io.github.romvoid95.util.discord.entity.SuggestionMessage;
import io.github.romvoid95.util.discord.entity.SuggestionMessage_V1;
import io.github.romvoid95.util.rec.LinkedMessagesRecord;
import net.dv8tion.jda.api.entities.channel.ChannelType;

public class EditSuggestion extends GalacticSlashCommand
{

    public enum Section implements KeyValueSupplier
    {

        TITLE, DESCRIPTION;

        public static Section getSection(String value)
        {
            return Section.valueOf(value);
        }

        @Override
        public String key()
        {
            return StringUtils.capitalize(toString().toLowerCase());
        }

        @Override
        public String value()
        {
            return toString();
        }
    }

    public EditSuggestion()
    {
        super("edit", "[DM ONLY] Edit the description of title of your suggestions");
        options(RequiredOption.text("id", "The Unique ID provided to you by the Bot in DM's"), RequiredOption.text("section", "Title or Description", ChoiceList.toList(Section.class)),
            RequiredOption.text("edit-type", "Replace, Append, or Prepend current value", ChoiceList.toList(EditType.class)), RequiredOption.text("content", "The content used in the action you chose"));
        this.directMessagesAllowed();
    }

    @Override
    protected void onExecute(SlashCommandEvent event)
    {
        if (!event.getChannel().getType().equals(ChannelType.PRIVATE))
        {
            Reply.EphemeralReply(event, ResultLevel.ERROR, "This command can only be used in Private Channels");
            return;
        }

        if(!Servers.galacticraftCentral.getGuild().isMember(event.getAuthor()))
        {
            Reply.Error(event, "Sorry, you must be a member of the Galacticraft Central server to edit any suggestions you have posted");
            return;
        }
        
        boolean isBlacklisted = BotData.database().blacklist().isBlacklisted(event.getAuthor().getId());
        if (isBlacklisted)
        {
            Reply.Error(event, "You have been blacklisted and cannot edit your suggestions, Contact staff if you believe this is an error");
            return;
        }

        String               id               = event.getOption("id").getAsString();
        Optional<Suggestion> suggestionToEdit = BotData.database().galacticBot().getSuggestionFromUniqueId(id);

        if (suggestionToEdit.isPresent())
        {
            Suggestion       suggestion = suggestionToEdit.get();
            SuggestionStatus status     = suggestion.getStatus();
            if (SuggestionStatus.nonEditable(status))
            {
                Reply.Error(event, "The current Suggestion Status does not allow editing");
                return;
            }

            LinkedMessagesRecord lmr = suggestion.getMessages().getLinkedMessagesRecord();
            SuggestionMessage    msg;
            if (lmr.postMsg().get().getEmbeds().size() == 1)
            {
                msg = SuggestionMessage_V1.fromEmbed(lmr.postMsg().get().getEmbeds().get(0)).convertToNewFormat();
            } else
            {
                msg = SuggestionMessage.fromMessage(lmr.postMsg().get().getEmbeds());
            }

            if (Section.getSection(event.getOption("section").getAsString()) == Section.TITLE)
            {
                runEditTitle(event, msg, lmr);
            } else
            {
                runEditDescription(event, msg, lmr);
            }
        } else
        {
            Reply.Error(event, "Your Suggestion with ID `%s` cannot be found, was it deleted by chance?".formatted(id));
        }
    }

    private void runEditDescription(SlashCommandEvent event, SuggestionMessage msg, LinkedMessagesRecord lmr)
    {
        if (event.getOption("content").getAsString().length() >= 64)
        {
            Reply.Error(event, "Suggestion titles cannot be longer than 64 characters");
            return;
        }

        msg.setDescription(EditType.getEditType(event.getOption("edit-type").getAsString()), event.getOption("content").getAsString());

        lmr.editMessages(msg).queue(s ->
        {
            Reply.Success(event, "Sucessfully edited Suggestion Description");
        }, e ->
        {
            Reply.Error(event, "An error occured when editing your suggestion description, Please let staff know about this error");
        });
        ;
    }

    private void runEditTitle(SlashCommandEvent event, SuggestionMessage msg, LinkedMessagesRecord lmr)
    {
        msg.setTitle(EditType.getEditType(event.getOption("edit-type").getAsString()), event.getOption("content").getAsString());

        lmr.editMessages(msg).queue(s ->
        {
            Reply.Success(event, "Sucessfully edited Suggestion Title");
        }, e ->
        {
            Reply.Error(event, "An error occured when editing your suggestion Title, Please let staff know about this error");
        });
    }
}
