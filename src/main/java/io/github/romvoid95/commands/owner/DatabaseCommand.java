package io.github.romvoid95.commands.owner;

import io.github.readonly.command.event.SlashCommandEvent;
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
        name("database");

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
        DBGalacticBot db = BotData.database().galacticBot();

        SuggestionManager m = db.getManager();
        db.setSuggestionManager(m);
        
        Reply.Success(event, "Done");
    }
}
