package com.readonlydev.database;

import com.readonlydev.Conf;
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
		synchronized (Rethink.class) {
			connection = getConnectionBuilder().connect();
		}
		return connection;
	}

	private static Connection.Builder getConnectionBuilder() {
		return Conf.Bot().RethinkDatabase().buildConnection(new Connection.Builder());
	}
}
