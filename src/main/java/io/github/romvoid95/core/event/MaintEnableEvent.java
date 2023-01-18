package io.github.romvoid95.core.event;

import net.dv8tion.jda.api.JDA;

public class MaintEnableEvent
{
    private JDA jda;
    
    public MaintEnableEvent(JDA jda)
    {
        this.jda = jda;
    }
    
    public JDA getJda()
    {
        return jda;
    }
}
