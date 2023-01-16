package com.readonlydev;

import com.readonlydev.command.client.ServerCommands;
import com.readonlydev.common.utils.SafeIdUtil;

import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;

public class Server
{
	private final long guildId;
	@Getter
	private final ServerCommands serverCommands;
	@Getter
	private Guild guild;
	
	
	public Server(long guildId)
	{
		this.guildId = guildId;
		this.serverCommands = new ServerCommands(guildId);
	}
	
	public Server(String guildId)
	{
		this(SafeIdUtil.safeConvert(guildId));
	}
	
	void initGuild(JDA jda)
	{
		this.guild = jda.getGuildById(this.guildId);
	}
	
	
}
