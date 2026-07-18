package com.adventureplanner;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Range;

@ConfigGroup(AdventurePlannerConfig.GROUP)
public interface AdventurePlannerConfig extends Config
{
    String GROUP = "adventureplanner";

    @ConfigItem(
        keyName = "language",
        name = "Taal / Language",
        description = "Kies Nederlands of English voor alle plannerteksten",
        position = -1
    )
    default PlannerLanguage language()
    {
        return PlannerLanguage.DUTCH;
    }

    @ConfigSection(
        name = "Skills voor taken",
        description = "Aangevinkt wordt meegenomen; uitgevinkt wordt geblokkeerd",
        position = 0,
        closedByDefault = true
    )
    String SKILLS_SECTION = "skillsForTasks";

    @ConfigSection(
        name = "Dagelijkse taken",
        description = "Aangevinkt wordt getoond en gevolgd; uitgevinkt wordt verborgen",
        position = 1,
        closedByDefault = true
    )
    String DAILY_SECTION = "dailyTasks";

    @ConfigSection(
        name = "Wekelijkse taken",
        description = "Aangevinkt wordt getoond en gevolgd; uitgevinkt wordt verborgen",
        position = 2,
        closedByDefault = true
    )
    String WEEKLY_SECTION = "weeklyTasks";

    @ConfigSection(
        name = "Timers en runs",
        description = "Aangevinkte terugkerende activiteiten worden gevolgd en getoond",
        position = 3,
        closedByDefault = true
    )
    String RUNS_SECTION = "timersAndRuns";

    @ConfigSection(
        name = "Focus",
        description = "Bepaal waar de taakgenerator rekening mee houdt",
        position = 4,
        closedByDefault = true
    )
    String FOCUS_SECTION = "plannerFocus";

    @ConfigSection(
        name = "Weergave",
        description = "Instellingen voor het zijpaneel en het spelvenster",
        position = 5,
        closedByDefault = true
    )
    String DISPLAY_SECTION = "display";

    @ConfigItem(
        keyName = "includeAttack", name = "Attack", description = "Attack meenemen",
        position = 0, section = SKILLS_SECTION
    )
    default boolean includeAttack() { return true; }

    @ConfigItem(keyName = "includeStrength", name = "Strength", description = "Strength meenemen", position = 1, section = SKILLS_SECTION)
    default boolean includeStrength() { return true; }

    @ConfigItem(keyName = "includeDefence", name = "Defence", description = "Defence meenemen", position = 2, section = SKILLS_SECTION)
    default boolean includeDefence() { return true; }

    @ConfigItem(keyName = "includeHitpoints", name = "Hitpoints", description = "Hitpoints meenemen", position = 3, section = SKILLS_SECTION)
    default boolean includeHitpoints() { return true; }

    @ConfigItem(keyName = "includeRanged", name = "Ranged", description = "Ranged meenemen", position = 4, section = SKILLS_SECTION)
    default boolean includeRanged() { return true; }

    @ConfigItem(keyName = "includePrayer", name = "Prayer", description = "Prayer meenemen", position = 5, section = SKILLS_SECTION)
    default boolean includePrayer() { return true; }

    @ConfigItem(keyName = "includeMagic", name = "Magic", description = "Magic meenemen", position = 6, section = SKILLS_SECTION)
    default boolean includeMagic() { return true; }

    @ConfigItem(keyName = "includeCooking", name = "Cooking", description = "Cooking meenemen", position = 7, section = SKILLS_SECTION)
    default boolean includeCooking() { return true; }

    @ConfigItem(keyName = "includeWoodcutting", name = "Woodcutting", description = "Woodcutting meenemen", position = 8, section = SKILLS_SECTION)
    default boolean includeWoodcutting() { return true; }

    @ConfigItem(keyName = "includeFletching", name = "Fletching", description = "Fletching meenemen", position = 9, section = SKILLS_SECTION)
    default boolean includeFletching() { return true; }

    @ConfigItem(keyName = "includeFishing", name = "Fishing", description = "Fishing meenemen", position = 10, section = SKILLS_SECTION)
    default boolean includeFishing() { return true; }

