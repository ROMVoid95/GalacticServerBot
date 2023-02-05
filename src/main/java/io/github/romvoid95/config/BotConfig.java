package io.github.romvoid95.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rethinkdb.net.Connection;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

@NoArgsConstructor
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Getter
public class BotConfig
{

	private String				avatarUrl	= "https://i.imgur.com/S7CqwYg.png";
	private String				token		= "TOKEN";
	private String				owner		= "owner-id";
	private String				rootLog		= "";
	private String				webhookUrl	= "URL";
	private PlatformKeys		platforms   = new PlatformKeys();
	private RethinkDataseConfig	database	= new RethinkDataseConfig();

	@JsonIgnore
	public RethinkDataseConfig RethinkDatabase()
	{
		return database;
	}

	public boolean isOwner(Member member)
	{
		return isOwner(member.getUser());
	}

	public boolean isOwner(User user)
	{
		return isOwner(user.getId());
	}

	public boolean isOwner(String id)
	{
		return owner.equals(id);
	}

	@Getter
	@Setter
	@NoArgsConstructor
	public static class PlatformKeys
	{
		private String	modrinth_ApiKey		= "key";
		private String	curseforge_ApiKey	= "key";
	}

	@Getter
	@Setter
	@NoArgsConstructor
	public static class RethinkDataseConfig
	{

		private String	hostname		= "127.0.0.1";
		private int		port			= 28015;
		private String	databaseName	= "readonly";
		private String	user			= "USER";
		private String	password		= "PASSWORD";

		public Connection.Builder buildConnection(Connection.Builder builder)
		{
			return builder.hostname(getHostname()).port(getPort()).db(getDatabaseName()).user(getUser(), getPassword());
		}
	}
}
