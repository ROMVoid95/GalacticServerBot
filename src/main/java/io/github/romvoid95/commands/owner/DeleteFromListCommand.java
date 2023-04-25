package io.github.romvoid95.commands.owner;

import io.github.readonly.command.event.SlashCommandEvent;
import io.github.readonly.command.option.Option;
import io.github.readonly.command.option.RequiredOption;
import io.github.romvoid95.BotData;
import io.github.romvoid95.commands.core.GalacticSlashCommand;
import io.github.romvoid95.database.entity.DBGalacticBot;
import io.github.romvoid95.util.Check;
import io.github.romvoid95.util.discord.Reply;

public class DeleteFromListCommand extends GalacticSlashCommand
{

    public DeleteFromListCommand()
    {
        name("list-delete");
        setOptions(Option.integer("id", "The id"));
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
        String id = event.getOption("id").getAsString();

        if (BotData.database().galacticBot().deleteSuggestion(id))
        {
            Reply.Success(event, "Sucessfully cleared Suggestions Database");
        }
    }
}
