package com.adventureplanner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Collections;
import java.util.Set;
import java.util.HashSet;
import net.runelite.api.Skill;

public final class SmartTaskGenerator
{
    private final Random random;
    private final TaskGenerator progressionGenerator;

    public SmartTaskGenerator(Random random)
    {
        this.random = random;
        this.progressionGenerator = new TaskGenerator(random);
    }

    public GeneratedTask generate(Map<Skill, Integer> levels, List<Skill> recent,
        int chunkSize, int minutes, Map<String, Integer> bank)
    {
        return generate(levels, recent, chunkSize, minutes, bank, Collections.emptySet());
    }

    public GeneratedTask generate(Map<Skill, Integer> levels, List<Skill> recent,
        int chunkSize, int minutes, Map<String, Integer> bank, Set<Skill> blocked)
    {
        return generate(levels, recent, chunkSize, minutes, bank, blocked,
            PlannerFocus.BALANCED, "");
    }

    public GeneratedTask generate(Map<Skill, Integer> levels, List<Skill> recent,
        int chunkSize, int minutes, Map<String, Integer> bank, Set<Skill> blocked,
        PlannerFocus focus, String focusQuest)
    {
        return generate(levels, recent, chunkSize, minutes, bank, blocked,
            focus, focusQuest, ItemPriceProvider.NONE);
    }

    public GeneratedTask generate(Map<Skill, Integer> levels, List<Skill> recent,
        int chunkSize, int minutes, Map<String, Integer> bank, Set<Skill> blocked,
        PlannerFocus focus, String focusQuest, ItemPriceProvider priceProvider)
    {
        int safeMinutes = Math.max(10, Math.min(120, minutes));
        if (focus == PlannerFocus.QUEST && focusQuest != null && !focusQuest.trim().isEmpty())
        {
            return GeneratedTask.focus("Werk " + safeMinutes + " minuten aan " + focusQuest.trim(),
                "Questfocus blijft actief; herb-, tree- en birdhouse-timers lopen gewoon door");
        }

        Set<Skill> effectiveBlocked = new HashSet<>(blocked);
        if (focus == PlannerFocus.COMBAT)
        {
            for (Skill skill : Skill.values())
            {
                if (!isCombat(skill)) effectiveBlocked.add(skill);
            }
        }
        List<Skill> excluded = new ArrayList<>(recent);
        excluded.addAll(effectiveBlocked);

        if (focus == PlannerFocus.DIARIES)
        {
            GeneratedTask diary = progressionGenerator.generate(levels, recent, chunkSize, effectiveBlocked);
            return trainingTaskForGoal(diary, levels, safeMinutes, bank, effectiveBlocked);
        }
        BankTaskCandidate seedBoxGoal = seedBoxGoal(levels, excluded, safeMinutes, bank);
        if (seedBoxGoal != null && random.nextInt(100) < 25)
        {
            return GeneratedTask.activity(seedBoxGoal.getSkill(), seedBoxGoal.getTitle(),
                seedBoxGoal.getDetail() + " · stop na maximaal " + safeMinutes + " minuten");
        }
        List<BankTaskCandidate> practical = new ArrayList<>();
        practical.addAll(SupplyChainCatalog.candidates(levels, effectiveBlocked, safeMinutes, bank));
        for (BankTaskCandidate candidate : BankTaskCatalog.candidates(bank, levels))
        {
            if (!excluded.contains(candidate.getSkill())) practical.add(candidate);
        }

        practical.addAll(SkillActivityCatalog.candidates(levels, excluded, safeMinutes));
        practical.addAll(LevelTrainingCatalog.candidates(levels, excluded, safeMinutes, random, bank));

        addActivities(practical, levels, excluded, safeMinutes, bank);
        practical.removeIf(candidate -> candidate.getSkill() == Skill.SLAYER);
        if (!excluded.contains(Skill.SLAYER) && levels.getOrDefault(Skill.SLAYER, 1) < 99)
        {
            practical.add(new BankTaskCandidate(Skill.SLAYER,
                "Train Slayer maximaal " + safeMinutes + " minuten",
                "Haal een nieuwe assignment of ga door met een bestaande; je kunt altijd wisselen"));
        }
        if (!practical.isEmpty())
        {
            BankTaskCandidate selected = ProfitAwareTaskSelector.select(practical, random, priceProvider);
            return GeneratedTask.activity(selected.getSkill(), selected.getTitle(),
                selected.getDetail() + " · stop na maximaal " + safeMinutes + " minuten");
        }
        GeneratedTask goal = progressionGenerator.generate(levels, excluded, chunkSize, effectiveBlocked);
        return trainingTaskForGoal(goal, levels, safeMinutes, bank, effectiveBlocked);
    }

