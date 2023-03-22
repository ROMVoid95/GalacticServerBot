package io.github.romvoid95.util.discord.entity;

import java.util.ArrayList;
import java.util.List;

import io.github.readonly.common.util.Embed;
import io.github.readonly.common.util.Embed.Condition;
import io.github.readonly.common.util.RGB;
import io.github.romvoid95.commands.core.EditType;
import io.github.romvoid95.util.discord.SuggestionStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SuggestionMessage_V2
{
	private String				title;
	private String				type;
	private String				numberAndAuthor;
	private RGB					embedColor;
	private MessageEmbed.Field	status;
	private MessageEmbed		descriptionEmbed;

	private final List<MessageEmbed> embeds = new ArrayList<>();

	private SuggestionMessage_V2(Builder builder)
	{
		this.title = builder.title;
		this.type = builder.type;
		this.numberAndAuthor = builder.numberAndAuthor;
		this.descriptionEmbed = Embed.newBuilder().description(builder.description).toEmbed();
		this.embedColor = SuggestionStatus.NONE.getRGB();
	}

	private static List<MessageEmbed> toEmbedsFromBuilder(Builder builder)
	{
		return new SuggestionMessage_V2(builder).toData().getEmbeds();
	}

	public static SuggestionMessage_V2 fromMessage(List<MessageEmbed> e)
	{
		MessageEmbed mainMsg = e.get(0);
		if (mainMsg.getFields().size() == 1)
		{
			//@noformat
            return new SuggestionMessage_V2(
            	mainMsg.getTitle(), 
            	mainMsg.getAuthor().getName(), 
            	mainMsg.getDescription(),
            	new RGB(mainMsg.getColor().getRGB()),
            	mainMsg.getFields().get(0),
            	e.get(1)
            );
        } else {
            return new SuggestionMessage_V2(
            	mainMsg.getTitle(), 
            	mainMsg.getAuthor().getName(), 
            	mainMsg.getDescription(),
            	new RGB(mainMsg.getColor().getRGB()),
            	e.get(1)
            );
            //@format
		}

	}

	private SuggestionMessage_V2(String title, String type, String numberAndAuthor, RGB color, MessageEmbed descriptionEmbed)
	{
		this(title, type, numberAndAuthor, color, null, descriptionEmbed);
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
		switch (editType)
		{
		case APPEND -> this.descriptionEmbed = Embed.fromMessageEmbed(this.descriptionEmbed).description(formatDescriptioon(description, false)).toEmbed();
		case PREPEND -> this.descriptionEmbed = Embed.fromMessageEmbed(this.descriptionEmbed).description(formatDescriptioon(description, true)).toEmbed();
		case REPLACE -> this.descriptionEmbed = Embed.fromMessageEmbed(this.descriptionEmbed).description(description).toEmbed();
		}
	}

	private String formatDescriptioon(String description, boolean isPrepend)
	{
		String existing = this.descriptionEmbed.getDescription();
		if (isPrepend)
		{
			if (description.endsWith(". ") || description.endsWith(" "))
				return description + existing;
			else
				return description + ". " + existing;
		} else
		{
			if (existing.endsWith(". ") || existing.endsWith(" ") || description.startsWith(". ") || description.startsWith(" "))
				return existing + description;
			else
				return existing + ". " + description;
		}
	}

	public MessageCreateData toData()
	{
		this.embeds.clear();
		this.embeds.add(createHeaderEmbed());
		this.embeds.add(descriptionEmbed);

		return new MessageCreateBuilder().addEmbeds(embeds).build();
	}

	private MessageEmbed createHeaderEmbed()
	{
		return Embed.newBuilder().title(title).setAuthor(type).color(embedColor).addFieldConditionally(Condition.NotNull(status)).toEmbed();
	}

	public static Builder builder()
	{
		return new Builder();
	}

	public static final class Builder
	{
		private String				title;
		private String				type;
		private String				numberAndAuthor;
		private String				description;

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

		public Builder description(String description)
		{
			this.description = description;
			return this;
		}

		public List<MessageEmbed> build()
		{
			return SuggestionMessage_V2.toEmbedsFromBuilder(this);
		}
	}
}
