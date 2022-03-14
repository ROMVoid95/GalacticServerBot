package com.readonlydev.database;

import javax.annotation.Nonnull;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface DBObject {
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
    
    /**
     * Deleted an object from the database.
     */
    default void delete() {
        DatabaseAccessor.database().delete(this);
    }

    /**
     * Saves an object to the database by replacing the value.
     */
    default void saveByReplacing() {
    	DatabaseAccessor.database().save(this);
    }

    /**
     * Updates an object in the database then saves the value to disk.
     */
    default void updateAndSave() {
    	DatabaseAccessor.database().saveUpdating(this);
    }

    
    /**
     * Attempts to delete this object from the database.
     * But remains in the queue if the server is busy
     */
    default void deleteAsync() {
    	DatabaseAccessor.Scheduled.queue(this::delete);
    }

    /**
     * Attempts to save this object to the database.
     * But remains in the queue if the server is busy
     */
    default void saveAsync() {
    	DatabaseAccessor.Scheduled.queue(this::saveByReplacing);
    }
}
