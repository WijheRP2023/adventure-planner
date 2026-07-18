package com.adventureplanner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.runelite.api.Skill;

public final class LevelTrainingCatalog
{
    private static final int[] VARIATION_PERCENTAGES =
        {55, 60, 65, 70, 75, 80, 85, 90, 95, 100, 105, 110};

    private static final List<TrainingMethod> METHODS = Collections.unmodifiableList(Arrays.asList(
        method(Skill.ATTACK, 1, "Versla", "sand crabs met accurate stijl", 85, "Gebruik stab of slash met je beste beschikbare wapen"),
        method(Skill.ATTACK, 40, "Versla", "ammonite crabs met accurate stijl", 95, "Fossil Island geeft een rustige melee-training"),
        method(Skill.ATTACK, 60, "Versla", "Scurrius met accurate stijl", 30, "Gebruik een rat-bane wapen zodra je dat hebt"),
        method(Skill.STRENGTH, 1, "Versla", "sand crabs met aggressive stijl", 85, "Zet je wapen op de Strength-trainingsstijl"),
        method(Skill.STRENGTH, 40, "Versla", "ammonite crabs met aggressive stijl", 95, "Fossil Island geeft een rustige melee-training"),
        method(Skill.STRENGTH, 60, "Versla", "Scurrius met aggressive stijl", 30, "Gebruik een rat-bane wapen zodra je dat hebt"),
        method(Skill.DEFENCE, 1, "Versla", "sand crabs met defensive stijl", 80, "Zet je wapen op de Defence-trainingsstijl"),
        method(Skill.DEFENCE, 40, "Versla", "ammonite crabs met defensive stijl", 90, "Fossil Island geeft een rustige melee-training"),
        method(Skill.DEFENCE, 60, "Versla", "Scurrius met defensive stijl", 30, "Gebruik een rat-bane wapen zodra je dat hebt"),
        method(Skill.HITPOINTS, 1, "Versla", "sand crabs", 85, "Train tegelijk je laagste bruikbare combatstat"),
        method(Skill.HITPOINTS, 40, "Versla", "ammonite crabs", 95, "Neem voedsel mee en train een combatstat naar keuze"),
        method(Skill.HITPOINTS, 60, "Versla", "Scurrius", 30, "Actieve Hitpoints-training met korte gevechten"),
        method(Skill.RANGED, 1, "Versla", "sand crabs met pijlen", 90, "Gebruik je beste shortbow en bestaande pijlen"),
        method(Skill.RANGED, 40, "Versla", "ammonite crabs met Ranged", 100, "Gebruik bestaande munitie uit de bank"),
        method(Skill.RANGED, 45, "Versla", "maniacal monkeys met chinchompas", 260, "Alleen kiezen als Monkey Madness II en voorraad beschikbaar zijn"),
        method(Skill.RANGED, 60, "Versla", "Scurrius met Ranged", 35, "Gebruik een bone shortbow zodra je dat hebt"),
        method(Skill.PRAYER, 1, "Begraaf", "big bones", 600, "Gebruik big bones uit bank of verzamel ze bij hill giants"),
        method(Skill.PRAYER, 30, "Verwerk", "calcified deposits", 220, "Prayer-training in Cam Torum met bone shards"),
        method(Skill.PRAYER, 43, "Bied", "dragon bones bij een gilded altar", 300, "Neem noted bones en teleports mee"),
        method(Skill.PRAYER, 16, "Reanimate", "ensouled goblin heads", 700, "Per head: 4 body en 2 nature runes; Basic Reanimation vereist 16 Magic"),
        method(Skill.PRAYER, 41, "Reanimate", "ensouled demon heads", 500, "Per head: 4 body, 3 nature en 1 soul rune; Adept Reanimation vereist 41 Magic"),
        method(Skill.PRAYER, 70, "Reanimate", "ensouled aviansie heads", 400, "Per head: 1 blood, 3 nature en 2 soul runes; Expert Reanimation vereist 72 Magic"),
        method(Skill.PRAYER, 72, "Reanimate", "ensouled bloodveld heads", 400, "Per head: 1 blood, 3 nature en 2 soul runes; Expert Reanimation vereist 72 Magic"),
        method(Skill.PRAYER, 90, "Reanimate", "ensouled dragon heads", 300, "Per head: 4 blood, 4 nature en 2 soul runes; Master Reanimation vereist 90 Magic"),
        method(Skill.MAGIC, 1, "Cast", "Fire Strikes op sand crabs", 900, "Neem mind, air en fire runes of een fire staff mee"),
        method(Skill.MAGIC, 33, "Verdien", "telekinetic MTA-punten", 25, "Gebruik Telekinetic Grab in de Mage Training Arena"),
        method(Skill.MAGIC, 55, "Cast", "High Level Alchemy-spreuken", 1100, "Kies een stapel alchbare bankitems en neem nature runes mee"),
        method(Skill.MAGIC, 70, "Versla", "dust devils met Ice Burst", 180, "Alleen met Ancient Magicks en een passende Slayer-locatie"),

        method(Skill.COOKING, 1, "Kook", "raw shrimps", 650, "Gebruik een range dicht bij een bank"),
        method(Skill.COOKING, 25, "Kook", "raw salmon", 700, "Gebruik je bankvoorraad of vang eerst salmon"),
        method(Skill.COOKING, 35, "Maak", "jugs of wine", 900, "Gebruik grapes en jugs of water"),
        method(Skill.COOKING, 40, "Kook", "raw lobsters", 650, "Gebruik je bankvoorraad of vang eerst lobsters"),
        method(Skill.COOKING, 62, "Kook", "raw monkfish", 700, "Gebruik je bankvoorraad of vang eerst monkfish"),
        method(Skill.COOKING, 84, "Kook", "raw anglerfish", 650, "Gebruik cooking gauntlets wanneer beschikbaar"),
        method(Skill.COOKING, 80, "Kook", "raw sharks", 700, "Gebruik cooking gauntlets wanneer beschikbaar"),
        method(Skill.WOODCUTTING, 1, "Hak", "regular logs", 260, "Hak gewone bomen op een plek naar keuze"),
        method(Skill.WOODCUTTING, 15, "Hak", "oak logs", 240, "Doe mee aan Forestry-events als die verschijnen"),
        method(Skill.WOODCUTTING, 30, "Hak", "willow logs", 300, "Draynor Village is een handige banklocatie"),
        method(Skill.WOODCUTTING, 35, "Hak", "teak logs", 260, "Gebruik Forestry of een ontgrendelde teak-locatie"),
        method(Skill.WOODCUTTING, 45, "Hak", "maple logs", 240, "Seers' Village is een handige banklocatie"),
        method(Skill.WOODCUTTING, 50, "Hak", "mahogany logs", 210, "Gebruik een beschikbare mahogany-locatie"),
        method(Skill.WOODCUTTING, 60, "Hak", "yew logs", 150, "Gebruik Forestry bij yew trees voor extra afwisseling"),
        method(Skill.WOODCUTTING, 75, "Hak", "magic logs", 110, "Kies een rustige magic-tree-locatie"),
        method(Skill.WOODCUTTING, 90, "Hak", "redwood logs", 170, "Hak redwoods in de Woodcutting Guild"),
        method(Skill.FLETCHING, 1, "Fletch", "arrow shafts", 1200, "Gebruik gewone logs uit de bank"),
        method(Skill.FLETCHING, 20, "Fletch", "oak shortbows (u)", 900, "Gebruik oak logs uit de bank"),
        method(Skill.FLETCHING, 35, "Fletch", "willow shortbows (u)", 900, "Gebruik willow logs uit de bank"),
        method(Skill.FLETCHING, 50, "Fletch", "maple shortbows (u)", 900, "Gebruik maple logs uit de bank"),
        method(Skill.FLETCHING, 65, "Fletch", "yew shortbows (u)", 900, "Gebruik yew logs uit de bank"),
        method(Skill.FLETCHING, 80, "Fletch", "magic shortbows (u)", 900, "Gebruik magic logs uit de bank"),
        method(Skill.FLETCHING, 25, "Fletch", "oak longbows (u)", 850, "Gebruik oak logs uit de bank"),
        method(Skill.FLETCHING, 40, "Fletch", "willow longbows (u)", 850, "Gebruik willow logs uit de bank"),
        method(Skill.FLETCHING, 55, "Fletch", "maple longbows (u)", 850, "Gebruik maple logs uit de bank"),
        method(Skill.FLETCHING, 70, "Fletch", "yew longbows (u)", 850, "Gebruik yew logs uit de bank"),
        method(Skill.FLETCHING, 85, "Fletch", "magic longbows (u)", 850, "Gebruik magic logs uit de bank"),

        method(Skill.FISHING, 1, "Vis", "shrimps of anchovies", 220, "Korte rustige Fishing-taak"),
        method(Skill.FISHING, 20, "Vis", "trout en salmon", 420, "Fly fishing voor actieve XP"),
        method(Skill.FISHING, 40, "Vis", "lobsters", 180, "Gebruik een lobster pot"),
        method(Skill.FISHING, 62, "Vis", "monkfish", 300, "Vereist toegang tot Piscatoris"),
        method(Skill.FISHING, 65, "Vis", "karambwans", 520, "Neem karambwanji en een karambwan vessel mee"),
        method(Skill.FISHING, 76, "Vis", "sharks", 150, "Harpoon fishing; 120 minuten kan ongeveer 300 opleveren"),
        method(Skill.FISHING, 82, "Vis", "anglerfish", 130, "Neem sandworms mee"),
        method(Skill.FISHING, 82, "Vis", "minnows", 600, "Ruil minnows later in voor sharks"),
        method(Skill.FISHING, 15, "Vang", "raw sea turtles via Fishing Trawler", 18, "Tel alleen de vangst uit korte Trawler-runs"),

        method(Skill.FIREMAKING, 1, "Verbrand", "regular logs", 750, "Gebruik gewone logs uit de bank"),
        method(Skill.FIREMAKING, 15, "Verbrand", "oak logs", 750, "Gebruik oak logs uit de bank"),
        method(Skill.FIREMAKING, 30, "Verbrand", "willow logs", 750, "Gebruik willow logs uit de bank"),
        method(Skill.FIREMAKING, 45, "Verbrand", "maple logs", 750, "Gebruik maple logs uit de bank"),
        method(Skill.FIREMAKING, 50, "Voltooi", "Wintertodt-games", 3, "Stop na de ingestelde taakduur"),
        method(Skill.FIREMAKING, 60, "Verbrand", "yew logs", 750, "Gebruik yew logs uit de bank"),
        method(Skill.FIREMAKING, 75, "Verbrand", "magic logs", 750, "Gebruik magic logs uit de bank"),
        method(Skill.CRAFTING, 1, "Maak", "leather gloves", 450, "Neem leather, thread en een needle mee"),
        method(Skill.CRAFTING, 7, "Maak", "gold bracelets", 900, "Neem gold bars en een bracelet mould mee"),
        method(Skill.CRAFTING, 20, "Maak", "sapphire rings", 800, "Neem gold bars, sapphires en een ring mould mee"),
        method(Skill.CRAFTING, 46, "Blaas", "glass orbs", 650, "Gebruik molten glass en een glassblowing pipe"),
        method(Skill.CRAFTING, 54, "Maak", "water battlestaves", 2450, "Gebruik water orbs en battlestaves uit de bank"),
        method(Skill.CRAFTING, 63, "Maak", "green d'hide bodies", 1650, "Neem green dragon leather, thread en een needle mee"),
        method(Skill.CRAFTING, 84, "Maak", "black d'hide bodies", 1650, "Neem black dragon leather, thread en een needle mee"),
        method(Skill.SMITHING, 1, "Smelt", "bronze bars", 700, "Neem copper en tin ore mee naar een furnace"),
        method(Skill.SMITHING, 15, "Smelt", "iron bars", 900, "Neem iron ore mee naar een furnace"),
        method(Skill.SMITHING, 33, "Smeed", "iron platebodies", 220, "Gebruik vijf iron bars per platebody"),
        method(Skill.SMITHING, 48, "Smeed", "steel platebodies", 220, "Gebruik vijf steel bars per platebody"),
        method(Skill.SMITHING, 54, "Smeed", "mithril dart tips", 1400, "Gebruik mithril bars en voltooi The Tourist Trap"),
        method(Skill.SMITHING, 15, "Maak", "Giants' Foundry-swords", 5, "Kies een passende alloy uit je voorraad"),
        method(Skill.MINING, 1, "Mine", "copper ore", 300, "Gebruik een copper-rock-locatie dicht bij een bank"),
        method(Skill.MINING, 15, "Mine", "iron ore", 650, "Mining Guild of Ardougne Monastery werkt goed"),
        method(Skill.MINING, 30, "Mine", "pay-dirt", 230, "Korte Motherlode Mine-taak"),
        method(Skill.MINING, 35, "Mine", "sandstone", 900, "Gebruik waterskins of Desert heat-bescherming"),
        method(Skill.MINING, 40, "Mine", "gem rocks", 500, "Gebruik Shilo Village of de gem mine"),
        method(Skill.MINING, 10, "Mine", "Shooting Star-lagen", 55, "Verzamel stardust in een korte sessie"),
        method(Skill.MINING, 92, "Mine", "amethyst", 110, "Mine amethyst in de Mining Guild"),
        method(Skill.HERBLORE, 3, "Maak", "attack potions", 700, "Gebruik guam potions (unf) en eyes of newt"),
        method(Skill.HERBLORE, 38, "Maak", "prayer potions", 700, "Gebruik ranarr potions (unf) en snape grass"),
        method(Skill.HERBLORE, 45, "Maak", "super attack potions", 700, "Gebruik irit potions (unf) en eyes of newt"),
        method(Skill.HERBLORE, 60, "Maak", "Mixology-pastes", 120, "Wissel recepten af in Mastering Mixology"),
        method(Skill.HERBLORE, 77, "Maak", "stamina potions", 700, "Gebruik super energy potions en amylase crystals"),
        method(Skill.AGILITY, 1, "Voltooi", "Gnome Stronghold-rondjes", 35, "Gebruik de basis Agility Course bij de Tree Gnome Stronghold"),
        method(Skill.AGILITY, 10, "Voltooi", "Draynor rooftop-rondjes", 40, "Verzamel marks of grace onderweg"),
        method(Skill.AGILITY, 30, "Voltooi", "Varrock rooftop-rondjes", 38, "Verzamel marks of grace onderweg"),
        method(Skill.AGILITY, 40, "Voltooi", "Canifis rooftop-rondjes", 45, "Verzamel marks of grace onderweg"),
        method(Skill.AGILITY, 52, "Voltooi", "Sepulchre-obstakels", 70, "Alleen wanneer Darkmeyer toegankelijk is"),
        method(Skill.AGILITY, 60, "Voltooi", "Seers' Village rooftop-rondjes", 45, "Gebruik Kandarin Diary-teleport indien beschikbaar"),
        method(Skill.AGILITY, 80, "Voltooi", "Rellekka rooftop-rondjes", 38, "Verzamel marks of grace onderweg"),
        method(Skill.THIEVING, 1, "Beroof", "men of women", 500, "Begin met een eenvoudig pickpocket-target"),
        method(Skill.THIEVING, 20, "Steel", "silk stall-voorraad", 800, "Gebruik de silk stall in Ardougne"),
        method(Skill.THIEVING, 25, "Steel", "fruit stall-voorraad", 1200, "Gebruik Hosidius fruit stalls"),
        method(Skill.THIEVING, 38, "Beroof", "master farmers", 700, "Bewaar nuttige herb seeds"),
        method(Skill.THIEVING, 55, "Beroof", "Ardougne knights", 900, "Gebruik dodgy necklaces en food"),
        method(Skill.THIEVING, 82, "Beroof", "vyres", 500, "Gebruik dodgy necklaces en Shadow Veil"),
        method(Skill.THIEVING, 85, "Beroof", "elves", 450, "Gebruik dodgy necklaces en Shadow Veil"),
        method(Skill.THIEVING, 21, "Voltooi", "Pyramid Plunder-kamers", 35, "Korte actieve Thieving-rondes"),
        method(Skill.SLAYER, 1, "Versla", "monsters van je Slayer assignment", 90, "Ga door met je huidige task of haal een nieuwe; wisselen blijft toegestaan"),
        method(Skill.FARMING, 1, "Oogst", "potatoes uit allotment patches", 160, "Plant potato seeds en werk de beschikbare allotments af"),
        method(Skill.FARMING, 34, "Plant", "Golovanova Tithe-seeds", 200, "Gebruik de level-34 Tithe Farm-seeds"),
        method(Skill.FARMING, 54, "Plant", "Bologano Tithe-seeds", 200, "Gebruik de level-54 Tithe Farm-seeds"),
        method(Skill.FARMING, 74, "Plant", "Logavano Tithe-seeds", 200, "Gebruik de level-74 Tithe Farm-seeds"),
        method(Skill.RUNECRAFT, 1, "Maak", "air runes", 900, "Neem rune essence en een air talisman of tiara mee"),
        method(Skill.RUNECRAFT, 23, "Maak", "lava runes", 1200, "Neem earth runes, pure essence en binding necklaces mee"),
        method(Skill.RUNECRAFT, 27, "Voltooi", "Guardians of the Rift-games", 4, "Korte Runecraft-minigameblokken"),
        method(Skill.RUNECRAFT, 44, "Maak", "nature runes", 900, "Gebruik de Nature altar of Abyss"),
        method(Skill.RUNECRAFT, 77, "Maak", "blood runes", 700, "Gebruik het Arceuus blood altar"),
        method(Skill.HUNTER, 1, "Vang", "crimson swifts", 180, "Neem een bird snare mee naar Feldip Hills"),
        method(Skill.HUNTER, 7, "Vang", "feldip weasels", 160, "Neem een noose wand mee"),
        method(Skill.HUNTER, 46, "Voltooi", "Hunter Rumours", 2, "Gebruik de contractlocatie en benodigdheden"),
        method(Skill.HUNTER, 47, "Vang", "orange salamanders", 180, "Neem ropes en small fishing nets mee"),
        method(Skill.CONSTRUCTION, 1, "Voltooi", "Beginner Mahogany Homes-contracten", 12, "Neem gewone planks, een saw, hammer en steel bars mee"),
        method(Skill.CONSTRUCTION, 20, "Voltooi", "Novice Mahogany Homes-contracten", 11, "Neem oak planks, een saw, hammer en steel bars mee"),
        method(Skill.CONSTRUCTION, 33, "Bouw", "oak larders", 420, "Gebruik oak planks in de kitchen van je POH"),
        method(Skill.CONSTRUCTION, 50, "Voltooi", "Adept Mahogany Homes-contracten", 10, "Neem teak planks, een saw, hammer en steel bars mee"),
        method(Skill.CONSTRUCTION, 50, "Bouw", "POH-upgrades", 80, "Werk aan portals, jewellery box of nuttige kamers"),
        method(Skill.CONSTRUCTION, 52, "Bouw", "mahogany tables", 500, "Gebruik mahogany planks in de dining room van je POH"),
        method(Skill.CONSTRUCTION, 66, "Bouw", "teak garden benches", 600, "Gebruik teak planks in de superior garden van je POH"),
        method(Skill.CONSTRUCTION, 70, "Voltooi", "Expert Mahogany Homes-contracten", 9, "Neem mahogany planks, een saw, hammer en steel bars mee"),
        method(Skill.SAILING, 1, "Voltooi", "Port Tasks", 6, "Kies korte courier- of bounty-taken"),
        method(Skill.SAILING, 15, "Berg", "shipwreck-onderdelen", 120, "Shipwreck Salvaging voor afwisseling")
    ));

