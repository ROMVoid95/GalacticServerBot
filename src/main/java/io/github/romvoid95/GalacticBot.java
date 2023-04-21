package io.github.romvoid95;

import java.util.Optional;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.filter.ThresholdFilter;
import io.github.readonly.common.event.EventHandler;
import io.github.readonly.discordbot.DiscordBot;
import io.github.romvoid95.commands.SortInitialize;
import io.github.romvoid95.commands.member.EditDescription;
import io.github.romvoid95.commands.member.EditSuggestion;
import io.github.romvoid95.core.ClientListener;
import io.github.romvoid95.core.GalacticEventListener;
import io.github.romvoid95.core.GuildSettings;
import io.github.romvoid95.logging.WebhookAppender;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.requests.ErrorResponse;
import net.dv8tion.jda.api.requests.RestAction;

@Slf4j
public class GalacticBot extends DiscordBot<GalacticBot>
{
	@Getter
	private JDA					jda;
	private static GalacticBot	_instance;

	public static final GalacticBot instance()
	{
		return GalacticBot._instance;
	}

	private void preStart()
	{
		log.info("Starting up %s {}, Git revision: {}", "GalacticBot", Info.VERSION, Info.GIT_REVISION);
		log.info("Reporting UA {} for HTTP requests.", Info.USER_AGENT);

		RestAction.setPassContext(true);
		RestAction.setDefaultFailure(ErrorResponseException.ignore(RestAction.getDefaultFailure(), ErrorResponse.UNKNOWN_MESSAGE));

		BotData.database();
	}

	private GalacticBot()
	{
		preStart();

		Conf.saveBotConfigJson();

		// @noformat
		SortInitialize.perform(this.getClientBuilder());
		this.getClientBuilder()
			.setAllRepliesAsEmbed()
			.addGlobalSlashCommands(new EditDescription(), new EditSuggestion())
			.setOwnerId(Conf.Bot().getOwner())
			.setActivity(Activity.watching("for Suggestion"))
			.useHelpBuilder(false)
			.setListener(new ClientListener())
			.setGuildSettingsManager(new GuildSettings());

		this.jda = JDABuilder.create(Conf.Bot().getToken(), BotData.JDA.INTENTS)
			.disableCache(BotData.JDA.DISABLED_CACHE_FLAGS)
			.setActivity(Activity.playing("Init Stage"))
			.addEventListeners(this.getEventWaiter(), this.buildClient(), new GalacticEventListener())
			.build();
		// @format

		EventHandler.instance().register(new BusListener());

		GalacticBot._instance = this;
		
		this.handleLogger();
	}
	
	private void handleLogger()
	{
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();

        ThresholdFilter filter = new ThresholdFilter();
        filter.setLevel("info");
        filter.setContext(lc);
        filter.start();

        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setPattern("[%level] [%logger{0}]: `%msg%n`");
        encoder.setContext(lc);
        encoder.start();

        WebhookAppender appender = new WebhookAppender();
        appender.setEncoder(encoder);
        appender.addFilter(filter);
        appender.setName("ERROR_WH");
        appender.setContext(lc);
        appender.start();

        Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.addAppender(appender);
	}

	public static void main(String[] args)
	{
		new GalacticBot();
		Runtime.getRuntime().addShutdownHook(new Thread(GalacticBot::shutdown));
	}

	private static void shutdown()
	{
		BotData.galacticExecutor().shutdown();
		BotData.database().getConnection().close();
		GalacticBot.instance().getJda().shutdownNow();
	}

	public boolean isDevBot()
	{
		return getJda().getSelfUser().getApplicationId().equals("1018818779276390401");
	}

	public static final class Info
	{
		public static final String	GITHUB_URL		= "https://github.com/ROMVoid95/GalacticBot";
		public static final String	USER_AGENT		= "%s/@version@/DiscordBot (%s)".formatted("GalacticBot", GITHUB_URL);
		public static final String	VERSION			= "@version@";
		public static final String	GIT_REVISION	= "@revision@";
	}

	@Override
	public String getId()
	{
		return "galacticbot";
	}

	@Override
	public Optional<?> getInstance()
	{
		return Optional.of(_instance);
	}
}
