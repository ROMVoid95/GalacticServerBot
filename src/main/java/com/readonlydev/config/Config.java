package com.readonlydev.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

@Data
@NoArgsConstructor
public class Config {

	private String botname = "ReadOnlyBot";
	private String avatarUrl = "https://i.imgur.com/S7CqwYg.png";
	private String token = "TOKEN";
	private String prefix = "PREFIX";
	private String clientId = "CLIENTID";
	private String secret = "SECRET";
	private Boolean debug = false;
	private String owner = "owner-id";
	private String webhookUrl = "URL";
	private String[] commandPackages = new String[] {};
	private Database database = new Database();

	public boolean isOwner(Member member) {
		return isOwner(member.getUser());
	}

	public boolean isOwner(User user) {
		return isOwner(user.getId());
	}

	public boolean isOwner(String id) {
		return owner.equals(id);
	}
}
