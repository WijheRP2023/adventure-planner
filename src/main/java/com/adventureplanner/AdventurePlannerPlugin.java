package com.adventureplanner;

import com.google.gson.Gson;
import com.google.inject.Provides;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.inject.Inject;
import javax.swing.SwingUtilities;
import net.runelite.api.Client;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.Skill;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.StatChanged;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.NpcLootReceived;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.overlay.worldmap.WorldMapPointManager;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;

@PluginDescriptor(
    name = "Adventure Planner",
    description = "Short, varied account progression tasks based on levels and local bank supplies",
    tags = {"tasks", "diary", "daily", "weekly", "skilling", "maxing"}
)
public class AdventurePlannerPlugin extends Plugin
{
    @Inject private Client client;
    @Inject private ClientThread clientThread;
    @Inject private ClientToolbar clientToolbar;
    @Inject private ConfigManager configManager;
    @Inject private AdventurePlannerConfig config;
    @Inject private Gson gson;
    @Inject private WorldMapPointManager worldMapPointManager;
    @Inject private OverlayManager overlayManager;
    @Inject private InfoBoxManager infoBoxManager;
    @Inject private ItemManager itemManager;

    private NavigationButton navigationButton;
    private AdventurePlannerPanel panel;
    private PlannerState state;
    private RecurringActivityManager recurringActivityManager;
    private AutomaticChecklistManager automaticChecklistManager;
    private RunNavigator runNavigator;
    private AdventurePlannerOverlay overlay;
    private TaskReminderManager taskReminderManager;
    private ShootingStarTracker shootingStarTracker;
    private final Map<Skill, Integer> lastSkillXp = new EnumMap<>(Skill.class);
    private long inventorySeededTaskEndsAt;

    @Provides
    AdventurePlannerConfig provideConfig(ConfigManager manager)
    {
        return manager.getConfig(AdventurePlannerConfig.class);
    }

    @Override
    protected void startUp()
    {
        SwingUtilities.invokeLater(() -> {
            state = new PlannerState(configManager, gson);
            taskReminderManager = new TaskReminderManager(
                this, infoBoxManager, itemManager, config, state);
            overlay = new AdventurePlannerOverlay(this, config, state);
            overlayManager.add(overlay);
            runNavigator = new RunNavigator(client, worldMapPointManager);
            shootingStarTracker = new ShootingStarTracker(client);
            panel = new AdventurePlannerPanel(client, clientThread, config, state,
                runNavigator, itemManager, shootingStarTracker);
            recurringActivityManager = new RecurringActivityManager(client, config, state,
                () -> SwingUtilities.invokeLater(() -> {
                    if (panel != null) panel.refresh();
                }));
            automaticChecklistManager = new AutomaticChecklistManager(client, state,
                () -> SwingUtilities.invokeLater(() -> {
                    if (panel != null) panel.refresh();
                }));
            BufferedImage icon = createIcon();
            navigationButton = NavigationButton.builder()
                .tooltip("Adventure Planner")
                .priority(6)
                .icon(icon)
                .panel(panel)
                .build();
            clientToolbar.addNavigation(navigationButton);
        });
    }

    @Override
    protected void shutDown()
    {
        lastSkillXp.clear();
        inventorySeededTaskEndsAt = 0L;
        SwingUtilities.invokeLater(() -> {
            if (taskReminderManager != null) taskReminderManager.clear();
            taskReminderManager = null;
            if (navigationButton != null) clientToolbar.removeNavigation(navigationButton);
            navigationButton = null;
            if (panel != null) panel.shutdown();
            panel = null;
            state = null;
            recurringActivityManager = null;
            automaticChecklistManager = null;
            if (runNavigator != null) runNavigator.clear();
            shootingStarTracker = null;
            runNavigator = null;
            if (overlay != null) overlayManager.remove(overlay);
            overlay = null;
        });
    }

    @Subscribe
    public void onGameObjectSpawned(GameObjectSpawned event)
    {
        if (shootingStarTracker != null
            && shootingStarTracker.onGameObjectSpawned(event))
        {
            refreshStarsPanel();
        }
    }


    @Subscribe
    public void onGameObjectDespawned(GameObjectDespawned event)
    {
        if (shootingStarTracker != null
            && shootingStarTracker.onGameObjectDespawned(event))
        {
            refreshStarsPanel();
        }
    }

    private void refreshStarsPanel()
    {
        SwingUtilities.invokeLater(() -> {
            if (panel != null) panel.refreshStars();
        });
    }

    @Subscribe
    public void onMenuOptionClicked(MenuOptionClicked event)
    {
        if (recurringActivityManager != null) recurringActivityManager.onMenuOptionClicked(event);
    }

    @Subscribe
    public void onStatChanged(StatChanged event)
    {
        if (recurringActivityManager != null) recurringActivityManager.onStatChanged(event);
        Integer previousXp = lastSkillXp.put(event.getSkill(), event.getXp());
        if (previousXp != null && event.getXp() > previousXp && panel != null)
        {
            Skill skill = event.getSkill();
            SwingUtilities.invokeLater(() -> {
                if (panel != null) panel.skillProgressed(skill);
            });
        }
    }

