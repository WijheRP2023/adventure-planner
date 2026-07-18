package com.adventureplanner;

import java.util.Arrays;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;
import net.runelite.api.Client;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.client.events.NpcLootReceived;
import net.runelite.client.util.Text;

public final class AutomaticChecklistManager
{
    private static final Pattern RUMOUR_COMPLETION = Pattern.compile(
        "you have completed [0-9,]+ rumours? for the hunter guild\\.", Pattern.CASE_INSENSITIVE);

    private static final Set<String> BOSSES = new HashSet<>(Arrays.asList(
        "abyssal sire", "alchemical hydra", "amoxliatl", "araxxor", "artio",
        "barrows", "brutus", "calvar'ion", "callisto", "cerberus", "chaos elemental",
        "chaos fanatic", "commander zilyana", "corporeal beast", "crazy archaeologist",
        "dagannoth prime", "dagannoth rex", "dagannoth supreme", "deranged archaeologist", "dusk",
        "general graardor", "giant mole", "grotesque guardians", "hueycoatl", "kalphite queen",
        "king black dragon", "kraken", "kree'arra", "k'ril tsutsaroth", "phantom muspah",
        "sarachnis", "scorpia", "scurrius", "shellbane gryphon", "spindel",
        "thermonuclear smoke devil", "the leviathan", "venenatis", "vet'ion", "vorkath",
        "yama", "zalcano", "zulrah"));

    private final Client client;
    private final PlannerState state;
    private final Runnable stateChanged;
    private boolean countersInitialized;
    private int previousTears;
    private int previousContractCount;
    private LocalDate lastResetCheck;

    public AutomaticChecklistManager(Client client, PlannerState state, Runnable stateChanged)
    {
        this.client = client;
        this.state = state;
        this.stateChanged = stateChanged;
    }

    public void onGameTick()
    {
        LocalDate today = LocalDate.now();
        if (!today.equals(lastResetCheck))
        {
            state.applyResets();
            lastResetCheck = today;
            stateChanged.run();
        }
        syncDailyClaims();

        int tears = client.getVarbitValue(VarbitID.TOG_TEARS_COLLECTED);
        int contracts = client.getVarbitValue(VarbitID.FARMGUILD_CONTRACT_COUNT);
        if (countersInitialized)
        {
            if (previousTears > 0 && tears == 0) complete("weekly_tears");
            if (contracts == previousContractCount + 1) complete("daily_contract");
        }
        previousTears = tears;
        previousContractCount = contracts;
        countersInitialized = true;
    }

    public void onChatMessage(ChatMessage event)
    {
        String message = Text.removeTags(event.getMessage()).toLowerCase(Locale.ENGLISH).trim();
        HunterRumour detectedRumour = HunterRumourCatalog.detect(message);
        if (detectedRumour != null)
        {
            state.saveCurrentRumour(detectedRumour);
            stateChanged.run();
        }
        if (RUMOUR_COMPLETION.matcher(message).find())
        {
            complete("daily_rumour");
            state.saveCurrentRumour(null);
        }
        if (isMinigameCompletion(message))
        {
            complete("weekly_minigame");
        }
        if (message.contains("congratulations - your raid is complete")
            || message.contains("the theatre of blood: completed"))
        {
            complete("weekly_boss");
        }
    }

    public void onWidgetLoaded(WidgetLoaded event)
    {
        if (event.getGroupId() == InterfaceID.MISC_BOTH_MANAGE
            || event.getGroupId() == InterfaceID.MISC_COLLECTION)
        {
            complete("weekly_kingdom");
        }
    }

    public void onNpcLootReceived(NpcLootReceived event)
    {
        String name = event.getNpc().getName();
        if (name != null && isBoss(name))
        {
            complete("weekly_boss");
        }
    }

    private void syncDailyClaims()
    {
        sync("daily_battlestaves", client.getVarbitValue(VarbitID.ZAFF_LAST_CLAIMED) != 0);
        sync("daily_sand", client.getVarbitValue(VarbitID.YANILLE_SAND_CLAIMED) != 0);
        sync("daily_dynamite", client.getVarbitValue(VarbitID.KOUREND_FREE_DYNAMITE) != 0);
        sync("daily_essence", client.getVarbitValue(VarbitID.ARDOUGNE_FREE_ESSENCE) != 0);
        sync("daily_arrows", client.getVarbitValue(VarbitID.WESTERN_RANTZ_ARROWS) != 0);

        int maximumBonemeal = 0;
        if (client.getVarbitValue(VarbitID.MORYTANIA_DIARY_MEDIUM_COMPLETE) == 1) maximumBonemeal += 13;
        if (client.getVarbitValue(VarbitID.MORYTANIA_DIARY_HARD_COMPLETE) == 1) maximumBonemeal += 13;
        if (client.getVarbitValue(VarbitID.MORYTANIA_DIARY_ELITE_COMPLETE) == 1) maximumBonemeal += 13;
        int collected = client.getVarbitValue(VarbitID.MORYTANIA_SLIME_CLAIMED);
        sync("daily_bonemeal", maximumBonemeal > 0 && collected >= maximumBonemeal);
    }

    private void sync(String taskId, boolean completed)
    {
        if (state.isCompleted(taskId) == completed) return;
        state.setCompleted(taskId, completed);
        stateChanged.run();
    }

    private void complete(String taskId)
    {
        if (state.isCompleted(taskId)) return;
        state.setCompleted(taskId, true);
        stateChanged.run();
    }

    static boolean isMinigameCompletion(String message)
    {
        String text = message.toLowerCase(Locale.ENGLISH);
        return text.contains("successfully subdued the wintertodt")
            || text.contains("successfully subdued tempoross")
            || text.contains("great guardian successfully closed the rift")
            || text.contains("you have been awarded") && text.contains("pest points")
            || text.contains("you won the soul wars game")
            || text.contains("your team won the castle wars game");
    }

    static boolean isBoss(String name)
    {
        return BOSSES.contains(name.toLowerCase(Locale.ENGLISH));
    }
}
