package com.readonlydev.database.impl.options;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.readonlydev.GalacticBot;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class SuggestionOptions
{

    private String               suggestionsChannelId      = "1017836524374589460";
    private String               popularChannelId          = "1017836571090768033";
    private String               devServerPopularChannelId = "988628452267671642";
    private boolean              suggestionsLocked         = false;
    private int                  starRequirement           = 10;

    @JsonIgnore
    public String getSuggestionChannel()
    {
        if (GalacticBot.isTesting())
        {
            return Development.suggestionsChannelIdOverride;
        }

        return suggestionsChannelId;
    }

    @JsonIgnore
    public String getPopularSuggestionChannel()
    {
        if (GalacticBot.isTesting())
        {
            return Development.popularIdOverride;
        }

        return popularChannelId;
    }

    @JsonIgnore
    public String getDevServerPopularChannel()
    {
        if (GalacticBot.isTesting())
        {
            return Development.devServerPopularIdOverride;
        }

        return devServerPopularChannelId;
    }
    
    public final class Development {
        public static final String suggestionsChannelIdOverride = "1008727436919320596";
        public static final String popularIdOverride = "1008728007411761263";
        public static final String devServerPopularIdOverride = "1014817759168827434";
    }
}
