package io.github.romvoid95.util.discord.entity;

import static io.github.romvoid95.util.Embed.Condition.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import io.github.readonly.common.util.RGB;
import io.github.romvoid95.commands.core.EditType;
import io.github.romvoid95.util.Embed;
import io.github.romvoid95.util.discord.SuggestionStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.utils.SplitUtil;
import net.dv8tion.jda.api.utils.SplitUtil.Strategy;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SuggestionMessage
{

    private String             title;
    private String             type;
    private String             numberAndAuthor;
    private RGB                embedColor;
    private MessageEmbed.Field status;
    private List<MessageEmbed> descriptionEmbedList;

    private final List<MessageEmbed> embeds = new ArrayList<>();
    
    public static Function<String, List<String>> split = s -> SplitUtil.split(s, 4092, Strategy.WHITESPACE, Strategy.ANYWHERE);

    private SuggestionMessage(Builder builder)
    {
        this.title = builder.title;
        this.type = builder.type;
        this.numberAndAuthor = builder.numberAndAuthor;
        this.descriptionEmbedList = this.buildDescriptions(builder.descriptions, SuggestionStatus.NONE.getRGB());
        this.embedColor = SuggestionStatus.NONE.getRGB();
    }

    private static List<MessageEmbed> toEmbedsFromBuilder(Builder builder)
    {
        return new SuggestionMessage(builder).toData().getEmbeds();
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
        //@format:off
        String temp = this.collectionDescriptions();
        switch (editType)
        {
            case APPEND ->
            {
                this.descriptionEmbedList = this.buildDescriptions(split.apply("%s %s".formatted(temp, description)), this.embedColor);
            }
            case PREPEND ->
            {
                this.descriptionEmbedList = this.buildDescriptions(split.apply("%s %s".formatted(description, temp)), this.embedColor);
            }
            case REPLACE ->
            {
                this.descriptionEmbedList = this.buildDescriptions(split.apply(description), this.embedColor);
            }
        }
        //@format
    }

    private List<MessageEmbed> buildDescriptions(List<String> descriptions, RGB color)
    {
        List<MessageEmbed> list = new ArrayList<>();
        for (String d : descriptions)
        {
            list.add(Embed.descriptionEmbed(d, color).toEmbed());
        }
        return list;
    }

    private String collectionDescriptions()
    {
        return String.join(" ", this.descriptionEmbedList.stream().map(MessageEmbed::getDescription).toList());
    }

    public MessageCreateData toData()
    {
        this.embeds.clear();
        this.embeds.add(createHeaderEmbed());
        this.embeds.addAll(descriptionEmbedList);

        return new MessageCreateBuilder().addEmbeds(embeds).build();
    }

    private MessageEmbed createHeaderEmbed()
    {
        return Embed.newBuilder().title(title).setAuthor(type).color(embedColor).addFieldConditionally(NotNull(status)).toEmbed();
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static final class Builder
    {

        private String       title;
        private String       type;
        private String       numberAndAuthor;
        private List<String> descriptions;

        private Builder()
        {
        }

        public Builder title(String title)
        {
            this.title = title;
            return this;
        }

        public Builder type(String type)
        {
            this.type = type;
            return this;
        }

        public Builder numberAndAuthor(String numberAndAuthor)
        {
            this.numberAndAuthor = numberAndAuthor;
            return this;
        }

        public Builder description(List<String> descriptions)
        {
            this.descriptions = descriptions;
            return this;
        }

        public List<MessageEmbed> build()
        {
            return SuggestionMessage.toEmbedsFromBuilder(this);
        }
    }
}
