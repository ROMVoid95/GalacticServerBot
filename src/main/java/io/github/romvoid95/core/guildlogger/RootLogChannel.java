package io.github.romvoid95.core.guildlogger;

import java.awt.Color;
import java.time.Instant;

import com.github.readonlydevelopment.command.event.SlashCommandEvent;
import com.github.readonlydevelopment.common.utils.ResultLevel;

import io.github.romvoid95.GalacticBot;
import io.github.romvoid95.database.impl.Suggestion;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class RootLogChannel
{
    private final TextChannel channel;
    
    public RootLogChannel(TextChannel channel)
    {
        this.channel = channel;
    }
    
    public final void sendMessage(String content, ResultLevel level)
    {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setAuthor("Root Log");
        builder.setColor(level.getColor());
        builder.setDescription(content);
        builder.setTimestamp(Instant.now());
        
        this.channel.sendMessageEmbeds(builder.build()).queue();
    }
    
    public final void sendLogMessage(SlashCommandEvent event)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("User: %s".formatted(event.getUser().getAsTag())).append("\n");
        sb.append("In Channel: %s".formatted(event.getChannel().getName()));
        
        EmbedBuilder builder = new EmbedBuilder();
        builder.setAuthor("Root Log");
        builder.setColor(Color.RED);
        builder.setDescription("Command invoked by member without valid Permissions");
        builder.addField("Invoked: %s".formatted(event.getFullCommandName()), sb.toString(), false);
        builder.setTimestamp(Instant.now());
        
        this.channel.sendMessageEmbeds(builder.build()).queue();
    }
    
    public final void sendDeletedLog(Member member, Suggestion suggestion, String reason)
    {
        EmbedBuilder builder = new EmbedBuilder();
        StringBuilder sb = new StringBuilder();
        sb.append("Title: %s".formatted(suggestion.getTitle()));

        builder.setAuthor("Root Log");
        builder.setTitle("Suggestion Deleted");
        builder.setDescription(sb.toString());
        builder.setColor(Color.RED);
        builder.addField("Deleted By", member.getAsMention(), false);
        builder.addField("Reason", reason, false);
        builder.setTimestamp(Instant.now());
        
        this.channel.sendMessageEmbeds(builder.build()).queue();
    }
    
    public final void sendBlacklistedLog(JDA jda, Member member, String action, User user, String reason)
    {
        if(!GalacticBot.isTesting())
        {
            EmbedBuilder builder = new EmbedBuilder();
            
            String actionTaken;
            if(action.equals("add"))
            {
                actionTaken = "Added To";
            } else {
                actionTaken = "Removed From";
            }

            builder.setAuthor("Root Log");
            builder.setTitle("User %s Blacklist".formatted(actionTaken));
            builder.setDescription("User: **%s**".formatted(user.getAsMention()));
            builder.addField("Staff Member", member.getAsMention(), false);
            builder.addField("Reason", reason, false);
            builder.setColor(Color.RED);
            builder.setTimestamp(Instant.now());
            
            this.channel.sendMessageEmbeds(builder.build()).queue();   
        }
    }
}
