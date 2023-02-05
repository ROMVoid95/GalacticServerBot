package io.github.romvoid95.database.impl.updates;

import java.util.Optional;

import javax.annotation.Nullable;

import io.github.romvoid95.database.impl.updates.UpdateMod.Curseforge;
import io.github.romvoid95.database.impl.updates.UpdateMod.Modrinth;

public record R_Platforms(@Nullable Optional<Curseforge> curseforge, @Nullable Optional<Modrinth> modrinth)
{

}
