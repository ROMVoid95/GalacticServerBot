package com.readonlydev.commands.staff.suggestions.devonly;

import java.awt.Color;
import java.util.Arrays;

import com.readonlydev.BotData;
import com.readonlydev.command.slash.SlashCommandEvent;
import com.readonlydev.commands.core.GalacticSlashCommand;
import com.readonlydev.database.entity.DBGalacticBot;
import com.readonlydev.util.Check;
import com.readonlydev.util.discord.Reply;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;

public class DevServerPopularChannel extends GalacticSlashCommand
{

    public DevServerPopularChannel()
    {
        this.name = "popular-channel";
        this.help = "Display or change the current channel Popular Suggestions are copy-posted in on the Dev Server";
        this.options = Arrays.asList(new OptionData(OptionType.CHANNEL, "channel", "Guild Channel to use for Popular Suggestions"));
        this.subcommandGroup = new SubcommandGroupData("dev-server", "Popular-Suggestions in the Development Server");
    }

    @Override
    protected void onExecute(SlashCommandEvent event)
    {
        if (event.getOptions().isEmpty())
        {
            boolean canRun = Check.staffRoles(event);

            if (!canRun)
            {
                Reply.InvalidPermissions(event);
                return;
            }

            final DBGalacticBot db = BotData.database().botDatabase();
            String popularChannelId = db.getSuggestionOptions().getDevServerPopularChannelId();
            TextChannel popularChannel = event.getGuild().getTextChannelById(popularChannelId);

            Reply.Success(event, new EmbedBuilder().setColor(Color.ORANGE).setTitle("Suggestions").addField("Current Dev Server Popular Channel", popularChannel.getAsMention(), false));
        } else
        {
            boolean canRun = Check.adminRoles(event);

            if (!canRun)
            {
                Reply.InvalidPermissions(event);
                return;
            }

            OptionMapping channelOption = event.getOptionsByType(OptionType.CHANNEL).get(0);

            // Make sure selected channel is a TextChannel
            if (!channelOption.getChannelType().equals(ChannelType.TEXT))
            {
                Reply.Error(event, "You must choose a Text Channel to use for Popular Suggestions");
                return;
            }
            final DBGalacticBot db = BotData.database().botDatabase();
            String popularChannelId = db.getSuggestionOptions().getDevServerPopularChannelId();
            TextChannel oldChannel = event.getGuild().getTextChannelById(popularChannelId);
            TextChannel newChannel = channelOption.getAsChannel().asTextChannel();

            //Check if it is already the set channel
            if (popularChannelId.equals(newChannel.getId()))
            {
                Reply.Success(event, "No operations performed: Selected channel is already the Popular Suggestions channel");
                return;
            }

            // Set new channel ID and send reply
            db.getSuggestionOptions().setDevServerPopularChannelId(newChannel.getId());
            db.saveUpdating();
            if(oldChannel != null)
            {
                Reply.Success(event, "Sucessfully changed Popular-Suggestions channel: " + oldChannel.getAsMention() + " -> " + newChannel.getAsMention());
            } else {
                Reply.Success(event, "Sucessfully set Popular-Suggestions channel: " + newChannel.getAsMention());
            }
        }

    }
}
