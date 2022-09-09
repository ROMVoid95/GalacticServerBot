package com.readonlydev.commands.staff.suggestions;

import java.awt.Color;
import java.util.Arrays;

import com.readonlydev.BotData;
import com.readonlydev.command.slash.SlashCommand;
import com.readonlydev.command.slash.SlashCommandEvent;
import com.readonlydev.database.entity.DBGalacticBot;
import com.readonlydev.util.Check;
import com.readonlydev.util.discord.Reply;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class PostChannel extends SlashCommand
{

    public PostChannel()
    {
        this.name = "channel";
        this.help = "Display or change the channel new Suggestions are posted in";
        this.options = Arrays.asList(new OptionData(OptionType.CHANNEL, "channel", "Guild Channel to use for new Suggestions"));
    }

    @Override
    protected void execute(SlashCommandEvent event)
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
            String suggestionsChannelId = db.getSuggestionOptions().getSuggestionsChannelId();
            TextChannel suggestionsChannel = event.getGuild().getTextChannelById(suggestionsChannelId);

            //@noformat
            Reply.Success(event, new EmbedBuilder().setColor(Color.ORANGE).setTitle("Current Suggestions Channel")
                .addField(EmbedBuilder.ZERO_WIDTH_SPACE, 
                    String.format("**Name:** ................... %s", suggestionsChannel.getAsMention()) + "\n" +
                    String.format("**ID:** .......................... %s", suggestionsChannel.getId()) + "\n" +
                    String.format("**In Category:** ........ %s", suggestionsChannel.getParentCategory().getName()), false));
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
                Reply.Error(event, "You must choose a Text Channel to use for Suggestions");
                return;
            }
            final DBGalacticBot db = BotData.database().botDatabase();
            String suggestionsChannelId = db.getSuggestionOptions().getSuggestionsChannelId();
            TextChannel oldChannel = event.getGuild().getTextChannelById(suggestionsChannelId);
            TextChannel newChannel = channelOption.getAsChannel().asTextChannel();

            //Check if it is already the set channel
            if (suggestionsChannelId.equals(newChannel.getId()))
            {
                Reply.Success(event, "No operations performed: Selected channel is already the Suggestions channel");
                return;
            }

            // Set new channel ID and send reply
            db.getSuggestionOptions().setSuggestionsChannelId(newChannel.getId());
            db.saveUpdating();
            if(oldChannel != null)
            {
                Reply.Success(event, "Sucessfully changed Suggestions Channel: " + oldChannel.getAsMention() + " -> " + newChannel.getAsMention());
            } else {
                Reply.Success(event, "Sucessfully set Suggestions Channel: " + newChannel.getAsMention());
            }
            
           
        }
    }
}