package io.github.romvoid95;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.filter.ThresholdFilter;
import io.github.readonly.common.event.EventHandler;
import io.github.romvoid95.commands.SortInitialize;
import io.github.romvoid95.commands.member.DeleteSuggestion;
import io.github.romvoid95.commands.member.EditSuggestion;
import io.github.romvoid95.core.ClientListener;
import io.github.romvoid95.core.GalacticEventListener;
import io.github.romvoid95.core.GuildSettings;
import io.github.romvoid95.database.entity.DBGalacticBot;
import io.github.romvoid95.logging.WebhookAppender;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.requests.ErrorResponse;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

@Slf4j
public class GalacticBot extends DiscordBotImpl<DBGalacticBot>
{
    @Getter
    private JDA					jda;
    private static GalacticBot	_instance;

    public static boolean InDevEnv = false;

    public static final GalacticBot instance()
    {
        return GalacticBot._instance;
    }

    private void preStart()
    {
        log.info("Starting up {}, Git revision: {}", "GalacticBot", Info.VERSION, Info.GIT_REVISION);
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
            .addSlashCommands(new EditSuggestion(), new DeleteSuggestion())
            .setOwnerId(Conf.Bot().getOwner())
            .setActivity(Activity.watching("for Suggestion"))
            .useHelpBuilder(false)
            .setListener(new ClientListener())
            .setGuildSettingsManager(new GuildSettings());

        this.jda = JDABuilder.create(Conf.Bot().getToken(), BotData.JDA.INTENTS)
            .disableCache(BotData.JDA.DISABLED_CACHE_FLAGS)
            .setMemberCachePolicy(MemberCachePolicy.ALL)
            .addEventListeners(this.getEventWaiter(), this.buildClient(), new GalacticEventListener())
            .build();
        // @format

        EventHandler.instance().register(new BusListener());

        GalacticBot._instance = this;

        this.handleLogger();
    }

    private void handleLogger()
    {
        var lc = (LoggerContext) LoggerFactory.getILoggerFactory();

        var filter = new ThresholdFilter();
        filter.setLevel("info");
        filter.setContext(lc);
        filter.start();

        var encoder = new PatternLayoutEncoder();
        encoder.setPattern("[%level] [%logger{0}]: `%msg%n`");
        encoder.setContext(lc);
        encoder.start();

        var appender = new WebhookAppender();
        appender.setEncoder(encoder);
        appender.addFilter(filter);
        appender.setName("ERROR_WH");
        appender.setContext(lc);
        appender.start();

        var root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
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
        BotData.conn().close();
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
}
