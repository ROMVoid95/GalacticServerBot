package io.github.romvoid95;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import io.github.readonly.command.ServerCommands;
import io.github.readonly.command.SlashCommand;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;

@Slf4j
public class Server
{
	private final String guildId;
	@Getter
	private ServerCommands serverCommands = null;
	private Guild guild = null;
	@Getter
	private final Map<String, String> slashCmdNameToIdMap = new HashMap<>();
	
	public static Optional<Server> get(Guild guild)
	{
		return Optional.ofNullable(BotData.serverMap().get(guild.getId()));
	}
	
	public Server(String guildId)
	{
		this(guildId, true);
	}
	
	Server(String guildId, boolean genServerCommands)
	{
		this.guildId = guildId;
		if(genServerCommands) {
			this.serverCommands = new ServerCommands();
			this.serverCommands.setGuildId(guildId);
		}
	}
	
	void initGuild(JDA jda)
	{
		this.guild = jda.getGuildById(guildId);
	}
	
	public boolean hasCommand(@Nonnull String commandName)
	{
		return this.slashCmdNameToIdMap.containsKey(commandName);
	}
	
	public String commandId(@Nonnull String commandName)
	{
		if(this.slashCmdNameToIdMap.containsKey(commandName))
		{
			return this.slashCmdNameToIdMap.get(commandName);
		}
		
		return "";
	}
	
	public void addSlashCommands(Collection<SlashCommand> commands)
	{
		if(this.serverCommands != null)
		{
			log.info("Server SlashCommands (ID: " + String.valueOf(this.guildId) + ")");
			log.info(String.join(" | ", commands.stream().map(SlashCommand::getName).collect(Collectors.toSet())));
			this.serverCommands.getSlashCommands().addAll(commands);
		} else {
			throw new IllegalStateException("Attempted to add SlashCommands to Server "+guildId+" with invalid ServerCommands object");
		}
	}
	
	public Optional<Guild> getGuild()
	{
		return Optional.ofNullable(guild);
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(obj == null || !(obj instanceof Server))
		{
			return false;
		}
		
		Server other = (Server) obj;
		
		if(other.guild == null)
		{
			return false;
		}
		
		if(!other.getGuild().isPresent() || !this.getGuild().isPresent())
		{
			return false;
		}
		
		return this.guild.getId().equals(other.getGuild().get().getId());
	}
}
