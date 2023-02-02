package io.github.romvoid95.core.event;

import io.github.romvoid95.GalacticBot;
import lombok.Getter;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.managers.Presence;

public class MaintenanceEvent extends BotEvent
{
	public static enum Action
	{
		Enabled {
			@Override
			public void setActivity(Presence presence)
			{
				presence.setPresence(OnlineStatus.DO_NOT_DISTURB, Activity.playing("Maintanence Mode"));
			}
		},
		Disabled {
			@Override
			public void setActivity(Presence presence)
			{
				presence.setPresence(OnlineStatus.DO_NOT_DISTURB, Activity.watching("for Suggestions"));
			}
		};
		
		public abstract void setActivity(Presence presence);
	}
	
	@Getter
	private final MaintenanceEvent.Action action;
	
	public MaintenanceEvent(MaintenanceEvent.Action action)
	{
		super(GalacticBot.instance().getJda());
		this.action = action;
	}
}
