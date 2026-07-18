package com.adventureplanner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.runelite.api.Skill;

final class SupplyChainCatalog
{
    private SupplyChainCatalog() {}

    static List<BankTaskCandidate> candidates(Map<Skill, Integer> levels,
        Set<Skill> blocked, int minutes, Map<String, Integer> supplies)
    {
        List<BankTaskCandidate> result = new ArrayList<>();
        BankTaskCandidate logs = logPrerequisite(levels, blocked, minutes, supplies);
        if (logs != null) result.add(logs);
        BankTaskCandidate fish = fishingPrerequisite(levels, blocked, minutes, supplies);
        if (fish != null) result.add(fish);
        BankTaskCandidate ore = miningPrerequisite(levels, blocked, minutes, supplies);
        if (ore != null) result.add(ore);
        result.addAll(ProductionSupplyPlanner.candidates(levels, blocked, minutes, supplies));
        return result;
    }

    static BankTaskCandidate prerequisiteFor(Skill goal, Map<Skill, Integer> levels,
        Set<Skill> blocked, int minutes, Map<String, Integer> supplies)
    {
        if (goal == Skill.FLETCHING || goal == Skill.FIREMAKING)
            return logPrerequisite(levels, blocked, minutes, supplies);
        if (goal == Skill.COOKING)
            return fishingPrerequisite(levels, blocked, minutes, supplies);
        if (goal == Skill.SMITHING)
            return miningPrerequisite(levels, blocked, minutes, supplies);
        return ProductionSupplyPlanner.prerequisiteFor(goal, levels, blocked, minutes, supplies);
    }

    private static BankTaskCandidate logPrerequisite(Map<Skill, Integer> levels,
        Set<Skill> blocked, int minutes, Map<String, Integer> supplies)
    {
        int fletching = levels.getOrDefault(Skill.FLETCHING, 1);
        int firemaking = levels.getOrDefault(Skill.FIREMAKING, 1);
        if ((fletching >= 99 && firemaking >= 99) || blocked.contains(Skill.WOODCUTTING)
            || hasAny(supplies, "Logs", "Oak logs", "Willow logs", "Maple logs",
                "Yew logs", "Magic logs")) return null;

        int woodcutting = levels.getOrDefault(Skill.WOODCUTTING, 1);
        int processing = Math.max(fletching < 99 ? fletching : 1,
            firemaking < 99 ? firemaking : 1);
        String logs;
        int rate;
        if (woodcutting >= 75 && processing >= 75)
        {
            logs = "magic logs";
            rate = 55;
        }
        else if (woodcutting >= 60 && processing >= 60)
        {
            logs = "yew logs";
            rate = 80;
        }
        else if (woodcutting >= 45 && processing >= 45)
        {
            logs = "maple logs";
            rate = 150;
        }
        else if (woodcutting >= 30 && processing >= 30)
        {
            logs = "willow logs";
            rate = 220;
        }
        else if (woodcutting >= 15 && processing >= 15)
        {
            logs = "oak logs";
            rate = 180;
        }
        else
        {
            logs = "regular logs";
            rate = 160;
        }
        int amount = scaled(rate, minutes);
        String next = fletching < 99 ? fletchingProduct(fletching) : "Firemaking";
        return new BankTaskCandidate(Skill.WOODCUTTING,
            "Hak " + amount + " " + logs + " voor " + next,
            "Voorraadketen: verzamel exact deze logs; daarna kan de planner ze verwerken");
    }

    private static BankTaskCandidate fishingPrerequisite(Map<Skill, Integer> levels,
        Set<Skill> blocked, int minutes, Map<String, Integer> supplies)
    {
        int cooking = levels.getOrDefault(Skill.COOKING, 1);
        if (cooking >= 99 || blocked.contains(Skill.FISHING)
            || hasRawFish(supplies)) return null;

        int fishing = levels.getOrDefault(Skill.FISHING, 1);
        String fish;
        int rate;
        if (fishing >= 82 && cooking >= 84)
        {
            fish = "raw anglerfish";
            rate = 130;
        }
        else if (fishing >= 62 && cooking >= 62)
        {
            fish = "raw monkfish";
            rate = 300;
        }
        else if (fishing >= 40 && cooking >= 40)
        {
            fish = "raw lobsters";
            rate = 180;
        }
        else if (fishing >= 20 && cooking >= 25)
        {
            fish = "raw trout/salmon";
            rate = 420;
        }
        else
        {
            fish = "raw shrimps";
            rate = 220;
        }
        int amount = scaled(rate, minutes);
        return new BankTaskCandidate(Skill.FISHING,
            "Vang " + amount + " " + fish + " voor Cooking",
            "Voorraadketen: vang exact deze vis; daarna kan de planner hem laten koken");
    }

    private static BankTaskCandidate miningPrerequisite(Map<Skill, Integer> levels,
        Set<Skill> blocked, int minutes, Map<String, Integer> supplies)
    {
        int smithing = levels.getOrDefault(Skill.SMITHING, 1);
        if (smithing >= 99 || blocked.contains(Skill.MINING)
            || hasAny(supplies, "Copper ore", "Tin ore", "Iron ore", "Coal",
                "Bronze bar", "Iron bar", "Steel bar", "Mithril bar")) return null;

        int mining = levels.getOrDefault(Skill.MINING, 1);
        String ore;
        int rate;
        if (mining >= 15 && smithing >= 15)
        {
            ore = "iron ore";
            rate = 650;
        }
        else
        {
            ore = "copper/tin ore (ongeveer half van elk)";
            rate = 300;
        }
        int amount = scaled(rate, minutes);
        return new BankTaskCandidate(Skill.MINING,
            "Mine " + amount + " " + ore + " voor Smithing",
            "Voorraadketen: verzamel exact deze ore; daarna kan de planner bars laten maken");
    }

    private static boolean hasRawFish(Map<String, Integer> supplies)
    {
        for (Map.Entry<String, Integer> entry : supplies.entrySet())
        {
            if (entry.getValue() > 0 && entry.getKey().toLowerCase().startsWith("raw "))
                return true;
        }
        return false;
    }

    private static boolean hasAny(Map<String, Integer> supplies, String... items)
    {
        return TrainingSupplyCatalog.count(supplies, items) > 0;
    }

    private static String fletchingProduct(int level)
    {
        if (level >= 85) return "magic longbows (u)";
        if (level >= 80) return "magic shortbows (u)";
        if (level >= 70) return "yew longbows (u)";
        if (level >= 65) return "yew shortbows (u)";
        if (level >= 55) return "maple longbows (u)";
        if (level >= 50) return "maple shortbows (u)";
        if (level >= 40) return "willow longbows (u)";
        if (level >= 35) return "willow shortbows (u)";
        if (level >= 25) return "oak longbows (u)";
        if (level >= 20) return "oak shortbows (u)";
        return "arrow shafts";
    }
    private static int scaled(int hourly, int minutes)
    {
        int amount = Math.max(10, hourly * minutes / 60);
        return amount < 20 ? amount : (amount / 5) * 5;
    }
}
