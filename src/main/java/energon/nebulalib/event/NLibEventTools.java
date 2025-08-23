package energon.nebulalib.event;

import energon.nebulalib.event.events.EventBase;
import energon.nebulalib.event.test.ITestBase;
import energon.nebulalib.event.test.TEST_FALSE;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.function.Function;

public class NLibEventTools {
    public static int eventIDs = 0;
    public static boolean eventIdIsPresent(int id) {
        return NLibEventHandler.getEventById(id) != null;
    }

    public static boolean eventNameIsPresent(String eventName) {
        return NLibEventHandler.getEventByName(eventName) != null;
    }

    public static boolean canRegisterEvent(int id, String name) {
        for (NLibEventHandler.EVENT test : NLibEventHandler.EVENTS) {
            if (test.eventID == id || test.name.equals(name)) {
                return false;
            }
        }
        return true;
    }

    public static boolean addEvent(NLibEventHandler.EVENT event) {
        if (canRegisterEvent(event.eventID, event.name)) {
            NLibEventHandler.EVENTS.add(event);
            return true;
        }
        return false;
    }

    /**Method for safely adding an event to the list.
     * @param id - a unique identifier for the event, used to reliably find it on both server and client sides.
     * @param name - a unique name for the event, used to reliably find it on both server and client sides.
     * @param description - a description of the event. At the moment, this information can only be accessed through chat commands.
     * @param side - the side of the triggers that caused the event:
     *        PLAYER_TICK – fired every n ticks for the player,
     *        WORLD_TICK – fired every n ticks of world update,
     *        VOID_INTERACT – occurs without player involvement,
     *        PLAYER_INTERACT – triggered when a player starts the event or is a participant,
     *        CUSTOM – disables automatic event execution, used only for custom modifications.
     * @param rarity - determines whether the next event can replace the current one. (if the value is lower or equal, the event is not replaced)
     * @param eventBase - a function that generates an EventBase instance, which will subsequently be executed for a specific player or the world.
     * @param tests - trigger 'tests' after which the player or world will be marked as eligible to start this event.
     * @return - true on success, false if an error occurred.
     */
    public static boolean addEvent(int id, String name, String description, NLibEventHandler.SIDE side, NLibEventHandler.RARITY rarity, Function<EntityPlayer, EventBase> eventBase, ITestBase... tests) {
        if (canRegisterEvent(id, name)) {
            NLibEventHandler.EVENTS.add(new NLibEventHandler.EVENT(id, name, side, rarity, eventBase, description, tests));
            return true;
        }
        return false;
    }

    public static boolean addEvent(String name, String description, NLibEventHandler.SIDE side, NLibEventHandler.RARITY rarity, Function<EntityPlayer, EventBase> eventBase, ITestBase... tests) {
        int eventId = ++eventIDs;
        if (canRegisterEvent(eventId, name)) {
            NLibEventHandler.EVENTS.add(new NLibEventHandler.EVENT(eventId, name, side, rarity, eventBase, description, tests));
            return true;
        }
        return false;
    }

    public static boolean addCustomEvent(String name, String description, NLibEventHandler.RARITY rarity, Function<EntityPlayer, EventBase> eventBase) {
        int eventId = ++eventIDs;
        if (canRegisterEvent(eventId, name)) {
            NLibEventHandler.EVENTS.add(new NLibEventHandler.EVENT(eventId, name, NLibEventHandler.SIDE.CUSTOM, rarity, eventBase, description, new TEST_FALSE()));
            return true;
        }
        return false;
    }

    public static boolean playerEndEvent(EntityPlayer player, int id) {
        EventSaveData data = EventSaveData.get(player.world);
        EventSaveData.EVENT_PLAYER_DATA playerData = data.getPlayerData(player.getName(), false);
        return playerData != null && playerData.playerCompletedEvent(id);
    }

    public static boolean playerEndEvent(World world, String player, int id) {
        EventSaveData.EVENT_PLAYER_DATA playerData = EventSaveData.get(world).getPlayerData(player, false);
        return playerData != null && playerData.playerCompletedEvent(id);
    }

    public static boolean playerEndEvent(EntityPlayer player, String eventName) {
        EventSaveData.EVENT_PLAYER_DATA playerData = EventSaveData.get(player.world).getPlayerData(player.getName(), false);
        NLibEventHandler.EVENT event = NLibEventHandler.getEventByName(eventName);
        return playerData != null && event != null && playerData.playerCompletedEvent(event.eventID);
    }

    public static boolean playerEndEvent(World world, String player, String eventName) {
        EventSaveData.EVENT_PLAYER_DATA playerData = EventSaveData.get(world).getPlayerData(player, false);
        NLibEventHandler.EVENT event = NLibEventHandler.getEventByName(eventName);
        return playerData != null && event != null && playerData.playerCompletedEvent(event.eventID);
    }

