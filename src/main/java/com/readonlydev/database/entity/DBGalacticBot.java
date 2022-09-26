package com.readonlydev.database.entity;

import static com.readonlydev.util.ListUtils.addToList;
import static com.readonlydev.util.ListUtils.getRoleList;
import static com.readonlydev.util.ListUtils.removeFromList;
import static com.readonlydev.util.ListUtils.toStringList;

import java.beans.ConstructorProperties;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.readonlydev.commands.core.RoleType;
import com.readonlydev.database.ManagedObject;
import com.readonlydev.database.impl.Suggestion;
import com.readonlydev.database.impl.Suggestion.LinkedMessages;
import com.readonlydev.database.impl.SuggestionManager;
import com.readonlydev.database.impl.options.HasteOptions;
import com.readonlydev.database.impl.options.ServerOptions;
import com.readonlydev.database.impl.options.SuggestionOptions;
import com.readonlydev.util.rec.ListPair;

import lombok.Getter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class DBGalacticBot implements ManagedObject
{

    public static final String       DB_TABLE = "galacticbot";
    private boolean                  maintenanceMode;
    private Map<Long, ServerOptions> guilds;
    private SuggestionOptions        suggestionOptions;
    private HasteOptions             hasteOptions;
    private SuggestionManager        manager;

    //@noformat
    @JsonCreator
    @ConstructorProperties({"maintenanceMode", "guilds", "suggestionOptions", "hasteOptions", "manager"})
    public DBGalacticBot(
        @JsonProperty("maintenanceMode") boolean maintenanceMode,
        @JsonProperty("guilds") Map<Long, ServerOptions> guilds, 
        @JsonProperty("suggestionOptions") SuggestionOptions suggestionOptions,
        @JsonProperty("hasteOptions") HasteOptions hasteOptions,
        @JsonProperty("manager") SuggestionManager manager)
    {
        this.maintenanceMode = maintenanceMode;
        this.guilds = guilds;
        this.suggestionOptions = suggestionOptions;
        this.hasteOptions = hasteOptions;
        this.manager = manager;
    }
    //@format

    public static DBGalacticBot create()
    {
        DBGalacticBot botObj = new DBGalacticBot(false, new LinkedHashMap<>(), new SuggestionOptions(), new HasteOptions(), new SuggestionManager());
        botObj.save();
        return botObj;
    }

    public void setMaintenanceMode(boolean maintenanceMode)
    {
        this.maintenanceMode = maintenanceMode;
        this.saveUpdating();
    }

    public ServerOptions createServerOptionsIfMissing(Guild guild)
    {
        Map<Long, ServerOptions> newMap = this.getGuilds();

        if (!newMap.containsKey(guild.getIdLong()))
        {
            newMap.put(guild.getIdLong(), new ServerOptions());
        }

        this.saveUpdating();
        return newMap.get(guild.getIdLong());
    }

    private ServerOptions getServerOptions(Guild guild)
    {
        return this.getGuilds().get(guild.getIdLong());
    }

    public List<Role> removeFrom(Guild guild, List<Role> rolesToRemove, RoleType roleType)
    {
        ServerOptions options = this.getServerOptions(guild);
        ListPair<Role> pair = removeFromList(getRoles(guild, roleType), rolesToRemove);
        options.setRoleList(toStringList(pair.left(), Role::getId), roleType);
        this.saveUpdating();
        return pair.right();
    }

    public List<Role> addTo(Guild guild, List<Role> rolesToAdd, RoleType roleType)
    {
        ServerOptions options = this.getServerOptions(guild);
        ListPair<Role> pair = addToList(getRoles(guild, roleType), rolesToAdd);
        options.setRoleList(toStringList(pair.left(), Role::getId), roleType);
        this.saveUpdating();
        return pair.right();
    }

    public List<Role> getRoles(Guild guild, RoleType roleType)
    {
        ServerOptions options = this.getServerOptions(guild);
        List<Role> roles;
        switch (roleType)
        {
            case ADMIN -> roles = getRoleList(guild, options.getServerAdminRoles());
            case MOD -> roles = getRoleList(guild, options.getServerModeratorRoles());
            default -> throw new IllegalArgumentException("Unexpected value: " + roleType);
        }
        return roles;
    }

    @JsonIgnore
    public List<String> getAllSuggestionMessageIds()
    {
        return this.getManager().getList().stream().map(Suggestion::postMsgId).toList();
    }

    public void addNewDevServerPopularMessage(String messageId, Suggestion suggestion)
    {
        LinkedMessages messages = suggestion.getMessages();
        // Even though we checked this before calling, make sure it really is empty before setting it
        if (messages.getDevPopularMsgId().isEmpty())
        {
            messages.setDevPopularMsgId(messageId);
            this.saveUpdating();
        }
    }

    public void addNewCommunityPopularMessage(String messageId, Suggestion suggestion)
    {
        LinkedMessages messages = suggestion.getMessages();
        // Even though we checked this before calling, make sure it really is empty before setting it
        if (messages.getCommunityPopularMsgId().isEmpty())
        {
            messages.setCommunityPopularMsgId(messageId);
            this.saveUpdating();
        }
    }

    public String addNewSuggestion(int count, Suggestion suggestion)
    {
        this.getManager().setCount(count);
        List<Suggestion> newList = this.getManager().getList();
        LinkedHashMap<String, Integer> newMap = this.getManager().getMap();

        String returnId = null;

        if (newList.add(suggestion))
        {
            String[] uuid = UUID.randomUUID().toString().split("-");
            returnId = uuid[uuid.length - 1];
            suggestion.set_id(returnId);

            newMap.put(suggestion.postMsgId(), newList.indexOf(suggestion));
            this.getManager().setList(newList);
            this.getManager().setMap(newMap);
            this.saveUpdating();
        }

        return returnId;
    }

    @Nullable
    public Suggestion getSuggestionFromNumber(int number)
    {
        try
        {
            return this.getManager().getList().get(number - 1);
        } catch (Exception e)
        {
            return null;
        }
    }
    
    public int getSuggestionNumber(Suggestion suggestion)
    {
        return this.getManager().getMap().get(suggestion.getMessages().getPostMsgId());
    }

    public Suggestion getSuggestionFromMessageId(String messageId)
    {
        return (Suggestion) this.getManager().getList().get(this.getManager().getMap().get(messageId));
    }

    public Optional<Suggestion> getSuggestionFromUniqueId(String id)
    {
        Optional<Suggestion> suggestion = Optional.empty();
        List<Suggestion> suggestions = this.getManager().getList();
        for (Suggestion s : suggestions)
        {
            if (s.get_id().equalsIgnoreCase(id))
            {
                suggestion = Optional.of(s);
            }
        }
        return suggestion;
    }

    public void clearMessageIdsFromSuggestion(int number)
    {
        LinkedMessages linked = this.getSuggestionFromNumber(number).getMessages();
        linked.setPostMsgId("");
        linked.setCommunityPopularMsgId("");
        linked.setDevPopularMsgId("");
        this.saveUpdating();
    }

    public void clearSuggestionDatabase()
    {
        SuggestionManager manager = this.getManager();
        manager.setList(new LinkedList<>());
        manager.setMap(new LinkedHashMap<>());
        manager.setCount(0);
        this.save();
    }

    @Override
    @Nonnull
    public String getId()
    {
        return "galacticbot";
    }

    @JsonIgnore
    @Override
    @Nonnull
    public String getTableName()
    {
        return DB_TABLE;
    }
}