    private GeneratedTask trainingTaskForGoal(GeneratedTask goal, Map<Skill, Integer> levels,
        int minutes, Map<String, Integer> bank, Set<Skill> blocked)
    {
        if (goal == null) return null;
        BankTaskCandidate method = LevelTrainingCatalog.randomCandidateFor(
            goal.getSkill(), levels, minutes, random, bank);
        if (method == null)
        {
            BankTaskCandidate prerequisite = SupplyChainCatalog.prerequisiteFor(
                goal.getSkill(), levels, blocked, minutes, bank);
            if (prerequisite != null)
            {
                return GeneratedTask.activity(prerequisite.getSkill(), prerequisite.getTitle(),
                    prerequisite.getDetail() + " - voorbereiding voor "
                        + goal.getSkill().getName() + " level " + goal.getTargetLevel());
            }
            return GeneratedTask.shortProgression(goal, minutes);
        }
        return GeneratedTask.activity(goal.getSkill(), method.getTitle(),
            method.getDetail() + " - " + goal.getPhase() + "-richting: level "
                + goal.getTargetLevel() + " - " + goal.getReason()
                + " - stop na maximaal " + minutes + " minuten");
    }

    private static boolean isCombat(Skill skill)
    {
        return skill == Skill.ATTACK || skill == Skill.STRENGTH || skill == Skill.DEFENCE
            || skill == Skill.HITPOINTS || skill == Skill.RANGED || skill == Skill.PRAYER
            || skill == Skill.MAGIC || skill == Skill.SLAYER;
    }

    private static void addActivities(List<BankTaskCandidate> result,
        Map<Skill, Integer> levels, List<Skill> recent, int minutes, Map<String, Integer> supplies)
    {
        boolean hasSeedBox = supplies.getOrDefault("Seed box", 0) > 0;
        String seedBoxNote = hasSeedBox
            ? " · Seed box aanwezig; haal seeds eruit voordat je plant"
            : " · Seed box niet gevonden";

        if (levels.getOrDefault(Skill.HUNTER, 1) >= 46
            && levels.getOrDefault(Skill.HUNTER, 1) < 99 && !recent.contains(Skill.HUNTER))
        {
            result.add(new BankTaskCandidate(Skill.HUNTER,
                "Voltooi 1 Hunter Rumour", "Afwisselende Hunter-activiteit · geen vaste methode"));
        }
        if (levels.getOrDefault(Skill.FARMING, 1) >= 45
            && levels.getOrDefault(Skill.FARMING, 1) < 99 && !recent.contains(Skill.FARMING))
        {
            long seedTypes = supplies.keySet().stream()
                .filter(name -> name.endsWith(" seed") || name.endsWith(" seeds") || name.endsWith(" spore"))
                .count();
            result.add(new BankTaskCandidate(Skill.FARMING,
                "Voltooi 1 Farming Contract",
                "Gebruik bank + Seed Vault · " + seedTypes + " soorten zaden opgeslagen" + seedBoxNote));
        }
        if (levels.getOrDefault(Skill.HUNTER, 1) >= 5
            && levels.getOrDefault(Skill.HUNTER, 1) < 99 && !recent.contains(Skill.HUNTER))
        {
            result.add(new BankTaskCandidate(Skill.HUNTER,
                "Doe 1 volledige Birdhouse-run", "Korte passieve Hunter XP · gebruik clocks en logs uit je bank"));
        }
        if (levels.getOrDefault(Skill.FARMING, 1) >= 9
            && levels.getOrDefault(Skill.FARMING, 1) < 99 && !recent.contains(Skill.FARMING))
        {
            String herbSeed = bestHerbSeed(supplies, levels.getOrDefault(Skill.FARMING, 1));
            result.add(new BankTaskCandidate(Skill.FARMING,
                herbSeed == null ? "Doe 1 herb-run" : "Doe 1 herb-run met " + herbSeed,
                herbSeed == null ? "Geen bruikbaar herb seed gezien; scan bank en Seed Vault opnieuw"
                    : supplies.getOrDefault(herbSeed, 0) + " beschikbaar in bank + Seed Vault" + seedBoxNote));
        }
        if (levels.getOrDefault(Skill.FARMING, 1) >= 15
            && levels.getOrDefault(Skill.FARMING, 1) < 99 && !recent.contains(Skill.FARMING))
        {
            String treeSeed = bestTreeSeed(supplies, levels.getOrDefault(Skill.FARMING, 1));
            result.add(new BankTaskCandidate(Skill.FARMING,
                treeSeed == null ? "Doe 1 tree- of fruit-tree-run" : "Bereid/plant " + treeSeed,
                treeSeed == null ? "Scan bank en Seed Vault voor een passend tree seed of sapling"
                    : supplies.getOrDefault(treeSeed, 0) + " beschikbaar in bank + Seed Vault" + seedBoxNote));
        }
        if (levels.getOrDefault(Skill.FARMING, 1) >= 23
            && levels.getOrDefault(Skill.FARMING, 1) < 99 && !recent.contains(Skill.FARMING))
        {
            int spores = supplies.getOrDefault("Seaweed spore", 0);
            result.add(new BankTaskCandidate(Skill.FARMING,
                "Doe 1 giant seaweed-run",
                spores > 0 ? spores + " seaweed spores beschikbaar in bank + Seed Vault"
                    : "Geen spores gezien; scan bank en Seed Vault opnieuw"));
        }

    }

