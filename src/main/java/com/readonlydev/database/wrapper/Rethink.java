package com.readonlydev.database.wrapper;

import com.readonlydev.core.Accessors;
import com.readonlydev.core.config.Config;
import com.readonlydev.logback.LogUtils;
import com.rethinkdb.RethinkDB;
import com.rethinkdb.net.Connection;

public final class Rethink extends RethinkDB {

	public static final RethinkDB RethinkDB = new RethinkDB();
	private static final Config.RethinkCredentials dbconfig = Accessors.rethinkCredentials();
	private static Connection connection;

	public static Connection connect() {
		if (connection == null) {
			buildConnection();
		}
		return connection;
	}

	private static Connection buildConnection() {
		synchronized (Rethink.class) {
			connection = getConnectionBuilder().connect();
			String log = "Established first database connection to %s:%s (%s)".formatted(
					dbconfig.getHostname(),
					dbconfig.getPort(),
					dbconfig.getUser()
				);
			LogUtils.log("Database Initialization", log);
		}
		return connection;
	}

	private static Connection.Builder getConnectionBuilder() {
		return dbconfig.buildConnection(new Connection.Builder());
	}
}