    public static boolean playerCanStartEvent(EntityPlayer player, int id, NLibEventHandler.RARITY rarity) {
        EventSaveData data = EventSaveData.get(player.world);
        EventSaveData.EVENT_PLAYER_DATA playerData = data.getPlayerData(player.getName(), false);
        return playerData != null && playerData.playerCanStartEvent(id, rarity);
    }

    public static boolean playerCanStartEvent(World world, String player, int id, NLibEventHandler.RARITY rarity) {
        EventSaveData.EVENT_PLAYER_DATA playerData = EventSaveData.get(world).getPlayerData(player, false);
        return playerData != null && playerData.playerCanStartEvent(id, rarity);
    }

    public static boolean playerCanStartEvent(EntityPlayer player, String eventName, NLibEventHandler.RARITY rarity) {
        EventSaveData.EVENT_PLAYER_DATA playerData = EventSaveData.get(player.world).getPlayerData(player.getName(), false);
        NLibEventHandler.EVENT event = NLibEventHandler.getEventByName(eventName);
        return playerData != null && event != null && playerData.playerCanStartEvent(event.eventID, rarity);
    }

    public static boolean playerCanStartEvent(World world, String player, String eventName, NLibEventHandler.RARITY rarity) {
        EventSaveData.EVENT_PLAYER_DATA playerData = EventSaveData.get(world).getPlayerData(player, false);
        NLibEventHandler.EVENT event = NLibEventHandler.getEventByName(eventName);
        return playerData != null && event != null && playerData.playerCanStartEvent(event.eventID, rarity);
    }

    public static boolean playerCanStartEvent(EntityPlayer player, NLibEventHandler.EVENT event) {
        EventSaveData.EVENT_PLAYER_DATA playerData = EventSaveData.get(player.world).getPlayerData(player.getName(), false);
        return playerData != null && playerData.playerCanStartEvent(event.eventID, event.rarity);
    }

    public static boolean playerCanStartEvent(World world, String player, NLibEventHandler.EVENT event) {
        EventSaveData.EVENT_PLAYER_DATA playerData = EventSaveData.get(world).getPlayerData(player, false);
        return playerData != null && playerData.playerCanStartEvent(event.eventID, event.rarity);
    }


    public static boolean playerStartEvent(EntityPlayer player, int id, NLibEventHandler.RARITY rarity) {
        NLibEventHandler.EVENT event = NLibEventHandler.getEventById(id);
        EventSaveData data = EventSaveData.get(player.world);
        EventSaveData.EVENT_PLAYER_DATA playerData = data.getPlayerData(player.getName(), false);
        if (event != null && playerData != null && playerData.playerCanStartEvent(event.eventID, rarity)) {
            event.startEvent(player, false);
            return true;
        }
        return false;
    }

    public static boolean playerStartEvent(EntityPlayer player, String eventName, NLibEventHandler.RARITY rarity) {
        NLibEventHandler.EVENT event = NLibEventHandler.getEventByName(eventName);
        EventSaveData data = EventSaveData.get(player.world);
        EventSaveData.EVENT_PLAYER_DATA playerData = data.getPlayerData(player.getName(), false);
        if (event != null && playerData != null && playerData.playerCanStartEvent(event.eventID, rarity)) {
            event.startEvent(player, false);
            return true;
        }
        return false;
    }

    public static boolean playerStartEvent(String playerName, int id, NLibEventHandler.RARITY rarity) {
        EntityPlayer player = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUsername(playerName);
        if (player == null) {
            return false;
        }
        NLibEventHandler.EVENT event = NLibEventHandler.getEventById(id);
        EventSaveData data = EventSaveData.get(player.world);
        EventSaveData.EVENT_PLAYER_DATA playerData = data.getPlayerData(player.getName(), false);
        if (event != null && playerData != null && playerData.playerCanStartEvent(event.eventID, rarity)) {
            event.startEvent(player, false);
            return true;
        }
        return false;
    }

    public static boolean playerStartEvent(String playerName, String eventName, NLibEventHandler.RARITY rarity) {
        EntityPlayer player = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUsername(playerName);
        if (player == null) {
            return false;
        }
        NLibEventHandler.EVENT event = NLibEventHandler.getEventByName(eventName);
        EventSaveData data = EventSaveData.get(player.world);
        EventSaveData.EVENT_PLAYER_DATA playerData = data.getPlayerData(player.getName(), false);
        if (event != null && playerData != null && playerData.playerCanStartEvent(event.eventID, rarity)) {
            event.startEvent(player, false);
            return true;
        }
        return false;
    }
}
