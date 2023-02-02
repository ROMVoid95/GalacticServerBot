package io.github.romvoid95.commands.owner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.google.common.base.Throwables;

import io.github.readonly.command.event.SlashCommandEvent;
import io.github.readonly.command.option.RequiredOption;
import io.github.readonly.common.util.ResultLevel;
import io.github.romvoid95.commands.core.GalacticSlashCommand;
import io.github.romvoid95.util.Check;
import io.github.romvoid95.util.discord.Reply;

public class ExecCommand extends GalacticSlashCommand
{
	public ExecCommand()
	{
		this.name = "exec";
		setOptions(RequiredOption.text("cmd", "command to execute", 1024));
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
