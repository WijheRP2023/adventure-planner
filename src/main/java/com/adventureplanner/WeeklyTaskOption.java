package com.adventureplanner;

public enum WeeklyTaskOption
{
    TEARS("weekly_tears", "Tears of Guthix"),
    KINGDOM("weekly_kingdom", "Kingdom"),
    MINIGAME("weekly_minigame", "Afwisselende minigame"),
    BOSS("weekly_boss", "Boss-opdracht");

    private final String taskId;
    private final String label;

    WeeklyTaskOption(String taskId, String label)
    {
        this.taskId = taskId;
        this.label = label;
    }

    public static boolean isHidden(String taskId, java.util.Set<WeeklyTaskOption> hidden)
    {
        if (hidden == null) return false;
        for (WeeklyTaskOption option : hidden)
        {
            if (option.taskId.equals(taskId)) return true;
        }
        return false;
    }

    @Override
    public String toString()
    {
        return label;
    }
}
