package io.github.romvoid95.commands.owner;

import io.github.readonly.command.event.SlashCommandEvent;
import io.github.readonly.command.option.Option;
import io.github.readonly.command.option.RequiredOption;
import io.github.romvoid95.BotData;
import io.github.romvoid95.commands.core.GalacticSlashCommand;
import io.github.romvoid95.database.entity.DBGalacticBot;
import io.github.romvoid95.util.Check;
import io.github.romvoid95.util.discord.Reply;
import net.dv8tion.jda.api.entities.emoji.Emoji;

public class AddEmojiCommand extends GalacticSlashCommand
{

    public AddEmojiCommand()
    {
        name("add-emoji");
        setOptions(
            RequiredOption.text("channel-id", "channel-id"),
            RequiredOption.text("msg-id", "msg-id"),
            RequiredOption.text("emoji", "emoji")
        );
    }

    @Override
    protected void execute(SlashCommandEvent event)
    {
        if (!Check.isOwner(event))
        {
            Reply.InvalidPermissions(event);
            return;
        }
        super.execute(event);
    }

    @Override
    protected void onExecute(SlashCommandEvent event)
    {
        String channelId = event.getOption("channel-id").getAsString();
        String msgId = event.getOption("msg-id").getAsString();
        
        String emoji = event.getOption("emoji").getAsString();
        Emoji.fromFormatted(emoji);

        event.replyEmbeds(Reply.simpleEmbed(emoji)).queue(s -> {
             s.retrieveOriginal().queue(m -> m.addReaction(Emoji.fromFormatted(emoji)));
        });
    }
}