    @ConfigItem(keyName = "includeFiremaking", name = "Firemaking", description = "Firemaking meenemen", position = 11, section = SKILLS_SECTION)
    default boolean includeFiremaking() { return true; }

    @ConfigItem(keyName = "includeCrafting", name = "Crafting", description = "Crafting meenemen", position = 12, section = SKILLS_SECTION)
    default boolean includeCrafting() { return true; }

    @ConfigItem(keyName = "includeSmithing", name = "Smithing", description = "Smithing meenemen", position = 13, section = SKILLS_SECTION)
    default boolean includeSmithing() { return true; }

    @ConfigItem(keyName = "includeMining", name = "Mining", description = "Mining meenemen", position = 14, section = SKILLS_SECTION)
    default boolean includeMining() { return true; }

    @ConfigItem(keyName = "includeHerblore", name = "Herblore", description = "Herblore meenemen", position = 15, section = SKILLS_SECTION)
    default boolean includeHerblore() { return true; }

    @ConfigItem(keyName = "includeAgility", name = "Agility", description = "Agility meenemen", position = 16, section = SKILLS_SECTION)
    default boolean includeAgility() { return true; }

    @ConfigItem(keyName = "includeThieving", name = "Thieving", description = "Thieving meenemen", position = 17, section = SKILLS_SECTION)
    default boolean includeThieving() { return true; }

    @ConfigItem(keyName = "includeSlayer", name = "Slayer", description = "Slayer meenemen", position = 18, section = SKILLS_SECTION)
    default boolean includeSlayer() { return true; }

    @ConfigItem(keyName = "includeFarming", name = "Farming", description = "Farming meenemen", position = 19, section = SKILLS_SECTION)
    default boolean includeFarming() { return true; }

    @ConfigItem(keyName = "includeRunecraft", name = "Runecraft", description = "Runecraft meenemen", position = 20, section = SKILLS_SECTION)
    default boolean includeRunecraft() { return true; }

    @ConfigItem(keyName = "includeHunter", name = "Hunter", description = "Hunter meenemen", position = 21, section = SKILLS_SECTION)
    default boolean includeHunter() { return true; }

    @ConfigItem(keyName = "includeConstruction", name = "Construction", description = "Construction meenemen", position = 22, section = SKILLS_SECTION)
    default boolean includeConstruction() { return true; }

    @ConfigItem(keyName = "includeSailing", name = "Sailing", description = "Sailing meenemen", position = 23, section = SKILLS_SECTION)
    default boolean includeSailing() { return true; }

    @ConfigItem(keyName = "showBattlestaves", name = "Zaff: battlestaves", description = "Deze dagelijkse taak tonen", position = 0, section = DAILY_SECTION)
    default boolean showBattlestaves() { return true; }

    @ConfigItem(keyName = "showSand", name = "Bert: emmers zand", description = "Deze dagelijkse taak tonen", position = 1, section = DAILY_SECTION)
    default boolean showSand() { return true; }

    @ConfigItem(keyName = "showDynamite", name = "Thirus: dynamiet", description = "Deze dagelijkse taak tonen", position = 2, section = DAILY_SECTION)
    default boolean showDynamite() { return true; }

    @ConfigItem(keyName = "showEssence", name = "Cromperty: rune essence", description = "Deze dagelijkse taak tonen", position = 3, section = DAILY_SECTION)
    default boolean showEssence() { return true; }

    @ConfigItem(keyName = "showArrows", name = "Rantz: ogre arrows", description = "Deze dagelijkse taak tonen", position = 4, section = DAILY_SECTION)
    default boolean showArrows() { return true; }

    @ConfigItem(keyName = "showBonemeal", name = "Robin: bonemeal", description = "Deze dagelijkse taak tonen", position = 5, section = DAILY_SECTION)
    default boolean showBonemeal() { return true; }

    @ConfigItem(keyName = "showHerbRuns", name = "Herb-runs", description = "Herb-timers per patch tonen", position = 0, section = RUNS_SECTION)
    default boolean showHerbRuns() { return true; }

    @ConfigItem(keyName = "showTreeRuns", name = "Tree-runs", description = "Tree-timers per patch tonen", position = 1, section = RUNS_SECTION)
    default boolean showTreeRuns() { return true; }

