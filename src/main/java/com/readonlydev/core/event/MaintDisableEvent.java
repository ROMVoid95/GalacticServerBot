package com.readonlydev.core.event;

import net.dv8tion.jda.api.JDA;

public class MaintDisableEvent
{
    private JDA jda;
    
    public MaintDisableEvent(JDA jda)
    {
        this.jda = jda;
    }
    
    public JDA getJda()
    {
        return jda;
    }
}