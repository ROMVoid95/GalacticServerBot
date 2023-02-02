package io.github.romvoid95.util.discord;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.github.readonly.common.util.KeyValueSupplier;
import net.dv8tion.jda.api.entities.MessageEmbed;

public enum SuggestionStatus implements KeyValueSupplier
{

    //@noformat
    NONE
    (
        Color.YELLOW,
        "None"
    ),
    CONSIDERED
    (
        Color.BLUE,
        "Considered",
        "This has been considered by the developers"
    ),
    REJECTED
    (
        Color.RED,
        "Rejected",
        "This has been rejected by the developers"
    ),
    APPROVED
    (
        Color.GREEN,
        "Approved",
        "This has been approved by the developers"
    ),
    IMPLEMENTED
    (
        Color.CYAN,
        "Implemented",
        "This has been implemented by the developers"
    );
  //@format

    private Color                         color;
    private String                        fieldTitle;
    private String                        fieldDescription;

    private static List<SuggestionStatus> NOT_EDITABLE = new ArrayList<>();

    static
    {
        NOT_EDITABLE.addAll(Arrays.asList(APPROVED, REJECTED, IMPLEMENTED));
    }

    private SuggestionStatus(Color color, String fieldTitle)
    {
        this.color = color;
        this.fieldTitle = fieldTitle;
        this.fieldDescription = null;
    }

    private SuggestionStatus(Color color, String fieldTitle, String fieldDescription)
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

    public Color getColor()
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
