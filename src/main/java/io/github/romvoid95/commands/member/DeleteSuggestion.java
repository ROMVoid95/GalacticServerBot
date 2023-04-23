package io.github.romvoid95.commands.member;

import io.github.readonly.command.event.SlashCommandEvent;
import io.github.readonly.command.option.RequiredOption;
import io.github.readonly.common.util.ResultLevel;
import io.github.romvoid95.BotData;
import io.github.romvoid95.commands.core.GalacticSlashCommand;
import io.github.romvoid95.database.entity.DBGalacticBot;
import io.github.romvoid95.database.impl.Suggestion;
import io.github.romvoid95.util.discord.Reply;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

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
            DBGalacticBot db = BotData.database().galacticBot();
            Suggestion suggestion = db.getSuggestionFromUniqueId(id).get();
            
            boolean isPopular = suggestion.getMessages().communityPopularMsg().isPresent();
            
            if(isPopular)
            {
                String popularMsgId = suggestion.getMessages().getCommunityPopularMsgId();
                TextChannel communityPopularChannel = db.getSuggestionOptions().getPopularChannel();
                
                String devPopularMsgId = suggestion.getMessages().getDevPopularMsgId();
                TextChannel devPopularChannel = db.getSuggestionOptions().getDevPopularChannel();
                
                communityPopularChannel.deleteMessageById(popularMsgId).queue();
                devPopularChannel.deleteMessageById(devPopularMsgId).queue();
            }
            
            
            TextChannel txtChannel = db.getSuggestionOptions().getSuggestionChannel();
            txtChannel.deleteMessageById(suggestion.getMessages().getPostMsgId()).queue(s -> {
                if(BotData.database().galacticBot().deleteSuggestion(id)) {
                    Reply.Success(event, "Suggestion sucessfully deleted");
                    return;
                }
            });
        } catch (Exception e) {
            Reply.Error(event, "An error occoured when attempting to delete suggestion by ID: " + id + "\n\n" + e.getMessage());
            return;
        }
	}

}
