package io.github.romvoid95;

import java.util.EnumSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;

import com.rethinkdb.net.Connection;

import io.github.romvoid95.database.DatabaseManager;
import io.github.romvoid95.util.Factory;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import static com.rethinkdb.RethinkDB.r;

public class BotData {

	private static final ScheduledExecutorService galacticExec = Factory.newScheduledThreadPool(1, "GalacticBot",
			false);
	private static final ScheduledExecutorService updateExec = Factory.newScheduledThreadPool(1, "ModUpdates", true);
	private static DatabaseManager db;
	private static Connection connection;

	public static Connection conn() {
		var config = Conf.Bot().getDatabase();
		if (connection == null) {
			synchronized (BotData.class) {
				if (connection != null) {
					return connection;
				}
				//@noformat
				connection = r.connection()
						.hostname(config.getHostname())
						.port(config.getPort())
						.db(config.getDatabaseName())
						.user(config.getUser(), config.getPassword())
						.connect();
			}
		}

		return connection;
	}

	public static DatabaseManager database() {
		if (db == null) {
			db = new DatabaseManager(conn());
		}

		return db;
	}

	public static ScheduledExecutorService updateExecutor() {
		return updateExec;
	}

	public static ScheduledExecutorService galacticExecutor() {
		return galacticExec;
	}

	public static void queue(Callable<?> action) {
		galacticExecutor().submit(action);
	}

	public static void queue(Runnable runnable) {
		galacticExecutor().submit(runnable);
	}

	public static class JDA {

		// @noformat
		public static final Set<GatewayIntent> INTENTS = Set.of(GatewayIntent.DIRECT_MESSAGES,
				GatewayIntent.GUILD_MODERATION, GatewayIntent.GUILD_MESSAGE_REACTIONS, GatewayIntent.GUILD_PRESENCES,
				GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MEMBERS, GatewayIntent.MESSAGE_CONTENT);

		public static final Set<CacheFlag> DISABLED_CACHE_FLAGS = EnumSet.of(CacheFlag.SCHEDULED_EVENTS,
				CacheFlag.CLIENT_STATUS, CacheFlag.VOICE_STATE);

		public static final Set<Message.MentionType> DEFAULT_MENTIONS = EnumSet.of(Message.MentionType.EMOJI,
				Message.MentionType.CHANNEL);

		public static final Set<Permission> PERMISSIONS = EnumSet.of(Permission.MESSAGE_MANAGE,
				Permission.MANAGE_WEBHOOKS, Permission.MANAGE_THREADS, Permission.MANAGE_ROLES);
	}
}
