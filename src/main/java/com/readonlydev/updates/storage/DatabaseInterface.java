package com.readonlydev.updates.storage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.readonlydev.Conf;
import com.readonlydev.config.UpdateConfig;
import com.readonlydev.updates.CurseforgeProject;
import com.readonlydev.updates.Updates;
import com.readonlydev.updates.storage.json.Root;
import com.readonlydev.updates.util.DiscordBotException;

import ch.vorburger.exec.ManagedProcessException;
import ch.vorburger.mariadb4j.DB;
import ch.vorburger.mariadb4j.DBConfigurationBuilder;
import de.erdbeerbaerlp.cfcore.json.CFMod;

public class DatabaseInterface implements AutoCloseable
{

    public final StatusThread status;
    private Connection        conn;
    public Gson               gson = new GsonBuilder().create();
    private DB                db;
    private final UpdateConfig.EmbeddedServer config = null;//Conf.Update().EmbeddedSerer();

    public DatabaseInterface() throws SQLException, ClassNotFoundException, ManagedProcessException
    {
        setupEmbeddedServer();
        Class.forName("com.mysql.cj.jdbc.Driver");
        connect();
        if (conn == null)
        {
            throw new SQLException();
        }
        status = new StatusThread();
        status.start();
        if (conn == null)
        {
            throw new SQLException();
        }
        runUpdate("DROP DATABASE IF EXISTS test;");

        //@noformat
        runUpdate(
            "CREATE TABLE IF NOT EXISTS `projects` (\n" + 
            "  `latestFileID` bigint NOT NULL DEFAULT '0',\n" + 
            "  `projectid` bigint NOT NULL,\n" + 
            "  PRIMARY KEY (`projectid`),\n" + 
            "  UNIQUE KEY `cfcache_projectid_uindex` (`projectid`)\n" + 
            ");"
        );
        runUpdate(
            "CREATE TABLE IF NOT EXISTS `channels` (\n" + 
            "  `channeldata` json NOT NULL,\n" + 
            "  `channelid` bigint NOT NULL,\n" + 
            "  PRIMARY KEY (`channelid`),\n" + 
            "  UNIQUE KEY `channels_channelid_uindex` (`channelid`)\n" + 
            ");"
        );
        runUpdate(
            "alter table channels modify column channeldata json default ('" + gson.toJson(new Root()) + "') not null;"
        );
        //@format
    }

    private void setupEmbeddedServer() throws ManagedProcessException
    {
        DBConfigurationBuilder configBuilder;

        boolean isPresent;
        File dataDir = null;//new File(Conf.Update().EmbeddedSerer().getDataDir());
        if (!dataDir.exists())
        {
            isPresent = dataDir.mkdir();
        } else
        {
            isPresent = true;
        }
        if (isPresent)
        {
            if (!dataDir.isHidden())
            {
                try
                {
                    Path path = Paths.get(dataDir.getAbsolutePath());
                    Files.setAttribute(path, "dos:hidden", true, LinkOption.NOFOLLOW_LINKS);
                } catch (IOException e)
                {
                }
            }
            System.out.println("Using [%s] for DataDir on Database".formatted(dataDir.getAbsolutePath()));
            configBuilder = DBConfigurationBuilder.newBuilder().setPort(0).setDataDir(dataDir.getAbsolutePath());
        } else
        {
            throw new DiscordBotException("dataDir for the SQL database could not be created!");
        }

        if (configBuilder != null)
        {
            startEmbeddedServer(configBuilder);
        }
    }

    private void startEmbeddedServer(DBConfigurationBuilder configBuilder) throws ManagedProcessException
    {
        db = DB.newEmbeddedDB(configBuilder.build());
        db.start();
        db.createDB(config.getDbName(), config.getUsername(), config.getPassword());
    }

    private void connect() throws SQLException
    {
        conn = DriverManager.getConnection(db.getConfiguration().getURL(config.getDbName()), config.getUsername(), config.getPassword());
    }

    private boolean isConnected()
    {
        try
        {
            return conn.isValid(10);
        } catch (SQLException e)
        {
            return false;
        }
    }

    public CurseforgeProject.CFChannel deleteChannelFromProject(long projectID, long channel)
    {
        final CurseforgeProject.CFChannel chan = getOrCreateCFChannel(channel);
        final ArrayList<Long> projs = new ArrayList<>(List.of(chan.data.projects));
        projs.remove(projectID);
        chan.data.projects = projs.toArray(new Long[0]);
        runUpdate("REPLACE INTO `channels` (`channelid`,`channeldata`) VALUES (%d,'%s')".formatted(channel, gson.toJson(chan.data)));
        return getOrCreateCFChannel(channel);
    }

    public void addChannel(CurseforgeProject.CFChannel channel)
    {
        runUpdate("INSERT INTO `channels` (`channelid`,`channeldata`) VALUES (%d,'%s')".formatted(channel.channelID, gson.toJson(channel.data)));
    }

    public void delChannel(long serverID)
    {
        runUpdate("DELETE FROM `channels` WHERE `channelid` = %d".formatted(serverID));
    }

    public int getLatestFile(long projectid) throws SQLException
    {
        final ResultSet query = query("SELECT `latestFileID` FROM `cfcache` WHERE `projectid` = %d;".formatted(projectid));
        if (query.next())
        {
            return query.getInt(1);
        }
        return -1;
    }

