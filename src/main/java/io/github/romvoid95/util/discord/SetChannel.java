package io.github.romvoid95.util.discord;

import io.github.romvoid95.BotData;
import io.github.romvoid95.Server;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;

public enum SetChannel
{
    POST
    (
        BotData.database().botDatabase().getSuggestionOptions().getSuggestionChannel(),
        BotData.galacticraftCentralServer()
    ),
    
    POPULAR
    (
        BotData.database().botDatabase().getSuggestionOptions().getPopularChannelId(),
        BotData.galacticraftCentralServer()
    ),
    
    DEV_POPULAR
    (
        BotData.database().botDatabase().getSuggestionOptions().getDevServerPopularChannel(),
        BotData.teamGalacticraftServer()
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
    	return server.getGuild().get().getTextChannelById(channelId);
    }
}