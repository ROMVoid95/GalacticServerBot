package io.github.romvoid95.commands.staff.updates;

import io.github.readonly.command.OptionHelper;
import io.github.readonly.command.event.SlashCommandEvent;
import io.github.readonly.command.option.Option;
import io.github.readonly.command.option.RequiredOption;
import io.github.readonly.common.event.EventHandler;
import io.github.readonly.common.util.ResultLevel;
import io.github.romvoid95.BotData;
import io.github.romvoid95.commands.core.GalacticSlashCommand;
import io.github.romvoid95.core.event.ModUpdateEvent;
import io.github.romvoid95.database.entity.DBUpdates;
import io.github.romvoid95.database.impl.updates.Mod;
import io.github.romvoid95.database.impl.updates.Mod.UpdateInfo;
import io.github.romvoid95.util.Check;
import io.github.romvoid95.util.discord.Reply;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.unions.GuildMessageChannelUnion;

public class AddCommand extends GalacticSlashCommand
{

	public AddCommand()
	{
		this.name("add-project");
		this.description("Adds a project to update notifications");
		//@noformat
		setOptions(
			RequiredOption.text("name", "The mods name"), 
			Option.trueFalse("genrole", "If `false` no ping-role will be created (Default: `true`)"), 
			Option.text("rolename", "Name of the Ping-Role if it is set to generate (Ignored if `genrole` is `false`)", 32)
		);
		//@format
	}

	@Override
	protected void onExecute(SlashCommandEvent event)
	{
		if (Check.userNotStaff(event))
		{
			Reply.InvalidPermissions(event);
			return;
		}
		
		if(OptionHelper.optBoolean(event, "test", false))
		{
			Reply.EphemeralReply(event, ResultLevel.SUCCESS, event.getCommandId());
		}

		final String				modName			= OptionHelper.optString(event, "name");
		final boolean				genPingRole		= OptionHelper.optBoolean(event, "genrole", true);
		final String				pingRoleName	= OptionHelper.optString(event, "rolename", modName);

		DBUpdates				updates	= BotData.database().updates();
		Mod						mod		= updates.getMod(modName);
		
		if(!mod.getNotifications().isEmpty() && mod.getNotifications().containsKey(event.getGuild().getId()))
		{
			UpdateInfo existing = mod.getNotifications().get(event.getGuild().getId());
			final String channelMention = event.getGuild().getGuildChannelById(existing.getChannelId()).getAsMention();
			String roleMention = null;
			if(existing.getRoleId().isPresent())
			{
				roleMention = event.getGuild().getRoleById(existing.getRoleId().get()).getAsMention();
			}
			
			EmbedBuilder embedBuilder = new EmbedBuilder()
				.setDescription("The Mod `" + modName + "` is already configured for this server with the following configurations")
				.addField("Update Channel", channelMention, false)
				.addField("Ping Role", roleMention != null ? roleMention : "No Ping Role configured", false);
			
			Reply.EphemeralReply(event, ResultLevel.WARNING, embedBuilder);
			return;
		}
		
		Role role = null;

		String pingRoleError = "";
		
		if (genPingRole)
		{
			if(event.getGuild().getSelfMember().hasPermission(Permission.MANAGE_ROLES))
			{
				role = this.createPingRole(event.getGuild(), pingRoleName);
			} else {
				pingRoleError = "Could not create ping role - Missing permission `MANAGE_ROLES`";
			}
		} else {
			pingRoleError = "No Ping Role configured";
		}
		
		GuildMessageChannelUnion channel = event.getGuildChannel();
		
		Mod.UpdateInfo updateInfo = new UpdateInfo();
		updateInfo.setChannelId(channel.getId());
		if(role != null)
		{
			updateInfo.setPingRoleId(role.getId());
		}
		
		mod.getNotifications().put(event.getGuild().getId(), updateInfo);
		updates.saveUpdating();
		
		final String channelMention = channel.getAsMention();
		String roleMention = role != null ? role.getAsMention() : pingRoleError;

		EmbedBuilder embedBuilder = new EmbedBuilder()
			.setDescription("Sucessfully added mod `" + modName + "` to Update Notifications with the following configurations")
			.addField("Update Channel", channelMention, false)
			.addField("Ping Role", roleMention, false);
		
		EventHandler.instance().post(new ModUpdateEvent.Add(event.getCommandId(), event.getGuild()));
		
		Reply.Success(event, embedBuilder);
	}

	private long newRoleId;

	private Role createPingRole(final Guild guild, final String roleName)
	{
		guild.createRole().setName(roleName).setMentionable(true).queue(s ->
		{
			this.newRoleId = s.getIdLong();
		});
		return guild.getRoleById(this.newRoleId);
	}
}
