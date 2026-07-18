package com.adventureplanner;

import java.util.Locale;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.runelite.api.Client;
import net.runelite.api.Skill;
import net.runelite.api.SoundEffectID;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.StatChanged;
import net.runelite.api.gameval.VarPlayerID;
import net.runelite.client.util.Text;

public final class RecurringActivityManager
{
    static final String HERB_TASK = "daily_herbs";
    static final String TREE_TASK = "daily_trees";
    static final String BIRDHOUSE_TASK = "daily_birdhouses";

    private static final long RUN_FINISH_DELAY_MS = 120L * 1000L;
    private static final long PLANT_CONFIRM_WINDOW_MS = 15L * 1000L;

    private static final int[] BIRDHOUSE_VARPS = {
        VarPlayerID.BIRDHOUSE_TRANSMIT_A,
        VarPlayerID.BIRDHOUSE_TRANSMIT_B,
        VarPlayerID.BIRDHOUSE_TRANSMIT_C,
        VarPlayerID.BIRDHOUSE_TRANSMIT_D
    };

    private final Client client;
    private final AdventurePlannerConfig config;
    private final PlannerState state;
    private final Runnable stateChanged;
    private final int[] previousBirdhouseValues = new int[BIRDHOUSE_VARPS.length];

    private PlantAttempt plantAttempt;
    private final Map<String, PendingRun> pendingRuns = new HashMap<>();
    private boolean birdhousesInitialized;
    private long lastUiRefreshBucket;

    public RecurringActivityManager(Client client, AdventurePlannerConfig config,
        PlannerState state, Runnable stateChanged)
    {
        this.client = client;
        this.config = config;
        this.state = state;
        this.stateChanged = stateChanged;
    }

    public void onMenuOptionClicked(MenuOptionClicked event)
    {
        if (!config.autoTrackRuns()) return;
        String target = Text.removeTags(event.getMenuTarget()).toLowerCase(Locale.ENGLISH);
        RunType type = classifyPlantTarget(target);
        if (type != null)
        {
            String taskId = taskId(type);
            if (isHidden(taskId)) return;
            RunLocation location = RunLocationCatalog.nearest(type,
                client.getLocalPlayer() == null ? null : client.getLocalPlayer().getWorldLocation());
            plantAttempt = new PlantAttempt(type, location, System.currentTimeMillis(), treeCooldownMinutes(target));
        }
    }

    public void onStatChanged(StatChanged event)
    {
        if (!config.autoTrackRuns() || event.getSkill() != Skill.FARMING || plantAttempt == null) return;
        long now = System.currentTimeMillis();
        if (now - plantAttempt.detectedAt <= PLANT_CONFIRM_WINDOW_MS)
        {
            if (plantAttempt.location != null)
            {
                pendingRuns.put(plantAttempt.location.timerId(),
                    new PendingRun(plantAttempt.location, now + RUN_FINISH_DELAY_MS,
                        plantAttempt.cooldownMinutes));
            }
        }
        plantAttempt = null;
    }

    public void onGameTick()
    {
        if (!config.autoTrackRuns()) return;
        long now = System.currentTimeMillis();
        detectBirdhouses(now);

        Iterator<PendingRun> pending = pendingRuns.values().iterator();
        while (pending.hasNext())
        {
            PendingRun run = pending.next();
            if (now >= run.finishAt)
            {
                complete(run.location, run.cooldownMinutes, now);
                pending.remove();
            }
        }

        boolean hasActiveTimer = false;
        for (RunLocation location : RunLocationCatalog.locations())
        {
            if (isHidden(taskId(location.getType()))) continue;
            long readyAt = state.activityReadyAt(location.timerId());
            if (readyAt > now) hasActiveTimer = true;
            checkReady(location, now);
        }
        long bucket = now / 1_000L;
        if (hasActiveTimer && bucket != lastUiRefreshBucket)
        {
            lastUiRefreshBucket = bucket;
            stateChanged.run();
        }
    }

    private void detectBirdhouses(long now)
    {
        if (isHidden(BIRDHOUSE_TASK)) return;
        java.util.List<RunLocation> birdhouses = new java.util.ArrayList<>();
        for (RunLocation location : RunLocationCatalog.locations())
        {
            if (location.getType() == RunType.BIRDHOUSE) birdhouses.add(location);
        }
        for (int i = 0; i < BIRDHOUSE_VARPS.length; i++)
        {
            int value = client.getVarpValue(BIRDHOUSE_VARPS[i]);
            if (birdhousesInitialized && isSeeded(value) && !isSeeded(previousBirdhouseValues[i]))
            {
                RunLocation location = birdhouses.get(i);
                pendingRuns.put(location.timerId(),
                    new PendingRun(location, now + RUN_FINISH_DELAY_MS, 50));
            }
            previousBirdhouseValues[i] = value;
        }
        if (!birdhousesInitialized)
        {
            birdhousesInitialized = true;
        }
    }

