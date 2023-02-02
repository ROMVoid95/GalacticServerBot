package io.github.romvoid95;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.google.common.eventbus.Subscribe;

import io.github.readonly.command.lists.ChoiceList;
import io.github.readonly.command.option.RequiredOption;
import io.github.readonly.common.event.jda.PostReadyEvent;
import io.github.romvoid95.commands.core.SlashOptions;
import io.github.romvoid95.commands.staff.updates.ModPlatformCommand;
import io.github.romvoid95.commands.staff.updates.UpdatesCommand;
import io.github.romvoid95.core.event.MaintenanceEvent;
import io.github.romvoid95.core.event.ModUpdateEvent;
import io.github.romvoid95.database.impl.options.ServerOptions;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.Command.Subcommand;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.managers.Presence;

@Slf4j
public class BusListener
{
	@Subscribe
	public void onModUpdateAdded(ModUpdateEvent.Add event)
	{
		if (event.getServer().isPresent())
		{
			Server server = event.getServer().get();
			if (server.hasCommand("updates"))
			{
				this.updateModPlatformCommand(server, SlashOptions.UpdateMods.getGuildChoiceList(server.getGuild().get().getId()));
			}
		}
	}

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

		for (Guild guild : jda.getGuilds())
		{
			log.info("Server: " + guild.getName());
			Server s;
			if (!BotData.serverMap().containsKey(guild.getId()))
			{
				s = BotData.serverMap().put(guild.getId(), new Server(guild.getId(), false));
			} else
			{
				s = BotData.serverMap().get(guild.getId());
			}

			List<Command> cmdList = tryGetCommands(guild);
			if (!cmdList.isEmpty())
			{
				for (Command cmd : cmdList)
				{
					if (cmd.getSubcommands().size() != 0)
					{
						for (Subcommand subCmd : cmd.getSubcommands())
						{
							s.getSlashCmdNameToIdMap().put(subCmd.getFullCommandName(), subCmd.getId());
							log.debug("'" + subCmd.getFullCommandName() + "' | '" + subCmd.getId() + "'");
						}
					}

					s.getSlashCmdNameToIdMap().put(cmd.getFullCommandName(), cmd.getId());
					log.debug("'" + cmd.getFullCommandName() + "' | '" + cmd.getId() + "'");
				}
			}

			if (!SlashOptions.UpdateMods.getGuildChoiceList(guild.getId()).isEmpty())
			{
				this.updateModPlatformCommand(s, SlashOptions.UpdateMods.getGuildChoiceList(s.getGuild().get().getId()));
			}
		}
	}
	
	private void updateModPlatformCommand(Server server , ChoiceList choices)
	{
		OptionData			newOptions	= RequiredOption.text("mod", "The Mod you want to update", choices);
		ModPlatformCommand	mpc			= new ModPlatformCommand(newOptions, ModPlatformCommand.curseforgeOpt, ModPlatformCommand.modrinthOpt);
		UpdatesCommand		updates		= new UpdatesCommand(mpc);

		server.getGuild().get().editCommandById(server.commandId("updates")).apply(updates.build()).queue();
	}

	private static List<Command> tryGetCommands(Guild guild)
	{
		try
		{
			return guild.retrieveCommands().submit().get();
		} catch (InterruptedException | ExecutionException e)
		{
			log.error("Encountered error trying to retrieveCommands for " + guild.getName());
			log.trace("TRACE:", e);
			return Collections.emptyList();
		}
	}
}
