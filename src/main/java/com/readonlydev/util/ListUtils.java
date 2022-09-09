package com.readonlydev.util;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.readonlydev.util.rec.DiffListPair;
import com.readonlydev.util.rec.ListPair;

import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;

@UtilityClass
public class ListUtils
{
    
    // DiffListPair static methods
    
    public static <T, K> DiffListPair<T, K> removeFromList(List<T> removeFrom, List<T> toRemove, Function<T, K> mapper)
    {
        List<T> removed = new LinkedList<>();
        for(T remove : toRemove)
        {
            if(removeFrom.remove(remove))
            {
                removed.add(remove);
            }
        }
        return DiffListPair.of(removed, removeFrom.stream().map(mapper).collect(Collectors.toList()));
    }
    
    public static <T, K> DiffListPair<T, K> addToList(List<T> addTo, List<T> toAdd, Function<T, K> mapper)
    {
        List<T> added = new LinkedList<>();
        for(T add : toAdd)
        {
            if(addTo.add(add))
            {
                added.add(add);
            }
        }
        return DiffListPair.of(added, addTo.stream().map(mapper).collect(Collectors.toList()));
    }
    
    // ListPair static methods
    
    public static <T> ListPair<T> removeFromList(List<T> removeFrom, List<T> toRemove)
    {
        List<T> removed = new LinkedList<>();
        for(T remove : toRemove)
        {
            if(removeFrom.remove(remove))
            {
                removed.add(remove);
            }
        }
        return ListPair.of(removeFrom, removed);
    }
    
    public static <T> ListPair<T> addToList(List<T> addTo, List<T> toAdd)
    {
        List<T> added = new LinkedList<>();
        for(T add : toAdd)
        {
            if(addTo.add(add))
            {
                added.add(add);
            }
        }
        return ListPair.of(addTo, added);
    }
    
    public static <T> List<String> toStringList(List<T> list, Function<T, String> mapper)
    {
        return list.stream().map(mapper).collect(Collectors.toList());
    }
    
    public static List<Role> getRoleList(Guild guild, List<String> idList)
    {
        if(!idList.isEmpty())
        {
            List<Role> roleList = new LinkedList<>();
            for(String id : idList)
            {
                Role role = guild.getRoleById(id);
                if(role != null)
                {
                    roleList.add(role);
                }
            }
            return roleList;
        }
        
        return new LinkedList<>();
    }
}
