package com.readonlydev.commands.owner;

import java.util.Arrays;

import com.readonlydev.BotData;
import com.readonlydev.GalacticBot;
import com.readonlydev.command.slash.SlashCommandEvent;
import com.readonlydev.commands.core.GalacticSlashCommand;
import com.readonlydev.core.event.MaintDisableEvent;
import com.readonlydev.core.event.MaintEnableEvent;
import com.readonlydev.database.entity.DBGalacticBot;
import com.readonlydev.util.Check;
import com.readonlydev.util.discord.Reply;

import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class MaintanenceModeCommand extends GalacticSlashCommand
{

    public MaintanenceModeCommand()
    {
        this.name = "maintanence-mode";
        this.isOwnerCommand();
        this.isHidden();
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
