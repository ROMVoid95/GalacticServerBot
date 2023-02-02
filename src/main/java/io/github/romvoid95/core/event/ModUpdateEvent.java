package io.github.romvoid95.core.event;

import java.util.Optional;

import io.github.romvoid95.GalacticBot;
import io.github.romvoid95.Server;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Guild;

@Getter
public abstract class ModUpdateEvent extends BotEvent
{
	private String commandId;
	private Optional<Server> server;
	
	public ModUpdateEvent(String commandId, Guild guild)
	{
		super(GalacticBot.instance().getJda());
		this.commandId = commandId;
		this.server = Server.get(guild);
	}
	
	public static class Add extends ModUpdateEvent
	{

		public Add(String commandId, Guild guild)
		{
			super(commandId, guild);
		}
	}
	
	public static class Remove extends ModUpdateEvent
	{

		public Remove(String commandId, Guild guild)
		{
			super(commandId, guild);
		}
	}
}
