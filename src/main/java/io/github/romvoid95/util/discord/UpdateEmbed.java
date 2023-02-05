package io.github.romvoid95.util.discord;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter;

import club.minnced.discord.webhook.DiscordEmoji;
import club.minnced.discord.webhook.send.component.button.Button;
import club.minnced.discord.webhook.send.component.button.Button.Style;
import de.erdbeerbaerlp.cfcore.CurseAPI;
import de.erdbeerbaerlp.cfcore.json.CFFile;
import de.erdbeerbaerlp.cfcore.json.CFMod;
import io.github.readonly.common.util.RGB;
import io.github.romvoid95.updates.UpdateManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.entities.emoji.Emoji;

public class UpdateEmbed
{
	private static CurseAPI curseApi = UpdateManager.instance().getApiManager().orElseThrow().curseApi();
	
	public static final Emoji	CURSE	= Emoji.fromFormatted("<:curse:1005324626630553650>");
	
	private static final String	Blank				= EmbedBuilder.ZERO_WIDTH_SPACE;
	private static final String	InfoListTemplate	= "" + "**Release Type**: `%s`\n" + "**GameVersion%s**: `%s`\n";
	
	public static WebhookManager WEBWOOKS = WebhookManager.of("ModUpdate");
	
	public static EmbedBuilder createWebhookEmbed(CFMod proj, CFFile file)
	{

		String	version	= tryParseVersionFromFilename(file);
		String	title = proj.name;
		final int versionsLength = file.gameVersions.length;

		String gameVersions;
		if (versionsLength > 0)
		{
			gameVersions = String.join(", ", file.gameVersions);
		} else
		{
			gameVersions = "Unknown";
		}

		EmbedBuilder embed = new EmbedBuilder();
		embed.setTitle(title);
		embed.setThumbnail(proj.logo.thumbnailUrl);
		embed.setDescription("Version `" + version + "` released");
		embed.addField(Blank, InfoListTemplate.formatted(file.releaseType.name(), versionsLength > 1 ? "s" : "", gameVersions), false);
		embed.setColor(RGB.YELLOW.getColor());
		if (proj.slug.equals("galacticraft-legacy"))
		{
			for (Field field : getFields(proj, file))
			{
				embed.addField(field);
			}
		} else {
			embed.addField("Changelog", "```less\n" + formatChangelog(curseApi.getChangelog(proj.id, file.id)) + "\n```", false);
		}
		return embed;
	}
	
	public static EmbedBuilder getChangelogAsEmbed(CFMod proj, CFFile file)
	{
		EmbedBuilder embed = new EmbedBuilder();
		embed.setTitle("Changelog");
		for (Field field : getFields(proj, file))
		{
			embed.addField(field);
		}
		return embed;
	}
	
    private static String formatChangelog(String s) {
        String string = Processor.parse(s).replace("<br>", "\n").replace("&lt;", "<").replace("&gt;",
                ">").replaceAll("(?s)<[^>]*>(<[^>]*>)*", "").replace("\\", "");
        string = string.replaceAll("https.*?\\s", "");
        String out = "";
        int additionalLines = 0;
        for (final String st : string.split("\n")) {
            if ((out + st.trim() + "\n").length() > 950) {
                additionalLines++;
            } else // noinspection StringConcatenationInLoop
                out = out + st.trim() + "\n";
        }
        return out + (additionalLines > 0 ? ("... And " + additionalLines + " more lines") : "");
    }
    
    public class Processor
    {
        public static String parse(final String htmlString)
        {
            return FlexmarkHtmlConverter.builder().build().convert(htmlString);
        }
    }
    
	private static List<Field> getFields(CFMod proj, CFFile file)
	{
		String string = curseApi.getChangelog(proj.id, file.id);

		String						temp	= string.replaceAll("(<h2><span.+\">)(\\w.+)<\\/span><\\/h2>", "__**$2**__");
		List<String>				lines	= Arrays.asList(temp.split("\n"));
		final String[]				headers	= { "Changes", "Bug Fixes", "Features", "Misc" };
		StringMap<String, Integer>	map		= new StringMap<>();

		for (int i = 0; i < lines.size(); i++)
		{
			for (String header : headers)
			{
				if (lines.get(i).contains(header))
				{
					map.put(header, i);
				}
			}
		}

		List<Field> fields = new ArrayList<>();

		for (int i = 0; i < map.size() - 1; i++)
		{
			Entry<String, Integer>	entry		= map.getEntry(i);
			Entry<String, Integer>	nextEntry	= map.getEntry(i + 1);
			int						idx			= entry.getValue();
			String					part		= entry.getKey();
			int						nextIdx		= nextEntry.getValue();
			List<String>			li			= new ArrayList<>();
			lines.subList(idx + 1, nextIdx - 1).forEach(s ->
			{
				li.add(formatting(s));
			});
			fields.add(new Field(part, String.join("\n", li), false));
		}

		return fields;
	}

	private static String formatting(String string)
	{
		String node = "⁣  🔸 ";
		string = string.replace("\u200B", "");
		string = string.replace("<p></p>", "");

		string = string.replaceAll("<hr/?>", "\\_\\_\\_\\_\\_\\_\\_\\_\\_\\_\\_\\_");
		string = string.replaceAll("</hr>", "");

		string = string.replaceAll("[\n]*</p>\n+<p>[\n]*", "\n\n");
		string = string.replaceAll("[\n]*<[/]*p>[\n]*", "\n");
		string = string.replaceAll("<a href.+a>", "");

		string = string.replace("<strong>", "**");
		string = string.replace("</strong>", "**");

		string = string.replace("<code>", "`");
		string = string.replace("</code>", "`");

		string = string.replaceAll("[\n]*<h\\d+>[\n]*", "");
		string = string.replaceAll("[\n]*</h\\d+>[\n]*", "");

		string = string.replaceAll("[\n]*<[ou]l>[\n]*", "");
		string = string.replaceAll("[\n]*</[ou]l>[\n]*", "");

		string = string.replaceAll("[\n]*</li>\n+<li>[\n]*", "%s ".formatted(node));
		string = string.replaceAll("([\n]{2,})?<li>[\n]*", "%s ".formatted(node));
		string = string.replaceAll("[\n]*</li>[\n]*", "");
		return string;
	}
    
	private static String tryParseVersionFromFilename(final CFFile file)
	{
		String		name	= file.fileName.replace(".jar", "");
		String[]	parts	= name.split("-");
		return parts[parts.length - 1];
	}

	public static Button getCurseforgeLinkButton(final CFMod proj, CFFile file)
	{
		String url = "%s/files/%s".formatted(proj.links.websiteUrl, file.id);
		return new Button(Style.LINK, url).setLabel("CurseForge").setEmoji(DiscordEmoji.custom("curseflame", 1071731594479534100L, false));
	}
	
	public static Button getModrinthLinkButton(final String slug, String version)
	{
		String url = "https://modrinth.com/mod/%s/version/%s".formatted(slug, version);
		return new Button(Style.LINK, url).setLabel("Modrinth").setEmoji(DiscordEmoji.custom("modrinth", 1071693567698935859L, false));
	}
	
	private static class StringMap<K, V> extends LinkedHashMap<K, V>
	{
	    private static final long serialVersionUID = 5646659841311515373L;

	    public Map.Entry<K, V> getEntry(int i)
	    {
	        Set<Map.Entry<K,V>>entries = entrySet();
	        int j = 0;

	        for(Map.Entry<K, V>entry : entries)
	            if(j++ == i) {
	                
	                return entry;
	            }

	        return null;

	    }
	}
}
