package com.readonlydev.commands;

import java.time.temporal.ChronoUnit;

import com.readonlydev.annotation.GalacticCommand;
import com.readonlydev.cmd.CommandEvent;
import com.readonlydev.commands.core.BaseCommand;
import com.readonlydev.commands.core.CommandCategory;

@GalacticCommand
public class PingCommand extends BaseCommand {

    public PingCommand()
    {
    	super("ping", CommandCategory.SERVER_MEMBER);
        this.help = "checks the bot's latency";
        this.guildOnly = false;
        this.aliases = new String[]{"pong"};
    }

    @Override
	public void onExecute(CommandEvent event) {
        event.reply("Ping: ...", m -> {
            long ping = event.getMessage().getTimeCreated().until(m.getTimeCreated(), ChronoUnit.MILLIS);
            m.editMessage("Ping: " + ping  + "ms | Websocket: " + event.getJDA().getGatewayPing() + "ms").queue();
        });
    }

}
