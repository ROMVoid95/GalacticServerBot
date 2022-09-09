package com.readonlydev.database.impl;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class UpdateProject
{
    private long notificationChannelId;
    private List<Long> pingRoles = new ArrayList<>();
}
