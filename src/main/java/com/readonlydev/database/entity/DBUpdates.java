package com.readonlydev.database.entity;

import java.beans.ConstructorProperties;
import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.readonlydev.database.ManagedObject;
import com.readonlydev.database.impl.updates.Mod;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DBUpdates implements ManagedObject
{
    public static final String       DB_TABLE = "updates";
    private Map<Integer, Mod> mods;
    
    @JsonCreator
    @ConstructorProperties({"mods"})
    public DBUpdates(@JsonProperty("mods") Map<Integer, Mod> mods)
    {
        this.mods = mods;
    }
    
    public static DBUpdates create()
    {
        DBUpdates updates = new DBUpdates(new LinkedHashMap<>());
        updates.save();
        return updates;
    }
    
//    public Mod addNewMod(int modId, String updateChannelId)
//    {
//        if(getMods().containsKey(modId))
//        {
//            return ExistingCurseMod.fromMod(getMods().get(modId));
//        }
//        
//        Mod newMod = Mod.builder()
//            .fileId(ModHelper.getLastestFileId(modId))
//            .updateChannelId(updateChannelId)
//            .build();
//        
//        this.getMods().put(modId, newMod);
//        this.saveUpdating();
//        
//        return newMod;
//    }
    
    public Mod getMod(int modId)
    {
        return getMods().get(modId);
    }
    
    public void updateLatestFileId(int modId, int fileId)
    {
        Mod mod = getMods().get(modId);
        mod.setFileId(fileId);
        this.saveUpdating();
    }
    
    @Override
    public String getId()
    {
        return DBUpdates.DB_TABLE;
    }

    @Override
    public String getTableName()
    {
        return DBUpdates.DB_TABLE;
    }
}
