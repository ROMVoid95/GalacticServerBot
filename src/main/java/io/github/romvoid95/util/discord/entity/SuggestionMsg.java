package io.github.romvoid95.util.discord.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import io.github.readonly.common.util.RGB;
import io.github.romvoid95.commands.core.EditType;
import io.github.romvoid95.util.Embed;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.utils.SplitUtil;
import net.dv8tion.jda.api.utils.SplitUtil.Strategy;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

public interface SuggestionMsg
{
    public static Function<String, List<String>> split = s -> SplitUtil.split(s, 4092, Strategy.WHITESPACE, Strategy.ANYWHERE);
    
    public MessageCreateData toData();
    
    public void setTitle(String title);
    
    public void setDescription(EditType editType, String description);
    
    default List<MessageEmbed> buildDescriptions(List<String> descriptions, RGB color)
    {
        List<MessageEmbed> list = new ArrayList<>();
        for (String d : descriptions)
        {
            list.add(Embed.descriptionEmbed(d, color).toEmbed());
        }
        return list;
    }
}
