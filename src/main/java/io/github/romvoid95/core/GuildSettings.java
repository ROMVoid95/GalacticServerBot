package io.github.romvoid95.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.readonlydevelopment.settings.GuildSettingsManager;

import io.github.romvoid95.core.guildlogger.RootLogChannel;
import io.github.romvoid95.core.guildlogger.ServerSettings;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

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
