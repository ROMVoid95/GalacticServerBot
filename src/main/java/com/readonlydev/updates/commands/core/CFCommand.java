package com.readonlydev.updates.commands.core;

import java.awt.Color;

import javax.annotation.Nullable;

import com.readonlydev.command.slash.SlashCommand;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

public abstract class CFCommand extends SlashCommand
{

    /**
     * Returns a simple MessageEmbed where the message is placed into the desciption
     *
     * @param message The String to place in the Embed
     * @return The MessageEmbed
     */
    protected MessageEmbed simpleEmbed(final String message)
    {
        return simpleEmbed(message, null);
    }

    /**
     * Simple embed.
     *
     * @param message the message
     * @param color the color
     * @return the message embed
     */
    protected MessageEmbed simpleEmbed(final String message, @Nullable Color color)
    {
        EmbedBuilder builder = new EmbedBuilder().setDescription(message);
        if (color != null)
        {
            builder.setColor(color);
        }
        return builder.build();
    }
}
