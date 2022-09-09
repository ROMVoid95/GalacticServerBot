package com.readonlydev.database.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.readonlydev.database.ManagedObject;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DBUpdateProject implements ManagedObject
{

    public static final String DB_TABLE = "updates";
    //private final String id;
    
    
    
    @Override
    public String getId()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getTableName()
    {
        // TODO Auto-generated method stub
        return null;
    }

}
