package io.github.romvoid95.commands.owner;

import java.util.LinkedHashMap;

import io.github.readonly.command.event.SlashCommandEvent;
import io.github.readonly.command.option.Option;
import io.github.readonly.command.option.RequiredOption;
import io.github.romvoid95.BotData;
import io.github.romvoid95.commands.core.GalacticSlashCommand;
import io.github.romvoid95.database.entity.DBGalacticBot;
import io.github.romvoid95.util.Check;
import io.github.romvoid95.util.discord.Reply;

public class DatabaseCommand extends GalacticSlashCommand
{

    public DatabaseCommand()
    {
        name("database");
        setOptions(
            RequiredOption.text("msgid", "msgid"),
            Option.integer("number", "number")
        );
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
        String msgId = event.getOption("msgid").getAsString();
        if(event.hasOption("number"))
        {
//            int num = event.getOption("number").getAsInt();
//            LinkedHashMap<String, Integer> m = db.getManager().getMap();
//            m.remove(msgId, num);
//            db.getManager().setMap(m);
//            db.saveUpdating();
        }
        Reply.Success(event, "Done");
    }
}
