package io.github.romvoid95.commands.staff.suggestions.devonly;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.github.readonlydevelopment.command.event.SlashCommandEvent;

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
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class SuggestionSetStatus extends GalacticSlashCommand
{

    public SuggestionSetStatus()
    {
        this.name = "set-status";
        OptionData suggestionNumber = new OptionData(OptionType.INTEGER, "number", "The Suggestion #", true);
        OptionData option = new OptionData(OptionType.STRING, "status", "Status to set", true);
        List<Command.Choice> choices = new LinkedList<>();
        for(SuggestionStatus status : SuggestionStatus.values())
        {
            if(status != SuggestionStatus.NONE)
            {
                choices.add(new Command.Choice(status.getName(), status.toString().toUpperCase()));
            }
        }
        this.options = Arrays.asList(suggestionNumber, option.addChoices(choices));
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
        
        DBGalacticBot suggestions = BotData.database().botDatabase();
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
