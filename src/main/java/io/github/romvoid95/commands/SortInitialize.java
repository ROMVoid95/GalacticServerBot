package io.github.romvoid95.commands;

import java.util.Arrays;
import java.util.List;

import com.github.readonlydevelopment.command.ClientBuilder;
import com.github.readonlydevelopment.command.SlashCommand;

import io.github.romvoid95.BotData;
import io.github.romvoid95.commands.member.CloseDiscussionThread;
import io.github.romvoid95.commands.member.NewSuggestion;
import io.github.romvoid95.commands.owner.DatabaseCommand;
import io.github.romvoid95.commands.owner.EvalCommand;
import io.github.romvoid95.commands.owner.ExecCommand;
import io.github.romvoid95.commands.owner.MaintanenceModeCommand;
import io.github.romvoid95.commands.staff.Suggestions;
import io.github.romvoid95.commands.staff.server.ServerStaff;
import io.github.romvoid95.commands.staff.suggestions.devonly.DevServerPopularChannel;
import io.github.romvoid95.commands.staff.suggestions.devonly.SuggestionSetStatus;

public class SortInitialize
{
	public static void perform(ClientBuilder clientBuilder)
	{
		BotData.botDevServer().addSlashCommands(SortInitialize.botServerCommands());
		BotData.galacticraftCentralServer().addSlashCommands(SortInitialize.centralServerCommands());
		BotData.teamGalacticraftServer().addSlashCommands(SortInitialize.teamServerCommands());
		
		clientBuilder.addServerCommands(BotData.botDevServer().getServerCommands());
		clientBuilder.addServerCommands(BotData.galacticraftCentralServer().getServerCommands());
		clientBuilder.addServerCommands(BotData.teamGalacticraftServer().getServerCommands());
	}

	private static final List<SlashCommand> centralServerCommands()
	{
		return Arrays.asList(new Suggestions(), new ServerStaff(), new NewSuggestion(), new CloseDiscussionThread());
	}
	
	private static final List<SlashCommand> teamServerCommands()
	{
		return Arrays.asList(new DevServerPopularChannel(), new SuggestionSetStatus());
	}
	
	private static final List<SlashCommand> botServerCommands()
	{
		return Arrays.asList(new DatabaseCommand(), new EvalCommand(), new ExecCommand(), new MaintanenceModeCommand());
	}
}