    static BankTaskCandidate seedBoxGoal(Map<Skill, Integer> levels, List<Skill> recent,
        int minutes, Map<String, Integer> supplies)
    {
        if (supplies.getOrDefault("Seed box", 0) > 0 || recent.contains(Skill.FARMING)) return null;
        int level = levels.getOrDefault(Skill.FARMING, 1);
        if (level < 34)
        {
            return new BankTaskCandidate(Skill.FARMING,
                "Train Farming " + minutes + " minuten richting level 34",
                "Level 34 ontgrendelt Tithe Farm om de Seed box te halen");
        }
        return new BankTaskCandidate(Skill.FARMING,
            "Doe " + minutes + " minuten Tithe Farm voor de Seed box",
            "Doel: 250 punten · neem " + titheSeedFor(level) + " van de tafel");
    }

    static String titheSeedFor(int farmingLevel)
    {
        if (farmingLevel >= 74) return "Logavano seeds";
        if (farmingLevel >= 54) return "Bologano seeds";
        return "Golovanova seeds";
    }

    static String bestHerbSeed(Map<String, Integer> supplies, int level)
    {
        return bestUsable(supplies, level, new String[][] {
            {"Guam seed", "9"}, {"Marrentill seed", "14"}, {"Tarromin seed", "19"},
            {"Harralander seed", "26"}, {"Ranarr seed", "32"}, {"Toadflax seed", "38"},
            {"Irit seed", "44"}, {"Avantoe seed", "50"}, {"Kwuarm seed", "56"},
            {"Snapdragon seed", "62"}, {"Cadantine seed", "67"}, {"Lantadyme seed", "73"},
            {"Dwarf weed seed", "79"}, {"Torstol seed", "85"}
        });
    }

    static String bestTreeSeed(Map<String, Integer> supplies, int level)
    {
        return bestUsable(supplies, level, new String[][] {
            {"Acorn", "15"}, {"Apple tree seed", "27"}, {"Willow seed", "30"},
            {"Banana tree seed", "33"}, {"Orange tree seed", "39"}, {"Curry tree seed", "42"},
            {"Maple seed", "45"}, {"Pineapple seed", "51"}, {"Papaya tree seed", "57"},
            {"Yew seed", "60"}, {"Palm tree seed", "68"}, {"Magic seed", "75"},
            {"Dragonfruit tree seed", "81"}, {"Oak sapling", "15"}, {"Willow sapling", "30"},
            {"Maple sapling", "45"}, {"Yew sapling", "60"}, {"Magic sapling", "75"},
            {"Apple sapling", "27"}, {"Banana sapling", "33"}, {"Orange sapling", "39"},
            {"Curry sapling", "42"}, {"Pineapple sapling", "51"}, {"Papaya sapling", "57"},
            {"Palm sapling", "68"}, {"Dragonfruit sapling", "81"}
        });
    }

    private static String bestUsable(Map<String, Integer> supplies, int level, String[][] options)
    {
        String best = null;
        int bestRequirement = -1;
        for (String[] option : options)
        {
            int requirement = Integer.parseInt(option[1]);
            if (level >= requirement && requirement > bestRequirement
                && supplies.getOrDefault(option[0], 0) > 0)
            {
                best = option[0];
                bestRequirement = requirement;
            }
        }
        return best;
    }
}
