package com.readonlydev.commands.owner;

import com.readonlydev.BotData;
import com.readonlydev.api.annotation.BotCommand;
import com.readonlydev.command.event.CommandEvent;
import com.readonlydev.commands.core.AbstractCommand;
import com.readonlydev.database.entity.DBGalacticBot;
import com.readonlydev.database.impl.SuggestionManager;
import com.readonlydev.util.discord.Reply;

@BotCommand
public class DatabaseCommand extends AbstractCommand
{

    public DatabaseCommand()
    {
        super("database");
        this.isOwnerCommand();
        this.isHidden();
    }

    @Override
    protected void onExecute(CommandEvent event)
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
