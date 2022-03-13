package com.readonlydev.database.entity;

import java.beans.ConstructorProperties;

import javax.annotation.Nonnull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.readonlydev.database.ManagedObject;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class BotObj implements ManagedObject {
    public static final String DB_TABLE = "botgc";
    public static final String id = "botgc";
    public String linksChannelId;
    
    @ConstructorProperties({"linksChannelId"})
    @JsonCreator
    public BotObj(@JsonProperty("linksChannel") String linksChannelId) {
        this.linksChannelId = linksChannelId;
    }
    
    public static BotObj create() {
        return new BotObj("undefined");
    }

    @Override
	@Nonnull
    public String getId() {
        return id;
    }

    @JsonIgnore
    @Override
    @Nonnull
    public String getTableName() {
        return DB_TABLE;
    }
}
