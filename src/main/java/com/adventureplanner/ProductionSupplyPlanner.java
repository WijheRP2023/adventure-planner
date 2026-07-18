package com.adventureplanner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.runelite.api.Skill;

/**
 * Exact refill tasks for production skills whose inputs are not covered by the
 * basic log, fish and ore chains.
 */
final class ProductionSupplyPlanner
{
    private ProductionSupplyPlanner() {}

    static List<BankTaskCandidate> candidates(Map<Skill, Integer> levels,
        Set<Skill> blocked, int minutes, Map<String, Integer> supplies)
    {
        List<BankTaskCandidate> result = new ArrayList<>();
        add(result, crafting(levels, blocked, minutes, supplies));
        add(result, herblore(levels, blocked, minutes, supplies));
        add(result, prayer(levels, blocked, minutes, supplies));
        add(result, runecraft(levels, blocked, minutes, supplies));
        add(result, construction(levels, blocked, minutes, supplies));
        return result;
    }

    static BankTaskCandidate prerequisiteFor(Skill goal, Map<Skill, Integer> levels,
        Set<Skill> blocked, int minutes, Map<String, Integer> supplies)
    {
        if (goal == Skill.CRAFTING) return crafting(levels, blocked, minutes, supplies);
        if (goal == Skill.HERBLORE) return herblore(levels, blocked, minutes, supplies);
        if (goal == Skill.PRAYER) return prayer(levels, blocked, minutes, supplies);
        if (goal == Skill.RUNECRAFT) return runecraft(levels, blocked, minutes, supplies);
        if (goal == Skill.CONSTRUCTION) return construction(levels, blocked, minutes, supplies);
        return null;
    }

    private static BankTaskCandidate crafting(Map<Skill, Integer> levels, Set<Skill> blocked,
        int minutes, Map<String, Integer> supplies)
    {
        int level = levels.getOrDefault(Skill.CRAFTING, 1);
        if (level >= 99 || hasCraftingInputs(supplies)) return null;
        int amount = scaled(level >= 46 ? 650 : 220, minutes);
        if (level >= 63 && !blocked.contains(Skill.ATTACK))
        {
            String colour = level >= 84 ? "black" : "green";
            return new BankTaskCandidate(Skill.ATTACK,
                "Verzamel " + amount + " " + colour + " dragonhides voor d'hide bodies",
                "Laat de hides daarna tanen; 3 leather per body - exacte Crafting-voorraad");
        }
        if (level >= 46 && !blocked.contains(Skill.MINING))
        {
            return new BankTaskCandidate(Skill.MINING,
                "Verzamel " + amount + " buckets of sand en giant seaweed voor glass orbs",
                "Smelt 6 sand met 1 giant seaweed tot molten glass; bewaar een glassblowing pipe");
        }
        if (level >= 7 && !blocked.contains(Skill.MINING))
        {
            return new BankTaskCandidate(Skill.MINING,
                "Mine " + amount + " gold ore voor gold bracelets",
                "Smelt de ore tot gold bars; bewaar een bracelet mould voor het eindproduct");
        }
        if (!blocked.contains(Skill.ATTACK))
        {
            return new BankTaskCandidate(Skill.ATTACK,
                "Verzamel " + amount + " cowhides voor leather gloves",
                "Laat de hides tanen tot leather; bewaar needle en thread");
        }
        return null;
    }

    private static BankTaskCandidate herblore(Map<Skill, Integer> levels, Set<Skill> blocked,
        int minutes, Map<String, Integer> supplies)
    {
        int level = levels.getOrDefault(Skill.HERBLORE, 1);
        if (level >= 99 || hasHerbloreInputs(supplies)) return null;
        int amount = scaled(80, minutes);
        String herb;
        String secondary;
        String product;
        if (level >= 77)
        {
            herb = "avantoe";
            secondary = "mort myre fungi";
            product = "super energy en daarna stamina potions";
        }
        else if (level >= 45)
        {
            herb = "irit";
            secondary = "eyes of newt";
            product = "super attack potions";
        }
        else if (level >= 38)
        {
            herb = "ranarr";
            secondary = "snape grass";
            product = "prayer potions";
        }
        else
        {
            herb = "guam";
            secondary = "eyes of newt";
            product = "attack potions";
        }
        Skill source = !blocked.contains(Skill.FARMING) ? Skill.FARMING : Skill.ATTACK;
        if (blocked.contains(source)) return null;
        return new BankTaskCandidate(source,
            "Verzamel " + amount + " " + herb + " herbs en " + amount + " " + secondary,
            "Exacte Herblore-voorraad voor " + product + "; gebruik herb-runs en bank eerst");
    }

