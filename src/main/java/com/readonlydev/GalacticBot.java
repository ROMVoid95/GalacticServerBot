package com.readonlydev;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.readonlydev.command.client.Client;
import com.readonlydev.command.client.ClientBuilder;
import com.readonlydev.commands.SortInitialize;
import com.readonlydev.commands.member.EditDescription;
import com.readonlydev.commands.member.EditTitle;
import com.readonlydev.common.waiter.EventWaiter;
import com.readonlydev.core.BusListener;
import com.readonlydev.core.ClientListener;
import com.readonlydev.core.GalacticEventListener;
import com.readonlydev.core.GuildSettings;
import com.readonlydev.core.event.JDAEvent;
import com.readonlydev.logback.LogFilter;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.requests.ErrorResponse;
import net.dv8tion.jda.api.requests.RestAction;

@Slf4j
public class GalacticBot
{

	private static boolean TESTING = true;

	private static JDA			jda;
	private static Client		client;
	private static EventWaiter	eventWaiter	= new EventWaiter();
	private static EventBus		EVENT_BUS	= new EventBus("GalacticBot EventBus");

	private void preStart()
	{
		log.info("Starting up %s {}, Git revision: {}", "GalacticBot", Info.VERSION, Info.GIT_REVISION);
		log.info("Reporting UA {} for HTTP requests.", Info.USER_AGENT);

		RestAction.setPassContext(true);
		RestAction.setDefaultFailure(ErrorResponseException.ignore(RestAction.getDefaultFailure(), ErrorResponse.UNKNOWN_MESSAGE));

		log.info("Filtering all logs below {}", LogFilter.LEVEL);
	}

	private GalacticBot() throws Exception
	{
		preStart();

		Conf.saveBotConfigJson();

		ClientBuilder clientBuilder = new ClientBuilder();

		SortInitialize.perform(clientBuilder);
		clientBuilder.setAllRepliesAsEmbed();
		clientBuilder.addGlobalSlashCommands(new EditDescription(), new EditTitle());
		clientBuilder.setOwnerId(Conf.Bot().getOwner());
		clientBuilder.setPrefix(Conf.Bot().getPrefix());
		clientBuilder.setActivity(Activity.watching("for Suggestion"));
		clientBuilder.useHelpBuilder(false);
		clientBuilder.setListener(new ClientListener());
		clientBuilder.setGuildSettingsManager(new GuildSettings());

		// @noformat
		GalacticBot.client = clientBuilder.build();

		GalacticBot.jda = JDABuilder.create(Conf.Bot().getToken(), BotData.JDA.INTENTS)
			.disableCache(BotData.JDA.DISABLED_CACHE_FLAGS)
			.setActivity(Activity.playing("Init Stage"))
			.addEventListeners(eventWaiter, client, new GalacticEventListener())
			.build();
		// @format

		EVENT_BUS.register(new BusListener());
		EVENT_BUS.register(this);
	}

	public static void main(String[] args) throws Exception
	{
		new GalacticBot();
		Runtime.getRuntime().addShutdownHook(new Thread(GalacticBot::shutdown));
	}

	private static void shutdown()
	{
		BotData.galacticExecutor().shutdown();
		BotData.database().getConnection().close();
		getJda().shutdownNow();
	}

	public static boolean isTesting()
	{
		return TESTING;
	}

	public static EventBus EventBus()
	{
		return EVENT_BUS;
	}

	public static EventWaiter getEventWaiter()
	{
		return eventWaiter;
	}

	public static Client getClient()
	{
		return client;
	}

	public static JDA getJda()
	{
		return jda;
	}

	@Subscribe
	private void onReadyEvent(JDAEvent<ReadyEvent> event)
	{
		log.info("JDAEvent ONREADY fired, init Server Guilds");
		ReadyEvent ready = event.getEvent();
		for (Server server : BotData.servers)
		{
			server.initGuild(ready.getJDA());
		}

	}

	public static final class Info
	{
		public static final String	GITHUB_URL		= "https://github.com/ROMVoid95/GalacticBot";
		public static final String	USER_AGENT		= "%s/@version@/DiscordBot (%s)".formatted("GalacticBot", GITHUB_URL);
		public static final String	VERSION			= "@version@";
		public static final String	GIT_REVISION	= "@revision@";
	}
}
