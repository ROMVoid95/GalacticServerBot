package com.readonlydev.core.guildlogger;

import java.awt.Color;
import java.time.Instant;

import com.readonlydev.command.slash.SlashCommandEvent;
import com.readonlydev.database.impl.Suggestion;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class RootLogChannel
{
    private final TextChannel channel;
    
    public RootLogChannel(TextChannel channel)
    {
        this.channel = channel;
    }
    
    public final void sendLogMessage(SlashCommandEvent event)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("User: %s".formatted(event.getUser().getAsTag())).append("\n");
        sb.append("In Channel: %s".formatted(event.getChannel().getName()));
        
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Color.RED);
        builder.setTitle("Root Log");
        builder.setDescription("Command invoked by member without valid Permissions");
        builder.addField("Invoked: %s".formatted(event.getCommandPath()), sb.toString(), false);
        builder.setTimestamp(Instant.now());
        
        this.channel.sendMessageEmbeds(builder.build()).queue();
    }
    
    public final void sendDeletedLog(Member member, Suggestion suggestion, String reason)
    {
        EmbedBuilder builder = new EmbedBuilder();
        StringBuilder sb = new StringBuilder();
        sb.append("Title: %s".formatted(suggestion.getTitle()));

        builder.setTitle("Suggestion Deleted");
        builder.setDescription(sb.toString());
        builder.setColor(Color.RED);
        builder.addField("Deleted By", member.getAsMention(), false);
        builder.addField("Reason", reason, false);
        
        this.channel.sendMessageEmbeds(builder.build()).queue();
    }
    
    public final void sendBlacklistedLog(JDA jda, Member member, String action, String userId, String reason)
    {
        EmbedBuilder builder = new EmbedBuilder();
        User user = jda.getUserById(userId);
        
        String actionTaken;
        if(action.equals("add"))
        {
            actionTaken = "Added To";
        } else {
            actionTaken = "Removed From";
        }
        String mentionOrId;
        if(user != null)
        {
            mentionOrId = user.getAsMention();
        } else {
            mentionOrId = "with ID " + userId;
        }
        
        builder.setTitle("User %s Blacklist".formatted(actionTaken));
        builder.setDescription("User: **%s**".formatted(mentionOrId));
        builder.addField("Staff Member", member.getAsMention(), false);
        builder.addField("Reason", reason, false);
        builder.setColor(Color.RED);
        
        this.channel.sendMessageEmbeds(builder.build()).queue();
    }
}
