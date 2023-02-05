package io.github.romvoid95.updates;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import de.erdbeerbaerlp.cfcore.CurseAPI;
import io.github.romvoid95.BotData;
import io.github.romvoid95.Conf;
import io.github.romvoid95.GalacticBot;
import masecla.modrinth4j.client.agent.UserAgent;
import masecla.modrinth4j.main.ModrinthAPI;

public final class UpdateManager
{
	private static UpdateManager instance;
	
	private APIManager apiManager;
	
	public UpdateManager()
	{
		CurseAPI curseApi = new CurseAPI(Conf.Bot().getPlatforms().getCurseforge_ApiKey());
		
		UserAgent userAgent = UserAgent.builder()
			.authorUsername("ROMVoid95")
			.projectName("GalacticBot")
			.projectVersion(GalacticBot.Info.VERSION)
			.contact("Discord ROM#0950")
			.build();
		
		ModrinthAPI modrinthApi = ModrinthAPI.rateLimited(userAgent, Conf.Bot().getPlatforms().getModrinth_ApiKey());
		final var allMods = new AllMods();
		this.apiManager = new APIManager(curseApi, modrinthApi, allMods);
		
		BotData.updateExecutor().scheduleAtFixedRate(allMods, 0, 3, TimeUnit.MINUTES);
		instance = this;
	}
	
	public Optional<APIManager> getApiManager()
	{
		return Optional.ofNullable(apiManager);
	}
	
	public static UpdateManager instance()
	{
		return instance;
	}
}
