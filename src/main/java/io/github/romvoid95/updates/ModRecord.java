package io.github.romvoid95.updates;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import club.minnced.discord.webhook.send.AllowedMentions;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import club.minnced.discord.webhook.send.component.ComponentElement;
import club.minnced.discord.webhook.send.component.layout.ActionRow;
import de.erdbeerbaerlp.cfcore.json.MinecraftVersion;
import io.github.readonly.common.version.Version;
import io.github.romvoid95.Conf;
import io.github.romvoid95.GalacticBot;
import io.github.romvoid95.database.impl.updates.R_Platforms;
import io.github.romvoid95.database.impl.updates.R_UpdateInfo;
import io.github.romvoid95.database.impl.updates.UpdateMod.Curseforge;
import io.github.romvoid95.database.impl.updates.UpdateMod.Modrinth;
import io.github.romvoid95.util.discord.UpdateEmbed;
import net.dv8tion.jda.api.entities.channel.attribute.IWebhookContainer;

public record ModRecord(String name, List<R_UpdateInfo> infoRecords, R_Platforms platforms) implements Runnable
{
	@Override
	public void run()
	{
		final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger("%s".formatted(name));

		log.info("Check - Running");
		final var	manager		= UpdateManager.instance().getApiManager().orElseThrow();
		final var	curseApi	= manager.curseApi();
		final var	modrinthApi	= manager.modrinthApi();
		final var	allMods		= manager.allmods();

		final var cursePlatform = platforms.curseforge().orElse(null);
		final var modrinthPlatform = platforms.modrinth().orElse(null);
		
		List<ComponentElement> buttons = new ArrayList<>();
		
		WebhookMessageBuilder webhookBuilder = new WebhookMessageBuilder();
		webhookBuilder.setAllowedMentions(new AllowedMentions().withParseRoles(true));
		webhookBuilder.setAvatarUrl(Conf.Bot().getAvatarUrl());
		
		boolean curseInfoIsNew = false;
		var newCurseInfo = new Curseforge();
		
		boolean modrinthInfoIsNew = false;
		var newModrinthInfo = new Modrinth();
		
		if(cursePlatform != null)
		{
			var knownCurseId = cursePlatform.getProjectId();
			var knownLastFileId = cursePlatform.getLatestFileId();
			
			var	cfMod			= curseApi.getModFromID(knownCurseId);
			var cfFile			= cfMod.latestFiles[0];
			var	latestFileId	= cfFile.id;
			
			if(latestFileId > knownLastFileId && !cfFile.getVersion().equals(MinecraftVersion.V1_7_10))
			{
				log.info("Check - Found Update");
				newCurseInfo.setProjectId(knownCurseId);
				newCurseInfo.setLatestFileId(latestFileId);
				curseInfoIsNew = true;
				
				var	updateEmbed		= UpdateEmbed.createWebhookEmbed(cfMod, cfFile);
				
				buttons.add(UpdateEmbed.getCurseforgeLinkButton(cfMod, cfFile));
				webhookBuilder.addEmbeds(WebhookEmbedBuilder.fromJDA(updateEmbed.build()).build());
				
				if(modrinthPlatform != null)
				{
					try {
						var knownProjectId = modrinthPlatform.getProjectId();
						var knownVersionId = modrinthPlatform.getLatestVersionId();
						Version knownLatest = Version.of(modrinthApi.versions().getVersion(knownVersionId).get().getVersionNumber());
						
						var project = modrinthApi.projects().get(modrinthPlatform.getProjectId()).get();
						var slug = project.getSlug();
						var versions = project.getVersions();
						
						var version = modrinthApi.versions().getVersion(versions.get(versions.size() -1)).get();
						var latestVersionId = version.getId();
						var latestVersionNumber = version.getVersionNumber();
						Version latest = Version.of(latestVersionNumber);
						
						if(latest.greaterThan(knownLatest))
						{
							newModrinthInfo.setProjectId(knownProjectId);
							newModrinthInfo.setLatestVersionId(latestVersionId);
							modrinthInfoIsNew = true;
						}
						buttons.add(UpdateEmbed.getModrinthLinkButton(slug, latestVersionNumber));
					} catch (Exception e) {
					}
				}
				
				ActionRow row = ActionRow.of().of(buttons);
				webhookBuilder.addComponents(row);
				
				R_Platforms newPlatformsInfo;
				
				if(curseInfoIsNew && modrinthInfoIsNew)
				{
					newPlatformsInfo = new R_Platforms(Optional.of(newCurseInfo), Optional.of(newModrinthInfo));
				} else if(curseInfoIsNew && !modrinthInfoIsNew)
				{
					newPlatformsInfo = new R_Platforms(Optional.of(newCurseInfo), platforms.modrinth());
				} else {
					newPlatformsInfo = new R_Platforms(platforms.curseforge(), platforms.modrinth());
				}
				
				allMods.save(name, newPlatformsInfo);
				
				infoRecords.forEach(info ->
				{
					final var ch = GalacticBot.instance().getJda().getChannelById(IWebhookContainer.class, info.channelId());

					if (!info.pingRoleId().isBlank())
					{
						webhookBuilder.setContent(GalacticBot.instance().getJda().getRoleById(info.pingRoleId()).getAsMention());
					}

					UpdateEmbed.WEBWOOKS.sendAndCrosspost(ch, webhookBuilder.build());
				});
			}
		}
	}
}
