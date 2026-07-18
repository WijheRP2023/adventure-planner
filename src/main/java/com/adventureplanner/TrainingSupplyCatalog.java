package com.adventureplanner;

import java.util.Map;
import net.runelite.api.Skill;

final class TrainingSupplyCatalog
{
    private TrainingSupplyCatalog() {}

    /**
     * Returns -1 when the method gathers/provides its own materials, otherwise
     * the maximum number of task units supported by bank plus inventory.
     */
    static int availableUnits(TrainingMethod method, Map<String, Integer> supplies)
    {
        Skill skill = method.getSkill();
        String target = method.getTarget().toLowerCase();

        if (skill == Skill.FLETCHING)
        {
            if (target.contains("arrow shafts")) return count(supplies, "Logs") * 15;
            if (target.contains("oak ") && target.contains("bows")) return count(supplies, "Oak logs");
            if (target.contains("willow ") && target.contains("bows")) return count(supplies, "Willow logs");
            if (target.contains("maple ") && target.contains("bows")) return count(supplies, "Maple logs");
            if (target.contains("yew ") && target.contains("bows")) return count(supplies, "Yew logs");
            if (target.contains("magic ") && target.contains("bows")) return count(supplies, "Magic logs");
            return 0;
        }

        if (skill == Skill.COOKING)
        {
            if (target.contains("jugs of wine"))
            {
                return Math.min(count(supplies, "Grapes"), count(supplies, "Jug of water"));
            }
            if (target.startsWith("raw "))
            {
                return count(supplies, target);
            }
            return 0;
        }

        if (skill == Skill.FIREMAKING)
        {
            if (target.contains("wintertodt")) return -1;
            if (target.contains("regular logs")) return count(supplies, "Logs");
            if (target.contains("oak logs")) return count(supplies, "Oak logs");
            if (target.contains("willow logs")) return count(supplies, "Willow logs");
            if (target.contains("maple logs")) return count(supplies, "Maple logs");
            if (target.contains("yew logs")) return count(supplies, "Yew logs");
            if (target.contains("magic logs")) return count(supplies, "Magic logs");
            return 0;
        }

        if (skill == Skill.SMITHING)
        {
            if (target.contains("bronze bars"))
            {
                return Math.min(count(supplies, "Copper ore"), count(supplies, "Tin ore"));
            }
            if (target.contains("iron bars")) return count(supplies, "Iron ore");
            if (target.contains("iron platebodies")) return count(supplies, "Iron bar") / 5;
            if (target.contains("steel platebodies")) return count(supplies, "Steel bar") / 5;
            if (target.contains("mithril dart tips")) return count(supplies, "Mithril bar") * 10;
            if (target.contains("giants' foundry"))
            {
                return totalBars(supplies) / 20;
            }
            return 0;
        }

        if (skill == Skill.CRAFTING)
        {
            if (target.contains("leather gloves"))
            {
                if (count(supplies, "Needle") <= 0) return 0;
                return Math.min(count(supplies, "Leather"), count(supplies, "Thread") * 5);
            }
            if (target.contains("gold bracelets"))
            {
                return count(supplies, "Bracelet mould") > 0 ? count(supplies, "Gold bar") : 0;
            }
            if (target.contains("sapphire rings"))
            {
                if (count(supplies, "Ring mould") <= 0) return 0;
                return Math.min(count(supplies, "Gold bar"), count(supplies, "Sapphire"));
            }
            if (target.contains("glass orbs"))
            {
                return count(supplies, "Glassblowing pipe") > 0
                    ? count(supplies, "Molten glass") : 0;
            }
            if (target.contains("water battlestaves"))
            {
                return Math.min(count(supplies, "Water orb"), count(supplies, "Battlestaff"));
            }
            if (target.contains("green d'hide bodies"))
            {
                return count(supplies, "Green dragon leather") / 3;
            }
            if (target.contains("black d'hide bodies"))
            {
                return count(supplies, "Black dragon leather") / 3;
            }
            return 0;
        }

        if (skill == Skill.HERBLORE)
        {
            if (target.contains("attack potions") && !target.contains("super"))
            {
                return Math.min(count(supplies, "Guam potion (unf)"),
                    count(supplies, "Eye of newt"));
            }
            if (target.contains("prayer potions"))
            {
                return Math.min(count(supplies, "Ranarr potion (unf)"),
                    count(supplies, "Snape grass"));
            }
            if (target.contains("super attack potions"))
            {
                return Math.min(count(supplies, "Irit potion (unf)"),
                    count(supplies, "Eye of newt"));
            }
            if (target.contains("mixology"))
            {
                return totalCleanHerbs(supplies) / 3;
            }
            if (target.contains("stamina potions"))
            {
                return Math.min(count(supplies, "Super energy(4)"),
                    count(supplies, "Amylase crystal") / 4);
            }
            return 0;
        }

        if (skill == Skill.PRAYER)
        {
            if (target.contains("big bones")) return count(supplies, "Big bones");
            if (target.contains("dragon bones")) return count(supplies, "Dragon bones");
            if (target.contains("calcified deposits"))
            {
                return count(supplies, "Calcified deposit", "Calcified deposits");
            }
            if (target.contains("ensouled goblin heads"))
            {
                return minimum(count(supplies, "Ensouled goblin head"),
                    count(supplies, "Body rune") / 4, count(supplies, "Nature rune") / 2);
            }
            if (target.contains("ensouled demon heads"))
            {
                return minimum(count(supplies, "Ensouled demon head"),
                    count(supplies, "Body rune") / 4, count(supplies, "Nature rune") / 3,
                    count(supplies, "Soul rune"));
            }
            if (target.contains("ensouled aviansie heads"))
            {
                return minimum(count(supplies, "Ensouled aviansie head"), count(supplies, "Blood rune"),
                    count(supplies, "Nature rune") / 3, count(supplies, "Soul rune") / 2);
            }
            if (target.contains("ensouled bloodveld heads"))
            {
                return minimum(count(supplies, "Ensouled bloodveld head"), count(supplies, "Blood rune"),
                    count(supplies, "Nature rune") / 3, count(supplies, "Soul rune") / 2);
            }
            if (target.contains("ensouled dragon heads"))
            {
                return minimum(count(supplies, "Ensouled dragon head"), count(supplies, "Blood rune") / 4,
                    count(supplies, "Nature rune") / 4, count(supplies, "Soul rune") / 2);
            }
            return 0;
        }

        if (skill == Skill.RUNECRAFT)
        {
            if (target.contains("guardians of the rift")) return -1;
            int essence = count(supplies, "Rune essence", "Pure essence", "Daeyalt essence");
            if (target.contains("lava runes"))
            {
                essence = Math.min(essence, count(supplies, "Earth rune"));
            }
            return essence;
        }

        if (skill == Skill.CONSTRUCTION)
        {
            if (target.contains("beginner mahogany homes"))
                return count(supplies, "Plank") / 20;
            if (target.contains("novice mahogany homes"))
                return count(supplies, "Oak plank") / 20;
            if (target.contains("adept mahogany homes"))
                return count(supplies, "Teak plank") / 20;
            if (target.contains("expert mahogany homes"))
                return count(supplies, "Mahogany plank") / 20;
            if (target.contains("oak larders")) return count(supplies, "Oak plank") / 8;
            if (target.contains("mahogany tables")) return count(supplies, "Mahogany plank") / 6;
            if (target.contains("teak garden benches")) return count(supplies, "Teak plank") / 6;
            if (target.contains("poh-upgrades")) return totalPlanks(supplies) / 10;
            return 0;
        }

        return -1;
    }

