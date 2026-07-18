package com.adventureplanner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class TaskCatalog
{
    private TaskCatalog() {}

    public static List<PlannerTask> defaults()
    {
        return Collections.unmodifiableList(Arrays.asList(
            task("daily_battlestaves", "Zaff: battlestaves ophalen", TaskCadence.DAILY),
            task("daily_sand", "Bert: emmers zand ophalen", TaskCadence.DAILY),
            task("daily_dynamite", "Thirus: gratis dynamiet ophalen", TaskCadence.DAILY),
            task("daily_essence", "Wizard Cromperty: rune essence ophalen", TaskCadence.DAILY),
            task("daily_arrows", "Rantz: ogre arrows ophalen", TaskCadence.DAILY),
            task("daily_bonemeal", "Robin: bones omzetten in bonemeal", TaskCadence.DAILY),
            recurring("daily_herbs", "Herb-run doen"),
            recurring("daily_trees", "Tree- of fruit-tree-run doen"),
            recurring("daily_seaweed", "Giant seaweed-run doen"),
            recurring("daily_birdhouses", "Volledige Birdhouse-run doen"),
            task("daily_contract", "Farming Contract controleren", TaskCadence.DAILY),
            task("daily_rumour", "Hunter Rumour doen", TaskCadence.DAILY),
            task("weekly_tears", "Tears of Guthix", TaskCadence.WEEKLY),
            task("weekly_kingdom", "Kingdom-goedkeuring en resources", TaskCadence.WEEKLY),
            task("weekly_minigame", "Een afwisselende minigame spelen", TaskCadence.WEEKLY),
            task("weekly_boss", "Een nieuwe of vergeten boss proberen", TaskCadence.WEEKLY)
        ));
    }

    private static PlannerTask task(String id, String title, TaskCadence cadence)
    {
        return new PlannerTask(id, title, cadence, false);
    }

    private static PlannerTask recurring(String id, String title)
    {
        return new PlannerTask(id, title, TaskCadence.DAILY, false, true);
    }
}
