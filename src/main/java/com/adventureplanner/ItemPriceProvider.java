package com.adventureplanner;

interface ItemPriceProvider
{
    ItemPriceProvider NONE = itemName -> 0;

    int price(String itemName);
}
