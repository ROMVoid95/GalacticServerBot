package io.github.romvoid95.commands.owner;

import io.github.readonly.command.event.SlashCommandEvent;
import io.github.readonly.command.option.Option;
import io.github.readonly.command.option.RequiredOption;
import io.github.romvoid95.BotData;
import io.github.romvoid95.commands.core.GalacticSlashCommand;
import io.github.romvoid95.database.entity.DBGalacticBot;
import io.github.romvoid95.util.Check;
import io.github.romvoid95.util.discord.Reply;

public class SetCountCommand extends GalacticSlashCommand
{

    public SetCountCommand()
    {
        name("set-count");
        setOptions(Option.integer("count", "count"));
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
        int count = event.getOption("count").getAsInt();

        DBGalacticBot db = BotData.database().galacticBot();
        db.getManager().setCount(count);
        db.saveUpdating();
        
        Reply.EphemeralReply(event, "Sucessfully set count");
    }
}
