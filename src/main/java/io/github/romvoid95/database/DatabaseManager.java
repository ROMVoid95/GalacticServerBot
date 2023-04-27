package io.github.romvoid95.database;

import static com.rethinkdb.RethinkDB.r;

import com.rethinkdb.net.Connection;

import io.github.romvoid95.database.entity.DBBlacklist;
import io.github.romvoid95.database.entity.DBGalacticBot;
import io.github.romvoid95.database.entity.DBUpdates;

public class DatabaseManager
{

    private Connection conn;
    
    public DatabaseManager(Connection connection)
    {
    	this.conn = connection;
    }

    public DBGalacticBot galacticBot()
    {
        DBGalacticBot obj = r.table(DBGalacticBot.DB_TABLE).get("galacticbot").runAtom(conn, DBGalacticBot.class);
        return obj == null ? DBGalacticBot.create() : obj;
    }
    
    public DBBlacklist blacklist()
    {
        DBBlacklist obj = r.table(DBBlacklist.DB_TABLE).get("blacklist").runAtom(conn, DBBlacklist.class);
        return obj == null ? DBBlacklist.create() : obj;
    }
    
    public DBUpdates updates()
    {
        DBUpdates obj = r.table(DBUpdates.DB_TABLE).get("updates").runAtom(conn, DBUpdates.class);
        return obj == null ? DBUpdates.create() : obj;
    }

    public void save( ManagedObject object)
    {
    	r.table(object.getTableName()).insert(object).optArg("conflict", "replace").runNoReply(conn);
    }

    public void saveUpdating( ManagedObject object)
    {
    	r.table(object.getTableName()).insert(object).optArg("conflict", "update").runNoReply(conn);
    }

    public void delete( ManagedObject object)
    {
    	r.table(object.getTableName()).get(object.getId()).delete().runNoReply(conn);
    }
}
