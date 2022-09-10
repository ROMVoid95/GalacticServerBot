package com.readonlydev.core;

import com.readonlydev.command.Command;
import com.readonlydev.command.CommandListener;
import com.readonlydev.command.event.CommandEvent;
import com.readonlydev.command.slash.SlashCommand;
import com.readonlydev.command.slash.SlashCommandEvent;
import com.readonlydev.logback.LogUtils;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.ChannelType;

@Slf4j
public class ClientListener implements CommandListener
{

    @Override
    public void onCommand(CommandEvent event, Command command)
    {
        String asTag = event.getAuthor().getAsTag();
        String id = event.getAuthor().getId();
        String content = event.getMessage().getContentRaw();

        String output;
        
        if (event.getChannelType().equals(ChannelType.PRIVATE))
        {
            output = "%s(%s) invoked Command %s in Private Channel".formatted(asTag, id, content);
        } else
        {
            output = "[%s] %s(%s) invoked Command %s in Channel %s".formatted(event.getGuild().getName(), asTag, id, content, event.getChannel().getName());
        }
        
        LogUtils.log("CommandEvent", output);
        log.info(output);
    }

    @Override
    public void onSlashCommand(SlashCommandEvent event, SlashCommand command)
    {
        String asTag = event.getMember().getUser().getAsTag();
        String id = event.getMember().getId();
        String content = event.getCommandPath();
        
        String output;

        if (event.getChannelType().equals(ChannelType.PRIVATE))
        {
            output = "%s(%s) invoked Slash Command %s in Private Channel".formatted(asTag, id, content);
        } else
        {
            output = "[%s] %s(%s) invoked Slash Command %s in Channel %s".formatted(event.getGuild().getName(), asTag, id, content, event.getChannel().getName());
        }
        
        LogUtils.log("SlashCommandEvent", output);
        log.info(output);
    }
}