    private LevelTrainingCatalog() {}

    public static List<BankTaskCandidate> candidates(Map<Skill, Integer> levels,
        List<Skill> excluded, int minutes, Random random)
    {
        return candidates(levels, excluded, minutes, random, Collections.emptyMap(), false);
    }

    public static List<BankTaskCandidate> candidates(Map<Skill, Integer> levels,
        List<Skill> excluded, int minutes, Random random, Map<String, Integer> supplies)
    {
        return candidates(levels, excluded, minutes, random, supplies, true);
    }

    private static List<BankTaskCandidate> candidates(Map<Skill, Integer> levels,
        List<Skill> excluded, int minutes, Random random, Map<String, Integer> supplies,
        boolean enforceSupplies)
    {
        List<BankTaskCandidate> result = new ArrayList<>();
        java.util.Map<Skill, Integer> highestUnlocked = new java.util.EnumMap<>(Skill.class);
        for (TrainingMethod method : METHODS)
        {
            int level = levels.getOrDefault(method.getSkill(), 1);
            if (level >= method.getMinimumLevel())
            {
                highestUnlocked.merge(method.getSkill(), method.getMinimumLevel(), Math::max);
            }
        }
        for (TrainingMethod method : METHODS)
        {
            int level = levels.getOrDefault(method.getSkill(), 1);
            if (level < method.getMinimumLevel() || level >= 99 || excluded.contains(method.getSkill())) continue;
            int bestTier = highestUnlocked.getOrDefault(method.getSkill(), method.getMinimumLevel());
            if (method.getMinimumLevel() < bestTier - 25) continue;
            int percentage = VARIATION_PERCENTAGES[random.nextInt(VARIATION_PERCENTAGES.length)];
            int quantity = quantity(method.getUnitsPerHour(), minutes, percentage);
            int available = enforceSupplies
                ? TrainingSupplyCatalog.availableUnits(method, supplies) : -1;
            if (available == 0) continue;
            if (available > 0) quantity = Math.min(quantity, available);
            String supplyNote = available >= 0
                ? " - maximaal " + quantity + " met huidige bank/inventory" : "";
            result.add(new BankTaskCandidate(method.getSkill(),
                method.getVerb() + " " + quantity + " " + method.getTarget(),
                method.getDetail() + " - level " + method.getMinimumLevel() + "+" + supplyNote));
        }
        addMeleeMonster(result, levels, excluded, minutes);
        return result;
    }

