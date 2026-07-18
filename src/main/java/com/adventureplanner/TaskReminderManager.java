package com.adventureplanner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;

final class TaskReminderManager
{
    private final Plugin plugin;
    private final InfoBoxManager infoBoxManager;
    private final ItemManager itemManager;
    private final AdventurePlannerConfig config;
    private final PlannerState state;
    private final Map<String, TaskReminderInfoBox> boxes = new LinkedHashMap<>();

    TaskReminderManager(Plugin plugin, InfoBoxManager infoBoxManager, ItemManager itemManager,
        AdventurePlannerConfig config, PlannerState state)
    {
        this.plugin = plugin;
        this.infoBoxManager = infoBoxManager;
        this.itemManager = itemManager;
        this.config = config;
        this.state = state;
    }

    void refresh()
    {
        if (!config.showTaskReminderInfoBoxes())
        {
            clear();
            return;
        }

        List<PlannerTask> tasks = new ArrayList<>(TaskCatalog.defaults());
        tasks.addAll(state.customTasks());
        Set<String> wanted = new HashSet<>();

        for (PlannerTask task : tasks)
        {
            if (!show(task) || state.isCompleted(task.getId())) continue;
            wanted.add(task.getId());
            if (!boxes.containsKey(task.getId()))
            {
                TaskReminderInfoBox box = new TaskReminderInfoBox(
                    TaskReminderIconFactory.icon(task, itemManager), plugin, task, config);
                boxes.put(task.getId(), box);
                infoBoxManager.addInfoBox(box);
            }
        }

        Iterator<Map.Entry<String, TaskReminderInfoBox>> iterator = boxes.entrySet().iterator();
        while (iterator.hasNext())
        {
            Map.Entry<String, TaskReminderInfoBox> entry = iterator.next();
            if (!wanted.contains(entry.getKey()))
            {
                infoBoxManager.removeInfoBox(entry.getValue());
                iterator.remove();
            }
        }
    }

    void clear()
    {
        for (TaskReminderInfoBox box : boxes.values())
        {
            infoBoxManager.removeInfoBox(box);
        }
        boxes.clear();
    }

    int size()
    {
        return boxes.size();
    }

    private boolean show(PlannerTask task)
    {
        if (task.isRecurring()) return false;
        if (task.isCustom()) return true;
        if (DailyTaskOption.isHidden(task.getId(),
            PlannerConfigSelections.hiddenDailyTasks(config))) return false;
        return !WeeklyTaskOption.isHidden(task.getId(),
            PlannerConfigSelections.hiddenWeeklyTasks(config));
    }
}
