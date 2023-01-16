package com.readonlydev.commands.owner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Throwables;
import com.readonlydev.api.annotation.BotCommand;
import com.readonlydev.command.event.CommandEvent;
import com.readonlydev.commands.core.AbstractCommand;
import com.readonlydev.common.utils.ResultLevel;

import net.dv8tion.jda.api.entities.Message;

@BotCommand
public class ExecCommand extends AbstractCommand
{
	Message message;

	public ExecCommand()
	{
		super("exec");
		aliases("run", "cmd");
		this.help = "Runs a command on the underlying system";
		this.guildOnly = true;
		this.ownerCommand = true;
	}

	@Override
	public void onExecute(CommandEvent event)
	{
		if (getMessageContent().isBlank())
		{
			temporaryReply(ResultLevel.ERROR, "No command was provided", 20, TimeUnit.SECONDS);
		}

		ProcessBuilder builder = new ProcessBuilder(getMessageContent().stripTrailing().split("\\s+")).redirectErrorStream(true);
		try
		{
			Process systemProcess = builder.start();
			InputStream input = systemProcess.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(input));
			
			StringBuilder outputBuilder = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				outputBuilder.append(line).append("\n");
			}
			
			String output = outputBuilder.toString();
			if(output.trim().isBlank())
			{
				output = "Command returned no output";
			}
			
			replySuccess(output);
			
		} catch (IOException e)
		{
			replyError("```\n" + "%s\n" + "```".formatted(Throwables.getStackTraceAsString(e)));

			e.printStackTrace();
		}
	}
}
