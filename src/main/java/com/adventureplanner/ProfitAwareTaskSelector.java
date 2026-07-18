package com.adventureplanner;

import java.util.List;
import java.util.Random;

final class ProfitAwareTaskSelector
{
    private ProfitAwareTaskSelector() {}

    static BankTaskCandidate select(List<BankTaskCandidate> candidates,
        Random random, ItemPriceProvider prices)
    {
        int[] margins = new int[candidates.size()];
        boolean[] known = new boolean[candidates.size()];
        int totalWeight = 0;
        int[] weights = new int[candidates.size()];
        for (int i = 0; i < candidates.size(); i++)
        {
            Integer margin = marginPerUnit(candidates.get(i), prices);
            if (margin != null)
            {
                known[i] = true;
                margins[i] = margin;
                weights[i] = margin >= 0 ? 6 : 1;
            }
            else
            {
                weights[i] = 2;
            }
            totalWeight += weights[i];
        }

        int roll = random.nextInt(Math.max(1, totalWeight));
        int selectedIndex = 0;
        for (int i = 0; i < weights.length; i++)
        {
            roll -= weights[i];
            if (roll < 0)
            {
                selectedIndex = i;
                break;
            }
        }

        BankTaskCandidate selected = candidates.get(selectedIndex);
        if (!known[selectedIndex]) return selected;
        int margin = margins[selectedIndex];
        String economy = margin >= 0
            ? "geschatte GE-winst: +" + margin + " gp per eindproduct"
            : "geschatte GE-kosten: " + Math.abs(margin) + " gp per eindproduct";
        return new BankTaskCandidate(selected.getSkill(), selected.getTitle(),
            selected.getDetail() + " - " + economy);
    }

    static Integer marginPerUnit(BankTaskCandidate task, ItemPriceProvider prices)
    {
        String title = task.getTitle().toLowerCase();

        Integer bow = bowMargin(title, prices);
        if (bow != null) return bow;

        if (title.contains("jug of wine"))
            return margin(prices, "Jug of wine", "Grapes", "Jug of water");
        if (title.contains("raw shrimps"))
            return margin(prices, "Shrimps", "Raw shrimps");
        if (title.contains("raw salmon"))
            return margin(prices, "Salmon", "Raw salmon");
        if (title.contains("raw lobster"))
            return margin(prices, "Lobster", "Raw lobster");
        if (title.contains("raw monkfish"))
            return margin(prices, "Monkfish", "Raw monkfish");
        if (title.contains("raw shark"))
            return margin(prices, "Shark", "Raw shark");
        if (title.contains("raw anglerfish"))
            return margin(prices, "Anglerfish", "Raw anglerfish");

        if (title.contains("bronze bars"))
            return margin(prices, "Bronze bar", "Copper ore", "Tin ore");
        if (title.contains("iron bars"))
            return margin(prices, "Iron bar", "Iron ore");
        if (title.contains("iron platebodies"))
            return marginWithInputCount(prices, "Iron platebody", "Iron bar", 5);
        if (title.contains("steel platebodies"))
            return marginWithInputCount(prices, "Steel platebody", "Steel bar", 5);
        if (title.contains("mithril dart tips"))
            return marginPerTen(prices, "Mithril dart tip", "Mithril bar");

        if (title.contains("gold bracelets"))
            return margin(prices, "Gold bracelet", "Gold bar");
        if (title.contains("sapphire rings"))
            return margin(prices, "Sapphire ring", "Gold bar", "Sapphire");
        if (title.contains("glass orbs"))
            return margin(prices, "Unpowered orb", "Molten glass");
        if (title.contains("water battlestaves"))
            return margin(prices, "Water battlestaff", "Water orb", "Battlestaff");
        if (title.contains("green d'hide bodies"))
            return marginWithInputCount(prices, "Green d'hide body", "Green dragon leather", 3);
        if (title.contains("black d'hide bodies"))
            return marginWithInputCount(prices, "Black d'hide body", "Black dragon leather", 3);

        if (title.contains("attack potions") && !title.contains("super"))
            return margin(prices, "Attack potion(3)", "Guam potion (unf)", "Eye of newt");
        if (title.contains("prayer potions"))
            return margin(prices, "Prayer potion(3)", "Ranarr potion (unf)", "Snape grass");
        if (title.contains("super attack potions"))
            return margin(prices, "Super attack(3)", "Irit potion (unf)", "Eye of newt");
        if (title.contains("stamina potions"))
            return marginWithInputs(prices, "Stamina potion(4)",
                new String[] {"Super energy(4)", "Amylase crystal"},
                new int[] {1, 4});

        return null;
    }

    private static Integer bowMargin(String title, ItemPriceProvider prices)
    {
        if (title.contains("oak shortbows (u)"))
            return margin(prices, "Oak shortbow (u)", "Oak logs");
        if (title.contains("willow shortbows (u)"))
            return margin(prices, "Willow shortbow (u)", "Willow logs");
        if (title.contains("maple shortbows (u)"))
            return margin(prices, "Maple shortbow (u)", "Maple logs");
        if (title.contains("yew shortbows (u)"))
            return margin(prices, "Yew shortbow (u)", "Yew logs");
        if (title.contains("magic shortbows (u)"))
            return margin(prices, "Magic shortbow (u)", "Magic logs");
        if (title.contains("oak longbows (u)"))
            return margin(prices, "Oak longbow (u)", "Oak logs");
        if (title.contains("willow longbows (u)"))
            return margin(prices, "Willow longbow (u)", "Willow logs");
        if (title.contains("maple longbows (u)"))
            return margin(prices, "Maple longbow (u)", "Maple logs");
        if (title.contains("yew longbows (u)"))
            return margin(prices, "Yew longbow (u)", "Yew logs");
        if (title.contains("magic longbows (u)"))
            return margin(prices, "Magic longbow (u)", "Magic logs");
        return null;
    }

    private static Integer margin(ItemPriceProvider prices, String output, String... inputs)
    {
        int outputPrice = prices.price(output);
        if (outputPrice <= 0) return null;
        int inputPrice = 0;
        for (String input : inputs)
        {
            int price = prices.price(input);
            if (price <= 0) return null;
            inputPrice += price;
        }
        return outputPrice - inputPrice;
    }

    private static Integer marginWithInputCount(ItemPriceProvider prices,
        String output, String input, int inputCount)
    {
        return marginWithInputs(prices, output, new String[] {input}, new int[] {inputCount});
    }

    private static Integer marginPerTen(ItemPriceProvider prices,
        String output, String input)
    {
        int outputPrice = prices.price(output);
        int inputPrice = prices.price(input);
        if (outputPrice <= 0 || inputPrice <= 0) return null;
        return outputPrice - inputPrice / 10;
    }

    private static Integer marginWithInputs(ItemPriceProvider prices, String output,
        String[] inputs, int[] counts)
    {
        int outputPrice = prices.price(output);
        if (outputPrice <= 0) return null;
        int inputPrice = 0;
        for (int i = 0; i < inputs.length; i++)
        {
            int price = prices.price(inputs[i]);
            if (price <= 0) return null;
            inputPrice += price * counts[i];
        }
        return outputPrice - inputPrice;
    }
}
