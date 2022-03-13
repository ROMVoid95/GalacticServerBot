package com.readonlydev.commands.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.readonlydev.cmd.BotCommand;
import com.readonlydev.cmd.CommandEvent;
import com.readonlydev.cmd.arg.CommandArgument;
import com.readonlydev.cmd.arg.Optional;
import com.readonlydev.cmd.arg.Required;
import com.readonlydev.logback.LogUtils;
import com.readonlydev.util.StringUtils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;

public abstract class BaseCommand extends BotCommand {

	private ArgumentIndex argumentIndex;
	private CommandEvent event;
	private Message returnMsg;

	protected BaseCommand(String name, Category category) {
		this.name = name;
		this.category = category;
	}
	
	@Override
	public void execute(CommandEvent event) {
		this.argumentIndex = new ArgumentIndex(event);
		this.event = event;
		List<String> log = new ArrayList<>();
		log.add(event.getAuthor().getAsTag() + " ran the " + StringUtils.capitalize(this.name) + " command");
		if(this.getArgCount() > 0) {
			log.add("**Invoked**: [ " + this.name + " " + this.getArgsAsString() + " ]");
			LogUtils.log("CommandEvent", String.join("\n", log));
			if(this.getArgCount() == 0) {
				event.reply(this.getHelpEmbed());
				return;
			}
		}
		if(event.getArgs().equalsIgnoreCase("help")) {
			temporaryReply(ResultLevel.SUCCESS, this.getHelpEmbed(), 30, TimeUnit.SECONDS);
			event.getMessage().delete().queue();
		} else {
			LogUtils.log("CommandEvent", log.get(0));
			onExecute(event);
		}
	}
	
	public abstract void onExecute(CommandEvent event);
	
	protected void aliases(String... aliases) {
		this.aliases = aliases;
	}
	
	protected void help(String string) {
		this.help = string;
	}
	
	protected void allowDms() {
		this.guildOnly = false;
	}
	
	protected void ownerOnly() {
		this.ownerCommand = true;
	}

	protected void requiredRoles(String... roles) {
		Arrays.asList(roles).forEach(r -> this.addRequiredRoles(r));
	}
	
	public void temporaryReply(ResultLevel level, String message, int time, TimeUnit unit) {
		MessageEmbed embed = new MessageEmbed(null, null, message, null, null, level.getColorInt(), null, null, null, null, null, null, null);
		event.reply(embed, success -> {
			success.delete().queueAfter(time, unit);
		});
	}
	
	public void temporaryReply(ResultLevel level, MessageEmbed embed, int time, TimeUnit unit) {
		event.reply(embed, success -> {
			success.delete().queueAfter(time, unit);
		});
	}
	
	public Message sendMessage(String msg) {
		event.reply(msg, m -> this.returnMsg = m);
		return returnMsg;
	}
	
	protected int getArgCount() {
		return argumentIndex.count();
	}
	
	protected String getArgValue(int index) {
		return argumentIndex.getArg(index).val();
	}
	
	protected Argument getArg(int index) {
		return argumentIndex.getArg(index);
	}
	
	protected boolean noArgs() {
		return argumentIndex.isEmpty();
	}
	
	protected String getArgsAsString() {
		StringBuilder b = new StringBuilder();
		argumentIndex.list().forEach(s -> b.append(s.val() + " "));
		return b.toString();
	}
	
	public MessageEmbed getHelpEmbed() {
		String s = "⁣  ";
		
		List<Required> requiredArguments = new ArrayList<>();
		List<Optional> optionalArguments = new ArrayList<>();
		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle(StringUtils.capitalize(name + "Command"));
		builder.setDescription(this.getHelp());
		StringBuilder b1 = new StringBuilder();
		for(CommandArgument<?> arg : this.getArguments()) {
			if(arg instanceof Required) {
				Required a = (Required) arg;
				requiredArguments.add(a);
				b1.append(" " + a.getArgumentForHelp());
			} else if(arg instanceof Optional) {
				Optional a = (Optional) arg;
				optionalArguments.add(a);
				b1.append(" " + a.getArgumentForHelp());
			}
		}
		b1.append("`");
		builder.appendDescription("\n\n**Usage:** `" + name + b1.toString());
		if(!requiredArguments.isEmpty()) {
			StringBuilder b2 = new StringBuilder();
			for(Required r : requiredArguments) {
				b2.append(r.getArgumentForHelp() + "\n");
				b2.append(s + "-").append(" *" + r.getDescription() + "*\n");
			}
			builder.addField("Required Arguments", b2.toString(), false);
		}
		if(!optionalArguments.isEmpty()) {
			StringBuilder b2 = new StringBuilder();
			for(Optional r : optionalArguments) {
				b2.append(r.getArgumentForHelp() + "\n");
				b2.append(s + "⁣-").append(" *" + r.getDescription() + "*\n");
			}
			builder.addField("Optional Arguments", b2.toString(), false);
		}
		return builder.build();
	}
}
