package com.readonlydev.updates;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

import com.readonlydev.updates.CurseforgeProject.CFChannel;
import com.readonlydev.updates.util.StringMap;
import com.readonlydev.updates.util.Style;

import de.erdbeerbaerlp.cfcore.CFCoreAPI;
import de.erdbeerbaerlp.cfcore.json.CFFile;
import de.erdbeerbaerlp.cfcore.json.CFFileIndex;
import de.erdbeerbaerlp.cfcore.json.CFMod;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;

public class EmbedMessage
{

    public static final Emoji   CURSE               = Emoji.fromFormatted("<:curse:1005324626630553650>");
    public static final Emoji   CURSE2              = Emoji.fromFormatted("<curse2:1005324972497043466>");

    private static final String DEFAULT_DESCRIPTION = "New File detected For CurseForge Project";
    private static final String Blank               = EmbedBuilder.ZERO_WIDTH_SPACE;
    private static final String InfoListTemplate    = "" + "**Release Type**: `%s`\n" + "**Filename**: `%s`\n" + "**GameVersion%s**: `%s`\n";

    
    
    /**
     * Send update notification.
     *
     * @param channel the channel
     * @param proj    the proj
     */
    public static void sendUpdateNotification(CFChannel c, TextChannel channel, CFMod proj) throws InsufficientPermissionException
    {
        final Role role = c.data.settings.pingRole == 0 ? null : channel.getGuild().getRoleById(c.data.settings.pingRole);
        if (role != null)
        {
            channel.sendMessage(role.getAsMention()).queue();
        }
        EmbedMessage.messageWithoutLink(proj, proj.latestFilesIndexes[0], channel);
    }

    /**
     * Message without link.
     *
     * @param proj    the proj
     * @param file    the file
     * @param channel the channel
     */
    public static void messageWithoutLink(CFMod proj, CFFileIndex file, TextChannel channel)
    {

        final CFFile cFile = CFCoreAPI.getFileFromID(proj.id, file.fileId);
        String version = tryParseVersionFromFilename(file);
        String title = Updates.ifa.projectHasCustomTitle(proj.id);
        if (title.isEmpty())
        {
            title = proj.name;
        } else
        {
            if (Style.getStyle(title).equals(Style.Format))
            {
                title = title.split(";;")[1].formatted(version);
            }
        }
        String description = Updates.ifa.projectHasCustomDescription(proj.id);
        if (description.isBlank() || description.length() > 500)
        {
            System.out.println("Your messageDescription is over 500 characters, setting to default value **PLEASE CHANGE THIS**");
            description = DEFAULT_DESCRIPTION;
        } else
        {
            if (Style.getStyle(description).equals(Style.Format))
            {
                description = description.split(";;")[1].formatted(version);
            }
        }

        final int versionsLength = cFile.gameVersions.length;

        String gameVersions;
        if (versionsLength > 0)
        {
            gameVersions = String.join(", ", cFile.gameVersions);
        } else
        {
            gameVersions = "Unknown";
        }

        String projectUrl = proj.links.websiteUrl;

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(title);
        embed.setThumbnail(proj.logo.thumbnailUrl);
        embed.setDescription(description);
        embed.addField(Blank, InfoListTemplate.formatted(cFile.releaseType.name(), cFile.fileName, versionsLength > 1 ? "s" : "", gameVersions), false);
        if(proj.slug.equals("galacticraft-legacy"))
        {
            for(Field field : getFields(proj, file))
            {
                embed.addField(field);
            }
        }

        channel.sendMessageEmbeds(embed.build()).setActionRow(getWebsiteUrlButton("%s/files/%d".formatted(projectUrl, file.fileId))).queue();
    }
    
    

    private static List<Field> getFields(CFMod proj, CFFileIndex file)
    {
        String string = CFCoreAPI.getChangelog(proj.id, file.fileId);
        
        String temp = string.replaceAll("(<h2><span.+\">)(\\w.+)<\\/span><\\/h2>", "__**$2**__");
        List<String> lines = Arrays.asList(temp.split("\n"));
        final String[] headers = {"FEATURES", "FIXES", "CHANGES", "NEW CONTRIBUTORS"};
        StringMap<String, Integer> map = new StringMap<>();

        System.out.println("Building Map");
        for (int i = 0; i < lines.size(); i++)
        {
            for (String header : headers)
            {
                if (lines.get(i).contains(header))
                {
                    System.out.println("Putting " + header + " in Map");
                    map.put(header, i);
                }
            }
        }

        List<Field> fields = new ArrayList<>();
        
        
        
        
        for (int i = 0; i < map.size() - 1; i++)
        {
            Entry<String, Integer> entry = map.getEntry(i);
            Entry<String, Integer> nextEntry = map.getEntry(i + 1);
            int idx = entry.getValue();
            String part =entry.getKey();
            int nextIdx = nextEntry.getValue();
            List<String> li = new ArrayList<>();
            lines.subList(idx + 1, nextIdx - 1).forEach(s -> {
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

    private static String tryParseVersionFromFilename(final CFFileIndex file)
    {
        String name = file.filename.replace(".jar", "");
        String[] parts = name.split("-");
        return parts[parts.length - 1];
    }

    private static Button getWebsiteUrlButton(final String url)
    {
        return Button.of(ButtonStyle.LINK, url, "CurseForge Page", CURSE);
    }
}
