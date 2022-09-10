package com.readonlydev.database.entity;

import java.beans.ConstructorProperties;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.readonlydev.database.ManagedObject;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DBBlacklist implements ManagedObject
{

    public static final String DB_TABLE = "blacklist";
    private List<String>       users;

    @JsonCreator
    @ConstructorProperties({"users"})
    public DBBlacklist(@JsonProperty("users") List<String> users)
    {
        this.users = users;
    }

    public static DBBlacklist create()
    {
        DBBlacklist blacklist = new DBBlacklist(new LinkedList<>());
        blacklist.save();
        return blacklist;
    }

    public boolean addToBlacklist(String userId)
    {
        if (getUsers().contains(userId))
        {
            return false;
        } else
        {
            getUsers().add(userId);
            this.saveUpdating();
            return true;
        }
    }

    public boolean removeFromBlacklist(String userId)
    {
        if (getUsers().contains(userId))
        {
            getUsers().remove(userId);
            this.saveUpdating();
            return true;
        } else
        {
            return false;
        }
    }
    
    public boolean isBlacklisted(String userId)
    {
        return this.getUsers().contains(userId);
    }

    @Override
    public String getId()
    {
        return "blacklist";
    }

    @Override
    public String getTableName()
    {
        return "blacklist";
    }

}
