package io.github.romvoid95.commands.staff.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.github.readonlydevelopment.command.event.SlashCommandEvent;

import io.github.romvoid95.BotData;
import io.github.romvoid95.commands.core.GalacticSlashCommand;
import io.github.romvoid95.commands.core.RoleType;
import io.github.romvoid95.database.entity.DBGalacticBot;
import io.github.romvoid95.util.Check;
import io.github.romvoid95.util.discord.Reply;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;

public class AddStaffRole extends GalacticSlashCommand
{    
    public AddStaffRole()
    {
        this.name = "add";
        this.help = "Add a role to Admins or Moderators list";
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
        List<Role> added = db.addTo(event.getGuild(), mentionedRoles, type);
        String title = "**%s**".formatted(type.name);
        if(added.size() >= 1)
        {
            String roles = String.join(", ", added.stream().map(Role::getAsMention).collect(Collectors.toList()));
            Reply.Success(event, title + "\n\nSucessfully added ".formatted(type.name) + roles);
        } else {
            Reply.Error(event, title + "\n\nNo Roles added to %s".formatted(type.name));
        }
    }
}