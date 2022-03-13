package com.readonlydev.commands;

import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

import com.readonlydev.annotation.GalacticCommand;
import com.readonlydev.cmd.CommandEvent;
import com.readonlydev.commands.core.BaseCommand;
import com.readonlydev.commands.core.CommandCategory;
import com.readonlydev.commands.core.ResultLevel;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

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
    	EmbedBuilder buidler = new EmbedBuilder().setDescription("Ping: ...").setColor(ResultLevel.WARNING.getColorInt());
    	event.reply(buidler.build(), m -> {
            long ping = event.getMessage().getTimeCreated().until(m.getTimeCreated(), ChronoUnit.MILLIS);
            m.editMessageEmbeds(getFinishedEmbed(ping, event.getJDA().getGatewayPing())).queueAfter(3, TimeUnit.SECONDS);
        });
    	
    	event.getMessage().delete().queue();
    }
    
    
    private MessageEmbed getFinishedEmbed(long ping, long gateway) {
    	return new EmbedBuilder()
    			.setDescription("**Ping:** " + ping  + "ms\n**Websocket:** " + gateway + "ms")
    			.setColor(ResultLevel.SUCCESS.getColorInt())
    			.build();
    }
}