    private static BankTaskCandidate prayer(Map<Skill, Integer> levels, Set<Skill> blocked,
        int minutes, Map<String, Integer> supplies)
    {
        int prayer = levels.getOrDefault(Skill.PRAYER, 1);
        int magic = levels.getOrDefault(Skill.MAGIC, 1);
        if (prayer >= 99 || magic < 16 || hasPrayerInputs(supplies)
            || blocked.contains(Skill.ATTACK)) return null;
        int amount = Math.max(5, minutes / 3);
        String head;
        String runes;
        if (magic >= 90)
        {
            head = "ensouled dragon heads";
            runes = (amount * 4) + " blood, " + (amount * 4) + " nature en "
                + (amount * 2) + " soul runes";
        }
        else if (magic >= 72)
        {
            head = "ensouled bloodveld heads";
            runes = amount + " blood, " + (amount * 3) + " nature en "
                + (amount * 2) + " soul runes";
        }
        else if (magic >= 41)
        {
            head = "ensouled demon heads";
            runes = (amount * 4) + " body, " + (amount * 3) + " nature en "
                + amount + " soul runes";
        }
        else
        {
            head = "ensouled goblin heads";
            runes = (amount * 4) + " body en " + (amount * 2) + " nature runes";
        }
        return new BankTaskCandidate(Skill.ATTACK,
            "Verzamel " + amount + " " + head + " voor Prayer",
            "Reanimate daarna in Arceuus; neem totaal " + runes + " mee");
    }

    private static BankTaskCandidate runecraft(Map<Skill, Integer> levels, Set<Skill> blocked,
        int minutes, Map<String, Integer> supplies)
    {
        int level = levels.getOrDefault(Skill.RUNECRAFT, 1);
        if (level >= 99 || TrainingSupplyCatalog.count(supplies,
            "Rune essence", "Pure essence", "Daeyalt essence") > 0
            || blocked.contains(Skill.MINING)) return null;
        int amount = scaled(500, minutes);
        String essence = levels.getOrDefault(Skill.MINING, 1) >= 60
            ? "daeyalt essence" : "rune essence";
        return new BankTaskCandidate(Skill.MINING,
            "Mine " + amount + " " + essence + " voor Runecraft",
            "Vul de bank exact aan; daarna kiest de planner een passende rune op je level");
    }

    private static BankTaskCandidate construction(Map<Skill, Integer> levels, Set<Skill> blocked,
        int minutes, Map<String, Integer> supplies)
    {
        int level = levels.getOrDefault(Skill.CONSTRUCTION, 1);
        if (level >= 99 || hasPlanks(supplies) || blocked.contains(Skill.WOODCUTTING)) return null;
        int woodcutting = levels.getOrDefault(Skill.WOODCUTTING, 1);
        String log = "regular logs";
        String plank = "planks";
        String product = "Beginner Mahogany Homes";
        if (level >= 70 && woodcutting >= 50)
        {
            log = "mahogany logs";
            plank = "mahogany planks";
            product = "Expert Mahogany Homes";
        }
        else if (level >= 50 && woodcutting >= 35)
        {
            log = "teak logs";
            plank = "teak planks";
            product = "Adept Mahogany Homes";
        }
        else if (level >= 20 && woodcutting >= 15)
        {
            log = "oak logs";
            plank = "oak planks";
            product = level >= 33 ? "oak larders" : "Novice Mahogany Homes";
        }
        int amount = scaled(180, minutes);
        return new BankTaskCandidate(Skill.WOODCUTTING,
            "Hak " + amount + " " + log + " en maak " + amount + " " + plank,
            "Exacte Construction-voorraad voor " + product + "; gebruik de sawmill");
    }

    private static boolean hasCraftingInputs(Map<String, Integer> supplies)
    {
        return TrainingSupplyCatalog.count(supplies, "Leather", "Gold bar", "Molten glass",
            "Water orb", "Green dragon leather", "Black dragon leather") > 0;
    }

    private static boolean hasHerbloreInputs(Map<String, Integer> supplies)
    {
        return TrainingSupplyCatalog.count(supplies, "Guam potion (unf)", "Ranarr potion (unf)",
            "Irit potion (unf)", "Super energy(4)") > 0;
    }

    private static boolean hasPrayerInputs(Map<String, Integer> supplies)
    {
        return TrainingSupplyCatalog.count(supplies, "Big bones", "Dragon bones",
            "Calcified deposit", "Calcified deposits", "Ensouled goblin head",
            "Ensouled demon head", "Ensouled aviansie head", "Ensouled bloodveld head", "Ensouled dragon head") > 0;
    }

    private static boolean hasPlanks(Map<String, Integer> supplies)
    {
        return TrainingSupplyCatalog.count(supplies, "Plank", "Oak plank",
            "Teak plank", "Mahogany plank") > 0;
    }

    private static int scaled(int hourly, int minutes)
    {
        int amount = Math.max(10, hourly * minutes / 60);
        return amount < 20 ? amount : amount / 5 * 5;
    }

    private static void add(List<BankTaskCandidate> result, BankTaskCandidate task)
    {
        if (task != null) result.add(task);
    }
}
