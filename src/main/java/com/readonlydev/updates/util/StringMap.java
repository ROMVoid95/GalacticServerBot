package com.readonlydev.updates.util;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class StringMap<K, V> extends LinkedHashMap<K, V>
{
    private static final long serialVersionUID = 5646659841311515373L;

    public V getValue(int i)
    {

       Map.Entry<K, V>entry = this.getEntry(i);
       if(entry == null) return null;

       return entry.getValue();
    }

    public Map.Entry<K, V> getEntry(int i)
    {
        Set<Map.Entry<K,V>>entries = entrySet();
        int j = 0;

        for(Map.Entry<K, V>entry : entries)
            if(j++ == i) {
                //System.out.println("Entry= %s | %s".formatted(entry.getKey().toString(), entry.getValue().toString()));
                
                return entry;
            }

        return null;

    }

}