    @Subscribe
    public void onGameTick(GameTick event)
    {
        if (shootingStarTracker != null && shootingStarTracker.scanLoadedScene())
        {
            refreshStarsPanel();
        }
        for (Skill skill : Skill.values())
        {
            if (skill != Skill.OVERALL)
            {
                lastSkillXp.putIfAbsent(skill, client.getSkillExperience(skill));
            }
        }
        seedTaskProgressFromInventory();
        if (recurringActivityManager != null) recurringActivityManager.onGameTick();
        if (automaticChecklistManager != null) automaticChecklistManager.onGameTick();
        if (taskReminderManager != null) taskReminderManager.refresh();
        if (panel != null && config.autoGenerateTask())
        {
            SwingUtilities.invokeLater(panel::maybeAutoGenerateTask);
        }
    }

    @Subscribe
    public void onChatMessage(ChatMessage event)
    {
        if (automaticChecklistManager != null) automaticChecklistManager.onChatMessage(event);
    }

    @Subscribe
    public void onWidgetLoaded(WidgetLoaded event)
    {
        if (automaticChecklistManager != null) automaticChecklistManager.onWidgetLoaded(event);
    }

    @Subscribe
    public void onNpcLootReceived(NpcLootReceived event)
    {
        if (automaticChecklistManager != null) automaticChecklistManager.onNpcLootReceived(event);
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event)
    {
        if (!AdventurePlannerConfig.GROUP.equals(event.getGroup())) return;
        String key = event.getKey();
        boolean visibleSetting = key.startsWith("include") || key.startsWith("show")
            || "focusMode".equals(key) || "focusQuest".equals(key)
            || "autoGenerateTask".equals(key) || "levelChunk".equals(key)
            || "autoTrackRuns".equals(key)
            || "runReadySounds".equals(key) || "language".equals(key);
        if (!visibleSetting) return;
        if (panel != null) SwingUtilities.invokeLater(() -> panel.configChanged(key));
        if (taskReminderManager != null) taskReminderManager.refresh();
    }

    @Subscribe
    public void onItemContainerChanged(ItemContainerChanged event)
    {
        ItemContainer bank = client.getItemContainer(InventoryID.BANK);
        ItemContainer seedVault = client.getItemContainer(InventoryID.SEED_VAULT);
        if (state == null) return;

        if (bank != null && event.getItemContainer() == bank)
        {
            Map<String, Integer> snapshot = snapshot(bank);
            state.saveBankItems(snapshot);
            if (panel != null)
            {
                int count = snapshot.size();
                SwingUtilities.invokeLater(() -> panel.bankUpdated(count));
            }
        }
        else if (seedVault != null && event.getItemContainer() == seedVault)
        {
            Map<String, Integer> snapshot = snapshot(seedVault);
            state.saveSeedVaultItems(snapshot);
            if (panel != null)
            {
                int count = snapshot.size();
                SwingUtilities.invokeLater(() -> panel.seedVaultUpdated(count));
            }
        }
    }

    private void seedTaskProgressFromInventory()
    {
        if (state == null) return;
        GeneratedTask task = state.currentGeneratedTask();
        long taskEndsAt = state.currentTaskEndsAt();
        if (task == null || taskEndsAt <= 0L || taskEndsAt == inventorySeededTaskEndsAt) return;
        inventorySeededTaskEndsAt = taskEndsAt;

        int target = task.getTargetAmount();
        ItemContainer inventory = client.getItemContainer(InventoryID.INVENTORY);
        if (target > 0 && inventory != null && state.currentTaskProgress() == 0)
        {
            int startingAmount = task.startingInventoryProgress(snapshot(inventory));
            if (startingAmount > 0)
            {
                state.addCurrentTaskProgress(startingAmount, target);
            }
        }
        if (panel != null)
        {
            SwingUtilities.invokeLater(() -> {
                if (panel != null) panel.refreshTaskProgress();
            });
        }
    }

    private Map<String, Integer> snapshot(ItemContainer container)
    {
        Map<String, Integer> snapshot = new LinkedHashMap<>();
        for (Item item : container.getItems())
        {
            if (item.getId() <= 0 || item.getQuantity() <= 0) continue;
            String name = client.getItemDefinition(item.getId()).getName();
            snapshot.merge(name, item.getQuantity(), Integer::sum);
        }
        return snapshot;
    }

    private static BufferedImage createIcon()
    {
        BufferedImage icon = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = icon.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        graphics.setColor(new Color(28, 25, 22));
        graphics.fillOval(1, 1, 30, 30);
        graphics.setColor(new Color(232, 160, 48));
        graphics.setStroke(new BasicStroke(2.5f));
        graphics.drawOval(3, 3, 26, 26);

        graphics.setColor(new Color(255, 205, 112));
        int[] northX = {16, 20, 16, 12};
        int[] northY = {5, 16, 13, 16};
        graphics.fillPolygon(northX, northY, 4);

        graphics.setColor(Color.WHITE);
        graphics.setStroke(new BasicStroke(3.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        graphics.drawLine(9, 20, 14, 25);
        graphics.drawLine(14, 25, 24, 15);
        graphics.dispose();
        return icon;
    }
}
