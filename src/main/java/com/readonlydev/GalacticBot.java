/*
 * Copyright 2017 John Grosh (jagrosh).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.readonlydev;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.readonlydev.cmd.BotCommand;
import com.readonlydev.cmd.BotCommand.Category;
import com.readonlydev.cmd.arg.CommandArgument;
import com.readonlydev.cmd.arg.Optional;
import com.readonlydev.cmd.arg.Required;
import com.readonlydev.cmd.client.CommandClient;
import com.readonlydev.cmd.client.CommandClientBuilder;
import com.readonlydev.commands.slash.SuggestionSlashCommand;
import com.readonlydev.common.waiter.EventWaiter;
import com.readonlydev.config.Config;
import com.readonlydev.listener.SuggestionListener;
import com.readonlydev.logback.LogFilter;
import com.readonlydev.logback.LogUtils;
import com.readonlydev.util.ReflectCommands;
import com.readonlydev.util.RuntimeOptions;
import com.readonlydev.util.TracingPrintStream;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.requests.ErrorResponse;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

public class GalacticBot {
	private static final Logger log = LoggerFactory.getLogger(GalacticBot.class);

	private JDA jda;
	private static GalacticBot _instance;
	private final static Config config = BotData.config();

	private final EventWaiter eventWaiter = new EventWaiter();

	private void preStart() {
		log.info("Starting up %s {}, Git revision: {}", "GalacticBot", Info.VERSION, Info.GIT_REVISION);
		log.info("Reporting UA {} for HTTP requests.", Info.USER_AGENT);

		if (RuntimeOptions.VERBOSE) {
			System.setOut(new TracingPrintStream(System.out));
			System.setErr(new TracingPrintStream(System.err));
		}

		RestAction.setPassContext(true);
		if (RuntimeOptions.DEBUG) {
			log.info("Running in debug mode!");
		} else {
			RestAction.setDefaultFailure(
					ErrorResponseException.ignore(RestAction.getDefaultFailure(), ErrorResponse.UNKNOWN_MESSAGE));
		}

		log.info("Filtering all logs below {}", LogFilter.LEVEL);
	}

	private GalacticBot() throws Exception {
		_instance = this;
		preStart();
		LogUtils.log("Startup",
				"Starting up %s %s (Git: %s)".formatted(config.getBotname(), Info.VERSION, Info.GIT_REVISION));

		BotData.configManager().save();

		CommandClientBuilder clientBuilder = new CommandClientBuilder();

		Set<BotCommand> commands = ReflectCommands.commands();

		clientBuilder.setAllRepliesAsEmbed();
		clientBuilder.addCommands(commands.toArray(new BotCommand[commands.size()]));
		clientBuilder.addSlashCommand(new SuggestionSlashCommand());
		clientBuilder.setOwnerId(config.getOwner());
		clientBuilder.setPrefix(config.getPrefix());
		clientBuilder.setHelpConsumer((event) -> {
			StringBuilder builder = new StringBuilder("**" + event.getSelfUser().getName() + "** commands:\n\n");
			Category category = null;
			builder.append("[arg]   = Required Argument\n");
			builder.append("<args>  = Optional Argument\n");
			for (BotCommand command : commands) {
				if (!command.isHidden() && (!command.isOwnerCommand() || event.isOwner())) {
					if (!Objects.equals(category, command.getCategory())) {
						category = command.getCategory();
						builder.append("\n  __").append(category == null ? "No Category" : category.getName())
								.append("__:\n");
					}
					builder.append("\n`").append(config.getPrefix()).append(command.getName());
					for (CommandArgument<?> arg : command.getArguments()) {
						if (arg instanceof Required) {
							Required a = (Required) arg;
							builder.append(" " + a.getArgumentForHelp());
						} else if (arg instanceof Optional) {
							Optional a = (Optional) arg;
							builder.append(" " + a.getArgumentForHelp());
						}
					}
					builder.append("`");
					builder.append(" - ").append(command.getHelp());
				}
			}
			User owner = event.getJDA().getUserById(config.getOwner());
			if (owner != null) {
				builder.append("\n\nFor additional help, contact **").append(owner.getName()).append("**#")
						.append(owner.getDiscriminator());
			}
			event.replyInDm(builder.toString(), unused -> {
				if (event.isFromType(ChannelType.TEXT))
					event.reactSuccess();
			}, t -> event.replyWarning("Help cannot be sent because you are blocking Direct Messages."));
		});

		CommandClient client = clientBuilder.build();

		List<String> registered = new ArrayList<>();
		for (BotCommand cmd : client.getCommands()) {
			registered.add("`" + cmd.getName() + "`");
		}

		LogUtils.log("Registered Commands", String.join(" ", registered));

		EnumSet<GatewayIntent> intents = EnumSet.of(GatewayIntent.GUILD_EMOJIS, GatewayIntent.GUILD_MESSAGES,
				GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_MESSAGE_REACTIONS);

		EnumSet<CacheFlag> caches = EnumSet.of(CacheFlag.ACTIVITY, CacheFlag.CLIENT_STATUS, CacheFlag.VOICE_STATE);

		jda = JDABuilder.create(config.getToken(), intents).disableCache(caches)
				.setActivity(Activity.playing("Init Stage")).addEventListeners(eventWaiter, clientBuilder.build(), new SuggestionListener())
				.build().awaitReady();

		BotData.database();
	}

	public static void main(String[] args) throws Exception {
		new GalacticBot();
	}

	public static JDA getJda() {
		return GalacticBot._instance.jda;
	}

	public static EventWaiter getEventWaiter() {
		return GalacticBot._instance.eventWaiter;
	}

	private static final class Info {
		public static final String GITHUB_URL = "https://github.com/ROMVoid95/GalacticBot";
		public static final String USER_AGENT = "%s/@version@/DiscordBot (%s)".formatted("GalacticBot", GITHUB_URL);
		public static final String VERSION = "@version@";
		public static final String GIT_REVISION = "@revision@";
	}
}
