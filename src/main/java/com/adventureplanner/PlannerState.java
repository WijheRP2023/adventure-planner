package com.adventureplanner;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import net.runelite.api.Skill;
import net.runelite.client.config.ConfigManager;

public final class PlannerState
{
    private static final String COMPLETED = "completed";
    private static final String DAILY_STAMP = "dailyStamp";
    private static final String WEEKLY_STAMP = "weeklyStamp";
    private static final String CUSTOM = "customTasks";
    private static final String RECENT = "recentSkills";
    private static final String BANK_ITEMS = "bankItems";
    private static final String BANK_STAMP = "bankStamp";
    private static final String SEED_VAULT_ITEMS = "seedVaultItems";
    private static final String SEED_VAULT_STAMP = "seedVaultStamp";
    private static final String READY_AT_PREFIX = "readyAt.";
    private static final String STARTED_AT_PREFIX = "startedAt.";
    private static final String READY_NOTIFIED_PREFIX = "readyNotified.";
    private static final String RUN_SOUND_PREFIX = "runSound.";
    private static final String NOTES = "notes";
    private static final String CURRENT_TASK = "currentGeneratedTask";
    private static final String CURRENT_RUMOUR = "currentHunterRumour";
    private static final String CURRENT_TASK_ENDS_AT = "currentTaskEndsAt";
    private static final String CURRENT_TASK_PROGRESS = "currentTaskProgress";
    private static final String AUTOMATIC_TASKS_PAUSED = "automaticTasksPaused";

    private final ConfigManager configManager;
    private final Gson gson;

    public PlannerState(ConfigManager configManager, Gson gson)
    {
        this.configManager = configManager;
        this.gson = gson;
    }

    public void applyResets()
    {
        LocalDate today = LocalDate.now();
        String day = today.toString();
        WeekFields fields = WeekFields.ISO;
        String week = today.get(fields.weekBasedYear()) + "-W" + today.get(fields.weekOfWeekBasedYear());
        String savedDay = get(DAILY_STAMP);
        String savedWeek = get(WEEKLY_STAMP);

        Set<String> completed = completed();
        if (!day.equals(savedDay))
        {
            removeCadence(completed, TaskCadence.DAILY);
            set(DAILY_STAMP, day);
        }
        if (!week.equals(savedWeek))
        {
            removeCadence(completed, TaskCadence.WEEKLY);
            set(WEEKLY_STAMP, week);
        }
        saveCompleted(completed);
    }

    public boolean isCompleted(String id) { return completed().contains(id); }

    public void setCompleted(String id, boolean value)
    {
        Set<String> values = completed();
        if (value) values.add(id); else values.remove(id);
        saveCompleted(values);
    }

    public List<PlannerTask> customTasks()
    {
        List<String> rows = list(CUSTOM);
        List<PlannerTask> tasks = new ArrayList<>();
        for (String row : rows)
        {
            String[] parts = row.split("\\|", 3);
            if (parts.length == 3)
            {
                try
                {
                    tasks.add(new PlannerTask(parts[0], parts[2], TaskCadence.valueOf(parts[1]), true));
                }
                catch (IllegalArgumentException ignored) { }
            }
        }
        return tasks;
    }

    public void addCustom(String title, TaskCadence cadence)
    {
        List<String> rows = list(CUSTOM);
        String clean = title.replace("|", " ").trim();
        rows.add("custom_" + System.currentTimeMillis() + "|" + cadence.name() + "|" + clean);
        saveList(CUSTOM, rows);
    }

    public void removeCustom(String id)
    {
        List<String> rows = list(CUSTOM);
        rows.removeIf(row -> row.startsWith(id + "|"));
        saveList(CUSTOM, rows);
        setCompleted(id, false);
    }

    public List<Skill> recentSkills()
    {
        List<Skill> result = new ArrayList<>();
        for (String name : list(RECENT))
        {
            try { result.add(Skill.valueOf(name)); } catch (IllegalArgumentException ignored) { }
        }
        return result;
    }

    public void remember(Skill skill)
    {
        List<String> values = list(RECENT);
        values.remove(skill.name());
        values.add(0, skill.name());
        while (values.size() > 4) values.remove(values.size() - 1);
        saveList(RECENT, values);
    }

    public Map<String, Integer> bankItems()
    {
        return storedItems(BANK_ITEMS);
    }

