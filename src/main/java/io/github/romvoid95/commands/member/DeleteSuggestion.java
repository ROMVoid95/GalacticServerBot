package io.github.romvoid95.commands.member;

import io.github.readonly.command.event.SlashCommandEvent;
import io.github.readonly.command.option.RequiredOption;
import io.github.readonly.common.util.ResultLevel;
import io.github.romvoid95.BotData;
import io.github.romvoid95.commands.core.GalacticSlashCommand;
import io.github.romvoid95.util.discord.Reply;
import net.dv8tion.jda.api.entities.channel.ChannelType;

public class DeleteSuggestion extends GalacticSlashCommand
{

    public DeleteSuggestion()
    {
        this.name = "delete";
        setOptions(
            RequiredOption.text("id", "The Unique ID provided to you by the Bot in DM's")
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
        
        String id = event.getOption("id").getAsString();
        
        try {
            if(BotData.database().galacticBot().deleteSuggestion(id)) {
                Reply.Success(event, "Suggestion sucessfully deleted");
                return;
            }
        } catch (Exception e) {
            Reply.Error(event, "An error occoured when attempting to delete suggestion by ID: " + id);
            return;
        }
	}

}
