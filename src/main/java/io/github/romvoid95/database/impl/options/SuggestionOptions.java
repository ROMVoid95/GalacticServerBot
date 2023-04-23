package io.github.romvoid95.database.impl.options;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.github.romvoid95.GalacticBot;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

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

    public TextChannel getSuggestionChannel()
    {
        return GalacticBot.instance().getJda().getTextChannelById(getSuggestionsChannelId());
    }
    
    public TextChannel getPopularChannel()
    {
        return GalacticBot.instance().getJda().getTextChannelById(getPopularChannelId());
    }
    
    public TextChannel getDevPopularChannel()
    {
        return GalacticBot.instance().getJda().getTextChannelById(getDevServerPopularChannelId());
    }

    public final class Development {
        public static final String suggestionsChannelIdOverride = "1008727436919320596";
        public static final String popularIdOverride = "1008728007411761263";
        public static final String devServerPopularIdOverride = "1014817759168827434";
    }
}
