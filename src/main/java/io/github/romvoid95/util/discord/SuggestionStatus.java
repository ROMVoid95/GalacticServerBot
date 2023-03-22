package io.github.romvoid95.util.discord;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.github.readonly.common.util.KeyValueSupplier;
import io.github.readonly.common.util.RGB;
import net.dv8tion.jda.api.entities.MessageEmbed;

public enum SuggestionStatus implements KeyValueSupplier
{

	//@noformat
    NONE
    (
    	RGB.YELLOW,
        "None"
    ),
    CONSIDERED
    (
    	RGB.BLUE,
        "Considered",
        "This has been considered by the developers"
    ),
    REJECTED
    (
    	RGB.RED,
        "Rejected",
        "This has been rejected by the developers"
    ),
    APPROVED
    (
    	RGB.GREEN,
        "Approved",
        "This has been approved by the developers"
    ),
    IMPLEMENTED
    (
    	RGB.CYAN,
        "Implemented",
        "This has been implemented by the developers"
    );
  //@format

	private RGB		color;
	private String	fieldTitle;
	private String	fieldDescription;

	private static List<SuggestionStatus> NOT_EDITABLE = new ArrayList<>();

	static
	{
		NOT_EDITABLE.addAll(Arrays.asList(APPROVED, REJECTED, IMPLEMENTED));
	}

	private SuggestionStatus(RGB color, String fieldTitle)
	{
		this.color = color;
		this.fieldTitle = fieldTitle;
		this.fieldDescription = null;
	}

	private SuggestionStatus(RGB color, String fieldTitle, String fieldDescription)
	{
		this.color = color;
		this.fieldTitle = fieldTitle;
		this.fieldDescription = fieldDescription;
	}

	@Override
	public String key()
	{
		return fieldTitle;
	}

	public String getName()
	{
		return key();
	}

	public RGB getRGB()
	{
		return color;
	}

	@Override
	public String value()
	{
		return toString();
	}

	@Override
	public String toString()
	{
		return super.name().toLowerCase();
	}

	public MessageEmbed.Field getStatusEmbedField()
	{
		return new MessageEmbed.Field(fieldTitle, fieldDescription, false);
	}

	public static boolean nonEditable(SuggestionStatus status)
	{
		return NOT_EDITABLE.contains(status);
	}
}
