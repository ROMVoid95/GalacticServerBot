package io.github.romvoid95.commands.staff.updates;

import java.util.concurrent.ExecutionException;

import de.erdbeerbaerlp.cfcore.CurseAPI;
import io.github.readonly.command.OptionHelper;
import io.github.readonly.command.event.SlashCommandEvent;
import io.github.readonly.command.option.Option;
import io.github.readonly.command.option.RequiredOption;
import io.github.readonly.common.event.EventHandler;
import io.github.readonly.common.util.Embed;
import io.github.readonly.common.util.ResultLevel;
import io.github.romvoid95.BotData;
import io.github.romvoid95.commands.core.GalacticSlashCommand;
import io.github.romvoid95.core.event.ModUpdateEvent;
import io.github.romvoid95.database.entity.DBUpdates;
import io.github.romvoid95.database.impl.updates.UpdateMod;
import io.github.romvoid95.database.impl.updates.UpdateMod.Curseforge;
import io.github.romvoid95.database.impl.updates.UpdateMod.Modrinth;
import io.github.romvoid95.database.impl.updates.UpdateMod.UpdateInfo;
import io.github.romvoid95.updates.UpdateManager;
import io.github.romvoid95.util.Check;
import io.github.romvoid95.util.StringUtils;
import io.github.romvoid95.util.discord.Reply;
import masecla.modrinth4j.main.ModrinthAPI;
import net.dv8tion.jda.api.EmbedBuilder;
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
			RequiredOption.integer("curseforge", "The CurseForge projectId"),
			Option.text("modrinth", "The mods Modrinth projectId"),
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
		event.deferReply().setEphemeral(true).queue();

		final String	modName			= OptionHelper.optString(event, "name");
		final boolean	genPingRole		= OptionHelper.optBoolean(event, "genrole", true);
		final String	pingRoleName	= OptionHelper.optString(event, "rolename", StringUtils.capitalize(modName) + "Updates");

		DBUpdates	updates	= BotData.database().updates();
		UpdateMod	mod		= updates.getMod(modName);

		if (!mod.getNotifications().isEmpty() && mod.getNotifications().containsKey(event.getGuild().getId()))
		{
			UpdateInfo		existing		= mod.getNotifications().get(event.getGuild().getId());
			final String	channelMention	= event.getGuild().getGuildChannelById(existing.getChannelId()).getAsMention();
			String			roleMention		= null;
			if (!existing.getPingRoleId().isEmpty())
			{
				roleMention = event.getGuild().getRoleById(existing.getPingRoleId()).getAsMention();
			}

			EmbedBuilder embedBuilder = new EmbedBuilder().setDescription("The Mod `" + modName + "` is already configured for this server with the following configurations").addField("Update Channel", channelMention, false).addField("Ping Role", roleMention != null ? roleMention : "No Ping Role configured", false);

			Reply.EphemeralReply(event, ResultLevel.WARNING, embedBuilder);
			return;
		}
		
		int curseProjectId = OptionHelper.optInteger(event, "curseforge");
		String modrinthProject = OptionHelper.optString(event, "modrinth");
		
		if(curseProjectId == -1 && modrinthProject.isEmpty())
		{
			Reply.EphemeralReply(event, ResultLevel.ERROR, "Neither a CurseForge or a Modrinth projectId was provided, At least 1 must be provided");
			return;
		}
		
		Curseforge curse = new Curseforge();
		Modrinth modrinth = new Modrinth();
		
		modrinth.setProjectId(modrinthProject);
		modrinth.setLatestVersionId(getModrinthVersionId(modrinthProject));
		
		curse.setProjectId(curseProjectId);
		CurseAPI cfApi = UpdateManager.instance().getApiManager().orElseThrow().curseApi();
		curse.setLatestFileId(cfApi.getModFromID(curseProjectId).latestFilesIndexes[0].fileId);

		UpdateMod.UpdateInfo updateInfo = new UpdateInfo();

		String	pingRoleError	= "Could not create ping role - Missing permission `MANAGE_ROLES`";
		Role	pingRole		= null;

		if (genPingRole)
		{
			pingRole = event.getGuild().createRole().setName(pingRoleName).setMentionable(true).complete();
		} else
		{
			pingRoleError = "No Ping Role configured";
		}

		GuildMessageChannelUnion channel = event.getGuildChannel();

		updateInfo.setChannelId(channel.getId());
		if (pingRole != null)
		{
			updateInfo.setPingRoleId(pingRole.getId());
		}
		mod.getNotifications().put(event.getGuild().getId(), updateInfo);
		mod.setCurseforge(curse);
		mod.setModrinth(modrinth);
		updates.saveUpdating();

		final String	channelMention	= channel.getAsMention();
		final String	roleMention		= pingRole != null ? pingRole.getAsMention() : pingRoleError;

		EventHandler.instance().post(new ModUpdateEvent.Add(modName, event.getGuild()));
		
		
		
		Reply.EphemeralReply(event.getHook(), ResultLevel.SUCCESS, Embed.newBuilder().description("Sucessfully added mod `" + modName + "` to Update Notifications with the following configurations").field("Update Channel", channelMention).field("Ping Role", roleMention).toEmbed());
	}
	
	private String getModrinthVersionId(String projectId)
	{
		ModrinthAPI mrApi = UpdateManager.instance().getApiManager().orElseThrow().modrinthApi();
		try
		{
			var project = mrApi.projects().get(projectId).get();
			var versions = project.getVersions();
			return versions.get(versions.size() -1);
		} catch (InterruptedException | ExecutionException e)
		{
			e.printStackTrace();
		}
		return null;
	}
}
