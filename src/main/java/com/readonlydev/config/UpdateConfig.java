package com.readonlydev.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class UpdateConfig
{

    private EmbeddedServer embeddedServer = new EmbeddedServer();
    private CurseForge     curseforge     = new CurseForge();

    public EmbeddedServer EmbeddedSerer()
    {
        return embeddedServer;
    }

    public CurseForge CurseForge()
    {
        return curseforge;
    }

    @Getter
    @NoArgsConstructor
    public static class EmbeddedServer
    {
        private String hostname = "127.0.0.1";
        private int    port     = 3306;
        private String username = "curseforge";
        private String password = "password";
        private String dbName   = "curseforge";
        private String dataDir  = "database";
    }

    @Getter
    @NoArgsConstructor
    public static class CurseForge
    {
        private String apiKey               = "InsertHere";
        private String responseLogChannelId = "channelId";
    }
}
