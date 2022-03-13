package com.readonlydev.commands.slash;

import java.awt.Color;
import java.io.File;
import java.util.Arrays;

import com.readonlydev.cmd.OptionHelper;
import com.readonlydev.cmd.SlashCommand;
import com.readonlydev.logback.LogUtils;
import com.readonlydev.util.file.FileUtil;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class SuggestionSlashCommand extends SlashCommand {

	private final File file = new File("suggestion-count.txt");
	private int count;
	
	public SuggestionSlashCommand() {
		this.name = "suggestion";

		//@noformat
		this.options = Arrays.asList(
				new OptionData(OptionType.STRING, "type", "Suggestion Type", true)
					.addChoices(
						new Command.Choice("Discord Suggestion", "[Discord]"),
						new Command.Choice("Galacticraft Suggestion", "[Galacticraft]"),
						new Command.Choice("Addon Suggestion", "[Addon]")),
				new OptionData(OptionType.STRING, "title", "A short generalized title of your suggestion", true),
				new OptionData(OptionType.STRING, "description", "Describe in detail your suggestion", true)				
			);
	}

	@Override
	protected void execute(SlashCommandInteractionEvent event) {
			event.replyEmbeds(new EmbedBuilder()
					.setTitle(getTitle(event))
					.setAuthor(event.getOption("type").getAsString())
					.setDescription(
							"`Suggestion #`: **" + getNumber() + "**\n" +
							"`By:` **" + event.getMember().getAsMention() + "**"
							)
					.addField("Description", OptionHelper.optString(event, "description"), false)
					.setColor(Color.YELLOW)
					.build()).queue(s -> {
						int i = getCount() + 1;
						FileUtil.writeToFile(file, i);
						clearCount();
					});

	}
	
	private String getTitle(SlashCommandInteractionEvent event) {
		setCount(getNumber());
		String title = OptionHelper.optString(event, "title");

		return title;
	}
	
	private int getNumber() {
		int number = FileUtil.readIntFromFile(file);
		LogUtils.log("History Size: " + number);
		if(number == 0) {
			number = 1;
		}
		
		return number;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
	
	private void clearCount() {
		this.count = 0;
	}
}
