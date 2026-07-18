package com.adventureplanner;

import java.util.EnumSet;
import java.util.Set;
import net.runelite.api.Skill;

public final class PlannerConfigSelections
{
    private PlannerConfigSelections() {}

    public static Set<Skill> blockedSkills(AdventurePlannerConfig config)
    {
        Set<Skill> blocked = EnumSet.noneOf(Skill.class);
        if (!config.includeAttack()) blocked.add(Skill.ATTACK);
        if (!config.includeStrength()) blocked.add(Skill.STRENGTH);
        if (!config.includeDefence()) blocked.add(Skill.DEFENCE);
        if (!config.includeHitpoints()) blocked.add(Skill.HITPOINTS);
        if (!config.includeRanged()) blocked.add(Skill.RANGED);
        if (!config.includePrayer()) blocked.add(Skill.PRAYER);
        if (!config.includeMagic()) blocked.add(Skill.MAGIC);
        if (!config.includeCooking()) blocked.add(Skill.COOKING);
        if (!config.includeWoodcutting()) blocked.add(Skill.WOODCUTTING);
        if (!config.includeFletching()) blocked.add(Skill.FLETCHING);
        if (!config.includeFishing()) blocked.add(Skill.FISHING);
        if (!config.includeFiremaking()) blocked.add(Skill.FIREMAKING);
        if (!config.includeCrafting()) blocked.add(Skill.CRAFTING);
        if (!config.includeSmithing()) blocked.add(Skill.SMITHING);
        if (!config.includeMining()) blocked.add(Skill.MINING);
        if (!config.includeHerblore()) blocked.add(Skill.HERBLORE);
        if (!config.includeAgility()) blocked.add(Skill.AGILITY);
        if (!config.includeThieving()) blocked.add(Skill.THIEVING);
        if (!config.includeSlayer()) blocked.add(Skill.SLAYER);
        if (!config.includeFarming()) blocked.add(Skill.FARMING);
        if (!config.includeRunecraft()) blocked.add(Skill.RUNECRAFT);
        if (!config.includeHunter()) blocked.add(Skill.HUNTER);
        if (!config.includeConstruction()) blocked.add(Skill.CONSTRUCTION);
        if (!config.includeSailing()) blocked.add(Skill.SAILING);
        return blocked;
    }

    public static Set<DailyTaskOption> hiddenDailyTasks(AdventurePlannerConfig config)
    {
        Set<DailyTaskOption> hidden = EnumSet.noneOf(DailyTaskOption.class);
        if (!config.showBattlestaves()) hidden.add(DailyTaskOption.BATTLESTAVES);
        if (!config.showSand()) hidden.add(DailyTaskOption.SAND);
        if (!config.showDynamite()) hidden.add(DailyTaskOption.DYNAMITE);
        if (!config.showEssence()) hidden.add(DailyTaskOption.ESSENCE);
        if (!config.showArrows()) hidden.add(DailyTaskOption.ARROWS);
        if (!config.showBonemeal()) hidden.add(DailyTaskOption.BONEMEAL);
        if (!config.showHerbRuns()) hidden.add(DailyTaskOption.HERB_RUN);
        if (!config.showTreeRuns()) hidden.add(DailyTaskOption.TREE_RUN);
        if (!config.showSeaweedRuns()) hidden.add(DailyTaskOption.SEAWEED_RUN);
        if (!config.showBirdhouses()) hidden.add(DailyTaskOption.BIRDHOUSES);
        if (!config.showFarmingContracts()) hidden.add(DailyTaskOption.FARMING_CONTRACT);
        if (!config.showHunterRumours()) hidden.add(DailyTaskOption.HUNTER_RUMOUR);
        return hidden;
    }
    public static Set<WeeklyTaskOption> hiddenWeeklyTasks(AdventurePlannerConfig config)
    {
        Set<WeeklyTaskOption> hidden = EnumSet.noneOf(WeeklyTaskOption.class);
        if (!config.showWeeklyTears()) hidden.add(WeeklyTaskOption.TEARS);
        if (!config.showWeeklyKingdom()) hidden.add(WeeklyTaskOption.KINGDOM);
        if (!config.showWeeklyMinigame()) hidden.add(WeeklyTaskOption.MINIGAME);
        if (!config.showWeeklyBoss()) hidden.add(WeeklyTaskOption.BOSS);
        return hidden;
    }
}
