package io.github.romvoid95;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import io.github.readonly.api.BotContainer;
import io.github.readonly.api.scheduler.Task;
import io.github.readonly.common.event.EventHandler;
import io.github.readonly.discordbot.DiscordBot;
import io.github.romvoid95.commands.SortInitialize;
import io.github.romvoid95.commands.member.EditDescription;
import io.github.romvoid95.commands.member.EditTitle;
import io.github.romvoid95.core.ClientListener;
import io.github.romvoid95.core.GalacticEventListener;
import io.github.romvoid95.core.GuildSettings;
import io.github.romvoid95.util.DateFormatting;
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

	private static GalacticBot	_instance;
	@Getter
	private JDA					jda;

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
			.addGlobalSlashCommands(new EditDescription(), new EditTitle())
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
	}
	
	private void send()
	{
		Date now = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(now);
        
        String time = c.getTime().toString();
        String parsedTime = DateFormatting.formatDate(time);
        
		getJda().getTextChannelById("1069462759252693012").sendMessage(
				"Task Ran -> \n```\n" + "Time: %s\n".formatted(time) + "Parsed: %s\n".formatted(parsedTime) + "```"
		).queue();
	}

	public static void main(String[] args)
	{
		GalacticBot bot = new GalacticBot();
		Runtime.getRuntime().addShutdownHook(new Thread(GalacticBot::shutdown));
		Task.builder().interval(10, TimeUnit.SECONDS).name("Test Task").execute(bot::send).submit((BotContainer) bot.getInstance().get());
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
