package com.readonlydev.util.discord;

import java.awt.Color;
import java.time.Instant;

import org.jsoup.Jsoup;

import com.readonlydev.GalacticBot;

import io.github.matyrobbrt.curseforgeapi.request.AsyncRequest;
import io.github.matyrobbrt.curseforgeapi.request.helper.AsyncRequestHelper;
import io.github.matyrobbrt.curseforgeapi.schemas.mod.Mod;
import io.github.matyrobbrt.curseforgeapi.util.CurseForgeException;
import io.github.matyrobbrt.curseforgeapi.util.Pair;
import io.github.matyrobbrt.curseforgeapi.util.Utils;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.EmbedBuilder;

@UtilityClass
public class CurseHelper
{
//	public static AsyncRequest<EmbedBuilder> createWebhookFileEmbed(Mod mod, int fileId) throws CurseForgeException
//	{
//		final AsyncRequestHelper asyncHelper = GalacticBot.getCurseforge().api().getAsyncHelper();
//		// @format:off
//		return asyncHelper.getModFile(mod.id(), fileId).and(asyncHelper.getModFileChangelog(mod.id(), fileId))
//			.map(Pair::mapResponses)
//			.map(Utils.rethrowFunction(o -> o.orElseThrow(() -> new CurseForgeException("Could not find project " + mod.id()))))
//			.map(p -> p.map((file, changelog) -> {
//			
//			final var embed = new EmbedBuilder()
//				.setTimestamp(Instant.parse(file.fileDate()))
//				.setTitle("New version of **%s** Released".formatted(mod.name()), mod.links().websiteUrl())
//				.setColor(Color.BLUE)
//				.setThumbnail(mod.logo().thumbnailUrl());
//			try
//			{
//				embed.appendDescription("""
//					Changelog:
//					```
//					%s
//					```""".formatted(Jsoup.parse(changelog).text()));
//			// @format:on
//				} catch (IllegalArgumentException e)
//				{
//					embed.appendDescription("Changelog: *Too big to be displayed*");
//				}
//				return embed;
//			}));
//	}
}
