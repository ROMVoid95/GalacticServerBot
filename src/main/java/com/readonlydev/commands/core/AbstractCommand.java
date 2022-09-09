package com.readonlydev.commands.core;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

import com.readonlydev.command.Command;
import com.readonlydev.command.arg.CommandArgument;
import com.readonlydev.command.arg.parse.Argument;
import com.readonlydev.command.arg.parse.ArgumentIndex;
import com.readonlydev.command.event.CommandEvent;
import com.readonlydev.common.utils.ResultLevel;
import com.readonlydev.util.StringUtils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;

public abstract class AbstractCommand extends Command
{

    private ArgumentIndex argumentIndex;
    private CommandEvent  event;

    public AbstractCommand(String name)
    {
        this.name = name;
    }

    @Override
    public void execute(CommandEvent event)
    {
        this.argumentIndex = event.getArgumentIndex();
        this.event = event;
        if (getArgsAsString().equalsIgnoreCase("help"))
        {
            temporaryReply(this.getHelpEmbed(), 30, TimeUnit.SECONDS);
            event.getMessage().delete().queue();
        } else
        {
            onExecute(event);
        }
    }

    protected abstract void onExecute(CommandEvent event);

    protected void subCommands(AbstractCommand... commands)
    { 
        this.children = commands;
    }

    public AbstractCommand[] getSubCommands()
    {
        return (AbstractCommand[]) this.children;
    }

    protected void aliases(String... aliases)
    {
        this.aliases = aliases;
    }

    protected void help(String string)
    {
        this.help = string;
    }

    protected void allowDms()
    {
        this.guildOnly = false;
    }

    protected void ownerOnly()
    {
        this.ownerCommand = true;
    }
    
    protected String getMessageContent()
    {
        return event.getMessage().getContentDisplay()
            .replace(event.getPrefix(), "")
            .replace(event.getCommand().getName(), "");
    }

    protected void replySuccess(String message)
    {
        event.getMessage().delete().queue();
        event.reply(simpleEmbed(message, ResultLevel.SUCCESS.getColor()));
    }
    
    protected void replySuccess(Message message)
    {
        event.getMessage().delete().queue();
        event.reply(message);
    }

    protected void replySuccess(EmbedBuilder embed)
    {
        event.getMessage().delete().queue();
        event.reply(embed.setColor(ResultLevel.SUCCESS.getColor()).build());
    }

    protected void replyError(String message)
    {
        String cmd = event.getMessage().getContentRaw();
        event.getMessage().delete().queue();
        event.reply(simpleEmbed("`" + cmd + "`\n\n" + message, ResultLevel.ERROR.getColor()));
    }

    protected void requiredRoles(String... roles)
    {
        Arrays.asList(roles).forEach(r -> this.addRequiredRoles(r));
    }

    public void temporaryReply(ResultLevel level, String message, int time, TimeUnit unit)
    {
        MessageEmbed embed = new MessageEmbed(null, null, message, null, null, level.getColorInt(), null, null, null, null, null, null, null);
        event.reply(embed, success ->
        {
            success.delete().queueAfter(time, unit);
        });
    }

    public void temporaryReply(MessageEmbed embed, int time, TimeUnit unit)
    {
        event.reply(embed, success ->
        {
            success.delete().queueAfter(time, unit);
        });
    }

    public MessageEmbed simpleEmbed(final String message)
    {
        return simpleEmbed(message, null);
    }

    public MessageEmbed simpleEmbed(final String message, @Nullable Color color)
    {
        EmbedBuilder builder = new EmbedBuilder().setDescription(message);
        if (color != null)
        {
            builder.setColor(color);
        }
        return builder.build();
    }

    protected int getArgCount()
    {
        return argumentIndex.count();
    }

    protected String getArgValue(int index)
    {
        if (getArgCount() < index)
        {
            return "";
        } else
        {
            return argumentIndex.getArg(index).val();
        }
    }

    protected Argument getArg(int index)
    {
        return argumentIndex.getArg(index);
    }

    protected boolean noArgs()
    {
        return argumentIndex.isEmpty();
    }

    protected String getArgsAsString()
    {
        StringBuilder b = new StringBuilder();
        argumentIndex.list().forEach(s -> b.append(s.val() + " "));
        return b.toString();
    }

    public String getUseage()
    {
        StringBuilder builder = new StringBuilder();
        builder.append(getName() + " ");
        for (CommandArgument<?> arg : getArguments())
        {
            builder.append(arg.getArgumentForHelp() + " ");
        }
        return builder.toString().trim();
    }
    
    public String numberOfSubCommands()
    {
        if(getChildren().length > 0)
        {
            return "[%d Sub Commands]".formatted(getChildren().length);
        } else {
            return "";
        }
    }
    
    public String getArgsDescription()
    {
        String s = "⁣  ";
        String ss = s + s;

        List<CommandArgument<?>> requiredArguments = new ArrayList<>();
        List<CommandArgument<?>> optionalArguments = new ArrayList<>();
        StringBuilder b1 = new StringBuilder();
        for (CommandArgument<?> arg : this.getArguments())
        {
            if (arg.isRequired())
            {
                requiredArguments.add(arg);
            } else
            {
                optionalArguments.add(arg);
            }
        }
        if (!requiredArguments.isEmpty())
        {
            b1.append("**Required Arguments**\n");
            requiredArguments.forEach(arg ->
            {
                b1.append("%s`%s` %s- *%10s*\n".formatted(ss, arg.getArgumentForHelp(), s, arg.getDescription()));
            });
        }
        if (!optionalArguments.isEmpty())
        {
            b1.append("**Optional Arguments**\n");
            optionalArguments.forEach(arg ->
            {
                b1.append("%s`%s` %s- *%10s*\n".formatted(ss, arg.getArgumentForHelp(), s, arg.getDescription()));
            });
        }
        return b1.toString();
    }

    public MessageEmbed getHelpEmbed()
    {
        List<CommandArgument<?>> requiredArguments = new ArrayList<>();
        List<CommandArgument<?>> optionalArguments = new ArrayList<>();
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(StringUtils.capitalize(name + "Command"));
        builder.setDescription(this.getHelp());
        StringBuilder b1 = new StringBuilder();
        for (CommandArgument<?> arg : this.getArguments())
        {
            b1.append(" " + arg.getArgumentForHelp());
            if (arg.isRequired())
            {
                requiredArguments.add(arg);
            } else
            {
                optionalArguments.add(arg);
            }
        }
        b1.append("`");
        builder.appendDescription("\n\n" + getUseage());
        if (!requiredArguments.isEmpty())
        {
            StringBuilder b2 = new StringBuilder();
            requiredArguments.forEach(arg ->
            {
                b2.append("%-5s - *%5s*\n".formatted(arg.getArgumentForHelp(), arg.getDescription()));
            });
            builder.addField("Required Arguments", b2.toString(), false);
        }
        if (!optionalArguments.isEmpty())
        {
            StringBuilder b2 = new StringBuilder();
            optionalArguments.forEach(arg ->
            {
                b2.append("%-5s - *%5s*\n".formatted(arg.getArgumentForHelp(), arg.getDescription()));
            });
            builder.addField("Optional Arguments", b2.toString(), false);
        }
        builder.setColor(ResultLevel.SUCCESS.getColor());
        return builder.build();
    }
}
