package io.github.romvoid95;

import com.google.common.eventbus.Subscribe;

import io.github.readonly.common.event.jda.PostReadyEvent;
import io.github.romvoid95.core.event.MaintenanceEvent;
import io.github.romvoid95.database.impl.options.ServerOptions;
import io.github.romvoid95.updates.UpdateManager;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.managers.Presence;

@Slf4j
public class BusListener
{
	@Subscribe
	public void onMaintanenceEvent(MaintenanceEvent event)
	{
		event.getAction().setActivity(event.getJda().getPresence());
	}

	@Subscribe
	public void onPostReadyEvent(PostReadyEvent event)
	{
		JDA jda = event.getEvent().getJDA();

		jda.getGuilds().forEach(guild ->
		{
			ServerOptions options = BotData.database().galacticBot().createServerOptionsIfMissing(guild);
			if (options == null)
			{
				log.error("An error occured initializing ServerOptions for guild %s [%s]".formatted(guild.getName(), guild.getId()));
			} else
			{
				log.info("ServerOptions initialized for guild %s [%s]".formatted(guild.getName(), guild.getId()));
			}
		});

		if (BotData.database().galacticBot().isMaintenanceMode())
		{
			Presence presence = jda.getPresence();
			presence.setStatus(OnlineStatus.DO_NOT_DISTURB);
			presence.setActivity(Activity.playing("Maintanence Mode"));
		}

		for (Server server : BotData.serverMap().values())
		{
			server.initGuild(jda);
		}
		
		new UpdateManager();
	}
}
