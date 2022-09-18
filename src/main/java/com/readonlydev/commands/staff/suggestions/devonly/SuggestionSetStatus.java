package com.readonlydev.commands.staff.suggestions.devonly;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.readonlydev.BotData;
import com.readonlydev.command.slash.SlashCommandEvent;
import com.readonlydev.commands.core.GalacticSlashCommand;
import com.readonlydev.database.entity.DBGalacticBot;
import com.readonlydev.database.impl.Suggestion;
import com.readonlydev.util.Check;
import com.readonlydev.util.discord.Reply;
import com.readonlydev.util.discord.SuggestionStatus;
import com.readonlydev.util.discord.SuggestionsHelper;

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
