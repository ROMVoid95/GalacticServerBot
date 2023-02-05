package io.github.romvoid95.commands.core;

import java.util.Map.Entry;

import io.github.readonly.command.lists.ChoiceList;
import io.github.readonly.command.lists.OptionsList;
import io.github.readonly.command.option.Choice;
import io.github.readonly.command.option.RequiredOption;
import io.github.romvoid95.BotData;
import io.github.romvoid95.GalacticBot;
import io.github.romvoid95.database.entity.DBUpdates;
import io.github.romvoid95.database.impl.updates.UpdateMod;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class SlashOptions
{
	private static OptionData ViableChannels = RequiredOption.channel("channel", "Available Channels that Members are able to send messages in");

	public static OptionData getViableChannelsOption(Guild guild)
	{
		Role		role	= guild.getRoleById(691198175519965184L);
		ChoiceList	list	= new ChoiceList();
		guild.getTextChannels().forEach(channel ->
		{
			if (role.hasPermission(channel, Permission.MESSAGE_SEND))
			{
				list.add(Choice.add(channel.getAsMention(), channel.getId()));
			}
		});
		return ViableChannels.addChoices(list);
	}

	public static class Suggestion
	{
		public static OptionsList OptionsList()
		{
			return new OptionsList(
			//@noformat
				RequiredOption.text("type", "Suggestion Type", ChoiceList.of(
					Choice.add("Galacticraft 5", "[Galacticraft 5]"),
					Choice.add("Galacticraft-Legacy", "[Galacticraft-Legacy]"),
					Choice.add("Idea For New Addon", "[Addon Idea]"),
					Choice.add("Do Not Make Suggestions For Existing Addons", "existing")
					)
				),
				RequiredOption.text("title", "Short generalized title for your suggestion", 64),
				RequiredOption.text("description", "Describe in detail your suggestion", 1024));
				//@format
		}
	}

	public static class UpdateMods
	{
		public static ChoiceList getChoiceList(String guildId)
		{
			ChoiceList	list	= new ChoiceList();
			DBUpdates	updates	= BotData.database().updates();

			for (Entry<String, UpdateMod> e : updates.getMods().entrySet())
			{
				String name = e.getKey();
				if (e.getValue().getNotifications().containsKey(guildId))
				{
					list.add(Choice.add(name));
				}
			}

			list.forEach(c -> System.out.println(c.getName()));
			return list;
		}
	}

	public static OptionData getServers()
	{
		OptionData servers = new OptionData(OptionType.STRING, "guild", "Choose a Guild", true);

		for (Guild guild : GalacticBot.instance().getJda().getGuilds())
		{
			servers.addChoice(guild.getName(), guild.getId());
		}

		return servers;
	}
}
