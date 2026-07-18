package com.adventureplanner;

public enum DailyTaskOption
{
    BATTLESTAVES("daily_battlestaves", "Zaff: battlestaves"),
    SAND("daily_sand", "Bert: emmers zand"),
    DYNAMITE("daily_dynamite", "Thirus: dynamiet"),
    ESSENCE("daily_essence", "Cromperty: rune essence"),
    ARROWS("daily_arrows", "Rantz: ogre arrows"),
    BONEMEAL("daily_bonemeal", "Robin: bonemeal"),
    HERB_RUN("daily_herbs", "Herb-runs"),
    TREE_RUN("daily_trees", "Tree-runs"),
    SEAWEED_RUN("daily_seaweed", "Giant seaweed-runs"),
    BIRDHOUSES("daily_birdhouses", "Birdhouse-runs"),
    FARMING_CONTRACT("daily_contract", "Farming Contracts"),
    HUNTER_RUMOUR("daily_rumour", "Hunter Rumours");

    private final String taskId;
    private final String label;

    DailyTaskOption(String taskId, String label)
    {
        this.taskId = taskId;
        this.label = label;
    }

    public String getTaskId()
    {
        return taskId;
    }

    public static boolean isHidden(String taskId, java.util.Set<DailyTaskOption> hidden)
    {
        if (hidden == null) return false;
        for (DailyTaskOption option : hidden)
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
