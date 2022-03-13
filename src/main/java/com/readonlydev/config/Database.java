package com.readonlydev.config;

import com.rethinkdb.net.Connection;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Database {
	private String hostname = "127.0.0.1";
	private int port = 28015;
	private String databaseName = "readonly";
	private String user = "USER";
	private String password = "PASSWORD";

	public Connection.Builder buildConnection(Connection.Builder builder) {
		return builder.hostname(getHostname()).port(getPort()).db(getDatabaseName()).user(getUser(), getPassword());
	}
}
