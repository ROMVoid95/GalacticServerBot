package io.github.romvoid95.server;

import java.util.Collection;

import io.github.readonly.command.SlashCommand;
import io.github.readonly.common.ServerCommands;
import lombok.Getter;

public class SlashCommandServer extends Server
{
	@Getter
	private final ServerCommands serverCommands;

	public static SlashCommandServer of(String guildId)
	{
		return new SlashCommandServer(guildId);
	}
	
	SlashCommandServer(String guildId)
	{
		super(guildId);
		this.serverCommands = new ServerCommands();
		this.serverCommands.setGuildId(guildId);
	}

	public void addSlashCommands(Collection<SlashCommand> commands)
	{
		if(this.serverCommands != null)
		{
			this.serverCommands.getSlashCommands().addAll(commands);
		} else {
			throw new IllegalStateException("Attempted to add SlashCommands to Server "+getGuildId()+" with invalid ServerCommands object");
		}
	}
}
