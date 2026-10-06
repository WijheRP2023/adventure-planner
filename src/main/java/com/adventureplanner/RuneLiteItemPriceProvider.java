package com.adventureplanner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.runelite.client.game.ItemManager;
import net.runelite.http.api.item.ItemPrice;

final class RuneLiteItemPriceProvider implements ItemPriceProvider
{
    private final ItemManager itemManager;
    private final Map<String, Integer> cache = new HashMap<>();

    RuneLiteItemPriceProvider(ItemManager itemManager)
    {
        this.itemManager = itemManager;
    }

    @Override
    public int price(String itemName)
    {
        String key = itemName.toLowerCase();
        Integer cached = cache.get(key);
        if (cached != null) return cached;
        long price = 0;
        try
        {
            List<ItemPrice> matches = itemManager.search(itemName);
            for (ItemPrice match : matches)
            {
                if (!match.getName().equalsIgnoreCase(itemName)) continue;
                price = itemManager.getItemPrice(match.getId());
                if (price <= 0) price = match.getWikiPrice();
                if (price <= 0) price = match.getPrice();
                break;
            }
        }
        catch (RuntimeException ignored)
        {
            price = 0;
        }
        int safePrice = (int) Math.min(Integer.MAX_VALUE, Math.max(0L, price));
        cache.put(key, safePrice);
        return safePrice;
    }
}
