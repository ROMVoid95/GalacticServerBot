package io.github.romvoid95.commands.owner;

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

public class StatusOverrideCommand extends GalacticSlashCommand
{

    public StatusOverrideCommand()
    {
        name("set-count");
        setOptions(RequiredOption.integer("number", "The Suggestion #"), RequiredOption.text("status", "Status to set", ChoiceList.toList(SuggestionStatus.class)));
    }

    @Override
    protected void execute(SlashCommandEvent event)
    {
        if (!Check.isOwner(event))
        {
            Reply.InvalidPermissions(event);
            return;
        }
        super.execute(event);
    }

    @Override
    protected void onExecute(SlashCommandEvent event)
    {
        int              suggestionNumber = event.getOption("number").getAsInt();
        SuggestionStatus status           = SuggestionStatus.getStatus(event.getOption("status").getAsString());

        DBGalacticBot suggestions = BotData.database().galacticBot();
        Suggestion    suggestion  = suggestions.getSuggestionFromNumber(suggestionNumber);

        suggestion.setStatus(status);
        suggestions.saveUpdating();
        
        Reply.EphemeralReply(event, "Sucessfully set status");
    }
}
