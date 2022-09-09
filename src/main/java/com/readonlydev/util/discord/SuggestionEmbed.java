package com.readonlydev.util.discord;

import java.awt.Color;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;

@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SuggestionEmbed
{

    private String             title;
    private String             type;
    private String             numberAndAuthor;
    private Color              embedColor;
    private MessageEmbed.Field description;
    private MessageEmbed.Field status;
    

    private SuggestionEmbed(String title, String type, String numberAndAuthor, Color color, Field description)
    {
        this(title, type, numberAndAuthor, color, description, null);
    }

    public static SuggestionEmbed fromEmbed(MessageEmbed e)
    {
        if (e.getFields().size() == 2)
        {
            //@noformat
            return new SuggestionEmbed(
                e.getTitle(), 
                e.getAuthor().getName(), 
                e.getDescription(),
                e.getColor(),
                e.getFields().get(0), 
                e.getFields().get(1));
        } else {
            return new SuggestionEmbed(
                e.getTitle(), 
                e.getAuthor().getName(), 
                e.getDescription(),
                e.getColor(),
                e.getFields().get(0));
            //@format
        }

    }

    public void setStatus(SuggestionStatus status)
    {
        this.status = status.getStatusEmbedField();
    }
    
    public void setTitle(String title)
    {
        this.title = title;
    }
    
    public void setDescription(String description)
    {
        this.description = new MessageEmbed.Field("Description", description, false);
    }

    public EmbedBuilder toEmbedBuilder()
    {

        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(title).setAuthor(type).setDescription(numberAndAuthor).addField(description).setColor(embedColor);
        if(status != null)
        {
            builder.addField(status);
        }
        
        return builder;

    }
}
