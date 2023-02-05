package io.github.romvoid95.util.discord;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import io.github.readonly.command.event.CommandEvent;
import io.github.readonly.command.event.SlashCommandEvent;
import io.github.readonly.common.util.ResultLevel;
import io.github.romvoid95.core.guildlogger.ServerSettings;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

public final class Reply
{
    public static void InvalidPermissions(SlashCommandEvent event)
    {        
        event.replyEmbeds(
            simpleEmbed(event.getMember().getAsMention() + ", you do not have the required permissions/roles to run this command.\n\n**This attempt has been logged**", ResultLevel.WARNING.getColor())
        ).setEphemeral(true).queue(s -> {
            ((ServerSettings) event.getClient().getSettingsFor(event.getGuild())).getRootLogger().sendLogMessage(event);
        });
    }
    
    public static void EphemeralReply(SlashCommandEvent event, ResultLevel level, String message)
    {
        event.replyEmbeds(simpleEmbed(message, level.getColor())).setEphemeral(true).queue();
    }
    
    public static void EphemeralReply(SlashCommandEvent event, ResultLevel level, MessageEmbed embed)
    {
        event.replyEmbeds(embed).setEphemeral(true).queue();
    }
    
    public static void EphemeralReply(SlashCommandEvent event, ResultLevel level, EmbedBuilder builder)
    {
    	builder.setColor(level.getColor());
        event.replyEmbeds(builder.build()).setEphemeral(true).queue();
    }
    
    public static void EphemeralReply(InteractionHook hook, ResultLevel level, MessageEmbed embed)
    {
        hook.sendMessageEmbeds(embed).queue();
    }

    public static void EphemeralReply(SlashCommandEvent event, String message)
    {
        event.replyEmbeds(simpleEmbed(message)).setEphemeral(true).queue();
    }
    
    public static void EphemeralReply(SlashCommandEvent event, String message, Consumer<? super InteractionHook> onSuccess)
    {
        event.replyEmbeds(simpleEmbed(message)).setEphemeral(true).queue(onSuccess);
    }
    
    public static void EphemeralReply(SlashCommandEvent event, String message, Consumer<? super InteractionHook> onSuccess, Consumer<? super Throwable> failure)
    {
        event.replyEmbeds(simpleEmbed(message)).setEphemeral(true).queue(onSuccess, failure);
    }
    
    public static void EphemeralReply(SlashCommandEvent event, EmbedBuilder builder)
    {
        event.replyEmbeds(builder.build()).setEphemeral(true).queue();
    }
    
    public static void EphemeralReply(SlashCommandEvent event, EmbedBuilder builder, Consumer<? super InteractionHook> onSuccess)
    {
        event.replyEmbeds(builder.build()).setEphemeral(true).queue(onSuccess);
    }
    
    public static void EphemeralReply(SlashCommandEvent event, EmbedBuilder builder, Consumer<? super InteractionHook> onSuccess, Consumer<? super Throwable> failure)
    {
        event.replyEmbeds(builder.build()).setEphemeral(true).queue(onSuccess, failure);
    }
    
    public static void EphemeralReply(SlashCommandEvent event, MessageCreateData data)
    {
        event.replyEmbeds(simpleEmbed(data.getContent(), ResultLevel.SUCCESS.getColor())).setEphemeral(true).queue();
    }
    
    public static void EphemeralReply(SlashCommandEvent event, MessageCreateData data, Consumer<? super InteractionHook> onSuccess)
    {
        event.replyEmbeds(simpleEmbed(data.getContent(), ResultLevel.SUCCESS.getColor())).setEphemeral(true).queue(onSuccess);
    }
    
    public static void EphemeralReply(SlashCommandEvent event, MessageCreateData data, Consumer<? super InteractionHook> onSuccess, Consumer<? super Throwable> failure)
    {
        event.replyEmbeds(simpleEmbed(data.getContent(), ResultLevel.SUCCESS.getColor())).setEphemeral(true).queue(onSuccess, failure);
    }

    public static void Success(SlashCommandEvent event, String message)
    {
        event.replyEmbeds(simpleEmbed(message, ResultLevel.SUCCESS.getColor())).queue();
    }
    
    public static void Success(CommandEvent event, String message)
    {
        event.reply(simpleEmbed(message, ResultLevel.SUCCESS.getColor()));
    }

    public static void Success(SlashCommandEvent event, EmbedBuilder embed)
    {
        event.replyEmbeds(embed.setColor(ResultLevel.SUCCESS.getColor()).build()).queue();
    }

    public static void Error(SlashCommandEvent event, String message)
    {
        event.replyEmbeds(simpleEmbed(message, ResultLevel.ERROR.getColor())).queue();
    }
    
    public static void Error(CommandEvent event, String message)
    {
        event.reply(simpleEmbed(message, ResultLevel.ERROR.getColor()));
    }

    public static void Temporary(MessageChannelUnion channel, ResultLevel level, String message, int time, TimeUnit unit)
    {
        channel.sendMessageEmbeds(simpleEmbed(message, level.getColor())).queue(success ->
        {
            success.delete().queueAfter(time, unit);
        });
    }

    public static void temporaryReply(CommandEvent event, MessageEmbed embed, int time, TimeUnit unit)
    {
        event.getChannel().sendMessageEmbeds(embed).queue(success ->
        {
            success.delete().queueAfter(time, unit);
        });
    }
    
    public static void temporaryReply(MessageChannelUnion channel, MessageEmbed embed, int time, TimeUnit unit)
    {
        channel.sendMessageEmbeds(embed).queue(success ->
        {
            success.delete().queueAfter(time, unit);
        });
    }

    public static MessageEmbed simpleEmbed(final String message)
    {
        return simpleEmbed(message, 0);
    }

    public static MessageEmbed simpleEmbed(final String message, int color)
    {
        EmbedBuilder builder = new EmbedBuilder().setDescription(message);
        if (color != 0)
        {
            builder.setColor(color);
        }
        return builder.build();
    }
}
