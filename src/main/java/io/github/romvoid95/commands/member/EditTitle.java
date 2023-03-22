package io.github.romvoid95.commands.member;

import java.util.Optional;

import io.github.readonly.command.event.SlashCommandEvent;
import io.github.readonly.command.option.RequiredOption;
import io.github.readonly.common.util.ResultLevel;
import io.github.romvoid95.BotData;
import io.github.romvoid95.commands.core.GalacticSlashCommand;
import io.github.romvoid95.database.impl.Suggestion;
import io.github.romvoid95.util.discord.Reply;
import io.github.romvoid95.util.discord.SuggestionStatus;
import io.github.romvoid95.util.discord.entity.SuggestionMessage_V1;
import io.github.romvoid95.util.rec.LinkedMessagesRecord;
import net.dv8tion.jda.api.entities.channel.ChannelType;

public class EditTitle extends GalacticSlashCommand
{

    public EditTitle()
    {
        this.name = "edit-title";
        this.help = "Fully replaces the title of your suggestion";
        setOptions(
        	RequiredOption.text("id", "The Unique ID provided to you by the Bot in DM's"),
        	RequiredOption.text("title", "Your new title", 64)
        );
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
        
        boolean isBlacklisted = BotData.database().blacklist().isBlacklisted(event.getAuthor().getId());
        if(isBlacklisted)
        {
            Reply.EphemeralReply(event, ResultLevel.ERROR, "You have been blacklisted and cannot edit your suggestions, Contact staff if you believe this is an error");
            return;
        }

        String id = event.getOption("id").getAsString();
        Optional<Suggestion> suggestionToEdit = BotData.database().galacticBot().getSuggestionFromUniqueId(id);

        if (suggestionToEdit.isPresent())
        {
            Suggestion suggestion = suggestionToEdit.get();
            SuggestionStatus status = suggestion.getStatus();
            if (SuggestionStatus.nonEditable(status))
            {
                Reply.Error(event, "The current Suggestion Status does not allow editing");
                return;
            }

            LinkedMessagesRecord lmr = suggestion.getMessages().getLinkedMessagesRecord();
            SuggestionMessage_V1 embed = SuggestionMessage_V1.fromEmbed(lmr.postMsg().get().getEmbeds().get(0));
            embed.setTitle(event.getOption("title").getAsString());

            lmr.editMessages(embed).queue(s -> 
            {
                Reply.Success(event, "Sucessfully edited Suggestion Title");
            }, e -> 
            {
                Reply.Error(event, "An error occured when editing your suggestion Title, Please let staff know about this error");
            });
        }
        else {
            Reply.Error(event, "Your Suggestion with ID `%s` cannot be found, was it deleted by chance?".formatted(id));
        }
    }
}
