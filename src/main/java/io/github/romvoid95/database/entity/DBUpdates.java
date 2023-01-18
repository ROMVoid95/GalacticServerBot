package io.github.romvoid95.database.entity;

import java.beans.ConstructorProperties;
import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
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
		DBUpdates updates = new DBUpdates(new LinkedHashMap<>());
		updates.save();
		return updates;
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
