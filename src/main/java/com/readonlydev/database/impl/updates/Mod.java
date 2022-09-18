package com.readonlydev.database.impl.updates;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Mod
{
    private int fileId;
    private String updateChannelId;
    @Singular
    private List<Long> pingRoles;
    
    public boolean isFileNewer(int fileId)
    {
        return fileId > this.getFileId();
    }
}

