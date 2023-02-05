package io.github.romvoid95.updates;

import java.util.List;
import java.util.Optional;

import io.github.romvoid95.BotData;
import io.github.romvoid95.database.entity.DBUpdates;
import io.github.romvoid95.database.impl.updates.R_Platforms;

public final class AllMods implements Runnable
{
	public List<ModRecord> getProjects()
	{
		return BotData.database().updates().getAllAsRecords();
	}
	
    public void save(String name, R_Platforms platforms) {
    	DBUpdates updates = BotData.database().updates();
    	updates.getMod(name).setCurseforge(platforms.curseforge().get());
    	updates.getMod(name).setModrinth(platforms.modrinth().get());
    	updates.saveUpdating();
    }

	public Optional<ModRecord> getProjectByName(String modId)
	{
		return getProjects().stream().filter(p -> p.name() == modId).findAny();
	}

	@Override
	public void run()
	{
		getProjects().forEach(ModRecord::run);
	}

}