    public static int estimatedVariantCount()
    {
        return METHODS.size() * VARIATION_PERCENTAGES.length;
    }

    static String meleeMonster(Map<Skill, Integer> levels)
    {
        int average = (levels.getOrDefault(Skill.ATTACK, 1)
            + levels.getOrDefault(Skill.STRENGTH, 1)
            + levels.getOrDefault(Skill.DEFENCE, 1)) / 3;
        if (average < 20) return "cows";
        if (average < 35) return "hill giants";
        if (average < 50) return "moss giants";
        if (average < 65) return "ice trolls";
        if (average < 75) return "Scurrius";
        if (average < 85) return "Giant Mole";
        return "Sarachnis";
    }

    private static void addMeleeMonster(List<BankTaskCandidate> result, Map<Skill, Integer> levels,
        List<Skill> excluded, int minutes)
    {
        if (excluded.contains(Skill.ATTACK) && excluded.contains(Skill.STRENGTH)
            && excluded.contains(Skill.DEFENCE)) return;
        Skill skill = !excluded.contains(Skill.ATTACK) ? Skill.ATTACK
            : (!excluded.contains(Skill.STRENGTH) ? Skill.STRENGTH : Skill.DEFENCE);
        if (levels.getOrDefault(skill, 1) >= 99) return;
        int amount = Math.max(10, minutes * 2);
        result.add(new BankTaskCandidate(skill, "Versla " + amount + " x " + meleeMonster(levels),
            "Monster gekozen op basis van Attack, Strength en Defence"));
    }

