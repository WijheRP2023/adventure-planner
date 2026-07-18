package com.adventureplanner;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import net.runelite.api.ItemID;
import net.runelite.client.game.ItemManager;

final class TaskReminderIconFactory
{
    private TaskReminderIconFactory() {}

    static BufferedImage icon(PlannerTask task, ItemManager itemManager)
    {
        int itemId = itemId(task.getId());
        if (itemId > 0)
        {
            return itemManager.getImage(itemId);
        }
        return badge(task);
    }

    static int itemId(String taskId)
    {
        switch (taskId)
        {
            case "daily_battlestaves": return ItemID.BATTLESTAFF;
            case "daily_sand": return ItemID.BUCKET_OF_SAND;
            case "daily_dynamite": return ItemID.DYNAMITE;
            case "daily_essence": return ItemID.PURE_ESSENCE;
            case "daily_arrows": return ItemID.OGRE_ARROW;
            case "daily_bonemeal": return ItemID.BONEMEAL;
            case "daily_contract": return ItemID.SEED_DIBBER;
            case "daily_rumour": return ItemID.BIRD_SNARE;
            case "weekly_tears": return ItemID.BOWL_OF_WATER;
            case "weekly_kingdom": return ItemID.COINS_995;
            case "weekly_minigame": return ItemID.CASTLE_WARS_TICKET;
            case "weekly_boss": return ItemID.SKULL;
            default: return -1;
        }
    }

    private static BufferedImage badge(PlannerTask task)
    {
        BufferedImage image = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color accent = task.getCadence() == TaskCadence.DAILY
            ? new Color(232, 160, 48) : new Color(105, 184, 220);
        graphics.setColor(new Color(36, 34, 31, 245));
        graphics.fillRoundRect(1, 1, 30, 30, 8, 8);
        graphics.setColor(accent);
        graphics.setStroke(new BasicStroke(2f));
        graphics.drawRoundRect(2, 2, 28, 28, 7, 7);

        String symbol = initials(task.getTitle());
        graphics.setFont(new Font(Font.SANS_SERIF, Font.BOLD, symbol.length() > 2 ? 10 : 12));
        FontMetrics metrics = graphics.getFontMetrics();
        int x = (32 - metrics.stringWidth(symbol)) / 2;
        int y = (32 - metrics.getHeight()) / 2 + metrics.getAscent();
        graphics.drawString(symbol, x, y);
        graphics.dispose();
        return image;
    }

    private static String initials(String title)
    {
        StringBuilder result = new StringBuilder(2);
        for (String word : title.trim().split("\\s+"))
        {
            if (!word.isEmpty() && Character.isLetterOrDigit(word.charAt(0)))
            {
                result.append(Character.toUpperCase(word.charAt(0)));
                if (result.length() == 2) break;
            }
        }
        return result.length() == 0 ? "AP" : result.toString();
    }
}
