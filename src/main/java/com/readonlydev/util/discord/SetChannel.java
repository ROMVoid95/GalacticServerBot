package com.readonlydev.util.discord;

import com.readonlydev.BotData;
import com.readonlydev.GalacticBot;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;

public enum SetChannel
{
    POST
    (
        BotData.database().botDatabase().getSuggestionOptions().getSuggestionsChannelId(),
        BotData.communityServer(),
        BotData.botDevServer()
    ),
    
    POPULAR
    (
        BotData.database().botDatabase().getSuggestionOptions().getPopularChannelId(),
        BotData.communityServer(),
        BotData.botDevServer()
    ),
    
    DEV_POPULAR
    (
        BotData.database().botDatabase().getSuggestionOptions().getDevServerPopularChannelId(),
        BotData.devServer(),
        BotData.botDevDevServer()
    );
    
    private String channelId;
    private Guild guild;
    private Guild testingGuild;
    
    SetChannel(String channelId, Guild guild, Guild testingGuild)
    {
        this.channelId = channelId;
        this.guild = guild;
        this.testingGuild = testingGuild;
    }
    
    public MessageChannel getChannel()
    {
        if(GalacticBot.isTesting())
        {
            return testingGuild.getTextChannelById(channelId);
        } else {
            return guild.getTextChannelById(channelId);
        }
    }
}
