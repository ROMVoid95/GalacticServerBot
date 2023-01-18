package io.github.romvoid95.core.event;

import net.dv8tion.jda.api.events.Event;

public class JDAEvent<T extends Event>
{
	private T event;
	
	public JDAEvent(T event)
	{
		this.event = event;
	}
	
	public T getEvent()
	{
		return event;
	}
}
