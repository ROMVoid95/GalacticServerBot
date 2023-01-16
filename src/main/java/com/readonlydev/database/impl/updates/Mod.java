package com.readonlydev.database.impl.updates;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true)
public class Mod
{
    private final Modrinth modrinth;
    private final Curseforge curseforge;
    private String updateChannelId;
    private List<Long> pingRoles = new ArrayList<>();;
    
    public Mod(String modrinthProjectSlug, long curseforgeProjectId)
    {
    	this.modrinth = new Modrinth(modrinthProjectSlug);
    	this.curseforge = new Curseforge(curseforgeProjectId);
    }
    
    @Data
    public class Modrinth {
    	private final String slug;
    	private int versionCount;
    	private String latestVersionId;
    }
    
    @Data
    public class Curseforge {
    	private final long projectId;
    	private long latestFileId;
    }
}

