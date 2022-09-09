package com.readonlydev.database.impl.options;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class HasteOptions
{
    private List<String> hasteChannelIds                      = new ArrayList<>();
    private String       hasteUrl                             = "https://paste.galacticraft.net/";
}
