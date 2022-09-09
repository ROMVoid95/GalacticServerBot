package com.readonlydev.updates.commands;

import java.awt.Color;
import java.util.Arrays;

import com.readonlydev.command.slash.SlashCommandEvent;
import com.readonlydev.updates.CurseforgeProject;
import com.readonlydev.updates.Updates;
import com.readonlydev.updates.commands.core.CFCommand;

import de.erdbeerbaerlp.cfcore.CFCoreAPI;
import de.erdbeerbaerlp.cfcore.json.CFFileIndex;
import de.erdbeerbaerlp.cfcore.json.CFMod;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class AddProjectCommand extends CFCommand
{

    public AddProjectCommand()
    {
        this.name = "updates add";
        this.help = "Adds an curseforge project to this channel";
        this.options = Arrays.asList(new OptionData(OptionType.INTEGER, "project-id", "ID of the Curseforge Project", true));
    }

    @Override
    protected void execute(SlashCommandEvent event)
    {
        final OptionMapping id = event.getOption("project-id");
        if (event.getMember().hasPermission(Permission.MANAGE_SERVER))
        {
            if (id == null)
            {
                event.replyEmbeds(simpleEmbed("Project ID == null ?!?", Color.RED)).queue();
            } else
            {
                final long projectID = Long.parseLong(id.getAsString());
                if (Updates.projects.containsKey(projectID))
                {
                    Updates.projects.get(projectID).addChannel(Updates.ifa.addChannelToProject(projectID, event.getChannel().getIdLong()));
                    event.replyEmbeds(simpleEmbed("Attached Project \"" + Updates.projects.get(projectID).proj.name + "\" to this channel!" + (Updates.projects.get(projectID).proj.latestFilesIndexes.length == 0
                        ? "\n*This project does not have any files yet. If there are files already, this project's game or category is not (yet) supported*" : ""))).queue();

                } else
                {
                    final CFMod mod = CFCoreAPI.getModFromID(projectID);
                    if (mod != null)
                    {
                        CurseforgeProject prj = new CurseforgeProject(mod, Updates.ifa.getOrCreateCFChannel(event.getChannel().getIdLong()));
                        Updates.ifa.addChannelToProject(projectID, event.getChannel().getIdLong());
                        final CFFileIndex[] curseFiles = mod.latestFilesIndexes;
                        if (curseFiles.length != 0)
                            Updates.ifa.updateCache(projectID, curseFiles[0].fileId);
                        Updates.projects.put(projectID, prj);
                        event.replyEmbeds(simpleEmbed("Attached Project \"" + prj.proj.name + "\" to this channel!" + (curseFiles.length == 0 ? "\n*This project does not have any files yet. If there are files already, this project's game or category is not (yet) supported*" : ""))).queue();
                    } else
                    {
                        event.replyEmbeds(simpleEmbed("Project does not exist!")).queue();
                    }
                }
            }
        } else
        {
            event.replyEmbeds(simpleEmbed("Permission [MANAGE_SERVER] is required to run this command", Color.RED)).queue();
        }
    }
}
