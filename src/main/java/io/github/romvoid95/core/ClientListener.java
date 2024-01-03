package io.github.romvoid95.core;

import io.github.readonly.api.CommandListener;
import io.github.readonly.command.SlashCommand;
import io.github.readonly.command.event.SlashCommandEvent;
import io.github.romvoid95.util.LogUtils;
import io.github.romvoid95.util.discord.DiscordUtils;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.channel.ChannelType;

@Slf4j
public class ClientListener implements CommandListener
{
    @Override
    public void onCompletedSlashCommand(SlashCommandEvent event, SlashCommand command)
    {
        String asTag = DiscordUtils.getUser(event).getGlobalName();
        String id;
        String guild = "Private Channel";
        if(event.getChannelType().equals(ChannelType.PRIVATE))
        {
            id = event.getUser().getId();
        } else {
            id = event.getMember().getId();
            guild = event.getGuild().getName();
        }
        
        String commandName = event.getFullCommandName();
        String fullInput = event.getCommandString();

        LogUtils.log("SlashCommandEvent", webhookContent(event.getChannelType(), "Slash Command", guild, asTag, id, "`"+commandName+"`", event.getChannel().getName() + "\n\n"
            + "Full Input: `" + fullInput + "`"));
        log.info(logContent(event.getChannelType(), "Slash Command", guild, asTag, id, commandName, event.getChannel().getName() + " | input: " + fullInput));
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
