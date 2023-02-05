package io.github.romvoid95.server;

import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;

public class Server
{
	@Getter
	private final String guildId;
	private Guild guild;
	
	public static Server of(Guild guild)
	{
		return new Server(guild);
	}
	
	public static Server of(String guildId)
	{
		return new Server(guildId);
	}
	
	Server(String guildId)
	{
		this.guildId = guildId;
	}
	
	Server(Guild guild)
	{
		this.guild = guild;
		this.guildId = guild.getId();
	}
	
	public void initGuild(JDA jda)
	{
		this.guild = jda.getGuildById(this.getGuildId());
	}
	
	public Guild getGuild()
	{
		if(guild == null)
		{
			throw new RuntimeException("Attempted to get guild (%s) before JDA initialized it".formatted(getGuildId()));
		}
		
		return guild;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(obj == null || !(obj instanceof Server))
		{
			return false;
		}
		
		Server other = (Server) obj;

		return this.guildId.equals(other.guildId);
	}
}