    @ConfigItem(keyName = "showSeaweedRuns", name = "Giant seaweed-runs", description = "Giant seaweed-timers tonen", position = 2, section = RUNS_SECTION)
    default boolean showSeaweedRuns() { return true; }

    @ConfigItem(keyName = "showBirdhouses", name = "Birdhouse-runs", description = "Birdhouse-timers per plek tonen", position = 3, section = RUNS_SECTION)
    default boolean showBirdhouses() { return true; }

    @ConfigItem(keyName = "showFarmingContracts", name = "Farming Contracts", description = "Deze dagelijkse taak tonen", position = 6, section = DAILY_SECTION)
    default boolean showFarmingContracts() { return true; }

    @ConfigItem(keyName = "showHunterRumours", name = "Hunter Rumours", description = "Deze dagelijkse taak tonen", position = 7, section = DAILY_SECTION)
    default boolean showHunterRumours() { return true; }

    @ConfigItem(keyName = "showWeeklyTears", name = "Tears of Guthix", description = "Deze wekelijkse taak tonen", position = 0, section = WEEKLY_SECTION)
    default boolean showWeeklyTears() { return true; }

    @ConfigItem(keyName = "showWeeklyKingdom", name = "Kingdom", description = "Deze wekelijkse taak tonen", position = 1, section = WEEKLY_SECTION)
    default boolean showWeeklyKingdom() { return true; }

    @ConfigItem(keyName = "showWeeklyMinigame", name = "Afwisselende minigame", description = "Deze wekelijkse taak tonen", position = 2, section = WEEKLY_SECTION)
    default boolean showWeeklyMinigame() { return true; }

    @ConfigItem(keyName = "showWeeklyBoss", name = "Boss-opdracht", description = "Deze wekelijkse taak tonen", position = 3, section = WEEKLY_SECTION)
    default boolean showWeeklyBoss() { return true; }
    @ConfigItem(
        keyName = "focusMode", name = "Accountfocus",
        description = "Beperk gegenereerde taken tot deze richting",
        position = 0, section = FOCUS_SECTION
    )
    default PlannerFocus focusMode() { return PlannerFocus.BALANCED; }

    @ConfigItem(
        keyName = "focusQuest", name = "Questnaam",
        description = "Naam van de quest wanneer de focus op Quest staat",
        position = 1, section = FOCUS_SECTION
    )
    default String focusQuest() { return ""; }

    @ConfigItem(
        keyName = "autoGenerateTask", name = "Taak automatisch kiezen",
        description = "Kies automatisch een volgende taak als er nog geen actieve taak is",
        position = 2, section = FOCUS_SECTION
    )
    default boolean autoGenerateTask() { return true; }

    @ConfigItem(
        keyName = "showGameOverlay", name = "Taak in spelvenster",
        description = "Toon de actieve taak en eerstvolgende timer als instelbaar overlayvenster",
        position = 0, section = DISPLAY_SECTION
    )
    default boolean showGameOverlay() { return false; }

    @ConfigItem(
        keyName = "showTaskReminderInfoBoxes", name = "Dag-/weekreminders",
        description = "Toon voor iedere openstaande ingeschakelde dag- of weektaak een compacte infobox",
        position = 1, section = DISPLAY_SECTION
    )
    default boolean showTaskReminderInfoBoxes() { return true; }
    @Range(min = 1, max = 10)
    @ConfigItem(
        keyName = "levelChunk",
        name = "Levels per taak",
        description = "Hoeveel levels een gegenereerde taak maximaal vraagt"
    )
    default int levelChunk()
    {
        return 2;
    }


    @ConfigItem(
        keyName = "autoTrackRuns",
        name = "Runs automatisch volgen",
        description = "Start herb-, tree- en birdhouse-timers automatisch per locatie",
        position = 4,
        section = RUNS_SECTION
    )
    default boolean autoTrackRuns()
    {
        return true;
    }

    @ConfigItem(
        keyName = "runReadySounds",
        name = "Geluid bij klaar",
        description = "Speel voor elk type run een herkenbaar ander geluid af",
        position = 5,
        section = RUNS_SECTION
    )
    default boolean runReadySounds()
    {
        return true;
    }
}
