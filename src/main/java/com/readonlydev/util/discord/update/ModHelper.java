package com.readonlydev.util.discord.update;

import java.util.concurrent.ExecutionException;

import com.readonlydev.GalacticBot;

import io.github.matyrobbrt.curseforgeapi.util.CurseForgeException;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ModHelper
{
    public static int getLastestFileId(int modId)
    {
        try
        {
            return GalacticBot.getCurseAPI().getAsyncHelper().getMod(modId).get().get()
                .latestFilesIndexes().get(0).fileId();
        } catch (InterruptedException | ExecutionException | CurseForgeException e)
        {
            return -1;
        }
    }
}
