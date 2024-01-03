package io.github.romvoid95.commands.staff.server;

import java.util.List;

import io.github.readonly.command.event.SlashCommandEvent;
import io.github.readonly.command.lists.ChoiceList;
import io.github.readonly.command.option.RequiredOption;
import io.github.romvoid95.BotData;
import io.github.romvoid95.commands.core.GalacticSlashCommand;
import io.github.romvoid95.commands.core.RoleType;
import io.github.romvoid95.database.entity.DBGalacticBot;
import io.github.romvoid95.util.Check;
import io.github.romvoid95.util.discord.Reply;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;

public class ListStaffRoles extends GalacticSlashCommand
{

    public ListStaffRoles()
    {
        super("list","shows roles currently in the Admins or Moderators list");
        options(RequiredOption.text("type", "Mod or Admin", ChoiceList.toList(RoleType.class)));
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

        DBGalacticBot db    = BotData.database().galacticBot();
        List<Role>    roles = db.getRoles(event.getGuild(), type);
        String        title = "**%s**".formatted(type.name);
        String        list;
        if (roles.isEmpty())
        {
            list = "No Roles have been added";
        } else
        {
            StringBuilder builder = new StringBuilder();
            for (Role role : roles)
            {
                builder.append(role.getAsMention()).append("\n");
            }
            list = builder.toString();
        }
        EmbedBuilder embed = new EmbedBuilder().setTitle("Staff Role List").addField(title, list, false);
        Reply.Success(event, embed);
    }
}
