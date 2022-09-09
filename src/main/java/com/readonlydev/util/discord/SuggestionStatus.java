package com.readonlydev.util.discord;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.dv8tion.jda.api.entities.MessageEmbed;

public enum SuggestionStatus
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
        "This has been considered by the developers.\n\nOnce a final decision is made this message will change \nto either ***APPROVED*** or ***REJECTED***"
    ),
    REJECTED
    (
        Color.RED,
        "Rejected",
        "This has been rejected by the developers.\n\nIf reversed this message will change to ***APPROVED***.\nDuplicate suggestions made after will be deleted"
    ),
    APPROVED
    (
        Color.GREEN,
        "Approved",
        "This has been approved by the developers.\n\nOnce fully implemented this message will change to ***IMPLEMENTED***.\nNo ETA will be given or hinted towards"
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

    public String getName()
    {
        return fieldTitle;
    }

    public Color getColor()
    {
        return color;
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
