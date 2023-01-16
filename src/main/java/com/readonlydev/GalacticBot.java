package com.readonlydev;

import java.util.EnumSet;
import java.util.Set;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.readonlydev.command.Command;
import com.readonlydev.command.client.Client;
import com.readonlydev.command.client.ClientBuilder;
import com.readonlydev.command.client.ServerCommands;
import com.readonlydev.commands.member.CloseDiscussionThread;
import com.readonlydev.commands.member.EditDescription;
import com.readonlydev.commands.member.EditTitle;
import com.readonlydev.commands.member.NewSuggestion;
import com.readonlydev.commands.owner.MaintanenceModeCommand;
import com.readonlydev.commands.staff.Suggestions;
import com.readonlydev.commands.staff.server.ServerStaff;
import com.readonlydev.commands.staff.suggestions.devonly.DevServerPopularChannel;
import com.readonlydev.commands.staff.suggestions.devonly.SuggestionSetStatus;
import com.readonlydev.common.waiter.EventWaiter;
import com.readonlydev.context.PasteContextMenu;
import com.readonlydev.core.BusListener;
import com.readonlydev.core.ClientListener;
import com.readonlydev.core.GalacticEventListener;
import com.readonlydev.core.GuildSettings;
import com.readonlydev.core.event.JDAEvent;
import com.readonlydev.database.impl.updates.Mod.Curseforge;
import com.readonlydev.logback.LogFilter;
import com.readonlydev.util.ReflectCommands;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.requests.ErrorResponse;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

@Slf4j
public class GalacticBot
{

	private static boolean TESTING = true;

	private static JDA			jda;
	private static Client		client;
	private static EventWaiter	eventWaiter	= new EventWaiter();
	private static EventBus		EVENT_BUS	= new EventBus("GalacticBot EventBus");
	private static Curseforge	curseforge;

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
		// Conf.saveUpdateConfigJson();

		ClientBuilder clientBuilder = new ClientBuilder();

		Set<Command> conventionalCommands = ReflectCommands.getConventionalCommands();

		ServerCommands devServerCommands = new ServerCommands("775251052517523467");
		devServerCommands.addAllCommands(new DevServerPopularChannel(), new SuggestionSetStatus());

		ServerCommands communityServerCommands = new ServerCommands("449966345665249290");
		communityServerCommands.addAllCommands(new Suggestions(), new ServerStaff(), new NewSuggestion(), new CloseDiscussionThread(), new MaintanenceModeCommand());
		communityServerCommands.addContextMenus(new PasteContextMenu());

		clientBuilder.setAllRepliesAsEmbed();
		clientBuilder.addCommands(conventionalCommands);
		clientBuilder.addContextMenus(new PasteContextMenu());
		clientBuilder.addGlobalSlashCommands(new EditDescription(), new EditTitle());
		clientBuilder.addAllServerCommands(devServerCommands, communityServerCommands);
		clientBuilder.setOwnerId(Conf.Bot().getOwner());
		clientBuilder.setPrefix(Conf.Bot().getPrefix());
		clientBuilder.setActivity(Activity.watching("for Suggestions & stuff"));
		clientBuilder.useHelpBuilder(false);
		clientBuilder.setListener(new ClientListener());
		clientBuilder.setGuildSettingsManager(new GuildSettings());

		// @noformat
		EnumSet<GatewayIntent> intents = EnumSet.of(GatewayIntent.GUILD_EMOJIS_AND_STICKERS, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_MESSAGE_REACTIONS, GatewayIntent.MESSAGE_CONTENT);

		EnumSet<CacheFlag> caches = EnumSet.of(CacheFlag.ACTIVITY, CacheFlag.CLIENT_STATUS, CacheFlag.VOICE_STATE);

		GalacticBot.client = clientBuilder.build();

		GalacticBot.jda = JDABuilder.create(Conf.Bot().getToken(), intents).disableCache(caches).setActivity(Activity.playing("Init Stage")).addEventListeners(eventWaiter, client, new GalacticEventListener()).build();
		// @format

		//GalacticBot.curseforge = new Curseforge(CurseForgeAPI.builder().apiKey(Conf.Bot().getApiKey()).build());

		// new Updates();
		EVENT_BUS.register(new BusListener());
		EVENT_BUS.register(this);
		log.info("Conventional Commands:  " + client.getCommands().size());
	}

	public static void main(String[] args) throws Exception
	{
		new GalacticBot();
	}

	public static boolean isTesting()
	{
		return TESTING;
	}

	public static EventBus EventBus()
	{
		return EVENT_BUS;
	}

//	public static Curseforge getCurseforge()
//	{
//		return curseforge;
//	}

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
