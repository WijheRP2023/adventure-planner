package com.adventureplanner;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.Scrollable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.Skill;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.PluginPanel;

public final class AdventurePlannerPanel extends PluginPanel
{
    private static final Color BACKGROUND = new Color(20, 20, 20);
    private static final Color CARD = new Color(38, 38, 38);
    private static final Color CARD_HOVER = new Color(46, 46, 46);
    private static final Color INPUT = new Color(29, 29, 29);
    private static final Color ACCENT = new Color(226, 157, 58);
    private static final Color ACCENT_DARK = new Color(150, 91, 26);
    private static final Color SUCCESS = new Color(91, 173, 112);
    private static final Color TEXT = new Color(238, 238, 238);
    private static final Color MUTED = new Color(190, 190, 190);
    private static final int[] TASK_DURATIONS = {30, 60, 90};
    private static final int TASK_TEXT_WIDTH = 164;
    private static final Color TIME_EXPIRED = new Color(214, 103, 85);

    private final Client client;
    private final ClientThread clientThread;
    private final AdventurePlannerConfig config;
    private final PlannerState state;
    private final RunNavigator runNavigator;
    private final ItemPriceProvider itemPrices;
    private final ShootingStarTracker shootingStarTracker;
    private final Random random = new Random();
    private final SmartTaskGenerator generator = new SmartTaskGenerator(random);
    private final JPanel dailyList = listPanel();
    private final JPanel tasksList = listPanel();
    private final JPanel weeklyList = listPanel();
    private final JPanel recurringList = listPanel();
    private final JPanel starsList = listPanel();
    private final JLabel progressLabel = label("0 van 0 afgerond", MUTED, 12, Font.PLAIN);
    private final JTabbedPane tabs = new JTabbedPane();
    private final JProgressBar progressBar = new JProgressBar(0, 100);
    private final JLabel phaseBadge = label("VOLGENDE STAP", ACCENT, 10, Font.BOLD);
    private final JLabel generatedTitle = label("Nog geen taak gekozen", TEXT, 14, Font.BOLD);
    private final JLabel generatedDetail = label("Log in en laat je volgende doel kiezen.", MUTED, 12, Font.PLAIN);
    private final JLabel generatedProgress = label("VOORTGANG  0 / 0", ACCENT, 12, Font.BOLD);
    private final JLabel generatedTimer = label("GEEN ACTIEVE TIMER", MUTED, 12, Font.BOLD);
    private final JLabel bankStatus = label("Open je bank om voorraad te scannen", MUTED, 11, Font.PLAIN);
    private final JLabel seedVaultStatus = label("Open je Seed Vault om seeds te scannen", MUTED, 11, Font.PLAIN);
    private final JTextArea notesArea = new JTextArea();
    private RunType selectedRunType = RunType.HERB;
    private JLabel headerEyebrow;
    private JLabel headerSubtitle;
    private JLabel sourcesLabel;
    private JLabel notesLabel;
    private JButton generateButton;
    private JButton completeButton;
    private JButton stopButton;
    private volatile boolean taskGenerationPending;
    private volatile long taskGenerationSequence;
    private final javax.swing.Timer taskTimer = new javax.swing.Timer(1000, event -> {
        updateTaskTimer();
        refreshStars();
    });

