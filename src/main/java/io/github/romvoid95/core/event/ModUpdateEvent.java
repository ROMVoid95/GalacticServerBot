package io.github.romvoid95.core.event;

import java.util.Optional;

import io.github.romvoid95.GalacticBot;
import io.github.romvoid95.Server;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Guild;

@Getter
public abstract class ModUpdateEvent extends BotEvent
{
	private String mod;
	private Optional<Server> server;
	
	public ModUpdateEvent(String mod, Guild guild)
	{
		super(GalacticBot.instance().getJda());
		this.server = Server.get(guild);
		this.mod = mod;
	}
	
	public static class Add extends ModUpdateEvent
	{

		public Add(String mod, Guild guild)
		{
			super(mod, guild);
		}
	}
	
	public static class Remove extends ModUpdateEvent
	{

		public Remove(String mod, Guild guild)
		{
			super(mod, guild);
		}
	}
}
