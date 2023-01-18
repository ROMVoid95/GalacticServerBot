package io.github.romvoid95.database.impl;

import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.collections4.list.TreeList;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class SuggestionManager
{

    private int                  count;
    private List<Suggestion>     list = new TreeList<>();
    private LinkedHashMap<String, Integer> map = new LinkedHashMap<>();
    
}
