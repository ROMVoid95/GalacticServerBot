package io.github.romvoid95.commands.staff.server;

import java.util.Arrays;
import java.util.List;

import com.github.readonlydevelopment.command.event.SlashCommandEvent;

import io.github.romvoid95.BotData;
import io.github.romvoid95.commands.core.GalacticSlashCommand;
import io.github.romvoid95.commands.core.RoleType;
import io.github.romvoid95.database.entity.DBGalacticBot;
import io.github.romvoid95.util.Check;
import io.github.romvoid95.util.discord.Reply;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;

public class ListStaffRoles extends GalacticSlashCommand
{    
    public ListStaffRoles()
    {
        this.name = "list";
        this.help = "shows roles currently in the Admins or Moderators list";
        this.options = Arrays.asList(
            new OptionData(OptionType.STRING, "type", "Mod or Admin", true).addChoices(
                    new Command.Choice("Moderator", "MOD"),
                    new Command.Choice("Admin", "ADMIN")
                )
        );
        this.subcommandGroup = new SubcommandGroupData("staff", "Manage Roles that are considered staff in the server");
    }

    @Override
    protected void onExecute(SlashCommandEvent event)
    {
        
        boolean canRun = Check.staffRoles(event);
        
        if (!canRun)
        {
            Reply.InvalidPermissions(event);
            return;
        }
        
        RoleType type = RoleType.valueOf(event.getOption("type").getAsString());

        DBGalacticBot db = BotData.database().botDatabase();
        List<Role> roles = db.getRoles(event.getGuild(), type);
        String title = "**%s**".formatted(type.name);
        String list;
        if(roles.isEmpty())
        {
            list = "No Roles have been added";
        } else {
            StringBuilder builder = new StringBuilder();
            for(Role role : roles)
            {
                builder.append(role.getAsMention()).append("\n");
            }
            list = builder.toString();
        }
        EmbedBuilder embed = new EmbedBuilder()
            .setTitle("Staff Role List")
            .addField(title, list, false);
        Reply.Success(event, embed);
    }
}