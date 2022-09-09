package com.readonlydev.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.readonlydev.core.guildlogger.RootLogChannel;
import com.readonlydev.core.guildlogger.ServerSettings;
import com.readonlydev.settings.GuildSettingsManager;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

public class GuildSettings implements GuildSettingsManager<ServerSettings>
{
    
    private Map<Guild, ServerSettings> guildMap = new HashMap<>();

    @Override
    public ServerSettings getSettings(Guild guild)
    {
        return guildMap.get(guild);
    }
    
    @Override
    public void init(JDA jda)
    {
        List<Guild> guilds = jda.getGuilds();
        for(Guild g : guilds)
        {
            ServerSettings ss = new ServerSettings(g);
            TextChannel channel = g.getTextChannelsByName("root-log", true).get(0);
            ss.setRootLogger(new RootLogChannel(channel));
            guildMap.put(g, ss);
        }
    }
}
