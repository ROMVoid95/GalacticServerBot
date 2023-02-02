package io.github.romvoid95.database.entity;

import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.github.romvoid95.database.ManagedObject;
import io.github.romvoid95.database.impl.updates.Mod;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DBUpdates implements ManagedObject
{
	public static final String	DB_TABLE	= "updates";
	private Map<String, Mod>	mods;

	@JsonCreator
	@ConstructorProperties(
	{ "mods" })
	public DBUpdates(@JsonProperty("mods") Map<String, Mod> mods)
	{
		this.mods = mods;
	}

	public static DBUpdates create()
	{
		DBUpdates updates = new DBUpdates(new HashMap<>());
		updates.save();
		return updates;
	}

	public Mod getMod(String name)
	{
		if(!mods.containsKey(name))
		{
			mods.put(name, new Mod());
		}

		return mods.get(name);
	}
	
	@JsonIgnore
	public List<Mod> getAllMods()
	{
		return new ArrayList<>(mods.values());
	}
	
	@JsonIgnore
	public List<Mod> getActivatedMods()
	{
		return mods.values().stream().filter(Mod::isActive).collect(Collectors.toList());
	}

	@Override
	public String getId()
	{
		return DBUpdates.DB_TABLE;
	}

	@Override
	public String getTableName()
	{
		return DBUpdates.DB_TABLE;
	}
}
