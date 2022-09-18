package com.readonlydev.database.impl.updates;

public class ExistingCurseMod extends Mod
{
    public static ExistingCurseMod fromMod(Mod mod)
    {
        ExistingCurseMod existing = new ExistingCurseMod();
        existing.setFileId(mod.getFileId());
        existing.setUpdateChannelId(mod.getUpdateChannelId());
        existing.setPingRoles(mod.getPingRoles());
        return existing;
    }
}
