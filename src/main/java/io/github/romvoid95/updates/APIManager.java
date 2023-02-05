package io.github.romvoid95.updates;

import de.erdbeerbaerlp.cfcore.CurseAPI;
import masecla.modrinth4j.main.ModrinthAPI;

public record APIManager(CurseAPI curseApi, ModrinthAPI modrinthApi,  AllMods allmods)
{

}
