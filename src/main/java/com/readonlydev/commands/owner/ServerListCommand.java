package com.readonlydev.commands.owner;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.readonlydev.GalacticBot;
import com.readonlydev.api.annotation.BotCommand;
import com.readonlydev.command.event.CommandEvent;
import com.readonlydev.command.included.EmbedMessageMenu;
import com.readonlydev.commands.core.AbstractCommand;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.exceptions.PermissionException;

@BotCommand
public class ServerListCommand extends AbstractCommand {

	private final EmbedMessageMenu.Builder menuBuilder;

	public ServerListCommand() {
		super("guildlist");
		this.help("Shows the list of guilds the bot is on");
		this.isGuildOnly();
		this.isOwnerCommand();
        this.menuBuilder = new EmbedMessageMenu.Builder()
                .setText("Servers That I Am In")
                .setFinalAction(m -> {
                    try {
                        m.clearReactions().queue(s -> m.delete().queue());
                    } catch(PermissionException ex) {
                        m.delete().queue();
                    }
                })
                .setTimeout(3, TimeUnit.MINUTES);
	}

	@Override
	protected void onExecute(CommandEvent event) {
		menuBuilder.setEventWaiter(GalacticBot.getEventWaiter());
		menuBuilder.setJda(GalacticBot.getJda());
		menuBuilder.setUsers(GalacticBot.getJda().getUserById(event.getClient().getOwnerId()));
		
        menuBuilder.clearItems();

        Stream<Guild> guilds = Stream.of(GalacticBot.getJda().getGuilds().toArray(new Guild[0]));
        menuBuilder.setItems(guilds.collect(Collectors.toMap(g -> JoinedServerEmbed.func().apply(g), g -> g)));

        EmbedMessageMenu menu = menuBuilder.build();
        event.getMessage().delete().queue();
        menu.display(event.getChannel());
	}
	
    @FunctionalInterface
    interface FunctionalEmbed extends Function<Guild, MessageEmbed> {

        @Override
        MessageEmbed apply(Guild t);

    }

    final static class JoinedServerEmbed implements FunctionalEmbed {

        private EmbedBuilder embed = new EmbedBuilder();

        static JoinedServerEmbed func() {
            return new JoinedServerEmbed();
        }

        @Override
        public MessageEmbed apply(Guild g) {
            embed.setTitle(g.getName(), g.getIconUrl());
            embed.setDescription(format("Owner", g.getOwner().getUser().getAsTag()));
            embed.appendDescription(format("Members", g.getMemberCount()));
            embed.appendDescription(format("Created", time(g.getTimeCreated())));
            embed.setThumbnail(g.getIconUrl()).build();
            return embed.build();
        }

        private String time(OffsetDateTime offset) {
            return offset.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM));
        }

        private String format(String title, String rest) {
            return "**" + title + "**: \n" + rest + "\n\n";
        }

        private String format(String title, int rest) {
            return "**" + title + "**: \n" + rest + "\n\n";
        }
    }
}
