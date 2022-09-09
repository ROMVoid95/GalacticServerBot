package com.readonlydev.core.guildlogger;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Guild;

public class ServerSettings
{

    @Getter @Setter
    private RootLogChannel rootLogger;
    @Getter
    private final Guild    guild;

    public ServerSettings(Guild guild)
    {
        this.guild = guild;
    }
}
