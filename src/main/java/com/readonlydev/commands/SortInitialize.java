package com.readonlydev.commands;

import java.util.Arrays;
import java.util.List;

import com.readonlydev.BotData;
import com.readonlydev.command.client.ClientBuilder;
import com.readonlydev.command.slash.SlashCommand;
import com.readonlydev.commands.member.CloseDiscussionThread;
import com.readonlydev.commands.member.NewSuggestion;
import com.readonlydev.commands.owner.DatabaseCommand;
import com.readonlydev.commands.owner.EvalCommand;
import com.readonlydev.commands.owner.ExecCommand;
import com.readonlydev.commands.owner.MaintanenceModeCommand;
import com.readonlydev.commands.staff.Suggestions;
import com.readonlydev.commands.staff.server.ServerStaff;
import com.readonlydev.commands.staff.suggestions.devonly.DevServerPopularChannel;
import com.readonlydev.commands.staff.suggestions.devonly.SuggestionSetStatus;

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
