package io.github.romvoid95.util.discord;

import io.github.romvoid95.BotData;
import io.github.romvoid95.Servers;
import io.github.romvoid95.server.Server;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;

public enum SetChannel
{
    POST
    (
        BotData.database().galacticBot().getSuggestionOptions().getSuggestionsChannelId(),
        Servers.galacticraftCentral
    ),
    
    POPULAR
    (
        BotData.database().galacticBot().getSuggestionOptions().getPopularChannelId(),
        Servers.galacticraftCentral
    ),
    
    DEV_POPULAR
    (
        BotData.database().galacticBot().getSuggestionOptions().getDevServerPopularChannelId(),
        Servers.teamGalacticraft
    );
    
    private String channelId;
    private Server server;
    
    SetChannel(String channelId, Server server)
    {
        this.channelId = channelId;
        this.server = server;
    }
    
    public MessageChannel getChannel()
    {
    	Guild guild = server.getGuild();
    	return guild.getTextChannelById(channelId);
    }
}
