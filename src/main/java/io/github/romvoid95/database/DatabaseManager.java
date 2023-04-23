package io.github.romvoid95.database;

import static io.github.romvoid95.database.Rethink.Rethink;

import com.rethinkdb.net.Connection;

import io.github.romvoid95.database.entity.DBBlacklist;
import io.github.romvoid95.database.entity.DBGalacticBot;
import io.github.romvoid95.database.entity.DBUpdates;

public class DatabaseManager
{

    private Connection conn;

    public DatabaseManager( Connection conn)
    {
        this.conn = conn;
    }

    public DBGalacticBot galacticBot()
    {
        DBGalacticBot obj = Rethink.table(DBGalacticBot.DB_TABLE).get("galacticbot").runAtom(conn, DBGalacticBot.class);
        return obj == null ? DBGalacticBot.create() : obj;
    }
    
    public DBBlacklist blacklist()
    {
        DBBlacklist obj = Rethink.table(DBBlacklist.DB_TABLE).get("blacklist").runAtom(conn, DBBlacklist.class);
        return obj == null ? DBBlacklist.create() : obj;
    }
    
    public DBUpdates updates()
    {
        DBUpdates obj = Rethink.table(DBUpdates.DB_TABLE).get("updates").runAtom(conn, DBUpdates.class);
        return obj == null ? DBUpdates.create() : obj;
    }

    public void save( ManagedObject object)
    {
        Rethink.table(object.getTableName()).insert(object).optArg("conflict", "replace").runNoReply(conn);
    }

    public void saveUpdating( ManagedObject object)
    {
        Rethink.table(object.getTableName()).insert(object).optArg("conflict", "update").runNoReply(conn);
    }

    public void delete( ManagedObject object)
    {
        Rethink.table(object.getTableName()).get(object.getId()).delete().runNoReply(conn);
    }

    public Connection getConnection()
    {
        return conn;
    }
}
