package com.readonlydev.util.discord;

import com.readonlydev.GalacticBot;
import com.readonlydev.core.guildlogger.RootLogChannel;
import com.readonlydev.core.guildlogger.ServerSettings;

import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.entities.Guild;

@UtilityClass
public class SettingsHelper
{
    public static RootLogChannel getRootLogChannel(Guild guild)
    {
        ServerSettings settings = GalacticBot.getClient().getSettingsFor(guild);
        return settings.getRootLogger();
    }
}
