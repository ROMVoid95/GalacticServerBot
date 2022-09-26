package com.readonlydev.database;

import static com.readonlydev.database.Rethink.Rethink;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;

import com.readonlydev.database.entity.DBBlacklist;
import com.readonlydev.database.entity.DBGalacticBot;
import com.rethinkdb.net.Connection;

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
    public DBBlacklist blacklist()
    {
        DBBlacklist obj = Rethink.table(DBBlacklist.DB_TABLE).get("blacklist").runAtom(conn, DBBlacklist.class);
        return obj == null ? DBBlacklist.create() : obj;
    }

    public void save(@Nonnull ManagedObject object)
    {
        Rethink.table(object.getTableName()).insert(object).optArg("conflict", "replace").runNoReply(conn);
    }

    public void saveUpdating(@Nonnull ManagedObject object)
    {
        Rethink.table(object.getTableName()).insert(object).optArg("conflict", "update").runNoReply(conn);
    }

    public void delete(@Nonnull ManagedObject object)
    {
        Rethink.table(object.getTableName()).get(object.getId()).delete().runNoReply(conn);
    }

    public Connection getConnection()
    {
        return conn;
    }
}
