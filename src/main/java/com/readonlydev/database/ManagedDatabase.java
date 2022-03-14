package com.readonlydev.database;

import static com.readonlydev.database.wrapper.Rethink.RethinkDB;

import com.readonlydev.database.wrapper.Rethink;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;

import com.readonlydev.database.entity.AddonObject;
import com.readonlydev.database.entity.BotObj;
import com.readonlydev.util.entity.Addon;
import com.rethinkdb.net.Connection;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor
public class ManagedDatabase {
    private Connection conn;

    public ManagedDatabase connect() {
    	this.conn = Rethink.connect();
    	return this;
    }

    @Nonnull
    @CheckReturnValue
    public BotObj getBotData() {
        log.info("Requesting BotData from rethink");
        BotObj obj = RethinkDB.table(BotObj.DB_TABLE).get("galacticbot").runAtom(conn, BotObj.class);
        return obj == null ? BotObj.create() : obj;
    }
    
    @Nonnull
    @CheckReturnValue
    public AddonObject getAddonObject(@Nonnull String name) {
        log.info("Requesting addon {} from rethink", name);
        AddonObject addon = RethinkDB.table(AddonObject.DB_TABLE).get(name).runAtom(conn, AddonObject.class);
        return addon == null ? AddonObject.of(name) : addon;
    }

    @Nonnull
    @CheckReturnValue
    public AddonObject getAddon(@Nonnull Addon addon) {
        return getAddonObject(addon.getAddonData().getModName().toLowerCase());
    }

    @Nonnull
    @CheckReturnValue
    public Addon getAddon(@Nonnull String name) {
        return getAddonObject(name).getAddon();
    }
    
    public Boolean exists(@Nonnull String name) {
        log.info("Checking for addon {} from rethink", name);
        AddonObject addon = RethinkDB.table(AddonObject.DB_TABLE).get(name).runAtom(conn, AddonObject.class);
        return addon == null ? false : true;
    }

    public void save(@Nonnull DBObject object) {
        log.info("Saving {} {}:{} to rethink (replacing)", object.getClass().getSimpleName(), object.getTableName(), object.getDatabaseId());

        RethinkDB.table(object.getTableName())
                .insert(object)
                .optArg("conflict", "replace")
                .runNoReply(conn);
    }

    public void saveUpdating(@Nonnull DBObject object) {
        log.info("Saving {} {}:{} to rethink (updating)", object.getClass().getSimpleName(), object.getTableName(), object.getDatabaseId());

        RethinkDB.table(object.getTableName())
                .insert(object)
                .optArg("conflict", "update")
                .runNoReply(conn);
    }

    public void delete(@Nonnull DBObject object) {
        log.info("Deleting {} {}:{} from rethink", object.getClass().getSimpleName(), object.getTableName(), object.getDatabaseId());

        RethinkDB.table(object.getTableName())
                .get(object.getId())
                .delete()
                .runNoReply(conn);
    }
}