    public void saveBankItems(Map<String, Integer> items)
    {
        set(BANK_ITEMS, gson.toJson(items));
        set(BANK_STAMP, java.time.LocalDateTime.now().toString());
    }

    public String bankStamp()
    {
        return get(BANK_STAMP);
    }

    public Map<String, Integer> seedVaultItems()
    {
        return storedItems(SEED_VAULT_ITEMS);
    }

    public void saveSeedVaultItems(Map<String, Integer> items)
    {
        set(SEED_VAULT_ITEMS, gson.toJson(items));
        set(SEED_VAULT_STAMP, java.time.LocalDateTime.now().toString());
    }

    public String seedVaultStamp()
    {
        return get(SEED_VAULT_STAMP);
    }

    public Map<String, Integer> availableItems()
    {
        Map<String, Integer> combined = bankItems();
        seedVaultItems().forEach((name, quantity) -> combined.merge(name, quantity, Integer::sum));
        return combined;
    }

    public long activityReadyAt(String taskId)
    {
        String value = get(READY_AT_PREFIX + taskId);
        if (value == null) return 0L;
        try
        {
            return Long.parseLong(value);
        }
        catch (NumberFormatException ignored)
        {
            return 0L;
        }
    }

    public void setActivityReadyAt(String taskId, long readyAt)
    {
        set(READY_AT_PREFIX + taskId, Long.toString(readyAt));
    }
    public long activityStartedAt(String taskId)
    {
        return longValue(STARTED_AT_PREFIX + taskId);
    }

    public void setActivityTimer(String taskId, long startedAt, long readyAt)
    {
        set(STARTED_AT_PREFIX + taskId, Long.toString(startedAt));
        set(READY_AT_PREFIX + taskId, Long.toString(readyAt));
        setActivityReadyNotified(taskId, false);
    }

    public boolean isActivityReadyNotified(String taskId)
    {
        return Boolean.parseBoolean(get(READY_NOTIFIED_PREFIX + taskId));
    }

    public void setActivityReadyNotified(String taskId, boolean notified)
    {
        set(READY_NOTIFIED_PREFIX + taskId, Boolean.toString(notified));
    }

    public boolean isRunSoundEnabled(String taskId, boolean defaultValue)
    {
        String value = get(RUN_SOUND_PREFIX + taskId);
        return value == null ? defaultValue : Boolean.parseBoolean(value);
    }

    public void setRunSoundEnabled(String taskId, boolean enabled)
    {
        set(RUN_SOUND_PREFIX + taskId, Boolean.toString(enabled));
    }

    private long longValue(String key)
    {
        String value = get(key);
        if (value == null) return 0L;
        try
        {
            return Long.parseLong(value);
        }
        catch (NumberFormatException ignored)
        {
            return 0L;
        }
    }

    public void clearActivityReadyAt(String taskId)
    {
        configManager.unsetConfiguration(AdventurePlannerConfig.GROUP, READY_AT_PREFIX + taskId);
    }

    public String notes()
    {
        String value = get(NOTES);
        return value == null ? "" : value;
    }

    public void saveNotes(String notes)
    {
        String clean = notes == null ? "" : notes.trim();
        if (clean.isEmpty()) configManager.unsetConfiguration(AdventurePlannerConfig.GROUP, NOTES);
        else set(NOTES, clean);
    }

    public GeneratedTask currentGeneratedTask()
    {
        String value = get(CURRENT_TASK);
        if (value == null || value.isEmpty()) return null;
        try
        {
            return gson.fromJson(value, GeneratedTask.class);
        }
        catch (RuntimeException ignored)
        {
            return null;
        }
    }

    public void saveGeneratedTask(GeneratedTask task, int minutes)
    {
        if (task == null) clearGeneratedTask();
        else
        {
            set(CURRENT_TASK, gson.toJson(task));
            long duration = Math.max(1, minutes) * 60_000L;
            set(CURRENT_TASK_ENDS_AT, Long.toString(System.currentTimeMillis() + duration));
            set(CURRENT_TASK_PROGRESS, "0");
        }
    }

    public long currentTaskEndsAt()
    {
        String value = get(CURRENT_TASK_ENDS_AT);
        if (value == null) return 0L;
        try
        {
            return Long.parseLong(value);
        }
        catch (NumberFormatException ignored)
        {
            return 0L;
        }
    }

