package com.readonlydev.database.entity;

import java.beans.ConstructorProperties;

import javax.annotation.Nonnull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.errorprone.annotations.CheckReturnValue;
import com.readonlydev.database.DBObject;
import com.readonlydev.util.entity.Addon;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AddonObject implements DBObject {
    public static final String DB_TABLE = "addons";
    private final Addon data;
    private final String id;
    
    //@JsonIgnore
    //private final Config config = Accessors.config();
    
    @JsonCreator
    @ConstructorProperties({"id", "data"})
    public AddonObject(@JsonProperty("id") String id, @JsonProperty("data") Addon data) {
        this.id = id;
        this.data = data;
    }
    
    public static AddonObject of(String name) {
    	return new AddonObject(name, new Addon());
    }

    @Nonnull
    @CheckReturnValue
    public AddonObject getAddonObject(@Nonnull Addon addon) {
        return AddonObject.of(addon.getAddonData().getModName());
    }
    
    public Addon getAddon() {
    	return this.data;
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