    public AdventurePlannerPanel(Client client, ClientThread clientThread, AdventurePlannerConfig config,
        PlannerState state, RunNavigator runNavigator, ItemManager itemManager,
        ShootingStarTracker shootingStarTracker)
    {
        this.client = client;
        this.clientThread = clientThread;
        this.config = config;
        this.state = state;
        this.runNavigator = runNavigator;
        this.itemPrices = new RuneLiteItemPriceProvider(itemManager);
        this.shootingStarTracker = shootingStarTracker;
        setLayout(new BorderLayout(0, 0));
        setBackground(BACKGROUND);
        add(header(), BorderLayout.NORTH);

        tabs.setBackground(BACKGROUND);
        tabs.setForeground(TEXT);
        tabs.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 11));
        tabs.setBorder(BorderFactory.createEmptyBorder(3, 4, 4, 4));
        tabs.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        tabs.addTab(t("TAKEN"), scroll(tasksList));
        tabs.addTab("RUNS", scroll(recurringList));
        tabs.addTab("STARS", scroll(starsList));
        tabs.addTab(t("TAAK"), scroll(generatorPanel()));
        for (int i = 0; i < tabs.getTabCount(); i++)
        {
            tabs.setTabComponentAt(i, tabLabel(tabs.getTitleAt(i)));
        }
        add(tabs, BorderLayout.CENTER);

        GeneratedTask savedTask = state.currentGeneratedTask();
        if (savedTask != null && "Korte taak".equals(savedTask.getPhase()))
        {
            state.clearGeneratedTask();
            savedTask = null;
        }
        refresh();
        showGeneratedTask(savedTask);
        localizeChrome();
        if (state.bankStamp() != null)
        {
            bankStatus.setText(statusHtml("BANK", t("Snapshot beschikbaar"), SUCCESS));
        }
        if (state.seedVaultStamp() != null)
        {
            seedVaultStatus.setText(statusHtml("VAULT", t("Snapshot beschikbaar"), SUCCESS));
        }
        ensureInitialTaskFromBank();
        taskTimer.setInitialDelay(0);
        taskTimer.start();
    }

    public void refresh()
    {
        state.applyResets();
        tasksList.removeAll();
        dailyList.removeAll();
        weeklyList.removeAll();
        recurringList.removeAll();
        dailyList.add(sectionIntro(t("Dagelijkse routine"), t("Eenmalige taken die je vandaag kunt afronden")));
        weeklyList.add(sectionIntro(t("Weekoverzicht"), t("Kleine doelen voor extra afwisseling")));
        recurringList.add(sectionIntro(t("Terugkerende runs"),
            t("Timers per locatie; deze tellen niet mee met je dagelijkse routine")));
        recurringList.add(compactRunTypeSelector());
        recurringList.add(Box.createRigidArea(new Dimension(0, 8)));

        List<PlannerTask> tasks = new ArrayList<>(TaskCatalog.defaults());
        tasks.addAll(state.customTasks());
        int completed = 0;
        int counted = 0;
        for (PlannerTask task : tasks)
        {
            if (!task.isCustom()
                && (DailyTaskOption.isHidden(task.getId(),
                    PlannerConfigSelections.hiddenDailyTasks(config))
                    || WeeklyTaskOption.isHidden(task.getId(),
                        PlannerConfigSelections.hiddenWeeklyTasks(config))))
            {
                continue;
            }
            if (task.isRecurring()) continue;
            if (!task.isRecurring() && task.getCadence() == TaskCadence.DAILY)
            {
                counted++;
                if (state.isCompleted(task.getId())) completed++;
            }
            addTask(task);
        }
        addCompactRunLocations();
        dailyList.add(customTaskForm(TaskCadence.DAILY));
        weeklyList.add(customTaskForm(TaskCadence.WEEKLY));
        tasksList.add(dailyList);
        tasksList.add(Box.createRigidArea(new Dimension(0, 12)));
        tasksList.add(weeklyList);
        refreshStars();
        updateProgress(completed, counted);
        revalidate();
        repaint();
    }

    public void refreshStars()
    {
        starsList.removeAll();
        starsList.add(sectionIntro(t("Shooting stars"),
            t("Automatisch gevonden sterren met locatie, wereld en tier")));
        JLabel scanner = label(t("SCANNER  WERELD " + client.getWorld()),
            ACCENT, 10, Font.BOLD);
        scanner.setBorder(BorderFactory.createEmptyBorder(0, 2, 8, 0));
        scanner.setAlignmentX(LEFT_ALIGNMENT);
        starsList.add(scanner);

        List<ShootingStarSighting> sightings = shootingStarTracker.sightings();
        if (sightings.isEmpty())
        {
            RoundedPanel empty = new RoundedPanel(CARD, 8);
            empty.setLayout(new BoxLayout(empty, BoxLayout.Y_AXIS));
            empty.setBorder(BorderFactory.createEmptyBorder(12, 10, 12, 10));
            empty.setMaximumSize(new Dimension(Integer.MAX_VALUE, 92));
            empty.setAlignmentX(LEFT_ALIGNMENT);
            empty.add(label(t("Nog geen shooting star gezien"), TEXT, 12, Font.BOLD));
            empty.add(Box.createRigidArea(new Dimension(0, 5)));
            JTextArea hint = new JTextArea(t("De scanner vult deze lijst automatisch "
                + "zodra een crashed star in de geladen spelwereld verschijnt."));
            hint.setLineWrap(true);
            hint.setWrapStyleWord(true);
            hint.setEditable(false);
            hint.setOpaque(false);
            hint.setForeground(MUTED);
            hint.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
            starsList.add(empty);
        }
        else
        {
            for (ShootingStarSighting sighting : sightings)
            {
                addStarSighting(sighting);
            }
        }
        starsList.revalidate();
        starsList.repaint();
    }

    private void addStarSighting(ShootingStarSighting sighting)
    {
        RoundedPanel card = new RoundedPanel(CARD, 8);
        card.setLayout(new BorderLayout(7, 0));
        card.setBorder(BorderFactory.createEmptyBorder(9, 9, 9, 7));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 78));
        card.setAlignmentX(LEFT_ALIGNMENT);

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        JLabel location = label(sighting.getLocation(), TEXT, 11, Font.BOLD);
        String tier = sighting.getTier() > 0
            ? "  -  TIER " + sighting.getTier() : "";
        JLabel world = label(t("WERELD " + sighting.getWorld() + tier),
            ACCENT, 10, Font.BOLD);
        String statusText = sighting.isActive()
            ? t("Nu zichtbaar in de geladen omgeving")
            : t("Laatst gezien " + starAge(sighting.getLastSeenAt()) + " geleden");
        JLabel status = label(statusText,
            sighting.isActive() ? SUCCESS : MUTED, 9, Font.PLAIN);
        text.add(location);
        text.add(Box.createRigidArea(new Dimension(0, 3)));
        text.add(world);
        text.add(Box.createRigidArea(new Dimension(0, 2)));
        text.add(status);

        JButton navigate = accentButton("NAV");
        boolean sameWorld = client.getWorld() == sighting.getWorld();
        navigate.setEnabled(sameWorld);
        navigate.setPreferredSize(new Dimension(42, 30));
        navigate.setMargin(new Insets(0, 2, 0, 2));
        navigate.setToolTipText(sameWorld
            ? t("Zet een hintpijl op de gevonden ster")
            : t("Ga eerst naar wereld " + sighting.getWorld()));
        navigate.addActionListener(event -> clientThread.invokeLater(
            () -> client.setHintArrow(sighting.getPoint())));

        String coordinates = t("Wereld " + sighting.getWorld() + " - "
            + sighting.getPoint().getX() + ", " + sighting.getPoint().getY());
        card.setToolTipText(coordinates);
        location.setToolTipText(coordinates);
        card.add(text, BorderLayout.CENTER);
        card.add(navigate, BorderLayout.EAST);
        addHover(card);
        starsList.add(card);
        starsList.add(Box.createRigidArea(new Dimension(0, 5)));
    }

    private static String starAge(long timestamp)
    {
        long seconds = Math.max(0L,
            (System.currentTimeMillis() - timestamp) / 1_000L);
        if (seconds < 60L) return seconds + "s";
        long minutes = seconds / 60L;
        if (minutes < 60L) return minutes + "m";
        return minutes / 60L + "u " + minutes % 60L + "m";
    }
    public void configChanged(String key)
    {
        refresh();
        GeneratedTask task = state.currentGeneratedTask();
        if ("focusMode".equals(key) || "focusQuest".equals(key))
        {
        if ("language".equals(key))
        {
            localizeChrome();
            refresh();
            showGeneratedTask(state.currentGeneratedTask());
            return;
        }
            state.clearGeneratedTask();
            showGeneratedTask(null);
            maybeAutoGenerateTask();
            return;
        }
        if (task != null && PlannerConfigSelections.blockedSkills(config).contains(task.getSkill()))
        {
            state.clearGeneratedTask();
            showGeneratedTask(null);
        }
    }

    private JPanel compactRunTypeSelector()
    {
        if (selectedRunType == RunType.SEAWEED
            || !isCompactRunTypeVisible(selectedRunType))
        {
            selectedRunType = firstCompactRunType();
        }
        JPanel selector = new JPanel(new java.awt.GridLayout(1, 3, 4, 0));
        selector.setOpaque(false);
        selector.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        selector.setAlignmentX(LEFT_ALIGNMENT);
        RunType[] types = {RunType.HERB, RunType.TREE, RunType.BIRDHOUSE};
        for (RunType type : types)
        {
            JButton button = type == selectedRunType
                ? accentButton(type.getLabel()) : ghostButton(type.getLabel());
            button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 9));
            button.setIcon(RunIconFactory.icon(type));
            button.setIconTextGap(3);
            button.setEnabled(isCompactRunTypeVisible(type));
            button.addActionListener(event -> {
                selectedRunType = type;
                refresh();
            });
            selector.add(button);
        }
        return selector;
    }

    private RunType firstCompactRunType()
    {
        if (isCompactRunTypeVisible(RunType.HERB)) return RunType.HERB;
        if (isCompactRunTypeVisible(RunType.TREE)) return RunType.TREE;
        return RunType.BIRDHOUSE;
    }

    private boolean isCompactRunTypeVisible(RunType type)
    {
        switch (type)
        {
            case HERB: return config.showHerbRuns() || config.showSeaweedRuns();
            case TREE: return config.showTreeRuns();
            case BIRDHOUSE: return config.showBirdhouses();
            default: return false;
        }
    }

    private void addCompactRunLocations()
    {
        if (!isCompactRunTypeVisible(selectedRunType))
        {
            recurringList.add(sectionIntro(t("Geen timers geselecteerd"),
                t("Zet een run aan onder Instellingen > Timers en runs")));
            return;
        }

        long now = System.currentTimeMillis();
        String previousSection = null;
        for (RunLocation location : RunLocationCatalog.locations())
        {
            if (!matchesSelectedRunType(location) || !isLocationVisible(location)) continue;
            String section = runSection(location);
            if (!section.equals(previousSection))
            {
                if (previousSection != null)
                {
                    recurringList.add(Box.createRigidArea(new Dimension(0, 8)));
                }
                addRunSectionHeading(section);
                previousSection = section;
            }
            addCompactRunRow(location, now);
        }
    }

    private boolean matchesSelectedRunType(RunLocation location)
    {
        if (selectedRunType == RunType.HERB)
        {
            return location.getType() == RunType.HERB
                || location.getType() == RunType.SEAWEED;
        }
        return location.getType() == selectedRunType;
    }

    private boolean isLocationVisible(RunLocation location)
    {
        switch (location.getType())
        {
            case HERB: return config.showHerbRuns();
            case TREE: return config.showTreeRuns();
            case BIRDHOUSE: return config.showBirdhouses();
            case SEAWEED: return config.showSeaweedRuns();
            default: return false;
        }
    }

    private void addRunSectionHeading(String text)
    {
        JLabel heading = label(t(text).toUpperCase(), MUTED, 10, Font.BOLD);
        heading.setBorder(BorderFactory.createEmptyBorder(2, 2, 4, 0));
        heading.setAlignmentX(LEFT_ALIGNMENT);
        heading.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));
        recurringList.add(heading);
    }

    private void addCompactRunRow(RunLocation location, long now)
    {
        long readyAt = state.activityReadyAt(location.timerId());
        long startedAt = state.activityStartedAt(location.timerId());
        boolean ready = readyAt > 0 && readyAt <= now;
        String status = ready ? t("Done") : readyAt > now
            ? runCountdown(readyAt - now) : t("Nog niet gestart");
        Color statusColor = ready ? SUCCESS : readyAt > now ? ACCENT : MUTED;
        long effectiveStart = startedAt > 0 ? startedAt
            : readyAt > 0 ? readyAt - fallbackDurationMillis(location) : 0L;

        RoundedPanel row = new RoundedPanel(CARD, 8);
        row.setLayout(new BorderLayout(0, 0));
        row.setBorder(BorderFactory.createEmptyBorder(6, 7, 0, 5));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));
        row.setAlignmentX(LEFT_ALIGNMENT);
        String tooltip = location.getName() + " - " + t(runRequirements(location))
            + " - " + t(location.getTeleport());
        row.setToolTipText(tooltip);

        JPanel content = new JPanel(new BorderLayout(7, 0));
        content.setOpaque(false);
        JLabel locationIcon = new JLabel(RunIconFactory.locationIcon(location.getType()));
        locationIcon.setToolTipText(tooltip);

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        JLabel name = label(location.getName(), TEXT, 11, Font.BOLD);
        JLabel timer = label(status, statusColor, ready ? 8 : 10, Font.PLAIN);
        name.setToolTipText(tooltip);
        timer.setToolTipText(tooltip);
        text.add(name);
        text.add(Box.createRigidArea(new Dimension(0, 1)));
        text.add(timer);

        boolean soundEnabled = state.isRunSoundEnabled(
            location.timerId(), config.runReadySounds());
        JButton sound = new JButton(RunIconFactory.soundIcon(soundEnabled));
        sound.setToolTipText(soundEnabled
            ? t("Geluid voor deze locatie staat aan")
            : t("Geluid voor deze locatie staat uit"));
        sound.setBorder(BorderFactory.createEmptyBorder());
        sound.setContentAreaFilled(false);
        sound.setFocusPainted(false);
        sound.setPreferredSize(new Dimension(25, 30));
        sound.addActionListener(event -> {
            state.setRunSoundEnabled(location.timerId(), !soundEnabled);
            refresh();
        });

        content.add(locationIcon, BorderLayout.WEST);
        content.add(text, BorderLayout.CENTER);
        content.add(sound, BorderLayout.EAST);

        JProgressBar progress = new JProgressBar(0, 100);
        progress.setValue(runProgress(effectiveStart, readyAt, now));
        progress.setForeground(SUCCESS);
        progress.setBackground(new Color(30, 48, 34));
        progress.setBorderPainted(false);
        progress.setStringPainted(false);
        progress.setPreferredSize(new Dimension(1, 4));

        MouseAdapter navigate = new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent event)
            {
                runNavigator.navigate(location);
            }
        };
        row.addMouseListener(navigate);
        content.addMouseListener(navigate);
        locationIcon.addMouseListener(navigate);
        text.addMouseListener(navigate);
        name.addMouseListener(navigate);
        timer.addMouseListener(navigate);
        addHover(row);
        row.add(content, BorderLayout.CENTER);
        row.add(progress, BorderLayout.SOUTH);
        recurringList.add(row);
        recurringList.add(Box.createRigidArea(new Dimension(0, 4)));
    }

    static int runProgress(long startedAt, long readyAt, long now)
    {
        if (startedAt <= 0 || readyAt <= startedAt) return 0;
        if (now >= readyAt) return 100;
        if (now <= startedAt) return 0;
        return (int) Math.max(0, Math.min(100,
            (now - startedAt) * 100L / (readyAt - startedAt)));
    }

    private static long fallbackDurationMillis(RunLocation location)
    {
        String id = location.getId();
        long minutes;
        if (location.getType() == RunType.HERB) minutes = 80;
        else if (location.getType() == RunType.SEAWEED) minutes = 40;
        else if (location.getType() == RunType.BIRDHOUSE) minutes = 50;
        else if (id.startsWith("fruit_")) minutes = 960;
        else if (id.startsWith("hardwood_")) minutes = 4_260;
        else if (id.startsWith("spirit_")) minutes = 3_840;
        else if (id.startsWith("special_redwood")) minutes = 6_400;
        else if (id.startsWith("special_celastrus")) minutes = 800;
        else if (id.startsWith("special_calquat")) minutes = 1_280;
        else if (id.startsWith("special_crystal")) minutes = 480;
        else minutes = 480;
        return minutes * 60_000L;
    }

    private static String runSection(RunLocation location)
    {
        String id = location.getId();
        if (location.getType() == RunType.HERB) return "Herb patches";
        if (location.getType() == RunType.SEAWEED) return "Giant seaweed";
        if (location.getType() == RunType.BIRDHOUSE) return "Birdhouses";
        if (id.startsWith("fruit_")) return "Fruit trees";
        if (id.startsWith("hardwood_")) return "Hardwood trees";
        if (id.startsWith("spirit_")) return "Spirit trees";
        if (id.startsWith("special_")) return "Speciale bomen";
        return "Normale bomen";
    }

    private static String runCountdown(long millis)
    {
        long totalSeconds = Math.max(0, (millis + 999L) / 1_000L);
        long hours = totalSeconds / 3_600L;
        long minutes = (totalSeconds % 3_600L) / 60L;
        long seconds = totalSeconds % 60L;
        if (hours > 0) return hours + "u " + minutes + "m " + seconds + "s";
        return minutes + "m " + seconds + "s";
    }
    private JPanel runTypeSelector()
    {
        if (!isRunTypeVisible(selectedRunType))
        {
            for (RunType type : RunType.values())
            {
                if (isRunTypeVisible(type))
                {
                    selectedRunType = type;
                    break;
                }
            }
        }
        JPanel selector = new JPanel(new java.awt.GridLayout(1, 4, 4, 0));
        selector.setOpaque(false);
        selector.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        selector.setAlignmentX(LEFT_ALIGNMENT);
        for (RunType type : RunType.values())
        {
            JButton button = type == selectedRunType ? accentButton(type.getLabel()) : ghostButton(type.getLabel());
            button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 9));
            button.setIcon(RunIconFactory.icon(type));
            button.setIconTextGap(2);
            button.setEnabled(isRunTypeVisible(type));
            button.addActionListener(event -> {
                selectedRunType = type;
                refresh();
            });
            selector.add(button);
        }
        return selector;
    }

    private void addRunLocations()
    {
        if (!isRunTypeVisible(selectedRunType))
        {
            for (RunType type : RunType.values())
            {
                if (isRunTypeVisible(type))
                {
                    selectedRunType = type;
                    break;
                }
            }
        }
        if (!isRunTypeVisible(selectedRunType))
        {
            recurringList.add(sectionIntro("Geen timers geselecteerd",
                "Zet een run aan onder Instellingen > Timers en runs"));
            return;
        }

        long now = System.currentTimeMillis();
        for (RunLocation location : RunLocationCatalog.locations())
        {
            if (location.getType() != selectedRunType) continue;
            long readyAt = state.activityReadyAt(location.timerId());
            String status = readyAt <= now ? "Klaar" : remaining(readyAt - now);

            RoundedPanel row = new RoundedPanel(CARD, 9);
            row.setLayout(new BorderLayout(5, 0));
            row.setBorder(BorderFactory.createEmptyBorder(7, 8, 7, 6));
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 92));
            row.setAlignmentX(LEFT_ALIGNMENT);

            JPanel text = new JPanel();
            text.setOpaque(false);
            text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
            String color = readyAt <= now ? "#5bad70" : "#e29d3a";
            JLabel title = label("<html><b>" + htmlEscape(location.getName())
                + "</b>  <span style='color:" + color + "'>" + status + "</span></html>",
                TEXT, 11, Font.PLAIN);
            title.setIcon(RunIconFactory.icon(location.getType()));
            JLabel items = label("<html><div style='width:145px'>" + htmlEscape(runRequirements(location))
                + "</div></html>", MUTED, 10, Font.PLAIN);
            JLabel teleport = label("<html><div style='width:145px'>" + htmlEscape(location.getTeleport())
                + "</div></html>", new Color(166, 166, 166), 9, Font.PLAIN);
            text.add(title);
            text.add(Box.createRigidArea(new Dimension(0, 2)));
            text.add(items);
            text.add(Box.createRigidArea(new Dimension(0, 2)));
            text.add(teleport);

            JButton navigate = accentButton("NAV");
            navigate.setToolTipText("Zet een hintpijl en wereldkaartmarkering");
            navigate.setPreferredSize(new Dimension(42, 30));
            navigate.setMargin(new Insets(0, 2, 0, 2));
            navigate.addActionListener(event -> runNavigator.navigate(location));
            row.add(text, BorderLayout.CENTER);
            row.add(navigate, BorderLayout.EAST);
            addHover(row);
            recurringList.add(row);
            recurringList.add(Box.createRigidArea(new Dimension(0, 5)));
        }
    }

    private String runRequirements(RunLocation location)
    {
        int farming = client.getGameState() == GameState.LOGGED_IN
            ? client.getRealSkillLevel(Skill.FARMING) : 1;
        Map<String, Integer> supplies = state.availableItems();
        if (location.getType() == RunType.HERB)
        {
            String seed = SmartTaskGenerator.bestHerbSeed(supplies, farming);
            if (seed != null) return t("Neem: 1 x " + seed + " + seed dibber");
        }
        if (location.getType() == RunType.TREE)
        {
            String seed = SmartTaskGenerator.bestTreeSeed(supplies, farming);
            if (seed != null) return t("Neem: 1 x " + seed + " + spade");
        }
        return t("Neem: " + location.getRequirements());
    }

    private boolean isRunTypeVisible(RunType type)
    {
        switch (type)
        {
            case HERB: return config.showHerbRuns();
            case TREE: return config.showTreeRuns();
            case BIRDHOUSE: return config.showBirdhouses();
            case SEAWEED: return config.showSeaweedRuns();
            default: return false;
        }
    }

    private static String remaining(long millis)
    {
        long totalMinutes = Math.max(1, (millis + 59_999L) / 60_000L);
        long hours = totalMinutes / 60;
        long minutes = totalMinutes % 60;
        return hours > 0 ? hours + "u " + minutes + "m" : minutes + "m";
    }

    private JPanel header()
    {
        HeaderPanel panel = new HeaderPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(14, 14, 12, 14));

        headerEyebrow = label(t("ACCOUNT ROUTE"), new Color(255, 218, 153), 11, Font.BOLD);
        headerEyebrow.setAlignmentX(LEFT_ALIGNMENT);
        JLabel title = label("Adventure Planner", Color.WHITE, 18, Font.BOLD);
        title.setAlignmentX(LEFT_ALIGNMENT);
        headerSubtitle = label(t("Afwisseling met richting"), new Color(235, 225, 211), 12, Font.PLAIN);
        headerSubtitle.setAlignmentX(LEFT_ALIGNMENT);
        progressLabel.setAlignmentX(LEFT_ALIGNMENT);

        progressBar.setValue(0);
        progressBar.setStringPainted(false);
        progressBar.setForeground(ACCENT);
        progressBar.setBackground(new Color(63, 49, 35));
        progressBar.setBorderPainted(false);
        progressBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 6));
        progressBar.setPreferredSize(new Dimension(190, 6));
        progressBar.setAlignmentX(LEFT_ALIGNMENT);

        panel.add(headerEyebrow);
        panel.add(Box.createRigidArea(new Dimension(0, 2)));
        panel.add(title);
        panel.add(Box.createRigidArea(new Dimension(0, 2)));
        panel.add(headerSubtitle);
        panel.add(Box.createRigidArea(new Dimension(0, 11)));
        panel.add(progressBar);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(progressLabel);
        return panel;
    }

    private JPanel generatorPanel()
    {
        JPanel panel = listPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(10, 5, 12, 5));

        RoundedPanel taskCard = new RoundedPanel(CARD, 12);
        taskCard.setLayout(new BoxLayout(taskCard, BoxLayout.Y_AXIS));
        taskCard.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        taskCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 340));
        taskCard.setAlignmentX(LEFT_ALIGNMENT);
        phaseBadge.setAlignmentX(CENTER_ALIGNMENT);
        generatedTitle.setAlignmentX(CENTER_ALIGNMENT);
        generatedDetail.setAlignmentX(CENTER_ALIGNMENT);
        generatedProgress.setAlignmentX(CENTER_ALIGNMENT);
        generatedTitle.setHorizontalAlignment(SwingConstants.CENTER);
        generatedTimer.setAlignmentX(CENTER_ALIGNMENT);
        generatedDetail.setHorizontalAlignment(SwingConstants.CENTER);
        generatedProgress.setHorizontalAlignment(SwingConstants.CENTER);
        generatedTitle.setText(centered(t("Nog geen taak gekozen"), TASK_TEXT_WIDTH));
        generatedTimer.setHorizontalAlignment(SwingConstants.CENTER);
        generatedDetail.setText(centered(t("Je levels, bank en recente taken bepalen de keuze."), TASK_TEXT_WIDTH));
        generatedTitle.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        generatedDetail.setMaximumSize(new Dimension(Integer.MAX_VALUE, 155));
        generatedProgress.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        generatedTimer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));
        generatedProgress.setFont(new Font(Font.MONOSPACED, Font.BOLD, 12));
        generatedTimer.setFont(new Font(Font.MONOSPACED, Font.BOLD, 12));
        generatedProgress.setVisible(false);

        taskCard.add(phaseBadge);
        taskCard.add(Box.createRigidArea(new Dimension(0, 9)));
        taskCard.add(generatedTitle);
        taskCard.add(Box.createRigidArea(new Dimension(0, 8)));
        taskCard.add(generatedDetail);
        taskCard.add(Box.createRigidArea(new Dimension(0, 9)));
        taskCard.add(generatedProgress);
        taskCard.add(Box.createRigidArea(new Dimension(0, 3)));

        generateButton = accentButton(t("SKIP / WISSEL TAAK"));
        taskCard.add(Box.createRigidArea(new Dimension(0, 12)));
        taskCard.add(generatedTimer);
        taskCard.add(Box.createRigidArea(new Dimension(0, 3)));
        generateButton.setAlignmentX(LEFT_ALIGNMENT);
        generateButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        generateButton.setToolTipText(t("Sla de huidige planner-taak over en kies direct een andere"));
        generateButton.addActionListener(event -> resumeAndGenerateTask());

        completeButton = ghostButton(t("AFRONDEN & VOLGENDE"));
        completeButton.setAlignmentX(LEFT_ALIGNMENT);
        completeButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        completeButton.setToolTipText(t("Rond de planner-taak af en kies automatisch een volgende"));
        completeButton.addActionListener(event -> completeGeneratedTask());

        stopButton = ghostButton(t("STOP TAAK (HANDMATIG)"));
        stopButton.setAlignmentX(LEFT_ALIGNMENT);
        stopButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        stopButton.setToolTipText(t("Stop de taak en pauzeer automatische vervolgopdrachten"));
        stopButton.addActionListener(event -> stopGeneratedTask());

        JPanel sources = sourceCard();
        sources.setAlignmentX(LEFT_ALIGNMENT);
        panel.add(taskCard);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(generateButton);
        panel.add(Box.createRigidArea(new Dimension(0, 6)));
        panel.add(completeButton);
        panel.add(Box.createRigidArea(new Dimension(0, 6)));
        panel.add(stopButton);
        panel.add(Box.createRigidArea(new Dimension(0, 13)));
        sourcesLabel = sectionLabel(t("GEGEVENSBRONNEN"));
        panel.add(sourcesLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 6)));
        panel.add(sources);
        panel.add(Box.createRigidArea(new Dimension(0, 13)));
        notesLabel = sectionLabel(t("PERSOONLIJKE NOTITIES"));
        panel.add(notesLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 6)));
        panel.add(notesCard());
        return panel;
    }

    private JPanel notesCard()
    {
        RoundedPanel card = new RoundedPanel(CARD, 10);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(7, 7, 7, 7));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 105));
        card.setAlignmentX(LEFT_ALIGNMENT);

        notesArea.setText(state.notes());
        notesArea.setRows(4);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        notesArea.setBackground(INPUT);
        notesArea.setForeground(TEXT);
        notesArea.setCaretColor(ACCENT);
        notesArea.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        notesArea.setBorder(BorderFactory.createEmptyBorder(5, 6, 5, 6));
        notesArea.setToolTipText("Bijvoorbeeld uitgeleende items; blijft bewaard tot je de tekst verwijdert");
        notesArea.getDocument().addDocumentListener(new DocumentListener()
        {
            @Override public void insertUpdate(DocumentEvent event) { save(); }
            @Override public void removeUpdate(DocumentEvent event) { save(); }
            @Override public void changedUpdate(DocumentEvent event) { save(); }
            private void save() { state.saveNotes(notesArea.getText()); }
        });
        card.add(notesArea, BorderLayout.CENTER);
        return card;
    }

    private JPanel sourceCard()
    {
        RoundedPanel panel = new RoundedPanel(CARD, 10);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 8, 10, 8));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        bankStatus.setAlignmentX(LEFT_ALIGNMENT);
        seedVaultStatus.setAlignmentX(LEFT_ALIGNMENT);
        bankStatus.setText(statusHtml("BANK", t("Nog niet gescand"), MUTED));
        seedVaultStatus.setText(statusHtml("VAULT", t("Nog niet gescand"), MUTED));
        panel.add(bankStatus);
        panel.add(Box.createRigidArea(new Dimension(0, 7)));
        panel.add(seedVaultStatus);
        return panel;
    }

    private void resumeAndGenerateTask()
    {
        state.setAutomaticTasksPaused(false);
        generateTask();
    }

    private void generateTask()
    {
        if (client.getGameState() != GameState.LOGGED_IN)
        {
            phaseBadge.setText(t("WACHTEN OP LOGIN"));
            generatedTitle.setText(centered(t("Log eerst in"), TASK_TEXT_WIDTH));
            generatedDetail.setText(centered(t("Daarna kan ik je echte levels en inventory lezen."), TASK_TEXT_WIDTH));
            return;
        }
        if (taskGenerationPending) return;
        taskGenerationPending = true;
        final long generation = ++taskGenerationSequence;
        clientThread.invokeLater(() ->
        {
            try
            {
                Map<Skill, Integer> levels = new EnumMap<>(Skill.class);
                for (Skill skill : Skill.values()) levels.put(skill, client.getRealSkillLevel(skill));
                Map<String, Integer> supplies = state.availableItems();
                mergeInventory(supplies);
                int taskMinutes = TASK_DURATIONS[random.nextInt(TASK_DURATIONS.length)];
                GeneratedTask task = generator.generate(levels, state.recentSkills(), config.levelChunk(),
                    taskMinutes, supplies,
                    PlannerConfigSelections.blockedSkills(config), config.focusMode(), config.focusQuest(),
                    itemPrices);
                if (generation != taskGenerationSequence) return;
                if (task != null)
                {
                    if (task.getSkill() != Skill.OVERALL) state.remember(task.getSkill());
                    state.saveGeneratedTask(task, taskMinutes);
                }
                SwingUtilities.invokeLater(() -> finishTaskGeneration(task, generation));
            }
            catch (RuntimeException | AssertionError exception)
            {
                if (generation == taskGenerationSequence) taskGenerationPending = false;
                throw exception;
            }
        });
    }

    private void finishTaskGeneration(GeneratedTask task, long generation)
    {
        if (generation != taskGenerationSequence) return;
        taskGenerationPending = false;
        if (task == null)
        {
            phaseBadge.setText(t("ALLES KLAAR"));
            phaseBadge.setForeground(SUCCESS);
            generatedTitle.setText(centered("Maxed!", TASK_TEXT_WIDTH));
            generatedDetail.setText(centered(t("Alle beschikbare skills zijn 99 of geblokkeerd."), TASK_TEXT_WIDTH));
            updateTaskTimer();
            return;
        }
        showGeneratedTask(task);
    }

    public void maybeAutoGenerateTask()
    {
        if (!config.autoGenerateTask() || state.automaticTasksPaused()) return;
        GeneratedTask task = state.currentGeneratedTask();
        if (isGenericSkillTask(task))
        {
            generateTask();
            return;
        }
        if (shouldAdvanceTask(task, state.currentTaskEndsAt(),
            state.currentTaskProgress(), System.currentTimeMillis()))
        {
            generateTask();
            return;
        }
        ensureInitialTaskFromBank();
    }

    static boolean isGenericSkillTask(GeneratedTask task)
    {
        return task != null && "Korte taak".equals(task.getPhase())
            && task.getTitle().startsWith("Train ")
            && task.getTitle().contains(" minuten");
    }

    static boolean shouldAdvanceTask(GeneratedTask task, long endsAt, int progress, long now)
    {
        if (task == null) return false;
        boolean timeReached = endsAt > 0L && now >= endsAt;
        int target = task.getTargetAmount();
        boolean amountReached = target > 0 && progress >= target;
        return timeReached || amountReached;
    }

    private void ensureInitialTaskFromBank()
    {
        if (state.automaticTasksPaused() || state.currentGeneratedTask() != null
            || client.getGameState() != GameState.LOGGED_IN
            || state.bankStamp() == null) return;
        generateTask();
    }

    private void completeGeneratedTask()
    {
        state.setAutomaticTasksPaused(false);
        state.clearGeneratedTask();
        showGeneratedTask(null);
        if (config.autoGenerateTask()) maybeAutoGenerateTask();
    }

    private void stopGeneratedTask()
    {
        taskGenerationSequence++;
        taskGenerationPending = false;
        state.setAutomaticTasksPaused(true);
        state.clearGeneratedTask();
        showGeneratedTask(null);
        phaseBadge.setForeground(TIME_EXPIRED);
        phaseBadge.setText(t("HANDMATIG GESTOPT"));
        generatedTitle.setText(centered(t("Automatische taken gepauzeerd"), TASK_TEXT_WIDTH));
        generatedDetail.setText(centered(
            t("Druk op skip / wissel taak om weer verder te gaan."), TASK_TEXT_WIDTH));
    }

    public void shutdown()
    {
        taskGenerationSequence++;
        taskGenerationPending = false;
        taskTimer.stop();
    }

    private void updateTaskTimer()
    {
        GeneratedTask task = state.currentGeneratedTask();
        if (task == null)
        {
            generatedTimer.setForeground(MUTED);
            generatedTimer.setText(t("GEEN ACTIEVE TIMER"));
            return;
        }
        long endsAt = state.ensureCurrentTaskEndsAt(30);
        long remaining = endsAt - System.currentTimeMillis();
        if (remaining <= 0L)
        {
            generatedTimer.setForeground(TIME_EXPIRED);
            generatedTimer.setText("<html><div style='width:185px;text-align:center'>" + t("TIJD VOORBIJ")
                + "<br><span style='font-size:10px'>" + t("Volgende taak wordt gekozen") + "</span></div></html>");
            maybeAutoGenerateTask();
            return;
        }
        generatedTimer.setForeground(remaining <= 5 * 60_000L ? ACCENT : SUCCESS);
        generatedTimer.setText(t("TAAKTIJD") + "  " + formatTaskTime(remaining));
    }

    private static String formatTaskTime(long millis)
    {
        long totalSeconds = Math.max(0L, (millis + 999L) / 1000L);
        long hours = totalSeconds / 3600L;
        long minutes = totalSeconds % 3600L / 60L;
        long seconds = totalSeconds % 60L;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public void refreshTaskProgress()
    {
        updateTaskProgress();
    }

    public void skillProgressed(Skill skill)
    {
        GeneratedTask task = state.currentGeneratedTask();
        if (task == null || task.getSkill() != skill) return;
        int target = task.getTargetAmount();
        if (target <= 0) return;
        int step = task.getTitle().toLowerCase().contains("arrow shafts") ? 15 : 1;
        int progress = state.addCurrentTaskProgress(step, target);
        updateTaskProgress();
        if (progress >= target) maybeAutoGenerateTask();
    }
    private void updateTaskProgress()
    {
        GeneratedTask task = state.currentGeneratedTask();
        int target = task == null ? 0 : task.getTargetAmount();
        if (target <= 0)
        {
            generatedProgress.setVisible(false);
            return;
        }
        int progress = Math.min(target, state.currentTaskProgress());
        boolean complete = progress >= target;
        generatedProgress.setForeground(complete ? SUCCESS : ACCENT);
        generatedProgress.setText(t("VOORTGANG") + "  " + progress + " / " + target
            + (complete ? "  " + t("KLAAR") : ""));
        generatedProgress.setVisible(true);
        if (generatedProgress.getParent() != null)
        {
            generatedProgress.getParent().revalidate();
            generatedProgress.getParent().repaint();
        }
    }

    private void showGeneratedTask(GeneratedTask task)
    {
        if (task == null)
        {
            phaseBadge.setForeground(ACCENT);
            phaseBadge.setText(t("VOLGENDE STAP"));
            generatedTitle.setText(centered(t("Nog geen taak gekozen"), TASK_TEXT_WIDTH));
            generatedDetail.setText(centered(t("Je levels, bank en recente taken bepalen de keuze."), TASK_TEXT_WIDTH));
            generatedTitle.setToolTipText(null);
            generatedDetail.setToolTipText(null);
            updateTaskProgress();
            updateTaskTimer();
            return;
        }
        phaseBadge.setForeground(ACCENT);
        phaseBadge.setText(task.getDisplayPhase(config.language()).toUpperCase());
        generatedTitle.setText(centered(task.getDisplayTitle(config.language()), TASK_TEXT_WIDTH));
        generatedDetail.setText(centered(task.getDisplayReason(config.language()), TASK_TEXT_WIDTH));
        generatedTitle.setToolTipText(task.getDisplayTitle(config.language()));
        generatedDetail.setToolTipText(task.getDisplayReason(config.language()));
        updateTaskProgress();
        updateTaskTimer();
    }

    public void bankUpdated(int uniqueItems)
    {
        bankStatus.setText(statusHtml("BANK", t(uniqueItems + " soorten items"), SUCCESS));
        ensureInitialTaskFromBank();
    }

    public void seedVaultUpdated(int uniqueItems)
    {
        seedVaultStatus.setText(statusHtml("VAULT", t(uniqueItems + " soorten seeds"), SUCCESS));
        maybeAutoGenerateTask();
    }

    private void mergeInventory(Map<String, Integer> supplies)
    {
        ItemContainer inventory = client.getItemContainer(InventoryID.INVENTORY);
        if (inventory == null) return;
        for (Item item : inventory.getItems())
        {
            if (item.getId() <= 0 || item.getQuantity() <= 0) continue;
            String name = client.getItemDefinition(item.getId()).getName();
            supplies.merge(name, item.getQuantity(), Integer::sum);
        }
    }

    private void addTask(PlannerTask task)
    {
        boolean done = state.isCompleted(task.getId());
        RoundedPanel row = new RoundedPanel(CARD, 9);
        row.setLayout(new BorderLayout(4, 0));
        row.setBorder(BorderFactory.createEmptyBorder(6, 7, 6, 5));
        HunterRumour rumour = "daily_rumour".equals(task.getId()) ? state.currentRumour() : null;
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, rumour == null ? 46 : 68));
        row.setAlignmentX(LEFT_ALIGNMENT);

        String shownTitle = rumour == null
            ? (task.isCustom() ? task.getTitle() : t(task.getTitle())) : "Rumour: " + rumour.getName();
        String title = htmlEscape(shownTitle);
        String detail = rumour == null ? "" : "<br><span style='color:#aaaaaa;font-size:9px'>"
            + htmlEscape(rumour.getLocation()) + " - " + htmlEscape(rumour.getRequirements()) + "</span>";
        int textWidth = task.isCustom() ? 124 : 178;
        String markup = done
            ? "<html><div style='width:" + textWidth + "px;color:#8f8f8f'><strike>" + title
                + "</strike>" + detail + "</div></html>"
            : "<html><div style='width:" + textWidth + "px;color:#ededed'>" + title + detail + "</div></html>";
        JCheckBox check = new JCheckBox(markup, done);
        check.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        check.setOpaque(false);
        check.setFocusPainted(false);
        check.setBorderPainted(false);
        if (rumour != null)
        {
            check.setToolTipText(t("Locatie: " + rumour.getLocation() + " | Neem: " + rumour.getRequirements()));
        }
        check.addActionListener(event -> {
            state.setCompleted(task.getId(), check.isSelected());
            refresh();
        });
        row.add(check, BorderLayout.CENTER);

        if (task.isCustom())
        {
            JButton remove = removeButton();
            remove.setToolTipText(t("Taak verwijderen"));
            remove.addActionListener(event -> { state.removeCustom(task.getId()); refresh(); });
            row.add(remove, BorderLayout.EAST);
        }
        addHover(row);
        JPanel target = task.isRecurring()
            ? recurringList
            : (task.getCadence() == TaskCadence.DAILY ? dailyList : weeklyList);
        target.add(row);
        target.add(Box.createRigidArea(new Dimension(0, 5)));
    }

    private JPanel customTaskForm(TaskCadence cadence)
    {
        RoundedPanel card = new RoundedPanel(new Color(31, 31, 31), 9);
        card.setLayout(new BorderLayout(5, 0));
        card.setBorder(BorderFactory.createEmptyBorder(7, 7, 7, 6));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        card.setAlignmentX(LEFT_ALIGNMENT);
        JTextField input = new JTextField();
        input.setToolTipText(t("Eigen " + cadence.getLabel().toLowerCase() + " taak"));
        input.setBackground(INPUT);
        input.setForeground(TEXT);
        input.setCaretColor(ACCENT);
        input.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(57, 57, 57)),
            BorderFactory.createEmptyBorder(4, 6, 4, 6)));
        JButton add = accentButton("+");
        add.setPreferredSize(new Dimension(34, 28));
        add.setMargin(new Insets(0, 0, 0, 0));
        add.addActionListener(event -> {
            if (!input.getText().trim().isEmpty())
            {
                state.addCustom(input.getText(), cadence);
                refresh();
            }
        });
        input.addActionListener(event -> add.doClick());
        card.add(input, BorderLayout.CENTER);
        card.add(add, BorderLayout.EAST);
        return card;
    }

    private void updateProgress(int completed, int total)
    {
        int percentage = total == 0 ? 0 : (int) Math.round(completed * 100.0 / total);
        progressBar.setValue(percentage);
        progressLabel.setText(config.language().isEnglish()
            ? completed + " of " + total + " daily tasks  -  " + percentage + "%" : completed + " van " + total + " dagelijkse taken  -  " + percentage + "%");
        progressLabel.setForeground(completed == total && total > 0 ? SUCCESS : new Color(220, 207, 190));
    }

    private String t(String text)
    {
        return PlannerText.translate(config.language(), text);
    }

    private void localizeChrome()
    {
        if (tabs.getTabCount() == 4)
        {
            tabs.setTitleAt(0, t("TAKEN"));
            tabs.setTitleAt(1, "RUNS");
            tabs.setTitleAt(2, "STARS");
            tabs.setTitleAt(3, t("TAAK"));
            for (int index = 0; index < tabs.getTabCount(); index++)
            {
                if (tabs.getTabComponentAt(index) instanceof JLabel)
                {
                    ((JLabel) tabs.getTabComponentAt(index)).setText(tabs.getTitleAt(index));
                }
            }
        }
        if (headerEyebrow != null) headerEyebrow.setText(t("ACCOUNT ROUTE"));
        if (headerSubtitle != null) headerSubtitle.setText(t("Afwisseling met richting"));
        if (generateButton != null)
        {
            generateButton.setText(t("SKIP / WISSEL TAAK"));
            completeButton.setText(t("AFRONDEN & VOLGENDE"));
            stopButton.setText(t("STOP TAAK (HANDMATIG)"));
            sourcesLabel.setText(t("GEGEVENSBRONNEN"));
            notesLabel.setText(t("PERSOONLIJKE NOTITIES"));
        }
        notesArea.setToolTipText(t("Bijvoorbeeld uitgeleende items; blijft bewaard tot je de tekst verwijdert"));
        bankStatus.setText(statusHtml("BANK", state.bankStamp() == null
            ? t("Nog niet gescand") : t("Snapshot beschikbaar"),
            state.bankStamp() == null ? MUTED : SUCCESS));
        seedVaultStatus.setText(statusHtml("VAULT", state.seedVaultStamp() == null
            ? t("Nog niet gescand") : t("Snapshot beschikbaar"),
            state.seedVaultStamp() == null ? MUTED : SUCCESS));
    }

    private static JPanel sectionIntro(String title, String subtitle)
    {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(3, 2, 11, 2));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));
        panel.setAlignmentX(LEFT_ALIGNMENT);
        JLabel titleLabel = label(title, TEXT, 15, Font.BOLD);
        JLabel subtitleLabel = label("<html><div style='width:190px'>" + htmlEscape(subtitle)
            + "</div></html>", MUTED, 11, Font.PLAIN);
        titleLabel.setAlignmentX(LEFT_ALIGNMENT);
        subtitleLabel.setAlignmentX(LEFT_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 2)));
        panel.add(subtitleLabel);
        return panel;
    }

    private static JLabel sectionLabel(String text)
    {
        JLabel label = label(text, MUTED, 11, Font.BOLD);
        label.setAlignmentX(LEFT_ALIGNMENT);
        return label;
    }

    private static JLabel label(String text, Color color, int size, int style)
    {
        JLabel label = new JLabel(text);
        label.setForeground(color);
        label.setFont(new Font(Font.SANS_SERIF, style, size));
        return label;
    }

    private static JButton accentButton(String text)
    {
        JButton button = new JButton(text);
        button.setBackground(ACCENT_DARK);
        button.setForeground(Color.WHITE);
        button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 11));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT, 1),
            BorderFactory.createEmptyBorder(6, 9, 6, 9)));
        button.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        return button;
    }

    private static JButton ghostButton(String text)
    {
        JButton button = new JButton(text);
        button.setForeground(MUTED);
        button.setBackground(CARD);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
        return button;
    }

    private static JButton removeButton()
    {
        JButton button = new JButton("X");
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(142, 58, 48));
        button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 11));
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setMargin(new Insets(0, 0, 0, 0));
        button.setBorder(BorderFactory.createLineBorder(new Color(214, 103, 85), 1));
        Dimension size = new Dimension(28, 28);
        button.setMinimumSize(size);
        button.setPreferredSize(size);
        button.setMaximumSize(size);
        button.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        return button;
    }

    private static void addHover(RoundedPanel panel)
    {
        panel.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseEntered(MouseEvent event)
            {
                panel.setFill(CARD_HOVER);
            }

            @Override
            public void mouseExited(MouseEvent event)
            {
                panel.setFill(CARD);
            }
        });
    }

    private static String centered(String text, int width)
    {
        return "<html><div style='text-align:center;width:" + width + "px'>" + htmlEscape(text) + "</div></html>";
    }

    private static String statusHtml(String source, String value, Color color)
    {
        String hex = String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
        return "<html><span style='color:" + hex + "'>&#9679;</span> <b>" + source
            + "</b> <span style='color:#a2a2a2'>" + htmlEscape(value) + "</span></html>";
    }

    private static String htmlEscape(String value)
    {
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private static JPanel listPanel()
    {
        JPanel panel = new WidthTrackingPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(9, 5, 10, 5));
        return panel;
    }

    private static JScrollPane scroll(JPanel panel)
    {
        JScrollPane scroll = new JScrollPane(panel);
        scroll.setBorder(null);
        scroll.setBackground(BACKGROUND);
        scroll.getViewport().setBackground(BACKGROUND);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        return scroll;
    }

    private static JLabel tabLabel(String text)
    {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setForeground(TEXT);
        label.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 10));
        int width = Math.max(34, label.getPreferredSize().width + 6);
        label.setPreferredSize(new Dimension(width, 22));
        return label;
    }

    private static final class WidthTrackingPanel extends JPanel implements Scrollable
    {
        @Override
        public Dimension getPreferredScrollableViewportSize()
        {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction)
        {
            return 16;
        }

        @Override
        public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction)
        {
            return Math.max(16, visibleRect.height - 32);
        }

        @Override
        public boolean getScrollableTracksViewportWidth()
        {
            return true;
        }

        @Override
        public boolean getScrollableTracksViewportHeight()
        {
            return false;
        }
    }

    private static final class RoundedPanel extends JPanel
    {
        private Color fill;
        private final int radius;

        private RoundedPanel(Color fill, int radius)
        {
            this.fill = fill;
            this.radius = radius;
            setOpaque(false);
        }

        private void setFill(Color fill)
        {
            this.fill = fill;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics graphics)
        {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(fill);
            g.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g.setColor(new Color(255, 255, 255, 12));
            g.setStroke(new BasicStroke(1f));
            g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            g.dispose();
            super.paintComponent(graphics);
        }
    }

    private static final class HeaderPanel extends JPanel
    {
        private HeaderPanel()
        {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics graphics)
        {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setPaint(new GradientPaint(0, 0, new Color(83, 50, 24), getWidth(), getHeight(), new Color(37, 30, 24)));
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(ACCENT);
            g.fillRect(0, getHeight() - 2, getWidth(), 2);
            g.dispose();
            super.paintComponent(graphics);
        }
    }
}
