package com.adventureplanner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import net.runelite.api.Skill;

public final class BankTaskCatalog
{
    private BankTaskCatalog() {}

    public static List<BankTaskCandidate> candidates(Map<String, Integer> bank, Map<Skill, Integer> levels)
    {
        List<BankTaskCandidate> result = new ArrayList<>();
        addBest(result, bank, levels, Skill.COOKING, 1, 25, "Kook", Arrays.asList(
            "Raw shrimps", "Raw anchovies", "Raw trout", "Raw salmon", "Raw lobster",
            "Raw swordfish", "Raw monkfish", "Raw karambwan", "Raw shark"));
        addWine(result, bank, levels);
        addBest(result, bank, levels, Skill.FLETCHING, 1, 25, "Fletch", Arrays.asList(
            "Logs", "Oak logs", "Willow logs", "Maple logs", "Yew logs", "Magic logs", "Redwood logs"));
        addBest(result, bank, levels, Skill.SMITHING, 1, 25, "Verwerk", Arrays.asList(
            "Bronze bar", "Iron bar", "Steel bar", "Mithril bar", "Adamantite bar", "Runite bar"));
        addBest(result, bank, levels, Skill.CRAFTING, 1, 20, "Slijp", Arrays.asList(
            "Uncut sapphire", "Uncut emerald", "Uncut ruby", "Uncut diamond", "Uncut dragonstone"));
        addBest(result, bank, levels, Skill.PRAYER, 1, 20, "Gebruik", Arrays.asList(
            "Bones", "Big bones", "Babydragon bones", "Dragon bones", "Wyvern bones", "Superior dragon bones"));
        addEnsouledHeads(result, bank, levels);
        addBest(result, bank, levels, Skill.HERBLORE, 3, 20, "Maak schoon", Arrays.asList(
            "Grimy guam leaf", "Grimy marrentill", "Grimy tarromin", "Grimy harralander", "Grimy ranarr weed",
            "Grimy toadflax", "Grimy irit leaf", "Grimy avantoe", "Grimy kwuarm", "Grimy snapdragon",
            "Grimy cadantine", "Grimy lantadyme", "Grimy dwarf weed", "Grimy torstol"));
        return result;
    }

    private static void addWine(List<BankTaskCandidate> result, Map<String, Integer> bank,
        Map<Skill, Integer> levels)
    {
        if (level(levels, Skill.COOKING) < 35 || level(levels, Skill.COOKING) >= 99) return;
        int amount = Math.min(bank.getOrDefault("Grapes", 0), bank.getOrDefault("Jug of water", 0));
        if (amount >= 20)
        {
            int taskAmount = Math.min(amount, 50);
            result.add(new BankTaskCandidate(Skill.COOKING,
                "Maak " + taskAmount + "× jug of wine",
                amount + " complete wijnsets in je bank · gebruikt druiven en jugs of water"));
        }
    }

    private static void addEnsouledHeads(List<BankTaskCandidate> result, Map<String, Integer> bank,
        Map<Skill, Integer> levels)
    {
        if (level(levels, Skill.PRAYER) >= 99) return;
        List<String> heads = Arrays.asList(
            "Ensouled goblin head", "Ensouled monkey head", "Ensouled imp head",
            "Ensouled minotaur head", "Ensouled scorpion head", "Ensouled bear head",
            "Ensouled unicorn head", "Ensouled dog head", "Ensouled chaos druid head",
            "Ensouled giant head", "Ensouled ogre head", "Ensouled elf head",
            "Ensouled troll head", "Ensouled horror head", "Ensouled kalphite head",
            "Ensouled dagannoth head", "Ensouled bloodveld head", "Ensouled tzhaar head",
            "Ensouled demon head", "Ensouled hellhound head", "Ensouled aviansie head",
            "Ensouled abyssal head", "Ensouled dragon head");
        String best = null;
        int bestAmount = 0;
        ReanimationTier bestTier = null;
        for (String head : heads)
        {
            ReanimationTier tier = reanimationTier(head);
            int possible = Math.min(bank.getOrDefault(head, 0), tier.castsFrom(bank));
            if (possible >= 5 && possible > bestAmount
                && level(levels, Skill.MAGIC) >= tier.magicLevel)
            {
                best = head;
                bestAmount = possible;
                bestTier = tier;
            }
        }
        if (best != null)
        {
            int taskAmount = Math.min(bestAmount, 25);
            result.add(new BankTaskCandidate(Skill.PRAYER,
                "Reanimateer en versla " + taskAmount + "× " + best,
                "Neem mee: " + bestTier.runesFor(taskAmount) + " · zet Arceuus spellbook aan"));
        }
    }

