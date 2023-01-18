package io.github.romvoid95.core;

import com.google.common.eventbus.Subscribe;

import io.github.romvoid95.core.event.MaintDisableEvent;
import io.github.romvoid95.core.event.MaintEnableEvent;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.managers.Presence;

public class BusListener
{
    private final Activity playing = Activity.playing("Maintanence Mode");
    private final Activity watching = Activity.watching("for Suggestions");
    
    @Subscribe
    public void onMaintanenceModeEnabled(MaintEnableEvent event)
    {
        Presence presence = event.getJda().getPresence();
        presence.setStatus(OnlineStatus.DO_NOT_DISTURB);
        presence.setActivity(playing);
    }
    
    @Subscribe
    public void onMaintanenceModeDisabled(MaintDisableEvent event)
    {
        Presence presence = event.getJda().getPresence();
        presence.setStatus(OnlineStatus.ONLINE);
        presence.setActivity(watching);
    }
}
