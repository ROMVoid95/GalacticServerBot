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
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.utils.SplitUtil;
import net.dv8tion.jda.api.utils.SplitUtil.Strategy;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

@AllArgsConstructor
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class SuggestionMessage
{

    @NonNull
    private String             title;
    @NonNull
    private String             type;
    @NonNull
    private String             numberAndAuthor;
    @NonNull
    private RGB                embedColor;
    private MessageEmbed.Field status;
    @NonNull
    private List<MessageEmbed> descriptionEmbedList;

    public static Function<String, List<String>> split = s -> SplitUtil.split(s, 4092, Strategy.WHITESPACE, Strategy.ANYWHERE);

    private SuggestionMessage(Builder builder)
    {
        this.title = builder.title;
        this.type = builder.type;
        this.numberAndAuthor = builder.numberAndAuthor;
        this.descriptionEmbedList = this.buildDescriptions(builder.descriptions, SuggestionStatus.NONE.getRGB());
        this.embedColor = SuggestionStatus.NONE.getRGB();
    }

    public List<MessageEmbed> toEmbeds()
    {
        return toData().getEmbeds();
    }

    private static List<MessageEmbed> toEmbedsFromBuilder(Builder builder)
    {
        return new SuggestionMessage(builder).toData().getEmbeds();
    }

    public static SuggestionMessage fromMessage(List<MessageEmbed> e)
    {
        MessageEmbed mainMsg = e.get(0);
        if (mainMsg.getFields().size() == 1)
        {
            //@noformat
            return new SuggestionMessage(
                mainMsg.getTitle(), 
                mainMsg.getAuthor().getName(), 
                mainMsg.getDescription(), 
                new RGB(mainMsg.getColor().getRGB()), 
                mainMsg.getFields().get(0), 
                e.stream().skip(1).toList()
            );
        } else
        {
            return new SuggestionMessage(
                mainMsg.getTitle(), 
                mainMsg.getAuthor().getName(), 
                mainMsg.getDescription(), 
                new RGB(mainMsg.getColor().getRGB()), 
                e.stream().skip(1).toList()
                );
            //@format
        }

    }

    public void setStatus(SuggestionStatus status)
    {
        this.status = status.getStatusEmbedField();
    }

    public void setTitle(EditType editType, String title)
    {
        switch (editType)
        {
            case APPEND ->
            {
                this.title = "%s %s".formatted(this.title, title);
            }
            case PREPEND ->
            {
                this.title = "%s %s".formatted(title, this.title);
            }
            case REPLACE ->
            {
                this.title = title;
            }
        }
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
            list.add(Embed.descriptionEmbed(d, color).title("Description").toEmbed());
        }
        return list;
    }

    private String collectionDescriptions()
    {
        return String.join(" ", this.descriptionEmbedList.stream().map(MessageEmbed::getDescription).toList());
    }

    public MessageCreateData toData()
    {
        List<MessageEmbed> embeds = new ArrayList<>();
        embeds.add(createHeaderEmbed());
        embeds.addAll(descriptionEmbedList);
        return new MessageCreateBuilder().addEmbeds(embeds).build();
    }

    private MessageEmbed createHeaderEmbed()
    {
        return Embed.newBuilder().title(title).setAuthor(type).description(numberAndAuthor).color(embedColor).addFieldConditionally(NotNull(status)).toEmbed();
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

        public SuggestionMessage create()
        {
            return new SuggestionMessage(this);
        }

        public List<MessageEmbed> build()
        {
            return SuggestionMessage.toEmbedsFromBuilder(this);
        }
    }
}
