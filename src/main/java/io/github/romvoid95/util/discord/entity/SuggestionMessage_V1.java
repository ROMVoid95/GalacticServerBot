package io.github.romvoid95.util.discord.entity;

import java.awt.Color;

import io.github.romvoid95.commands.core.EditType;
import io.github.romvoid95.util.discord.SuggestionStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.utils.SplitUtil;
import net.dv8tion.jda.api.utils.SplitUtil.Strategy;

@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SuggestionMessage_V1
{

    private String             title;
    private String             type;
    private String             numberAndAuthor;
    private Color              embedColor;
    private MessageEmbed.Field description;
    private MessageEmbed.Field status;

    private SuggestionMessage_V1(String title, String type, String numberAndAuthor, Color color, Field description)
    {
        this(title, type, numberAndAuthor, color, description, null);
    }

    public static SuggestionMessage_V1 fromEmbed(MessageEmbed e)
    {
        if (e.getFields().size() == 2)
        {
            //@noformat
            return new SuggestionMessage_V1(
                e.getTitle(), 
                e.getAuthor().getName(), 
                e.getDescription(),
                e.getColor(),
                e.getFields().get(0), 
                e.getFields().get(1));
        } else {
            return new SuggestionMessage_V1(
                e.getTitle(), 
                e.getAuthor().getName(), 
                e.getDescription(),
                e.getColor(),
                e.getFields().get(0));
            //@format
        }

    }
    
    public SuggestionMessage convertToNewFormat()
    {
        return SuggestionMessage.builder()
            .title(title)
            .numberAndAuthor(numberAndAuthor)
            .type(type)
            .description(SplitUtil.split(description.getValue(), 4092, Strategy.WHITESPACE, Strategy.ANYWHERE))
            .create();
    }
    
    public void setStatus(SuggestionStatus status)
    {
        this.status = status.getStatusEmbedField();
    }
    
    public void setTitle(String title)
    {
        this.title = title;
    }
    
    public void setDescription(EditType editType, String description)
    {
        if(editType.equals(EditType.APPEND))
        {
            String existing = this.description.getValue() + ". ";
            this.description = new MessageEmbed.Field("Description", existing + description, false);
        } else {
            this.description = new MessageEmbed.Field("Description", description, false);
        }
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
