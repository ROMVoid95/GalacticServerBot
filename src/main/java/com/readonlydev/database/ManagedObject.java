package com.readonlydev.database;

import javax.annotation.Nonnull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.readonlydev.BotData;

public interface ManagedObject {
    @Nonnull
    String getId();

    @JsonIgnore
    @Nonnull
    String getTableName();

    @JsonIgnore
    @Nonnull
    default String getDatabaseId() {
        return getId();
    }
    
    default void delete() {
        BotData.database().delete(this);
    }

    /**
     * Saves an object to the database.
     * This will save the object by REPLACING it, instead of updating.
     * Useful sometimes.
     */
    default void save() {
        BotData.database().save(this);
    }

    /**
     * Saves an object to the database.
     * This will save the object by updating it.
     * Useful sometimes.
     */
    default void saveUpdating() {
        BotData.database().saveUpdating(this);
    }
    
    default void saveUpdateAsync() {
        BotData.queue(this::saveUpdating);
    }

    default void deleteAsync() {
        BotData.queue(this::delete);
    }

    default void saveAsync() {
        BotData.queue(this::save);
    }
}
