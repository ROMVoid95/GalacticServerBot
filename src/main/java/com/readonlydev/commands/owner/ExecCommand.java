package com.readonlydev.commands.owner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

import com.google.common.base.Throwables;
import com.readonlydev.command.slash.SlashCommandEvent;
import com.readonlydev.commands.core.GalacticSlashCommand;
import com.readonlydev.common.utils.ResultLevel;
import com.readonlydev.util.Check;
import com.readonlydev.util.discord.Reply;

import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class ExecCommand extends GalacticSlashCommand
{
	public ExecCommand()
	{
		this.name = "exec";
		options = Arrays.asList(new OptionData(OptionType.STRING, "cmd", "command to execute", true).setMaxLength(1024));
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
	public void onExecute(SlashCommandEvent event)
	{
		if (event.optString("cmd").isBlank())
		{
			Reply.EphemeralReply(event, ResultLevel.ERROR, "No command was provided");
		}

		ProcessBuilder builder = new ProcessBuilder(event.optString("cmd").stripTrailing().split("\\s+")).redirectErrorStream(true);
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
			
			Reply.Success(event, output);
			
		} catch (IOException e)
		{
			Reply.Error(event, "```\n" + "%s\n" + "```".formatted(Throwables.getStackTraceAsString(e)));

			e.printStackTrace();
		}
	}
}
