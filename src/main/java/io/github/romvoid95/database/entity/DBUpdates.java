package io.github.romvoid95.database.entity;

import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.github.romvoid95.database.ManagedObject;
import io.github.romvoid95.database.impl.updates.UpdateMod;
import io.github.romvoid95.updates.ModRecord;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DBUpdates implements ManagedObject
{
	public static final String		DB_TABLE	= "updates";
	private Map<String, UpdateMod>	mods;

	@JsonCreator
	@ConstructorProperties(
	{ "mods" })
	public DBUpdates(@JsonProperty("mods") Map<String, UpdateMod> mods)
	{
		this.mods = mods;
	}

	public static DBUpdates create()
	{
		DBUpdates updates = new DBUpdates(new HashMap<>());
		updates.save();
		return updates;
	}

	public UpdateMod getMod(String name)
	{
		if (!mods.containsKey(name))
		{
			mods.put(name, new UpdateMod());
		}

		return mods.get(name);
	}

	@JsonIgnore
	public List<ModRecord> getAllAsRecords()
	{
		List<ModRecord> records = new ArrayList<>();
		for(Entry<String, UpdateMod> e : mods.entrySet())
		{
			records.add(new ModRecord(e.getKey(), e.getValue().infoRecords(), e.getValue().platforms()));
		}
		return records;
	}
	
	@JsonIgnore
	public ModRecord getModAsRecord(String name)
	{
		UpdateMod mu = getMod(name);
		return new ModRecord(name, mu.infoRecords(), mu.platforms());
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
