package io.github.romvoid95;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import com.github.readonlydevelopment.command.ServerCommands;
import com.github.readonlydevelopment.command.SlashCommand;

import io.github.romvoid95.logback.LogUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;

@Slf4j
public class Server
{
	private final long guildId;
	@Getter
	private ServerCommands serverCommands = null;
	private Guild guild = null;
	
	public static Server getServer(Guild guild)
	{
		return new Server(guild.getIdLong());
	}
	
	public Server(long guildId)
	{
		this(guildId, true);
	}
	
	Server(long guildId, boolean genServerCommands)
	{
		this.guildId = guildId;
		if(genServerCommands) {
			this.serverCommands = new ServerCommands();
			this.serverCommands.setGuildIdLong(guildId);
		}
	}
	
	void initGuild(JDA jda)
	{
		if(this.guild == null)
			this.guild = jda.getGuildById(this.guildId);
	}
	
	public void addSlashCommands(Collection<SlashCommand> commands)
	{
		if(this.serverCommands != null)
		{
			LogUtils.log("Server SlashCommands (ID: " + String.valueOf(this.guildId) + ")", String.join("\n", commands.stream().map(SlashCommand::getName).collect(Collectors.toSet())));
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
