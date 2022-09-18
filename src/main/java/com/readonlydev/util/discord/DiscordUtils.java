package com.readonlydev.util.discord;

import java.awt.Color;
import java.util.HashMap;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import com.readonlydev.GalacticBot;
import com.readonlydev.command.event.CommandEvent;
import com.readonlydev.command.slash.SlashCommandEvent;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

public class DiscordUtils
{

    public static final int BLACK       = 0xFF000000;
    public static final int DKGRAY      = 0xFF444444;
    public static final int GRAY        = 0xFF888888;
    public static final int LTGRAY      = 0xFFCCCCCC;
    public static final int WHITE       = 0xFFFFFFFF;
    public static final int RED         = 0xFFFF0000;
    public static final int GREEN       = 0xFF00FF00;
    public static final int BLUE        = 0xFF0000FF;
    public static final int YELLOW      = 0xFFFFFF00;
    public static final int CYAN        = 0xFF00FFFF;
    public static final int MAGENTA     = 0xFFFF00FF;
    public static final int TRANSPARENT = 0;

    public static User getUser(SlashCommandEvent event)
    {
        return (event.getChannelType() == ChannelType.PRIVATE) ? event.getUser() : event.getMember().getUser();
    }
    
    public static User getUser(CommandEvent event)
    {
        return (event.getChannelType() == ChannelType.PRIVATE) ? event.getAuthor() : event.getMember().getUser();
    }
    
    public static Boolean areGuildsTheSame(Guild guild1, Guild guild2)
    {
        return guild1.equals(guild2);
    }
    
    public static Message getMessageOrNull(MessageReactionAddEvent event)
    {
        try
        {
            return event.retrieveMessage().submit().get();
        } catch (InterruptedException | ExecutionException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public static Optional<Role> getRole(String id)
    {
        try
        {
            return Optional.of(GalacticBot.getJda().getRoleById(id));
        } catch (NumberFormatException e)
        {
            return Optional.empty();
        }
    }

    public static Optional<TextChannel> getChannel(String id)
    {
        return Optional.ofNullable(GalacticBot.getJda().getTextChannelById(id));
    }

    public static String capitalize(String input)
    {
        if (input == null || input.isEmpty())
            return "";
        if (input.length() == 1)
            return input.toUpperCase();
        return Character.toUpperCase(input.charAt(0)) + input.substring(1).toLowerCase();
    }

    public static int parseColor(String hex)
    {
        if (hex.charAt(0) == '#')
        {
            long color = Long.parseLong(hex.substring(1), 16);
            if (hex.length() == 7)
            {
                // Set the alpha value
                color |= 0x00000000ff000000;
            } else if (hex.length() != 9)
            {
                throw new IllegalArgumentException("Unknown color");
            }
            return (int) color;
        } else
        {
            Integer color = sColorNameMap.get(hex.toLowerCase(Locale.ROOT));
            if (color != null)
            {
                return color;
            }
        }
        throw new IllegalArgumentException("Unknown color");
    }

    public static String getHexValue(Color color)
    {
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }

    private static final HashMap<String, Integer> sColorNameMap;
    static
    {
        sColorNameMap = new HashMap<>();
        sColorNameMap.put("black", BLACK);
        sColorNameMap.put("darkgray", DKGRAY);
        sColorNameMap.put("gray", GRAY);
        sColorNameMap.put("lightgray", LTGRAY);
        sColorNameMap.put("white", WHITE);
        sColorNameMap.put("red", RED);
        sColorNameMap.put("green", GREEN);
        sColorNameMap.put("blue", BLUE);
        sColorNameMap.put("yellow", YELLOW);
        sColorNameMap.put("cyan", CYAN);
        sColorNameMap.put("magenta", MAGENTA);
        sColorNameMap.put("aqua", 0xFF00FFFF);
        sColorNameMap.put("fuchsia", 0xFFFF00FF);
        sColorNameMap.put("darkgrey", DKGRAY);
        sColorNameMap.put("grey", GRAY);
        sColorNameMap.put("lightgrey", LTGRAY);
        sColorNameMap.put("lime", 0xFF00FF00);
        sColorNameMap.put("maroon", 0xFF800000);
        sColorNameMap.put("navy", 0xFF000080);
        sColorNameMap.put("olive", 0xFF808000);
        sColorNameMap.put("purple", 0xFF800080);
        sColorNameMap.put("silver", 0xFFC0C0C0);
        sColorNameMap.put("teal", 0xFF008080);
    }
}
