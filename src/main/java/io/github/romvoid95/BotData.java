package io.github.romvoid95;

import java.util.EnumSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;

import io.github.romvoid95.database.DatabaseManager;
import io.github.romvoid95.database.Rethink;
import io.github.romvoid95.util.Factory;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

public class BotData
{

	private static final ScheduledExecutorService	galacticExec	= Factory.newScheduledThreadPool(1, "GalacticBot", false);
	private static final ScheduledExecutorService	updateExec		= Factory.newScheduledThreadPool(1, "ModUpdates", true);
	private static DatabaseManager					db;

	public static DatabaseManager database()
	{
		if (db == null)
		{
			db = new DatabaseManager(Rethink.connect());
		}

		return db;
	}
	
	public static ScheduledExecutorService updateExecutor()
	{
		return updateExec;
	}

	public static ScheduledExecutorService galacticExecutor()
	{
		return galacticExec;
	}

	public static void queue(Callable<?> action)
	{
		galacticExecutor().submit(action);
	}

	public static void queue(Runnable runnable)
	{
		galacticExecutor().submit(runnable);
	}

	public static class JDA
	{

		//@format:off
		public static final Set<GatewayIntent> INTENTS = Set.of(GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_MODERATION, GatewayIntent.GUILD_EMOJIS_AND_STICKERS, GatewayIntent.GUILD_MESSAGE_REACTIONS, GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MEMBERS, GatewayIntent.MESSAGE_CONTENT);

		public static final Set<CacheFlag> DISABLED_CACHE_FLAGS = EnumSet.of(CacheFlag.SCHEDULED_EVENTS, CacheFlag.CLIENT_STATUS, CacheFlag.VOICE_STATE);

		public static final Set<Message.MentionType> DEFAULT_MENTIONS = EnumSet.of(Message.MentionType.EMOJI, Message.MentionType.CHANNEL);

		public static final Set<Permission> PERMISSIONS = EnumSet.of(Permission.MESSAGE_MANAGE, Permission.MANAGE_WEBHOOKS, Permission.MANAGE_THREADS, Permission.MANAGE_ROLES);
	}
}
