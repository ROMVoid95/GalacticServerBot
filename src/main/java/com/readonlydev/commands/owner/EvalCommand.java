package com.readonlydev.commands.owner;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.tuple.Triple;

import com.readonlydev.api.annotation.BotCommand;
import com.readonlydev.command.event.CommandEvent;
import com.readonlydev.commands.core.AbstractCommand;
import com.readonlydev.common.utils.MessageContentBuilder;
import com.readonlydev.util.eval.EvalEngine;

import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.utils.SplitUtil.Strategy;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

@BotCommand
public class EvalCommand extends AbstractCommand
{

    public EvalCommand()
    {
        super("eval");
        this.isGuildOnly();
        this.isOwnerCommand();
        this.isHidden();
    }

    @Override
    protected void onExecute(CommandEvent event)
    {
        MessageContentBuilder builder = new MessageContentBuilder();
        
        // Execute code
        final Map<String, Object> shortcuts = new HashMap<>();

        shortcuts.put("api", event.getMessage().getJDA());
        shortcuts.put("jda", event.getJDA());
        shortcuts.put("event", event);

        shortcuts.put("channel", event.getChannel());
        shortcuts.put("server", event.getGuild());
        shortcuts.put("guild", event.getGuild());

        shortcuts.put("message", event.getMessage());
        shortcuts.put("msg", event.getMessage());
        shortcuts.put("me", event.getMember());
        shortcuts.put("bot", event.getJDA().getSelfUser());
        
        final Triple<Object, String, String> result = EvalEngine.GROOVY.eval(shortcuts, Collections.emptyList(), EvalEngine.DEFAULT_IMPORTS, 10, getMessageContent());

        if (result.getLeft() instanceof RestAction<?>)
            ((RestAction<?>) result.getLeft()).queue();
        else if (result.getLeft() != null)
            builder.appendCodeBlock(result.getLeft().toString(), "");
        if (!result.getMiddle().isEmpty())
            builder.append("\n").appendCodeBlock(result.getMiddle(), "");
        if (!result.getRight().isEmpty())
            builder.append("\n").appendCodeBlock(result.getRight(), "");

        if (builder.isEmpty())
            replySuccess(event.getMessage());
        else
            for (final MessageCreateBuilder m : builder.buildAll(Strategy.NEWLINE, Strategy.WHITESPACE, Strategy.ANYWHERE))
                event.getChannel().sendMessage(m.build()).queue();;
    }

}
