package io.github.romvoid95.commands.owner;

import io.github.readonly.command.event.SlashCommandEvent;
import io.github.readonly.command.lists.ChoiceList;
import io.github.readonly.command.option.Choice;
import io.github.readonly.command.option.RequiredOption;
import io.github.readonly.common.event.EventHandler;
import io.github.romvoid95.BotData;
import io.github.romvoid95.commands.core.GalacticSlashCommand;
import io.github.romvoid95.core.event.MaintenanceEvent;
import io.github.romvoid95.database.entity.DBGalacticBot;
import io.github.romvoid95.util.discord.Reply;

public class MaintanenceModeCommand extends GalacticSlashCommand
{

    public MaintanenceModeCommand()
    {
        super("maintanence-mode");
        options(RequiredOption.text("mode", "On or Off", ChoiceList.of(Choice.add("ON"), Choice.add("OFF"))));
        this.ownerCommand = true;
    }

    @Override
    protected void onExecute(SlashCommandEvent event)
    {
        DBGalacticBot database = BotData.database().galacticBot();

        String action = event.getOption("mode").getAsString();
        if (action.equals("on"))
        {
            EventHandler.instance().post(new MaintenanceEvent(MaintenanceEvent.Action.Enabled));
            database.setMaintenanceMode(true);
            Reply.EphemeralReply(event, "Now running in Maintanence Mode");
        }

        if (action.equals("off"))
        {
            EventHandler.instance().post(new MaintenanceEvent(MaintenanceEvent.Action.Disabled));
            database.setMaintenanceMode(false);
            Reply.EphemeralReply(event, "Now running in Normal Mode");
        }
    }
}