    private static ReanimationTier reanimationTier(String head)
    {
        if (head.contains("abyssal") || head.contains("aviansie") || head.contains("dragon"))
            return ReanimationTier.MASTER;
        if (head.contains("hellhound") || head.contains("demon") || head.contains("tzhaar")
            || head.contains("bloodveld") || head.contains("dagannoth") || head.contains("kalphite"))
            return ReanimationTier.EXPERT;
        if (head.contains("horror") || head.contains("troll") || head.contains("elf")
            || head.contains("ogre") || head.contains("giant") || head.contains("chaos druid")
            || head.contains("dog"))
            return ReanimationTier.ADEPT;
        return ReanimationTier.BASIC;
    }

    private enum ReanimationTier
    {
        BASIC(16, 4, 2, 0, 0),
        ADEPT(41, 4, 3, 1, 0),
        EXPERT(72, 0, 3, 2, 1),
        MASTER(90, 0, 4, 4, 2);

        private final int magicLevel;
        private final int body;
        private final int nature;
        private final int soul;
        private final int blood;

        ReanimationTier(int magicLevel, int body, int nature, int soul, int blood)
        {
            this.magicLevel = magicLevel;
            this.body = body;
            this.nature = nature;
            this.soul = soul;
            this.blood = blood;
        }

        private int castsFrom(Map<String, Integer> bank)
        {
            int casts = Integer.MAX_VALUE;
            if (body > 0) casts = Math.min(casts, bank.getOrDefault("Body rune", 0) / body);
            if (nature > 0) casts = Math.min(casts, bank.getOrDefault("Nature rune", 0) / nature);
            if (soul > 0) casts = Math.min(casts, bank.getOrDefault("Soul rune", 0) / soul);
            if (blood > 0) casts = Math.min(casts, bank.getOrDefault("Blood rune", 0) / blood);
            return casts;
        }

        private String runesFor(int casts)
        {
            List<String> runes = new ArrayList<>();
            if (body > 0) runes.add((body * casts) + " body");
            if (nature > 0) runes.add((nature * casts) + " nature");
            if (soul > 0) runes.add((soul * casts) + " soul");
            if (blood > 0) runes.add((blood * casts) + " blood");
            return String.join(", ", runes) + " runes";
        }
    }

    private static void addBest(List<BankTaskCandidate> result, Map<String, Integer> bank,
        Map<Skill, Integer> levels, Skill skill, int minimumLevel, int minimumAmount,
        String verb, List<String> materials)
    {
        if (level(levels, skill) < minimumLevel || level(levels, skill) >= 99) return;
        String best = null;
        int amount = 0;
        for (String material : materials)
        {
            int available = bank.getOrDefault(material, 0);
            if (level(levels, skill) >= requiredLevel(material)
                && available >= minimumAmount && available > amount)
            {
                best = material;
                amount = available;
            }
        }
        if (best != null)
        {
            int taskAmount = Math.min(amount, 50);
            result.add(new BankTaskCandidate(skill,
                verb + " " + taskAmount + "× " + best,
                amount + " in je bank · gebruikt bestaande voorraad"));
        }
    }

    private static int level(Map<Skill, Integer> levels, Skill skill)
    {
        return levels.getOrDefault(skill, 1);
    }

    private static int requiredLevel(String material)
    {
        switch (material)
        {
            case "Raw anchovies": return 1;
            case "Raw trout": return 15;
            case "Raw salmon": return 25;
            case "Raw lobster": return 40;
            case "Raw swordfish": return 45;
            case "Raw monkfish": return 62;
            case "Raw karambwan": return 30;
            case "Raw shark": return 80;
            case "Oak logs": return 15;
            case "Willow logs": return 30;
            case "Maple logs": return 45;
            case "Yew logs": return 60;
            case "Magic logs": return 75;
            case "Redwood logs": return 90;
            case "Iron bar": return 15;
            case "Steel bar": return 30;
            case "Mithril bar": return 50;
            case "Adamantite bar": return 70;
            case "Runite bar": return 85;
            case "Uncut emerald": return 27;
            case "Uncut ruby": return 34;
            case "Uncut diamond": return 43;
            case "Uncut dragonstone": return 55;
            case "Big bones": return 1;
            case "Babydragon bones": return 1;
            case "Dragon bones": return 1;
            case "Wyvern bones": return 1;
            case "Superior dragon bones": return 1;
            case "Grimy marrentill": return 5;
            case "Grimy tarromin": return 11;
            case "Grimy harralander": return 20;
            case "Grimy ranarr weed": return 25;
            case "Grimy toadflax": return 30;
            case "Grimy irit leaf": return 40;
            case "Grimy avantoe": return 48;
            case "Grimy kwuarm": return 54;
            case "Grimy snapdragon": return 59;
            case "Grimy cadantine": return 65;
            case "Grimy lantadyme": return 67;
            case "Grimy dwarf weed": return 70;
            case "Grimy torstol": return 75;
            default: return 1;
        }
    }
}
