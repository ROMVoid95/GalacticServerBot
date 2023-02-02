package io.github.romvoid95.commands.staff.suggestions.devonly;

import io.github.readonly.command.event.SlashCommandEvent;
import io.github.readonly.command.lists.ChoiceList;
import io.github.readonly.command.option.RequiredOption;
import io.github.romvoid95.BotData;
import io.github.romvoid95.commands.core.GalacticSlashCommand;
import io.github.romvoid95.database.entity.DBGalacticBot;
import io.github.romvoid95.database.impl.Suggestion;
import io.github.romvoid95.util.Check;
import io.github.romvoid95.util.discord.Reply;
import io.github.romvoid95.util.discord.SuggestionStatus;
import io.github.romvoid95.util.discord.SuggestionsHelper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;

public class SuggestionSetStatus extends GalacticSlashCommand
{

    public SuggestionSetStatus()
    {
        this.name = "set-status";
        setOptions(
        	RequiredOption.number("number", "The Suggestion #"),
        	RequiredOption.text("status", "Status to set", ChoiceList.toList(SuggestionStatus.class))
        );
    }
    
    @Override
    protected void onExecute(SlashCommandEvent event)
    {
        boolean canRun = Check.forRole(event, "775251491463364661");

        if (!canRun)
        {
            Reply.InvalidPermissions(event);
            return;
        }
        
        int suggestionNumber = event.getOption("number").getAsInt();
        SuggestionStatus status = SuggestionStatus.valueOf(event.getOption("status").getAsString());
        
        DBGalacticBot suggestions = BotData.database().galacticBot();
        Suggestion suggestion = suggestions.getSuggestionFromNumber(suggestionNumber);
        
        Message sucessfull = SuggestionsHelper.setStatus(status, suggestion);
        
        if(sucessfull != null)
        {
            suggestion.setStatus(status);
            suggestions.saveUpdating();
        
            String urlLink = sucessfull.getJumpUrl();
            
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("Suggestion " + status.getName());
            builder.setDescription("Suggestion [#" + suggestionNumber + "](" + urlLink + ") status set -> `" + status.getName() + "`");
            
            Reply.Success(event, builder);
        }
    }

}
