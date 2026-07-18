package com.adventureplanner;

import java.util.Objects;

public final class PlannerTask
{
    private final String id;
    private final String title;
    private final TaskCadence cadence;
    private final boolean custom;
    private final boolean recurring;

    public PlannerTask(String id, String title, TaskCadence cadence, boolean custom)
    {
        this(id, title, cadence, custom, false);
    }

    public PlannerTask(String id, String title, TaskCadence cadence, boolean custom, boolean recurring)
    {
        this.id = Objects.requireNonNull(id);
        this.title = Objects.requireNonNull(title);
        this.cadence = Objects.requireNonNull(cadence);
        this.custom = custom;
        this.recurring = recurring;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public TaskCadence getCadence() { return cadence; }
    public boolean isCustom() { return custom; }
    public boolean isRecurring() { return recurring; }
}
