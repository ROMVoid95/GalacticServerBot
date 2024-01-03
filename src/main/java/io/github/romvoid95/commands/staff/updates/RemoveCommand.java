package io.github.romvoid95.commands.staff.updates;

import io.github.readonly.command.event.SlashCommandEvent;
import io.github.readonly.command.option.Option;
import io.github.readonly.command.option.RequiredOption;
import io.github.readonly.common.OptionHelper;
import io.github.readonly.common.util.ResultLevel;
import io.github.romvoid95.BotData;
import io.github.romvoid95.commands.core.GalacticSlashCommand;
import io.github.romvoid95.database.entity.DBUpdates;
import io.github.romvoid95.database.impl.updates.UpdateMod;
import io.github.romvoid95.util.Check;
import io.github.romvoid95.util.Embed;
import io.github.romvoid95.util.discord.Reply;
import net.dv8tion.jda.api.entities.Role;

public class RemoveCommand extends GalacticSlashCommand
{
	public RemoveCommand()
	{
		super("rm-project", "Removes a project from update notifications");
		options(
			RequiredOption.text("name", "The mods name"),
			Option.trueFalse("deleterole", "If `false` the ping-role will be deleted (Default: `true`)")
		);
	}

	@Override
	protected void onExecute(SlashCommandEvent event)
	{
		if (Check.userNotStaff(event))
		{
			Reply.InvalidPermissions(event);
			return;
		}

		final String	modName			= OptionHelper.optString(event, "name");
		final boolean	deletPingRole	= OptionHelper.optBoolean(event, "deleterole", true);

		DBUpdates		updates	= BotData.database().updates();
		UpdateMod				mod		= updates.getMod(modName);
		UpdateMod.UpdateInfo	info	= mod.getNotifications().get(event.getGuild().getId());

		String roleReplyInfo = null;
		
		if (!deletPingRole && info.getPingRoleId().isEmpty())
		{
			Role pingRole = event.getGuild().getRoleCache().getElementById(info.getPingRoleId());
			roleReplyInfo = pingRole.getName() + "(%s)".formatted(pingRole.getId());
			pingRole.delete().queue();
		}

		mod.getNotifications().remove(event.getGuild().getId());
		updates.saveUpdating();

		Embed reply = Embed.newBuilder().description("Sucessfully removed mod `" + modName + "` from Update Notifications");
		if(deletPingRole && roleReplyInfo != null)
		{
			reply.field("Deleted Role", roleReplyInfo);
		}

		Reply.EphemeralReply(event, ResultLevel.SUCCESS, reply.toEmbed());
	}
}
