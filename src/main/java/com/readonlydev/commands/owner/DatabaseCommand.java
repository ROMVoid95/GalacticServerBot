package com.readonlydev.commands.owner;

import com.readonlydev.BotData;
import com.readonlydev.command.slash.SlashCommandEvent;
import com.readonlydev.commands.core.GalacticSlashCommand;
import com.readonlydev.database.entity.DBGalacticBot;
import com.readonlydev.database.impl.SuggestionManager;
import com.readonlydev.util.Check;
import com.readonlydev.util.discord.Reply;

import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;

public class DatabaseCommand extends GalacticSlashCommand
{

    public DatabaseCommand()
    {
        this.name = "clear-database";
        this.help = "Clears the content in the database";
        this.subcommandGroup = new SubcommandGroupData("owner", "Owner Only Commands");
        this.isOwnerCommand();
        this.isHidden();
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
