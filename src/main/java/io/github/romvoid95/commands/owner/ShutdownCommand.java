package io.github.romvoid95.commands.owner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.google.common.base.Throwables;

import io.github.readonly.command.event.SlashCommandEvent;
import io.github.romvoid95.commands.core.GalacticSlashCommand;
import io.github.romvoid95.util.Check;
import io.github.romvoid95.util.discord.Reply;

public class ShutdownCommand extends GalacticSlashCommand
{
    public ShutdownCommand()
    {
        this.name = "restart";
        this.help = "safely restarts the bot service";
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
        ProcessBuilder builder = new ProcessBuilder("systemctl restart galacticbot.service").redirectErrorStream(true);
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
