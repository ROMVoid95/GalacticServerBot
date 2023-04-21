package io.github.romvoid95.util.discord.entity;

import static io.github.romvoid95.util.Embed.Condition.NotNull;

import java.util.ArrayList;
import java.util.List;

import io.github.readonly.common.util.RGB;
import io.github.romvoid95.commands.core.EditType;
import io.github.romvoid95.util.Embed;
import io.github.romvoid95.util.discord.SuggestionStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

@Data
@AllArgsConstructor
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class SuggestionMessage implements SuggestionMsg
{

    @NonNull private String             title;
    @NonNull private String             type;
    @NonNull private String             numberAndAuthor;
    @NonNull private RGB                embedColor;
    private MessageEmbed.Field status;
    @NonNull private List<MessageEmbed> descriptionEmbedList;


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
          //@format:off
            return new SuggestionMessage(
              mainMsg.getTitle(), 
              mainMsg.getAuthor().getName(), 
              mainMsg.getDescription(),
              new RGB(mainMsg.getColor().getRGB()),
              mainMsg.getFields().get(0),
              e.stream().skip(1).toList()
            );
        } else {
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

    private String collectionDescriptions()
    {
        return String.join(" ", this.descriptionEmbedList.stream().map(MessageEmbed::getDescription).toList());
    }

    @Override
    public MessageCreateData toData()
    {
        List<MessageEmbed> embeds = new ArrayList<>();
        embeds.add(createHeaderEmbed());
        embeds.addAll(descriptionEmbedList);
        return new MessageCreateBuilder().addEmbeds(embeds).build();
    }

    private MessageEmbed createHeaderEmbed()
    {
        return Embed.newBuilder()
            .title(title)
            .setAuthor(type)
            .description(numberAndAuthor)
            .color(embedColor)
            .addFieldConditionally(NotNull(status))
            .toEmbed();
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
