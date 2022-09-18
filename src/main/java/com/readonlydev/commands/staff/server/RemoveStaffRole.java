package com.readonlydev.commands.staff.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.readonlydev.BotData;
import com.readonlydev.command.slash.SlashCommandEvent;
import com.readonlydev.commands.core.GalacticSlashCommand;
import com.readonlydev.commands.core.RoleType;
import com.readonlydev.database.entity.DBGalacticBot;
import com.readonlydev.util.Check;
import com.readonlydev.util.discord.Reply;

import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;

public class RemoveStaffRole extends GalacticSlashCommand
{
    public RemoveStaffRole()
    {
        this.name = "remove";
        this.help = "Remove a role from Admins or Moderators list";
        this.options = Arrays.asList(
            new OptionData(OptionType.STRING, "type", "Mod or Admin", true).addChoices(
                    new Command.Choice("Moderator", "MOD"),
                    new Command.Choice("Admin", "ADMIN")
                ),
            new OptionData(OptionType.ROLE, "role", "the Role to add", true)
        );
        this.subcommandGroup = new SubcommandGroupData("staff", "Manage Roles that are considered staff in the server");
    }

    @Override
    protected void onExecute(SlashCommandEvent event)
    {
        
        boolean canRun = Check.adminRoles(event);
        
        if (!canRun)
        {
            Reply.InvalidPermissions(event);
            return;
        }
        
        RoleType type = RoleType.valueOf(event.getOption("type").getAsString());
        
        List<Role> mentionedRoles = new ArrayList<>();
        
        event.getOptionsByType(OptionType.ROLE).forEach(m -> {
            mentionedRoles.add(m.getAsRole());
        });
        
        DBGalacticBot db = BotData.database().botDatabase();
        List<Role> removed = db.removeFrom(event.getGuild(), mentionedRoles, type);
        String title = "**%s**".formatted(type.name);
        if(removed.size() >= 1)
        {
            String roles = String.join(", ", removed.stream().map(Role::getAsMention).collect(Collectors.toList()));
            Reply.Success(event, title + "\n\nSucessfully removed ".formatted(type.name) + roles);
        } else {
            Reply.Error(event, title + "\n\nNo Roles removed from %s".formatted(type.name));
        }
    }
}