    private static int quantity(int hourly, int minutes, int percentage)
    {
        int raw = Math.max(1, hourly * minutes * percentage / 6000);
        if (raw < 10) return raw;
        return Math.max(5, (raw / 5) * 5);
    }

    private static TrainingMethod method(Skill skill, int level, String verb, String target,
        int hourly, String detail)
    {
        return new TrainingMethod(skill, level, verb, target, hourly, detail);
    }
    public static BankTaskCandidate randomCandidateFor(Skill skill, Map<Skill, Integer> levels,
        int minutes, Random random)
    {
        return randomCandidateFor(skill, levels, minutes, random, Collections.emptyMap(), false);
    }

    public static BankTaskCandidate randomCandidateFor(Skill skill, Map<Skill, Integer> levels,
        int minutes, Random random, Map<String, Integer> supplies)
    {
        return randomCandidateFor(skill, levels, minutes, random, supplies, true);
    }

    private static BankTaskCandidate randomCandidateFor(Skill skill, Map<Skill, Integer> levels,
        int minutes, Random random, Map<String, Integer> supplies, boolean enforceSupplies)
    {
        List<BankTaskCandidate> matching = new ArrayList<>();
        List<BankTaskCandidate> available = enforceSupplies
            ? candidates(levels, Collections.emptyList(), minutes, random, supplies)
            : candidates(levels, Collections.emptyList(), minutes, random);
        for (BankTaskCandidate candidate : available)
        {
            if (candidate.getSkill() == skill) matching.add(candidate);
        }
        if (!matching.isEmpty()) return matching.get(random.nextInt(matching.size()));
        if (skill == Skill.MINING && !enforceSupplies)
        {
            return miningFallback(levels, minutes, random);
        }
        return null;
    }

