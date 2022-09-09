package com.readonlydev.updates.commands;

import java.awt.Color;
import java.util.Arrays;

import com.readonlydev.command.slash.SlashCommandEvent;
import com.readonlydev.updates.commands.core.CFCommand;

import de.erdbeerbaerlp.cfcore.CFCoreAPI;
import de.erdbeerbaerlp.cfcore.json.CFFileIndex;
import de.erdbeerbaerlp.cfcore.json.CFMod;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class FetchLatestFileCommand extends CFCommand
{

    public FetchLatestFileCommand()
    {
        this.name = "updates getLatestFile";
        this.help = "Development command, fetches an curseforge project";
        options = Arrays.asList(
            new OptionData(OptionType.INTEGER, "project-id", "ID of the Curseforge Project", true)
        );
    }

    @Override
    public void execute(SlashCommandEvent ev)
    {
        final OptionMapping id = ev.getOption("project-id");
        if (ev.getUser().getIdLong() == 393847930039173131L)
        {
            if (id == null)
            {
                ev.replyEmbeds(simpleEmbed("Project ID == null ?!?", Color.RED)).queue();
            } else
            {
                try
                {
                    final int projectID = Integer.parseInt(id.getAsString());
                    final CFMod curseProject = CFCoreAPI.getModFromID(projectID);
                    final CFFileIndex[] files = curseProject.latestFilesIndexes;
                    final EmbedBuilder b = new EmbedBuilder();
                    b.setTitle(curseProject.name, curseProject.links.websiteUrl);
                    b.addField("Primary Category ID", curseProject.primaryCategoryId + "", false);
                    for (int i = 0; i < Math.min(5, files.length); i++)
                    {
                        final CFFileIndex curseFile = files[i];
                        b.addField(curseFile.filename, curseFile.fileId + "", true);
                    }
                    ev.replyEmbeds(b.build()).queue();
                } catch (Exception e)
                {
                    ev.getHook().deleteOriginal().queue();
                }
            }
        } else
        {
            ev.replyEmbeds(simpleEmbed("You don't look like the developer, do you?\n(This command can only be used by the developer)", Color.RED)).queue();
        }
    }
}
