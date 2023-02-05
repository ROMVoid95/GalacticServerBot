package io.github.romvoid95;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.github.romvoid95.server.Server;
import io.github.romvoid95.server.SlashCommandServer;

public class Servers
{
	public static final SlashCommandServer	readOnlyNetwork		= SlashCommandServer.of("538530739017220107");
	public static final SlashCommandServer	galacticraftCentral	= SlashCommandServer.of("449966345665249290");
	public static final SlashCommandServer	teamGalacticraft	= SlashCommandServer.of("775251052517523467");
	
	public static List<Server> allServers = new ArrayList<>();
	
	static {
		Servers.allServers.addAll(Arrays.asList(readOnlyNetwork, galacticraftCentral, teamGalacticraft));
	}
}
