package com.readonlydev.commands.member;

import java.util.Arrays;
import java.util.Optional;

import com.readonlydev.BotData;
import com.readonlydev.command.slash.SlashCommandEvent;
import com.readonlydev.commands.core.EditType;
import com.readonlydev.commands.core.GalacticSlashCommand;
import com.readonlydev.common.utils.ResultLevel;
import com.readonlydev.database.impl.Suggestion;
import com.readonlydev.util.discord.Reply;
import com.readonlydev.util.discord.SuggestionStatus;
import com.readonlydev.util.discord.entity.SuggestionEmbed;
import com.readonlydev.util.rec.LinkedMessagesRecord;

import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class EditDescription extends GalacticSlashCommand
{

    public EditDescription()
    {
        this.name = "edit-description";
        this.help = "Fully replaces the description of your suggestion";
        this.options = Arrays.asList(
            new OptionData(OptionType.STRING, "id", "The Unique ID provided to you by the Bot in DM's", true),
            new OptionData(OptionType.STRING, "type", "Replace or append", true).addChoices(EditType.toChoices()),
            new OptionData(OptionType.STRING, "description", "Your new description", true).setMaxLength(1024)
        );
        this.guildOnly = false;
    }

    @Override
    protected void onExecute(SlashCommandEvent event)
    {
        if (!event.getChannel().getType().equals(ChannelType.PRIVATE))
        {
            Reply.EphemeralReply(event, ResultLevel.ERROR, "This command can only be used in Private Channels");
            return;
        }
        
        boolean isBlacklisted = BotData.database().blacklist().isBlacklisted(event.getMember().getId());
        if(isBlacklisted)
        {
            Reply.EphemeralReply(event, ResultLevel.ERROR, "You have been blacklisted and cannot edit your suggestions, Contact staff if you believe this is an error");
            return;
        }

        String id = event.getOption("id").getAsString();
        Optional<Suggestion> suggestionToEdit = BotData.database().botDatabase().getSuggestionFromUniqueId(id);

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
            SuggestionEmbed embed = SuggestionEmbed.fromEmbed(lmr.postMsg().get().getEmbeds().get(0));
            
            EditType editType = EditType.getEditType(event.getOption("type").getAsString());
            
            embed.setDescription(editType, event.getOption("description").getAsString());

            lmr.editMessages(embed).queue(s -> 
            {
                Reply.Success(event, "Sucessfully edited Suggestion Description");
            }, e -> 
            {
                Reply.Error(event, "An error occured when editing your suggestion description, Please let staff know about this error");
            });;
        } else {
            Reply.Error(event, "Your Suggestion with ID `%s` cannot be found, was it deleted by chance?".formatted(id));
        }
    }
}
