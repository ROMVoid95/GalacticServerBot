package com.readonlydev.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.readonlydev.BotData;
import com.readonlydev.GalacticBot;
import com.readonlydev.annotation.GalacticCommand;
import com.readonlydev.cmd.CommandEvent;
import com.readonlydev.cmd.arg.Required;
import com.readonlydev.commands.core.BaseCommand;
import com.readonlydev.commands.core.CommandCategory;
import com.readonlydev.common.waiter.EventWaiter;
import com.readonlydev.database.entity.AddonObject;
import com.readonlydev.util.TableBuilder;
import com.readonlydev.util.TableBuilder.Borders;

import net.dv8tion.jda.api.EmbedBuilder;

@GalacticCommand
public class CommandManageAddonLinks extends BaseCommand {

	private final Logger LOG = LoggerFactory.getLogger(CommandManageAddonLinks.class);

	private EventWaiter eventWaiter = GalacticBot.getEventWaiter();
	private AddonObject addon;

	static String[] actions = { "edit", "add", "delete" };

	static Required action = Required.of(String.join("|", actions), "The desired action to take")
			.isMultiOption();
	static Required name = Required.of("addon_name", "Name of the Addon");

	public CommandManageAddonLinks() {
		super("addonlink", CommandCategory.SERVER_ADMIN);
		this.addAguments(action, name);

	}

	@Override
	public void onExecute(CommandEvent event) {
		
		
		
		String act = this.getArgValue(0);
		addon = BotData.database().getAddonObject(this.getArgValue(1));
		

		runEditHelp(event);
//		event.reply(this.getArgValue(2));
//		List<String> builder = new ArrayList<>();
//		for(String f : AddonData.getEditableFields()) {
//			builder.add("`" + f + "`");
//		}
//		
//		LOG.info(builder.toString());
//		
//		if(this.getArgValue(2) == "help") {
//
//			event.reply(new EmbedBuilder()
//					.setTitle("Editable Data")
//					.setDescription(String.join(" ", builder))
//					.build());
//		}
	}

	// @noformat
	private void runEditHelp(CommandEvent event) {
		TableBuilder table = new TableBuilder().setName("Option").addHeaders("Configured", "Info")
				.setValues(new String[][] { { "N", "The addons mod name" }, { "N", "A valid img link" },
						{ "N", "Optional description" }, { "N", "Most recent mod verison" },
						{ "N", "Minecraft versions of the mod" }, { "N", "The mods download link" },
						{ "N", "The mods git repo" }, { "N", "The mods issues tracker link" } })
				.addRowNames("modName", "iconUrl", "desc", "recentver", "mcVers", "link_dl", "link_src", "link_issues")
				.frame(true).codeblock(true).setBorders(Borders.HEADER_ROW_FRAME);

		event.reply(
				new EmbedBuilder().setTitle("Addon Links Manager")
						.setDescription("\n\n**AddonData for Addon**: `" + addon.getId() + "`").build(),
				(s) -> {
					event.reply(table.build());
				});
	}
}
