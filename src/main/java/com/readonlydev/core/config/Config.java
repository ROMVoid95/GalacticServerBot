package com.readonlydev.core.config;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.rethinkdb.net.Connection;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

@Data
@NoArgsConstructor
public class Config {
	private String token = "token";
	private String botname = "bot-name";
	private Secrets secrets = new Secrets();
	private BotInfo botInfo = new BotInfo();
	private StaffRoles staffRoles = new StaffRoles();
	private RethinkCredentials rethink = new RethinkCredentials();
	
	@Data
	@NoArgsConstructor
	public class Secrets {
		private String clientId = "client-id";
		private String clientSecret = "client-secret";
	}
	
	@Data
	@NoArgsConstructor
	public class StaffRoles {
		private Set<String> admins = new HashSet<>();
		private Set<String> moderators = new HashSet<>();
	}
	
	@Data
	@NoArgsConstructor
	public class BotInfo {
		private Boolean debug = false;
		private String avatarUrl = "image-url";
		private String webhookUrl = "webhook-url";
		private List<String> prefixes = new ArrayList<>();
		private Set<String> commandPackages = new HashSet<>();
		private String ownerId = "owner-id";
		private List<String> coOwners = new ArrayList<>();
		
		public boolean isOwner(Member member) {
			return isOwner(member.getUser());
		}

		public boolean isOwner(User user) {
			return isOwner(user.getId());
		}

		public boolean isOwner(String id) {
			return id.equals(getOwnerId()) ? true : coOwners.contains(id);
		}
		
		public String[] getCoOwnersAsArray() {
			return coOwners.toArray(new String[coOwners.size()]);
		}
	}
	
	@Data	
	@NoArgsConstructor
	public class RethinkCredentials {
		private String hostname = "127.0.0.1";
		private int port = 28015;
		private String databaseName = "test";
		private String user = "user";
		private String password = "passwd";

		public Connection.Builder buildConnection(Connection.Builder builder) {
			return builder.hostname(getHostname()).port(getPort()).db(getDatabaseName()).user(getUser(), getPassword());
		}
	}
}
