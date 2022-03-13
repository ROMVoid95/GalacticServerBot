package com.readonlydev.database.wrapper;

import com.readonlydev.BotData;
import com.readonlydev.logback.LogUtils;
import com.rethinkdb.RethinkDB;
import com.rethinkdb.net.Connection;

public final class Rethink extends RethinkDB {

	public static final RethinkDB Rethink = new RethinkDB();

	private static Connection connection;

	public static Connection connect() {
		if (connection == null) {
			buildConnection();
		}
		return connection;
	}

	private static Connection buildConnection() {
		var config = BotData.config();
		synchronized (Rethink.class) {
			connection = getConnectionBuilder().connect();
			String log = "Established first database connection to %s:%s (%s)".formatted(config.getDatabase().getHostname(), config.getDatabase().getPort(), config.getDatabase().getUser());
			LogUtils.log("Database Initialization", log);
		}
		return connection;
	}

	private static Connection.Builder getConnectionBuilder() {
		return BotData.config().getDatabase().buildConnection(new Connection.Builder());
	}
}
