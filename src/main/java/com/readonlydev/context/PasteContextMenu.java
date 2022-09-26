package com.readonlydev.context;

import com.readonlydev.command.ctx.MessageContextMenu;
import com.readonlydev.command.event.MessageContextMenuEvent;

import net.dv8tion.jda.api.interactions.components.Modal;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class PasteContextMenu extends MessageContextMenu
{

    public PasteContextMenu()
    {
        this.name = "To HasteBin";
    }
    
    @Override
    protected void execute(MessageContextMenuEvent event)
    {
        Modal modal = Modal.create("hastebin", "Send to HasteBin")
        	.addActionRow(Button.primary("hello", "Click Me"))
            .build();
        
        event.replyModal(modal).queue();
    }
}
