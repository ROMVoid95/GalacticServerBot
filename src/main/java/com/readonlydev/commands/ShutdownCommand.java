package com.readonlydev.commands;

import com.readonlydev.annotation.GalacticCommand;
import com.readonlydev.cmd.CommandEvent;
import com.readonlydev.commands.core.BaseCommand;
import com.readonlydev.commands.core.CommandCategory;

@GalacticCommand
public class ShutdownCommand extends BaseCommand {

    public ShutdownCommand()
    {
    	super("shutdown", CommandCategory.BOT_OWNER);
    	aliases("abort");
        this.help = "safely shuts off the bot";
        this.guildOnly = false;
        this.ownerCommand = true;
    }

    @Override
    public void onExecute(CommandEvent event) {
        event.reactWarning();
        event.getJDA().shutdown();
    }

}
