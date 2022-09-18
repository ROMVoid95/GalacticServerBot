package com.readonlydev.commands.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

import com.readonlydev.BotData;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.Command.Choice;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class SlashOptions
{
    private static OptionData ViableChannels = new OptionData(OptionType.STRING, "channel", "Available Channels that Members are able to send messages in", true);
    
    public static OptionData getViableChannelsOption(Guild guild)
    {
        Role role = guild.getRoleById(691198175519965184L);
        List<Command.Choice> choices = new ArrayList<>();
        guild.getTextChannels().forEach(channel -> {
            
            if(role.hasPermission(channel, Permission.MESSAGE_SEND))
            {
                choices.add(new Choice("#" + channel.getName(), channel.getId()));
            }
        });
        return ViableChannels.addChoices(choices);
    }

    public static class Suggestion
    {        
        //@noformat
        private static OptionData TypeOption = new OptionData(OptionType.STRING, "type", "Suggestion Type", true)
            .addChoices(
                new Command.Choice("Galacticraft 5", "[Galacticraft 5]"),
                new Command.Choice("Galacticraft-Legacy", "[Galacticraft-Legacy]"),
                new Command.Choice("Idea For New Addon", "[Addon Idea]"),
                new Command.Choice("Do Not Make Suggestions For Existing Addons", "existing")
             );
        //@format
        private static OptionData TitleOption       = new OptionData(OptionType.STRING, "title", "Short generalized title for your suggestion", true).setMaxLength(64);
        private static OptionData DescriptionOption = new OptionData(OptionType.STRING, "description", "Describe in detail your suggestion", true).setMaxLength(1024);
        
        public static  List<OptionData> OptionsList()
        {
            return Arrays.asList(TypeOption, TitleOption, DescriptionOption);
        }
    }

    public static class DeleteSuggestion
    {
        private static OptionData SuggestionOption = new OptionData(OptionType.STRING, "number", "Suggestion #", true);
        
        public static OptionData getSuggestionOptions()
        {
            List<Command.Choice> choices = new ArrayList<>();
            for(Entry<String, Integer> entry : BotData.database().botDatabase().getManager().getMap().entrySet())
            {
                choices.add(new Choice("#" + entry.getValue(), entry.getKey()));
            }
            return SuggestionOption.addChoices(choices);
        }
    }
}
