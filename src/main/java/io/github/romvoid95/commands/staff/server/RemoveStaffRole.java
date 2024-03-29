package io.github.romvoid95.commands.staff.server;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.github.readonly.command.event.SlashCommandEvent;
import io.github.readonly.command.lists.ChoiceList;
import io.github.readonly.command.option.RequiredOption;
import io.github.romvoid95.BotData;
import io.github.romvoid95.commands.core.GalacticSlashCommand;
import io.github.romvoid95.commands.core.RoleType;
import io.github.romvoid95.database.entity.DBGalacticBot;
import io.github.romvoid95.util.Check;
import io.github.romvoid95.util.discord.Reply;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;

public class RemoveStaffRole extends GalacticSlashCommand
{

    public RemoveStaffRole()
    {
        super("remove", "Remove a role from Admins or Moderators list");
        options(RequiredOption.text("type", "Mod or Admin", ChoiceList.toList(RoleType.class)), RequiredOption.role("role", "the Role to remove"));
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

        event.getOptionsByType(OptionType.ROLE).forEach(m ->
        {
            mentionedRoles.add(m.getAsRole());
        });

        DBGalacticBot db      = BotData.database().galacticBot();
        List<Role>    removed = db.removeFrom(event.getGuild(), mentionedRoles, type);
        String        title   = "**%s**".formatted(type.name);
        if (removed.size() >= 1)
        {
            String roles = String.join(", ", removed.stream().map(Role::getAsMention).collect(Collectors.toList()));
            Reply.Success(event, title + "\n\nSucessfully removed ".formatted(type.name) + roles);
        } else
        {
            Reply.Error(event, title + "\n\nNo Roles removed from %s".formatted(type.name));
        }
    }
}
