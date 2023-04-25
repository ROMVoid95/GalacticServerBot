package io.github.romvoid95.commands.staff.suggestions;

import java.awt.Color;

import io.github.readonly.command.event.SlashCommandEvent;
import io.github.readonly.command.option.Option;
import io.github.romvoid95.BotData;
import io.github.romvoid95.commands.core.GalacticSlashCommand;
import io.github.romvoid95.database.entity.DBGalacticBot;
import io.github.romvoid95.util.Check;
import io.github.romvoid95.util.discord.Reply;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;

public class PopularChannel extends GalacticSlashCommand
{

    public PopularChannel()
    {
        name("popular-channel");
        description("Display or change the current channel Popular Suggestions are posted in");
        setOptions(Option.channel("channel", "Guild Channel to use for Popular Suggestions"));
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

            final DBGalacticBot db               = BotData.database().galacticBot();
            String              popularChannelId = db.getSuggestionOptions().getPopularChannelId();
            TextChannel         popularChannel   = event.getGuild().getTextChannelById(popularChannelId);

            //@noformat
            Reply.Success(event, new EmbedBuilder().setColor(Color.ORANGE).setTitle("Current Popular Channel")
                .addField(EmbedBuilder.ZERO_WIDTH_SPACE, 
                    String.format("**Name:** ................... %s", popularChannel.getAsMention()) + "\n" +
                    String.format("**ID:** .......................... %s", popularChannel.getId()) + "\n" +
                    String.format("**In Category:** ........ %s", popularChannel.getParentCategory().getName()), false));
            //@format

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
            final DBGalacticBot db               = BotData.database().galacticBot();
            String              popularChannelId = db.getSuggestionOptions().getPopularChannelId();
            TextChannel         oldChannel       = event.getGuild().getTextChannelById(popularChannelId);
            TextChannel         newChannel       = channelOption.getAsChannel().asTextChannel();

            //Check if it is already the set channel
            if (popularChannelId.equals(newChannel.getId()))
            {
                Reply.Success(event, "No operations performed: Selected channel is already the Popular Suggestions channel");
                return;
            }

            // Set new channel ID and send reply
            db.getSuggestionOptions().setPopularChannelId(newChannel.getId());
            db.saveUpdating();
            if (oldChannel != null)
            {
                Reply.Success(event, "Sucessfully changed Popular-Suggestions channel: " + oldChannel.getAsMention() + " -> " + newChannel.getAsMention());
            } else
            {
                Reply.Success(event, "Sucessfully set Popular-Suggestions channel: " + newChannel.getAsMention());
            }
        }
    }
}