    static int count(Map<String, Integer> supplies, String... itemNames)
    {
        int amount = 0;
        for (Map.Entry<String, Integer> entry : supplies.entrySet())
        {
            for (String itemName : itemNames)
            {
                if (entry.getKey().equalsIgnoreCase(itemName))
                {
                    amount += Math.max(0, entry.getValue());
                    break;
                }
            }
        }
        return amount;
    }

    private static int totalBars(Map<String, Integer> supplies)
    {
        int amount = 0;
        for (Map.Entry<String, Integer> entry : supplies.entrySet())
        {
            if (entry.getKey().toLowerCase().endsWith(" bar"))
            {
                amount += Math.max(0, entry.getValue());
            }
        }
        return amount;
    }

    private static int totalPlanks(Map<String, Integer> supplies)
    {
        return count(supplies, "Plank", "Oak plank", "Teak plank", "Mahogany plank");
    }

    private static int totalCleanHerbs(Map<String, Integer> supplies)
    {
        int amount = 0;
        for (Map.Entry<String, Integer> entry : supplies.entrySet())
        {
            String name = entry.getKey().toLowerCase();
            if ((name.contains("guam") || name.contains("marrentill")
                || name.contains("tarromin") || name.contains("harralander")
                || name.contains("ranarr") || name.contains("toadflax")
                || name.contains("irit") || name.contains("avantoe")
                || name.contains("kwuarm") || name.contains("snapdragon")
                || name.contains("cadantine") || name.contains("lantadyme")
                || name.contains("dwarf weed") || name.contains("torstol"))
                && !name.startsWith("grimy "))
            {
                amount += Math.max(0, entry.getValue());
            }
        }
        return amount;
    }

    private static int minimum(int... amounts)
    {
        int minimum = Integer.MAX_VALUE;
        for (int amount : amounts)
        {
            minimum = Math.min(minimum, amount);
        }
        return minimum == Integer.MAX_VALUE ? 0 : minimum;
    }
}