    private void complete(RunLocation location, int cooldownMinutes, long now)
    {
        String taskId = taskId(location.getType());
        if (isHidden(taskId)) return;
        state.setCompleted(taskId, true);
        state.setActivityTimer(location.timerId(), now,
            now + cooldownMinutes * 60L * 1000L);
        stateChanged.run();
    }

    private void checkReady(RunLocation location, long now)
    {
        String taskId = taskId(location.getType());
        if (isHidden(taskId)) return;
        long readyAt = state.activityReadyAt(location.timerId());
        if (readyAt <= 0 || now < readyAt) return;
        if (state.isActivityReadyNotified(location.timerId())) return;
        state.setActivityReadyNotified(location.timerId(), true);
        state.setCompleted(taskId, false);
        if (state.isRunSoundEnabled(location.timerId(), config.runReadySounds()))
        {
            client.playSoundEffect(soundFor(location.getType()));
        }
        stateChanged.run();
    }

    private static String taskId(RunType type)
    {
        switch (type)
        {
            case HERB: return HERB_TASK;
            case TREE: return TREE_TASK;
            case BIRDHOUSE: return BIRDHOUSE_TASK;
            case SEAWEED: return "daily_seaweed";
            default: throw new IllegalArgumentException("Onbekend runtype");
        }
    }

    private static int soundFor(RunType type)
    {
        switch (type)
        {
            case HERB: return SoundEffectID.UI_BOOP;
            case TREE: return SoundEffectID.TOWN_CRIER_BELL_DING;
            case BIRDHOUSE: return SoundEffectID.GE_INCREMENT_PLOP;
            case SEAWEED: return SoundEffectID.UI_BOOP;
            default: return SoundEffectID.UI_BOOP;
        }
    }

    private boolean isHidden(String taskId)
    {
        return DailyTaskOption.isHidden(taskId, PlannerConfigSelections.hiddenDailyTasks(config));
    }

    static RunType classifyPlantTarget(String target)
    {
        boolean planting = target.contains("seed") || target.contains("sapling")
            || target.contains("spore");
        if (!planting) return null;
        if (target.contains("herb patch")) return RunType.HERB;
        if (target.contains("seaweed patch")) return RunType.SEAWEED;
        if (target.contains("tree patch") || target.contains("hardwood patch")
            || target.contains("calquat patch") || target.contains("celastrus patch"))
            return RunType.TREE;
        return null;
    }

    static int treeCooldownMinutes(String target)
    {
        if (target.contains("spirit sapling")) return 3_840;
        if (target.contains("redwood sapling")) return 6_400;
        if (target.contains("celastrus sapling")) return 800;
        if (target.contains("calquat sapling")) return 1_280;
        if (target.contains("crystal acorn")) return 480;
        if (target.contains("mahogany sapling")) return 7_680;
        if (target.contains("teak sapling")
            || target.contains("hardwood patch")) return 5_120;
        if (target.contains("fruit tree patch") || target.contains("apple sapling")
            || target.contains("banana sapling") || target.contains("orange sapling")
            || target.contains("curry sapling") || target.contains("pineapple sapling")
            || target.contains("papaya sapling") || target.contains("palm sapling")
            || target.contains("dragonfruit sapling")) return 960;
        if (target.contains("oak sapling")) return 200;
        if (target.contains("willow sapling")) return 280;
        if (target.contains("maple sapling")) return 320;
        if (target.contains("yew sapling")) return 400;
        if (target.contains("magic sapling")) return 480;
        return 480;
    }

    private static boolean isSeeded(int varp)
    {
        return varp > 0 && varp % 3 == 0;
    }

    private static final class PlantAttempt
    {
        private final RunType type;
        private final RunLocation location;
        private final long detectedAt;
        private final int cooldownMinutes;

        private PlantAttempt(RunType type, RunLocation location, long detectedAt, int cooldownMinutes)
        {
            this.type = type;
            this.location = location;
            this.detectedAt = detectedAt;
            this.cooldownMinutes = type == RunType.HERB ? 80
                : (type == RunType.SEAWEED ? 40 : cooldownMinutes);
        }
    }

    private static final class PendingRun
    {
        private final RunLocation location;
        private final long finishAt;
        private final int cooldownMinutes;

        private PendingRun(RunLocation location, long finishAt, int cooldownMinutes)
        {
            this.location = location;
            this.finishAt = finishAt;
            this.cooldownMinutes = cooldownMinutes;
        }
    }
}
