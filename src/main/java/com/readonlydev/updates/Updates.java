package com.readonlydev.updates;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import com.readonlydev.updates.storage.DatabaseInterface;

import ch.vorburger.exec.ManagedProcessException;
import de.erdbeerbaerlp.cfcore.CFCoreAPI;
import de.erdbeerbaerlp.cfcore.json.CFMod;

public class Updates {

    public static final HashMap<Long, CurseforgeProject> projects = new HashMap<>();
    public static DatabaseInterface ifa;
    
    public Updates() throws ClassNotFoundException, SQLException, ManagedProcessException, InterruptedException
    {
        //CFCoreAPI.setApiKey(Conf.Update().CurseForge().getApiKey());
        ifa = new DatabaseInterface();

        final ArrayList<CurseforgeProject.CFChannel> allChannels = ifa.getAllChannels();
        for (CurseforgeProject.CFChannel c : allChannels) {
            for (Long projID : c.data.projects) {
                if (projects.containsKey(projID)) {
                    projects.get(projID).addChannel(c);
                } else {
                    final CFMod project = CFCoreAPI.getModFromID(projID);
                    projects.put(projID, new CurseforgeProject(project, c));
                }
                Thread.sleep(100);
            }
        }

        while (true) {
            for (CurseforgeProject proj : projects.values()) {
                System.out.println(proj.proj.name);
                proj.run();
                Thread.sleep(100);
            }
            TimeUnit.MINUTES.sleep(1);

        }
    }
}
