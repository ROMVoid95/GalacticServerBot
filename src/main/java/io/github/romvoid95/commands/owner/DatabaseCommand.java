package io.github.romvoid95.commands.owner;

import com.github.readonlydevelopment.command.event.SlashCommandEvent;

import io.github.romvoid95.BotData;
import io.github.romvoid95.commands.core.GalacticSlashCommand;
import io.github.romvoid95.database.entity.DBGalacticBot;
import io.github.romvoid95.database.impl.SuggestionManager;
import io.github.romvoid95.util.Check;
import io.github.romvoid95.util.discord.Reply;

public class DatabaseCommand extends GalacticSlashCommand
{

    public DatabaseCommand()
    {
        this.name = "clear-database";
    }

    @Override
    protected void execute(SlashCommandEvent event)
    {
        if(!Check.isOwner(event))
        {
            Reply.InvalidPermissions(event);
            return;
        }
        super.execute(event);
    }

    @Override
    protected void onExecute(SlashCommandEvent event)
    {
        DBGalacticBot database = BotData.database().botDatabase();
        
        database.clearSuggestionDatabase();
        
        SuggestionManager manager = database.getManager();
        
        if(manager.getList().isEmpty() && manager.getMap().isEmpty())
        {
            Reply.Success(event, "Sucessfully cleared Suggestions Database");
        }
    }
}
