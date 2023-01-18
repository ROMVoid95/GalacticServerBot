package io.github.romvoid95.core;

import com.github.readonlydevelopment.command.Command;
import com.github.readonlydevelopment.command.CommandListener;
import com.github.readonlydevelopment.command.SlashCommand;
import com.github.readonlydevelopment.command.event.CommandEvent;
import com.github.readonlydevelopment.command.event.SlashCommandEvent;

import io.github.romvoid95.logback.LogUtils;
import io.github.romvoid95.util.discord.DiscordUtils;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.channel.ChannelType;

@Slf4j
public class ClientListener implements CommandListener
{
    @Override
    public void onCompletedCommand(CommandEvent event, Command command)
    {
        String asTag = DiscordUtils.getUser(event).getAsTag();
        String id = event.getAuthor().getId();
        String content = event.getMessage().getContentRaw();

        LogUtils.log("CommandEvent", webhookContent(event.getChannelType(), "Slash Command", event.getGuild().getName(), asTag, id, content, event.getChannel().getName()));
        log.info(logContent(event.getChannelType(), "Slash Command", event.getGuild().getName(), asTag, id, content, event.getChannel().getName()));
    }

    @Override
    public void onCompletedSlashCommand(SlashCommandEvent event, SlashCommand command)
    {
        String asTag = DiscordUtils.getUser(event).getAsTag();
        String id = event.getMember().getId();
        String content = event.getFullCommandName();

        LogUtils.log("SlashCommandEvent", webhookContent(event.getChannelType(), "Slash Command", event.getGuild().getName(), asTag, id, content, event.getChannel().getName()));
        log.info(logContent(event.getChannelType(), "Slash Command", event.getGuild().getName(), asTag, id, content, event.getChannel().getName()));
    }

    private String webhookContent(ChannelType channelType, String command, String guildName, String tag, String id, String content, String channelName)
    {
        if(channelType.equals(ChannelType.PRIVATE))
        {
            return "**%s** (*%s*)\n\ninvoked %s %s in Private Channel".formatted(tag, id, command, content);
        } else {
            return "**[%s]**\n\n**%s** (*%s*)\n\ninvoked %s %s in Channel %s".formatted(guildName, tag, id, command, content, channelName);
        }
    }
    
    private String logContent(ChannelType channelType, String command, String guildName, String tag, String id, String content, String channelName)
    {
        if(channelType.equals(ChannelType.PRIVATE))
        {
            return "%s (%s) | invoked %s %s in Private Channel".formatted(tag, id, command, content);
        } else {
            return "[%s] | %s (%s) | invoked %s %s in Channel %s".formatted(guildName, tag, id, command, content, channelName);
        }
    }
}
