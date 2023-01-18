package io.github.romvoid95.commands.owner;

import java.util.Arrays;

import com.github.readonlydevelopment.command.event.SlashCommandEvent;

import io.github.romvoid95.BotData;
import io.github.romvoid95.GalacticBot;
import io.github.romvoid95.commands.core.GalacticSlashCommand;
import io.github.romvoid95.core.event.MaintDisableEvent;
import io.github.romvoid95.core.event.MaintEnableEvent;
import io.github.romvoid95.database.entity.DBGalacticBot;
import io.github.romvoid95.util.Check;
import io.github.romvoid95.util.discord.Reply;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class MaintanenceModeCommand extends GalacticSlashCommand
{

    public MaintanenceModeCommand()
    {
        this.name = "maintanence-mode";
        this.options = Arrays.asList(
            new OptionData(OptionType.STRING, "mode", "On or Off", true).addChoices(
               new Command.Choice("ON", "on"),
               new Command.Choice("OFF", "off")
            )
        );
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
        
        String action = event.getOption("mode").getAsString();
        if(action.equals("on"))
        {
            GalacticBot.EventBus().post(new MaintEnableEvent(event.getJDA()));
            database.setMaintenanceMode(true);
            Reply.Success(event, "Now running in Maintanence Mode");
        }
        
        if(action.equals("off"))
        {
            GalacticBot.EventBus().post(new MaintDisableEvent(event.getJDA()));
            database.setMaintenanceMode(false);
            Reply.Success(event, "Now running in Normal Mode");
        }
    }
}
