package com.readonlydev.database;

import static com.readonlydev.database.Rethink.Rethink;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;

import com.readonlydev.database.entity.DBGalacticBot;
import com.readonlydev.database.impl.SuggestionManager;
import com.readonlydev.database.impl.options.HasteOptions;
import com.readonlydev.database.impl.options.SuggestionOptions;
import com.rethinkdb.net.Connection;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DatabaseManager
{

    private final Connection conn;

    public DatabaseManager(@Nonnull Connection conn)
    {
        this.conn = conn;
    }

    @Nonnull
    @CheckReturnValue
    public DBGalacticBot botDatabase()
    {
        DBGalacticBot obj = Rethink.table(DBGalacticBot.DB_TABLE).get("galacticbot").runAtom(conn, DBGalacticBot.class);
        return obj == null ? DBGalacticBot.create() : obj;
    }

    @Nonnull
    @CheckReturnValue
    public HasteOptions getHasteOptions()
    {
        return botDatabase().getHasteOptions();
    }

    @Nonnull
    @CheckReturnValue
    public SuggestionOptions getSuggestionOptions()
    {
        return botDatabase().getSuggestionOptions();
    }

    @Nonnull
    @CheckReturnValue
    public SuggestionManager getManager()
    {
        return botDatabase().getManager();
    }    
    
    public void save(@Nonnull ManagedObject object)
    {
        log.info("Saving {} {}:{} to rethink (replacing)", object.getClass().getSimpleName(), object.getTableName(), object.getDatabaseId());

        Rethink.table(object.getTableName()).insert(object).optArg("conflict", "replace").runNoReply(conn);
    }

    public void saveUpdating(@Nonnull ManagedObject object)
    {
        log.info("Saving {} {}:{} to rethink (updating)", object.getClass().getSimpleName(), object.getTableName(), object.getDatabaseId());

        Rethink.table(object.getTableName()).insert(object).optArg("conflict", "update").runNoReply(conn);
    }

    public void delete(@Nonnull ManagedObject object)
    {
        log.info("Deleting {} {}:{} from rethink", object.getClass().getSimpleName(), object.getTableName(), object.getDatabaseId());

        Rethink.table(object.getTableName()).get(object.getId()).delete().runNoReply(conn);
    }
    
    public Connection getConnection()
    {
        return conn;
    }
}
