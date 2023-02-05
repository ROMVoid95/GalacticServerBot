package io.github.romvoid95.database.impl.updates;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateMod
{
	private boolean active = false;
    private Modrinth modrinth;
    private Curseforge curseforge;
    private Map<String, UpdateInfo> notifications = new HashMap<>();

    public List<R_UpdateInfo> infoRecords()
    {
    	return notifications.values().stream().map(
    		i -> new R_UpdateInfo(i.getChannelId(), i.getPingRoleId())
    	).collect(Collectors.toList());
    }
    
    public R_Platforms platforms()
    {
    	return new R_Platforms(Optional.ofNullable(curseforge), Optional.ofNullable(modrinth));
    }

    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateInfo {
    	private String channelId;
    	private String pingRoleId;
    }
    
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Modrinth {
    	private String projectId;
    	private String latestVersionId;
    }
    
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Curseforge {
    	private Integer projectId;
    	private Integer latestFileId;
    }
}

