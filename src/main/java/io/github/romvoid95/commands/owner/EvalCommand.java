package io.github.romvoid95.commands.owner;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.tuple.Triple;

import io.github.readonly.command.event.SlashCommandEvent;
import io.github.readonly.command.option.RequiredOption;
import io.github.readonly.common.util.MessageContentBuilder;
import io.github.romvoid95.commands.core.GalacticSlashCommand;
import io.github.romvoid95.util.Check;
import io.github.romvoid95.util.discord.Reply;
import io.github.romvoid95.util.eval.EvalEngine;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.utils.SplitUtil.Strategy;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

public class EvalCommand extends GalacticSlashCommand
{

    public EvalCommand()
    {
        name("eval");
        setOptions(RequiredOption.text("ev", "statement to evaluate", 1024));
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
        MessageContentBuilder builder = new MessageContentBuilder();

        // Execute code
        final Map<String, Object> shortcuts = new HashMap<>();

        shortcuts.put("jda", event.getJDA());
        shortcuts.put("event", event);

        shortcuts.put("channel", event.getChannel());
        shortcuts.put("server", event.getGuild());
        shortcuts.put("guild", event.getGuild());

        shortcuts.put("me", event.getMember());
        shortcuts.put("bot", event.getJDA().getSelfUser());

        final Triple<Object, String, String> result = EvalEngine.GROOVY.eval(shortcuts, Collections.emptyList(), EvalEngine.DEFAULT_IMPORTS, 10, event.optString("ev"));

        if (result.getLeft() instanceof RestAction<?>)
            ((RestAction<?>) result.getLeft()).queue();
        else if (result.getLeft() != null)
            builder.appendCodeBlock(result.getLeft().toString(), "");
        if (!result.getMiddle().isEmpty())
            builder.append("\n").appendCodeBlock(result.getMiddle(), "");
        if (!result.getRight().isEmpty())
            builder.append("\n").appendCodeBlock(result.getRight(), "");

        if (builder.isEmpty())
            Reply.Success(event, event.optString("ev"));
        else
            for (final MessageCreateBuilder m : builder.buildAll(Strategy.NEWLINE, Strategy.WHITESPACE, Strategy.ANYWHERE))
                Reply.EphemeralReply(event, m.build());
    }
}
