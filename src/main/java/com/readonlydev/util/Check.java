package com.readonlydev.util;

import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

import com.readonlydev.BotData;
import com.readonlydev.command.slash.SlashCommandEvent;
import com.readonlydev.database.impl.Suggestion;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;

public class Check
{

    private static boolean isFromBotDevServer(SlashCommandEvent event)
    {
        return event.getGuild().equals(BotData.botDevServer());
    }
    
    public static boolean forSuggestionAuthor(SlashCommandEvent event)
    {
        String messageId = event.getChannel().asThreadChannel().retrieveParentMessage().complete().getId();
        Suggestion suggestion = BotData.database().botDatabase().getSuggestionFromMessageId(messageId);
        
        return suggestion.getAuthorId().equals(event.getMember().getId());
    }
    
    public static boolean forRole(SlashCommandEvent event, String roleId)
    {
        if(isFromBotDevServer(event))
        {
            return true;
        } else {
            boolean checksPass = false;
            Role role = event.getGuild().getRoleById(roleId);
            if(role != null)
            {
                if(event.getMember().getRoles().contains(role))
                {
                    checksPass = true;
                }
            }
            return checksPass;
        }
    }
    
    public static boolean adminRoles(SlashCommandEvent event)
    {
        if(isFromBotDevServer(event))
        {
            return true;
        } else {
            List<String> adminRoles = BotData.database().botDatabase().getGuilds().get(event.getGuild().getIdLong()).getServerAdminRoles();
            List<String> memberRoles = event.getMember().getRoles().stream().map(Role::getId).collect(Collectors.toList());
            boolean checksPass = false;
            
            if(memberHasAdminPermissions(event) || containsAny(adminRoles, memberRoles))
            {
                checksPass = true;
            }
            
            return checksPass;
        }
    }

    public static boolean staffRoles(SlashCommandEvent event)
    {
        if(isFromBotDevServer(event))
        {
            return true;
        } else {
            List<String> staffRoles = BotData.database().botDatabase().getGuilds().get(event.getGuild().getIdLong()).getServerModeratorRoles();
            List<String> memberRoles = event.getMember().getRoles().stream().map(Role::getId).collect(Collectors.toList());
            boolean checksPass = false;
            
            if(containsAny(staffRoles, memberRoles))
            {
                checksPass = true;
            }
            
            EnumSet<Permission> memberPermissions = event.getMember().getPermissions();
            if ((memberPermissions.contains(Permission.MODERATE_MEMBERS) || memberPermissions.contains(Permission.BAN_MEMBERS)) || memberHasAdminPermissions(event))
            {
                checksPass = true;
            }
            
            return checksPass;
        }
    }
    
    private static boolean containsAny(final Collection<?> coll1, final Collection<?> coll2) {
        if (coll1.size() < coll2.size()) {
            for (final Object aColl1 : coll1) {
                if (coll2.contains(aColl1)) {
                    return true;
                }
            }
        } else {
            for (final Object aColl2 : coll2) {
                if (coll1.contains(aColl2)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean memberHasAdminPermissions(SlashCommandEvent event)
    {
        return event.getMember().getPermissions().contains(Permission.ADMINISTRATOR);
    }
}