    public long ensureCurrentTaskEndsAt(int defaultMinutes)
    {
        long endsAt = currentTaskEndsAt();
        if (endsAt > 0L || currentGeneratedTask() == null) return endsAt;
        endsAt = System.currentTimeMillis() + Math.max(1, defaultMinutes) * 60_000L;
        set(CURRENT_TASK_ENDS_AT, Long.toString(endsAt));
        return endsAt;
    }

    public int currentTaskProgress()
    {
        String value = get(CURRENT_TASK_PROGRESS);
        if (value == null) return 0;
        try
        {
            return Math.max(0, Integer.parseInt(value));
        }
        catch (NumberFormatException ignored)
        {
            return 0;
        }
    }

    public int addCurrentTaskProgress(int amount, int target)
    {
        int progress = Math.min(Math.max(0, target),
            currentTaskProgress() + Math.max(0, amount));
        set(CURRENT_TASK_PROGRESS, Integer.toString(progress));
        return progress;
    }
    public boolean automaticTasksPaused()
    {
        return Boolean.parseBoolean(get(AUTOMATIC_TASKS_PAUSED));
    }

    public void setAutomaticTasksPaused(boolean paused)
    {
        if (paused)
        {
            set(AUTOMATIC_TASKS_PAUSED, "true");
        }
        else
        {
            configManager.unsetConfiguration(AdventurePlannerConfig.GROUP, AUTOMATIC_TASKS_PAUSED);
        }
    }

    public void clearGeneratedTask()
    {
        configManager.unsetConfiguration(AdventurePlannerConfig.GROUP, CURRENT_TASK);
        configManager.unsetConfiguration(AdventurePlannerConfig.GROUP, CURRENT_TASK_ENDS_AT);
        configManager.unsetConfiguration(AdventurePlannerConfig.GROUP, CURRENT_TASK_PROGRESS);
    }

    public HunterRumour currentRumour()
    {
        return HunterRumourCatalog.find(get(CURRENT_RUMOUR));
    }

    public void saveCurrentRumour(HunterRumour rumour)
    {
        if (rumour == null) configManager.unsetConfiguration(AdventurePlannerConfig.GROUP, CURRENT_RUMOUR);
        else set(CURRENT_RUMOUR, rumour.getId());
    }

    private Map<String, Integer> storedItems(String key)
    {
        String value = get(key);
        if (value == null || value.isEmpty()) return new LinkedHashMap<>();
        Map<String, Integer> result = new LinkedHashMap<>();
        try
        {
            JsonObject stored = gson.fromJson(value, JsonObject.class);
            if (stored == null) return result;
            for (Map.Entry<String, JsonElement> entry : stored.entrySet())
            {
                try
                {
                    int quantity = entry.getValue().getAsInt();
                    if (quantity > 0) result.put(entry.getKey(), quantity);
                }
                catch (RuntimeException ignored) { }
            }
        }
        catch (RuntimeException ignored)
        {
            return new LinkedHashMap<>();
        }
        return result;
    }

    private void removeCadence(Set<String> completed, TaskCadence cadence)
    {
        List<PlannerTask> all = new ArrayList<>(TaskCatalog.defaults());
        all.addAll(customTasks());
        for (PlannerTask task : all)
        {
            if (task.getCadence() == cadence
                && activityReadyAt(task.getId()) <= System.currentTimeMillis())
            {
                completed.remove(task.getId());
            }
        }
    }

    private Set<String> completed() { return new HashSet<>(list(COMPLETED)); }
    private void saveCompleted(Set<String> values) { saveList(COMPLETED, new ArrayList<>(values)); }

    private List<String> list(String key)
    {
        String value = get(key);
        if (value == null || value.isEmpty()) return new ArrayList<>();
        try
        {
            String[] result = gson.fromJson(value, String[].class);
            List<String> values = new ArrayList<>();
            if (result != null) java.util.Collections.addAll(values, result);
            return values;
        }
        catch (RuntimeException ignored) { return new ArrayList<>(); }
    }

    private void saveList(String key, List<String> values) { set(key, gson.toJson(values)); }
    private String get(String key) { return configManager.getConfiguration(AdventurePlannerConfig.GROUP, key); }
    private void set(String key, String value) { configManager.setConfiguration(AdventurePlannerConfig.GROUP, key, value); }
}