    private static BankTaskCandidate miningFallback(Map<Skill, Integer> levels,
        int minutes, Random random)
    {
        int level = levels.getOrDefault(Skill.MINING, 1);
        String target;
        String detail;
        int hourly;
        if (level >= 92)
        {
            target = "amethyst";
            detail = "Mine amethyst in de Mining Guild";
            hourly = 110;
        }
        else if (level >= 40)
        {
            target = "gem rocks";
            detail = "Gebruik Shilo Village of de gem mine";
            hourly = 500;
        }
        else if (level >= 30)
        {
            target = "pay-dirt";
            detail = "Doe een korte Motherlode Mine-sessie";
            hourly = 230;
        }
        else if (level >= 15)
        {
            target = "iron ore";
            detail = "Gebruik de Mining Guild of Ardougne Monastery";
            hourly = 650;
        }
        else
        {
            target = "copper ore";
            detail = "Gebruik een copper-rock-locatie dicht bij een bank";
            hourly = 300;
        }
        int percentage = VARIATION_PERCENTAGES[random.nextInt(VARIATION_PERCENTAGES.length)];
        int amount = quantity(hourly, minutes, percentage);
        return new BankTaskCandidate(Skill.MINING, "Mine " + amount + " " + target,
            detail + " - concrete Mining-noodopdracht");
    }

}