    public void updateCache(long projectid, long fileid)
    {
        //runUpdate("REPLACE INTO `cfcache` (`projectid`,`latestFileID`) VALUES(%d,%d)".formatted(projectid, fileid));
    }

    public void deleteProject(long projectid)
    {
        runUpdate("DELETE FROM `projects` WHERE `projectid` = %d".formatted(projectid));
    }

    public void addProject(long projectid)
    {
        runUpdate("INSERT INTO `projects` (`projectid`,`title`,`description`) VALUES (%d, '', '')".formatted(projectid));
    }

    public CurseforgeProject.CFChannel addChannelToProject(long projectID, long channel)
    {
        final CurseforgeProject.CFChannel chan = getOrCreateCFChannel(channel);
        final ArrayList<Long> projs = new ArrayList<>(List.of(chan.data.projects));
        projs.add(projectID);
        chan.data.projects = projs.toArray(new Long[0]);
        runUpdate("REPLACE INTO `channels` (`channelid`,`channeldata`) VALUES (%d,'%s')".formatted(channel, gson.toJson(chan.data)));
        if (!checkIfProjectExists(projectID))
        {
            addProject(projectID);
        }
        return getOrCreateCFChannel(channel);
    }

    public boolean checkIfProjectExists(final long projectID)
    {
        return query$next("SELECT `projectid` FROM `projects` WHERE `projectid` = %d;".formatted(projectID));
    }

    public String projectHasCustomTitle(final long projectID)
    {
        String title = "";
        if (query$next("SELECT `title` FROM `projects` WHERE `projectid` = %d;".formatted(projectID)))
        {
            title = query$string("SELECT `title` FROM `projects` WHERE `projectid` = %d;".formatted(projectID));
        }
        return title;
    }
    
    public String projectHasCustomDescription(final long projectID)
    {
        String description = "";
        if (query$next("SELECT `description` FROM `projects` WHERE `projectid` = %d;".formatted(projectID)))
        {
            description = query$string("SELECT `description` FROM `projects` WHERE `projectid` = %d;".formatted(projectID));
        }
        return description;
    }

    public class StatusThread extends Thread
    {

        private boolean alive = true;

        public boolean isDBAlive()
        {
            return alive;
        }

        @Override
        public void run()
        {
            while (true)
            {
                alive = DatabaseInterface.this.isConnected();
                if (!alive)
                    try
                    {
                        System.err.println("Attempting Database reconnect...");
                        DatabaseInterface.this.connect();
                    } catch (SQLException e)
                    {
                        System.err.println("Failed to reconnect to database: " + e.getMessage());
                        try
                        {
                            TimeUnit.SECONDS.sleep(15);
                        } catch (InterruptedException ex)
                        {
                            ex.printStackTrace();
                        }
                    }
                try
                {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e)
                {
                    return;
                }
            }
        }
    }

    public CurseforgeProject.CFChannel getOrCreateCFChannel(long channelID)
    {
        try (final ResultSet res = query("SELECT `channelid`,`channeldata` FROM `channels` WHERE `channelid` = %d".formatted(channelID)))
        {
            while (res != null && res.next())
            {
                final Root json = gson.fromJson(res.getString(2), Root.class);
                return new CurseforgeProject.CFChannel(res.getLong(1), json);
            }
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        return new CurseforgeProject.CFChannel(channelID, new Root());
    }

    public ArrayList<CurseforgeProject.CFChannel> getAllChannels()
    {
        final ArrayList<CurseforgeProject.CFChannel> channels = new ArrayList<>();
        try (final ResultSet res = query("SELECT * FROM `channels`"))
        {
            while (res != null && res.next())
            {
                final Root json = gson.fromJson(res.getString(2), Root.class);
                channels.add(new CurseforgeProject.CFChannel(res.getLong(1), json));
            }
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        return channels;
    }

    private void runUpdate(final String sql)
    {
        try (final Statement statement = conn.createStatement())
        {
            statement.executeUpdate(sql);
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    private boolean query$next(final String sql)
    {
        try
        {
            return query(sql).next();
        } catch (SQLException e)
        {
            throw new DiscordBotException("query$next threw an exception on query:\n%s".formatted(sql));
        }
    }

    private String query$string(final String sql)
    {
        return query$string(sql, 1);
    }
    
    private String query$string(final String sql, final int column)
    {
        try
        {
            ResultSet result = query(sql);
            if(result.next()) {
                String r = result.getString(column);
                return r;
            }
        } catch (SQLException e)
        {
            throw new DiscordBotException("query$string threw an exception on query:\n%s".formatted(sql), e);
        }
        throw new DiscordBotException("query$string threw an exception on query:\n%s".formatted(sql));
    }

    private ResultSet query(final String sql)
    {
        try
        {
            final Statement statement = conn.createStatement();
            return statement.executeQuery(sql);
        } catch (SQLException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void close() throws Exception
    {
        conn.close();
    }

    public boolean isNewFile(CFMod proj) throws SQLException
    {
        return Updates.ifa.getLatestFile(proj.id) < proj.latestFilesIndexes[0].fileId;
    }
}
