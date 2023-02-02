package io.github.romvoid95.database.impl.updates;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Mod
{
	private boolean active = false;
    private Modrinth modrinth = new Modrinth();
    private Curseforge curseforge = new Curseforge();
    private Map<String, UpdateInfo> notifications = new HashMap<>();

    
    @Setter
    @Getter
    @NoArgsConstructor
    public static class UpdateInfo {
    	private String channelId;
    	private String pingRoleId;
    	
    	@JsonIgnore
    	public Optional<String> getRoleId()
    	{
    		return Optional.ofNullable(pingRoleId);
    	}
    }
    
    @Getter
    @Setter
    @NoArgsConstructor
    public static class Modrinth {
    	private String projectId;
    	private String latestVersionId;
    }
    
    @Getter
    @Setter
    @NoArgsConstructor
    public static class Curseforge {
    	private String projectId;
    	private String latestFileId;
    }
}

