package io.github.romvoid95.util.discord;

import io.github.romvoid95.GalacticBot;
import io.github.romvoid95.core.guildlogger.RootLogChannel;
import io.github.romvoid95.core.guildlogger.ServerSettings;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.entities.Guild;

@UtilityClass
public class SettingsHelper
{
    public static RootLogChannel getRootLogChannel(Guild guild)
    {
        ServerSettings settings = GalacticBot.instance().getClient().getSettingsFor(guild);
        return settings.getRootLogger();
    }
}
