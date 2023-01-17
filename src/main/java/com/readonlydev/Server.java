package com.readonlydev;

import java.util.Collection;
import java.util.stream.Collectors;

import com.readonlydev.command.client.ServerCommands;
import com.readonlydev.command.slash.SlashCommand;
import com.readonlydev.logback.LogUtils;

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
	@Getter
	private Guild guild = null;
	
	public Server(Guild guild)
	{
		this(guild.getIdLong(), false);
		this.guild = guild;
	}
	
	public Server(long guildId)
	{
		this(guildId, true);
	}
	
	Server(long guildId, boolean genServerCommands)
	{
		this.guildId = guildId;
		if(genServerCommands) {
			this.serverCommands = new ServerCommands(guildId);
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
			this.serverCommands.addAllCommands(commands);
		}
		
		throw new IllegalStateException("Attempted to add SlashCommands to Server instance with invalid ServerCommands object");
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
		
		return this.guild.getId().equals(other.getGuild().getId());
	}
}
