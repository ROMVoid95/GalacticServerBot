package io.github.romvoid95.core.event;

import io.github.readonly.common.event.JDAToolsEvent;

import lombok.Getter;
import net.dv8tion.jda.api.JDA;

public abstract class BotEvent implements JDAToolsEvent
{
	@Getter
	private final JDA jda;
	
	public BotEvent(JDA jda)
	{
		this.jda = jda;
	}
}
