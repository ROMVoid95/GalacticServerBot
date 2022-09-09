package com.readonlydev.updates.commands;

import java.util.Arrays;

import com.readonlydev.command.slash.SlashCommandEvent;
import com.readonlydev.updates.Updates;
import com.readonlydev.updates.commands.core.CFCommand;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class DelProjectCommand extends CFCommand
{

    public DelProjectCommand()
    {
        this.name = "updates delete";
        this.help = "Removes an curseforge project from this channel";
        options = Arrays.asList(
            new OptionData(OptionType.INTEGER, "project-id", "ID of the Curseforge Project", true)
        );
    }

    @Override
    public void execute(SlashCommandEvent ev)
    {
        if (!ev.getMember().hasPermission(Permission.MANAGE_SERVER))
        {
            ev.deferReply(true).setContent("You need manage server permissions for this command!").queue();
            return;
        }
        final OptionMapping id = ev.getOption("project-id");
        if (id == null)
            ev.reply("Project ID == null ?!?").queue();
        else
        {
            final long projectID = Long.parseLong(id.getAsString());
            if (Updates.projects.containsKey(projectID))
            {
                final String name = Updates.projects.get(projectID).proj.name;
                Updates.ifa.deleteChannelFromProject(projectID, ev.getChannel().getIdLong());
                final boolean fullyRemoved = Updates.projects.get(projectID).removeChannel(ev.getChannel().getIdLong());
                ev.reply("Removed Project \"" + name + "\" from this channel!" + (fullyRemoved ? "" : "\nFailed to remove channel from memory")).queue();
            } else
            {
                ev.reply("This project does not seem to be attached to this channel :thinking:").queue();
            }
        }
    }
}
