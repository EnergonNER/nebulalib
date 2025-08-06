package energon.nebulalib.network;

import energon.nebulalib.event.NLibEventHandler;
import energon.nebulalib.event.EventSaveData;
import energon.nebulalib.event.events.EventBase;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.DimensionManager;
import org.apache.commons.lang3.ArrayUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NLibEventCommand extends CommandBase {
    @Override
    public String getName() {
        return "nebulalib_event";
    }

    @Override
    public String getUsage(ICommandSender iCommandSender) {
        return "nebulalib_event <text>";
    }

    @Override
    public boolean checkPermission(MinecraftServer p_checkPermission_1_, ICommandSender sender) {
        return sender.canUseCommand(2, "nebulalib_event");
    }

    public String getNameIDEventList(int[] events) {
        StringBuilder builder = new StringBuilder("[");
        oy:
        for (int i : events) {
            builder.append("\n    ID: ").append(i).append("  NAME: \"");
            for (NLibEventHandler.EVENT test : NLibEventHandler.EVENTS) {
                if (test.eventID == i) {
                    builder.append(test.name).append("\", ");
                    continue oy;
                }
            }
            builder.append("ERROR (Not Found)\", ");
        }
        return builder.append("\n]").toString();
    }

    public void help(ICommandSender iCommandSender) {
        iCommandSender.sendMessage(new TextComponentString("------------------------" +
                "\n> debug - ." +
                "\n> help - ." +
                "\n> list - ." +
                "\n> player - ." +
                "\n>    clear  <player_name>" +
                "\n>    data - ." +
                "\n>       add    <player_name> <event_id>" +
                "\n>       clear  <player_name>" +
                "\n>       info   <player_name>" +
                "\n>       remove <player_name> <event_id>" +
                "\n>    info   <player_name>" +
                "\n>    remove <player_name> <id-name>" +
                "\n>    set    <player_name> <id-name>" +
                "\n> world - ." +
                "\n>    clear  <world_id>" +
                "\n>    data - ." +
                "\n>       add    <world_id> <event_id>" +
                "\n>       clear  <world_id>" +
                "\n>       info   <world_id>" +
                "\n>       remove <world_id> <event_id>" +
                "\n>    info   <world_id>" +
                "\n>    remove <world_id> <id-name>" +
                "\n>    set    <world_id> <id-name>" +
                "\n------------------------"));
    }

    @Override
    public void execute(MinecraftServer minecraftServer, ICommandSender iCommandSender, String[] strings) throws CommandException {
        if (strings.length == 0) {
            this.help(iCommandSender);
            return;
        }
        switch (strings[0]) {
            case "debug":
                NLibEventHandler.DEBUG = !NLibEventHandler.DEBUG;
                iCommandSender.sendMessage(new TextComponentString("Debug - " + NLibEventHandler.DEBUG));
                break;
            case "help":
                this.help(iCommandSender);
                break;
            case "list":
                StringBuilder infoList = new StringBuilder("------------------------");
                for (NLibEventHandler.EVENT event : NLibEventHandler.EVENTS) {
                    infoList.append("\nEVENT_ID: ").append(event.eventID).append("\nEVENT_NAME: ").append(event.name).append("\n> INFO: ").append(event.description);
                    infoList.append("\n------------------------");
                }
                iCommandSender.sendMessage(new TextComponentString(infoList.toString()));
                break;
            case "world":
                if (strings.length == 1) {
                    iCommandSender.sendMessage(new TextComponentString("<?>"));
                    return;
                }
                switch (strings[1]) {
                    case "info":
                        List<EventBase> local = new ArrayList<>(NLibEventHandler.WORLDS_EVENT);
                        if (local.isEmpty()) {
                            iCommandSender.sendMessage(new TextComponentString("------------------------\nEMPTY\n------------------------"));
                            return;
                        }
                        if (strings.length == 3) {
                            int dimID = Integer.parseInt(strings[2]);
                            StringBuilder builder = new StringBuilder("------------------------");
                            for (EventBase event : local) {
                                if (event.world != null && event.world.provider.getDimension() == dimID) {
                                    builder.append("\nWORLD_ID: ").append(dimID);
                                    builder.append("\n> CORRECT_EVENT: ").append(event.eventID).append("\n> TIME: ").append(event.eventTime).append("\n> PROGRESS: ").append(event.eventProgress);
                                    break;
                                }
                            }
                            builder.append("\n------------------------");
                            iCommandSender.sendMessage(new TextComponentString(builder.toString()));
                            break;
                        }
                        StringBuilder builder = new StringBuilder("------------------------");
                        for (EventBase event : local) {
                            if (event.world != null) {
                                builder.append("\nWORLD_ID: ").append(event.world.provider.getDimension());
                            } else {
                                builder.append("\nWORLD_ID: [null]");
                            }
                            builder.append("\n> CORRECT_EVENT: ").append(event.eventID).append("\n> TIME: ").append(event.eventTime).append("\n> PROGRESS: ").append(event.eventProgress);
                        }
                        builder.append("\n------------------------");
                        iCommandSender.sendMessage(new TextComponentString(builder.toString()));
                        break;
                    case "set":
                        if (strings.length == 4) {
                            Integer newEvent;
                            try {
                                newEvent = Integer.parseInt(strings[3]);
                            } catch (NumberFormatException ignored) {
                                newEvent = null;
                            }
                            if (newEvent != null) {
                                for (NLibEventHandler.EVENT event : NLibEventHandler.EVENTS) {
                                    if (event.eventID == newEvent) {
                                        if (event.side.isWorldUpdateEvent()) {
                                            event.startEvent(DimensionManager.getWorld(Integer.parseInt(strings[2])), false);
                                            iCommandSender.sendMessage(new TextComponentString("Event set to – \"" + event.name + "\"."));
                                        } else {
                                            iCommandSender.sendMessage(new TextComponentString("ERROR! (Player Event)"));
                                        }
                                        return;
                                    }
                                }
                                iCommandSender.sendMessage(new TextComponentString("ERROR! (Event not found)"));
                            } else {
                                for (NLibEventHandler.EVENT event : NLibEventHandler.EVENTS) {
                                    if (event.name.equals(strings[3])) {
                                        if (event.side.isWorldUpdateEvent()) {
                                            event.startEvent(DimensionManager.getWorld(Integer.parseInt(strings[2])), false);
                                            iCommandSender.sendMessage(new TextComponentString("Event set to – \"" + event.name + "\"."));
                                        } else {
                                            iCommandSender.sendMessage(new TextComponentString("ERROR! (Player Event)"));
                                        }
                                        return;
                                    }
                                }
                                iCommandSender.sendMessage(new TextComponentString("ERROR! (Event not found)"));
                            }
                            return;
                        }
                        iCommandSender.sendMessage(new TextComponentString(strings.length == 2 ? "<world_id>" : "<id-name>"));
                        return;
                    case "clear":
                        if (strings.length == 3) {
                            int worldID = Integer.parseInt(strings[2]);
                            for (EventBase base : NLibEventHandler.WORLDS_EVENT) {
                                if (base.world != null && base.world.provider.getDimension() == worldID) {
                                    base.eventProgress += base.eventTime;
                                }
                            }
                            iCommandSender.sendMessage(new TextComponentString("List cleared."));
                            break;
                        }
                        for (EventBase base : NLibEventHandler.WORLDS_EVENT) {
                            if (base.world != null) {
                                base.eventProgress += base.eventTime;
                            }
                        }
                        iCommandSender.sendMessage(new TextComponentString("List cleared."));
                        break;
                    case "remove":
                        if (strings.length == 4) {
                            Integer removeEvent;
                            int dimID = Integer.parseInt(strings[2]);
                            try {
                                removeEvent = Integer.parseInt(strings[3]);
                            } catch (NumberFormatException ignored) {
                                removeEvent = null;
                            }
                            if (removeEvent != null) {
                                for (EventBase base : NLibEventHandler.WORLDS_EVENT) {
                                    if (base.world != null && base.world.provider.getDimension() == dimID && base.eventID == removeEvent) {
                                        base.eventProgress += base.eventTime;
                                        iCommandSender.sendMessage(new TextComponentString("Event deleted."));
                                        return;
                                    }
                                }
                            } else {
                                for (NLibEventHandler.EVENT event : NLibEventHandler.EVENTS) {
                                    if (event.side.isWorldUpdateEvent() && event.name.equals(strings[3])) {
                                        removeEvent = event.eventID;
                                    }
                                }
                                if (removeEvent != null) {
                                    for (EventBase base : NLibEventHandler.WORLDS_EVENT) {
                                        if (base.world != null && base.world.provider.getDimension() == dimID && base.eventID == removeEvent) {
                                            base.eventProgress += base.eventTime;
                                            iCommandSender.sendMessage(new TextComponentString("Event deleted."));
                                            return;
                                        }
                                    }
                                }
                            }
                            return;
                        }
                        iCommandSender.sendMessage(new TextComponentString(strings.length == 2 ? "<world_id>" : "<id-name>"));
                        return;
                    case "data":
                        if (strings.length == 2) {
                            iCommandSender.sendMessage(new TextComponentString("<?>"));
                            return;
                        }
                        switch (strings[2]) {
                            case "info":
                                EventSaveData da3ta = EventSaveData.get(iCommandSender.getEntityWorld());
                                if (strings.length == 4) {
                                    int dimID = Integer.parseInt(strings[3]);
                                    StringBuilder builde2r = new StringBuilder("------------------------");
                                    builde2r.append("\nDIM_ID: ").append(dimID);
                                    EventSaveData.EVENT_WORLD_DATA worldData = da3ta.getWorldData(dimID, false);
                                    if (worldData == null) {
                                        builde2r.append("ERROR!");
                                    } else {
                                        builde2r.append("\n> CORRECT_EVENT: ").append(worldData.correctEvent).append("\n> ENDED: ").append(worldData.correctEventEnded).append("\n> RARITY: ").append(worldData.correctEventRarity.name.toUpperCase());
                                        builde2r.append("\n> VARIABLES: ").append(worldData.variable);
                                        builde2r.append("\n> ENDED_EVENTS: ").append(this.getNameIDEventList(worldData.eventsEnded));
                                    }
                                    builde2r.append("\n------------------------");
                                    iCommandSender.sendMessage(new TextComponentString(builde2r.toString()));
                                    return;
                                }
                                StringBuilder builde2r = new StringBuilder("------------------------");
                                for (EventSaveData.EVENT_WORLD_DATA worldData : da3ta.worldsEventData) {
                                    builde2r.append("\nDIM_ID: ").append(worldData.worldID);
                                    builde2r.append("\n> CORRECT_EVENT: ").append(worldData.correctEvent).append("\n> ENDED: ").append(worldData.correctEventEnded).append("\n> RARITY: ").append(worldData.correctEventRarity.name.toUpperCase());
                                    builde2r.append("\n> VARIABLES: ").append(worldData.variable);
                                    builde2r.append("\n> ENDED_EVENTS: ").append(this.getNameIDEventList(worldData.eventsEnded));
                                }
                                builde2r.append("\n------------------------");
                                iCommandSender.sendMessage(new TextComponentString(builde2r.toString()));
                                break;
                            case "clear":
                                EventSaveData saveData = EventSaveData.get(iCommandSender.getEntityWorld());
                                if (strings.length == 4) {
                                    int dimID = Integer.parseInt(strings[3]);
                                    for (EventSaveData.EVENT_WORLD_DATA worldData : saveData.worldsEventData) {
                                        if (worldData.worldID == dimID) {
                                            worldData.eventsEnded = new int[]{};
                                            worldData.correctEvent = 0;
                                            worldData.correctEventEnded = true;
                                            worldData.correctEventRarity = NLibEventHandler.RARITY.COMMON;
                                            worldData.variable = "";
                                        }
                                    }
                                    saveData.setDirty(true);
                                    iCommandSender.sendMessage(new TextComponentString("List cleared."));
                                    return;
                                }
                                for (EventSaveData.EVENT_WORLD_DATA worldData : saveData.worldsEventData) {
                                    worldData.eventsEnded = new int[]{};
                                    worldData.correctEvent = 0;
                                    worldData.correctEventEnded = true;
                                    worldData.correctEventRarity = NLibEventHandler.RARITY.COMMON;
                                    worldData.variable = "";
                                }
                                saveData.setDirty(true);
                                iCommandSender.sendMessage(new TextComponentString("List cleared."));
                                break;
                            case "add":
                                if (strings.length == 5) {
                                    int dimID = Integer.parseInt(strings[3]);
                                    Integer newEvent;
                                    try {
                                        newEvent = Integer.parseInt(strings[4]);
                                        if (newEvent == 0) {
                                            iCommandSender.sendMessage(new TextComponentString("ERROR! (Event ID != 0)"));
                                        }
                                    } catch (NumberFormatException ignored) {
                                        newEvent = null;
                                    }
                                    if (newEvent != null) {
                                        for (NLibEventHandler.EVENT event : NLibEventHandler.EVENTS) {
                                            if (event.eventID == newEvent) {
                                                if (event.side.isWorldUpdateEvent()) {
                                                    EventSaveData save1Data = EventSaveData.get(iCommandSender.getEntityWorld());
                                                    for (EventSaveData.EVENT_WORLD_DATA worldData : save1Data.worldsEventData) {
                                                        if (worldData.worldID == dimID) {
                                                            if (!worldData.worldCompletedEvent(event.eventID)) {
                                                                worldData.eventsEnded = ArrayUtils.add(worldData.eventsEnded, event.eventID);
                                                            }
                                                            iCommandSender.sendMessage(new TextComponentString("Event - \"" + event.name + "\" added to data."));
                                                            return;
                                                        }
                                                    }
                                                    iCommandSender.sendMessage(new TextComponentString("ERROR! (Dimension ID not found)"));
                                                } else {
                                                    iCommandSender.sendMessage(new TextComponentString("ERROR! (Player Event)"));
                                                }
                                                return;
                                            }
                                        }
                                        iCommandSender.sendMessage(new TextComponentString("ERROR! (Event not found)"));
                                    } else {
                                        for (NLibEventHandler.EVENT event : NLibEventHandler.EVENTS) {
                                            if (event.name.equals(strings[4])) {
                                                if (event.side.isWorldUpdateEvent()) {
                                                    EventSaveData save1Data = EventSaveData.get(iCommandSender.getEntityWorld());
                                                    for (EventSaveData.EVENT_WORLD_DATA worldData : save1Data.worldsEventData) {
                                                        if (worldData.worldID == dimID) {
                                                            if (!worldData.worldCompletedEvent(event.eventID)) {
                                                                worldData.eventsEnded = ArrayUtils.add(worldData.eventsEnded, event.eventID);
                                                            }
                                                            iCommandSender.sendMessage(new TextComponentString("Event - \"" + event.name + "\" added to data."));
                                                            return;
                                                        }
                                                    }
                                                    iCommandSender.sendMessage(new TextComponentString("ERROR! (Dimension ID not found)"));
                                                } else {
                                                    iCommandSender.sendMessage(new TextComponentString("ERROR! (Player Event)"));
                                                }
                                                return;
                                            }
                                        }
                                        iCommandSender.sendMessage(new TextComponentString("ERROR! (Event not found)"));
                                    }
                                }
                                iCommandSender.sendMessage(new TextComponentString(strings.length == 4 ? "<event_name-id>" : "<world_id>"));
                                break;
                            case "remove":
                                if (strings.length == 5) {
                                    int dimID = Integer.parseInt(strings[3]);
                                    Integer removeEvent;
                                    try {
                                        removeEvent = Integer.parseInt(strings[4]);
                                        if (removeEvent == 0) {
                                            iCommandSender.sendMessage(new TextComponentString("ERROR! (Event ID != 0)"));
                                            return;
                                        }
                                    } catch (NumberFormatException ignored) {
                                        removeEvent = null;
                                    }
                                    if (removeEvent != null) {
                                        for (NLibEventHandler.EVENT event : NLibEventHandler.EVENTS) {
                                            if (event.eventID == removeEvent) {
                                                if (event.side.isWorldUpdateEvent()) {
                                                    EventSaveData save1Data = EventSaveData.get(iCommandSender.getEntityWorld());
                                                    for (EventSaveData.EVENT_WORLD_DATA worldData : save1Data.worldsEventData) {
                                                        if (worldData.worldID == dimID) {
                                                            worldData.eventsEnded = Arrays.stream(worldData.eventsEnded).filter(i -> i != event.eventID).toArray();
                                                            iCommandSender.sendMessage(new TextComponentString("Event - \"" + event.name + "\" removed from data."));
                                                            return;
                                                        }
                                                    }
                                                    iCommandSender.sendMessage(new TextComponentString("ERROR! (Dimension ID not found)"));
                                                } else {
                                                    iCommandSender.sendMessage(new TextComponentString("ERROR! (Player Event)"));
                                                }
                                                return;
                                            }
                                        }
                                    } else {
                                        for (NLibEventHandler.EVENT event : NLibEventHandler.EVENTS) {
                                            if (event.name.equals(strings[4])) {
                                                if (event.side.isWorldUpdateEvent()) {
                                                    EventSaveData save1Data = EventSaveData.get(iCommandSender.getEntityWorld());
                                                    for (EventSaveData.EVENT_WORLD_DATA worldData : save1Data.worldsEventData) {
                                                        if (worldData.worldID == dimID) {
                                                            worldData.eventsEnded = Arrays.stream(worldData.eventsEnded).filter(i -> i != event.eventID).toArray();
                                                            iCommandSender.sendMessage(new TextComponentString("Event - \"" + event.name + "\" removed from data."));
                                                            return;
                                                        }
                                                    }
                                                    iCommandSender.sendMessage(new TextComponentString("ERROR! (Dimension ID not found)"));
                                                } else {
                                                    iCommandSender.sendMessage(new TextComponentString("ERROR! (Player Event)"));
                                                }
                                                return;
                                            }
                                        }
                                    }
                                    iCommandSender.sendMessage(new TextComponentString("ERROR! (Event not found)"));
                                    return;
                                }
                                iCommandSender.sendMessage(new TextComponentString(strings.length == 4 ? "<event_name-id>" : "<world_id>"));
                                return;
                        }
                        break;
                }
                break;
            case "player":
                if (strings.length == 1) {
                    iCommandSender.sendMessage(new TextComponentString("<?>"));
                    return;
                }
                switch (strings[1]) {
                    case "info":
                        List<EventBase> local = new ArrayList<>(NLibEventHandler.PLAYERS_EVENT);
                        if (local.isEmpty()) {
                            iCommandSender.sendMessage(new TextComponentString("------------------------\nEMPTY\n------------------------"));
                            return;
                        }
                        if (strings.length == 3) {
                            StringBuilder builder = new StringBuilder("------------------------");
                            for (EventBase event : local) {
                                if (event.player != null && event.player.getName().equals(strings[2])) {
                                    builder.append("\nPLAYER_NAME: ").append(event.player.getName());
                                    builder.append("\n> CORRECT_EVENT: ").append(event.eventID).append("\n> TIME: ").append(event.eventTime).append("\n> PROGRESS: ").append(event.eventProgress);
                                    break;
                                }
                            }
                            builder.append("\n------------------------");
                            iCommandSender.sendMessage(new TextComponentString(builder.toString()));
                            break;
                        }
                        StringBuilder builder = new StringBuilder("------------------------");
                        for (EventBase event : local) {
                            if (event.player != null) {
                                builder.append("\nPLAYER_NAME: ").append(event.player.getName());
                            } else {
                                builder.append("\nPLAYER_NAME: [null]");
                            }
                            builder.append("\n> CORRECT_EVENT: ").append(event.eventID).append("\n> TIME: ").append(event.eventTime).append("\n> PROGRESS: ").append(event.eventProgress);
                        }
                        builder.append("\n------------------------");
                        iCommandSender.sendMessage(new TextComponentString(builder.toString()));
                        break;
                    case "set":
                        if (strings.length == 4) {
                            Integer newEvent;
                            try {
                                newEvent = Integer.parseInt(strings[3]);
                            } catch (NumberFormatException ignored) {
                                newEvent = null;
                            }
                            if (newEvent != null) {
                                for (NLibEventHandler.EVENT event : NLibEventHandler.EVENTS) {
                                    if (event.eventID == newEvent) {
                                        if (event.side.isPlayerEvent()) {
                                            EntityPlayer player = minecraftServer.getPlayerList().getPlayerByUsername(strings[2]);
                                            if (player != null) {
                                                event.startEvent(player, false);
                                                iCommandSender.sendMessage(new TextComponentString("Event set to – \"" + event.name + "\""));
                                            }
                                        } else {
                                            iCommandSender.sendMessage(new TextComponentString("ERROR! (World Event)"));
                                        }
                                        return;
                                    }
                                }
                                iCommandSender.sendMessage(new TextComponentString("ERROR! (Event not found)"));
                            } else {
                                for (NLibEventHandler.EVENT event : NLibEventHandler.EVENTS) {
                                    if (event.name.equals(strings[3])) {
                                        if (event.side.isPlayerEvent()) {
                                            EntityPlayer player = minecraftServer.getPlayerList().getPlayerByUsername(strings[2]);
                                            if (player != null) {
                                                event.startEvent(player, false);
                                                iCommandSender.sendMessage(new TextComponentString("Event set to – \"" + event.name + "\""));
                                            }
                                        } else {
                                            iCommandSender.sendMessage(new TextComponentString("ERROR! (World Event)"));
                                        }
                                        return;
                                    }
                                }
                                iCommandSender.sendMessage(new TextComponentString("ERROR! (Event not found)"));
                            }
                            return;
                        }
                        iCommandSender.sendMessage(new TextComponentString(strings.length == 2 ? "<player_name>" : "<id-name>"));
                        break;
                    case "clear":
                        if (strings.length == 3) {
                            for (EventBase base : NLibEventHandler.PLAYERS_EVENT) {
                                if (base.player != null && base.player.getName().equals(strings[2])) {
                                    base.eventProgress += base.eventTime;
                                    NLibNetwork.sendPlayerEvent(minecraftServer.getPlayerList().getPlayerByUsername(strings[2]), 0);
                                }
                            }
                            iCommandSender.sendMessage(new TextComponentString("List cleared."));
                            break;
                        }
                        for (EventBase base : NLibEventHandler.PLAYERS_EVENT) {
                            if (base.player != null) {
                                base.eventProgress += base.eventTime;
                                NLibNetwork.sendPlayerEvent(minecraftServer.getPlayerList().getPlayerByUsername(base.player.getName()), 0);
                            }
                        }
                        iCommandSender.sendMessage(new TextComponentString("List cleared."));
                        break;
                    case "remove":
                        if (strings.length == 4) {
                            Integer removeEvent;
                            try {
                                removeEvent = Integer.parseInt(strings[3]);
                            } catch (NumberFormatException ignored) {
                                removeEvent = null;
                            }
                            if (removeEvent != null) {
                                for (EventBase base : NLibEventHandler.PLAYERS_EVENT) {
                                    if (base.world == null && base.player != null && base.player.getName().equals(strings[2]) && base.eventID == removeEvent) {
                                        base.eventProgress += base.eventTime;
                                        NLibNetwork.sendPlayerEvent(minecraftServer.getPlayerList().getPlayerByUsername(strings[2]), 0);
                                        iCommandSender.sendMessage(new TextComponentString("Event deleted."));
                                        return;
                                    }
                                }
                            } else {
                                for (NLibEventHandler.EVENT event : NLibEventHandler.EVENTS) {
                                    if (event.side.isPlayerEvent() && event.name.equals(strings[3])) {
                                        removeEvent = event.eventID;
                                    }
                                }
                                if (removeEvent != null) {
                                    for (EventBase base : NLibEventHandler.PLAYERS_EVENT) {
                                        if (base.world == null && base.player != null && base.player.getName().equals(strings[2]) && base.eventID == removeEvent) {
                                            base.eventProgress += base.eventTime;
                                            NLibNetwork.sendPlayerEvent(minecraftServer.getPlayerList().getPlayerByUsername(strings[2]), 0);
                                            iCommandSender.sendMessage(new TextComponentString("Event deleted."));
                                            return;
                                        }
                                    }
                                }
                            }
                            return;
                        }
                        iCommandSender.sendMessage(new TextComponentString(strings.length == 2 ? "<player_name>" : "<id-name>"));
                        return;
                    case "data":
                        if (strings.length == 2) {
                            iCommandSender.sendMessage(new TextComponentString("<?>"));
                            return;
                        }
                        switch (strings[2]) {
                            case "info":
                                EventSaveData dataR = EventSaveData.get(iCommandSender.getEntityWorld());
                                if (strings.length == 4) {
                                    StringBuilder builderR = new StringBuilder("------------------------");
                                    builderR.append("\nPLAYER_NAME: ").append(strings[3]);
                                    EventSaveData.EVENT_PLAYER_DATA playerData = dataR.getPlayerData(strings[3], false);
                                    if (playerData == null) {
                                        builderR.append("ERROR!");
                                    } else {
                                        builderR.append("\n> CORRECT_EVENT: ").append(playerData.correctEvent).append("\n> ENDED: ").append(playerData.correctEventEnded).append("\n> RARITY: ").append(playerData.correctEventRarity.name.toUpperCase());
                                        builderR.append("\n> VARIABLES: ").append(playerData.variable);
                                        builderR.append("\n> ENDED_EVENTS: ").append(this.getNameIDEventList(playerData.eventsEnded));
                                    }
                                    builderR.append("\n------------------------");
                                    iCommandSender.sendMessage(new TextComponentString(builderR.toString()));
                                    break;
                                }
                                StringBuilder builderR = new StringBuilder("------------------------");
                                for (EventSaveData.EVENT_PLAYER_DATA playerData : dataR.playersEventData) {
                                    builderR.append("\nPLAYER_NAME: ").append(playerData.player);
                                    builderR.append("\n> CORRECT_EVENT: ").append(playerData.correctEvent).append("\n> ENDED: ").append(playerData.correctEventEnded).append("\n> RARITY: ").append(playerData.correctEventRarity.name.toUpperCase());
                                    builderR.append("\n> VARIABLES: ").append(playerData.variable);
                                    builderR.append("\n> ENDED_EVENTS: ").append(this.getNameIDEventList(playerData.eventsEnded));
                                }
                                builderR.append("\n------------------------");
                                iCommandSender.sendMessage(new TextComponentString(builderR.toString()));
                                break;
                            case "clear":
                                EventSaveData saveData = EventSaveData.get(iCommandSender.getEntityWorld());
                                if (strings.length == 4) {
                                    for (EventSaveData.EVENT_PLAYER_DATA playerData : saveData.playersEventData) {
                                        if (playerData.player.equals(strings[3])) {
                                            playerData.eventsEnded = new int[]{};
                                            playerData.correctEvent = 0;
                                            playerData.correctEventEnded = true;
                                            playerData.correctEventRarity = NLibEventHandler.RARITY.COMMON;
                                            playerData.variable = "";
                                        }
                                    }
                                    saveData.setDirty(true);
                                    iCommandSender.sendMessage(new TextComponentString("List cleared."));
                                    return;
                                }
                                for (EventSaveData.EVENT_PLAYER_DATA playerData : saveData.playersEventData) {
                                    playerData.eventsEnded = new int[]{};
                                    playerData.correctEvent = 0;
                                    playerData.correctEventEnded = true;
                                    playerData.correctEventRarity = NLibEventHandler.RARITY.COMMON;
                                    playerData.variable = "";
                                }
                                saveData.setDirty(true);
                                iCommandSender.sendMessage(new TextComponentString("List cleared."));
                                break;
                            case "add":
                                if (strings.length == 5) {
                                    Integer newEvent;
                                    try {
                                        newEvent = Integer.parseInt(strings[4]);
                                        if (newEvent == 0) {
                                            iCommandSender.sendMessage(new TextComponentString("ERROR! (Event ID != 0)"));
                                        }
                                    } catch (NumberFormatException ignored) {
                                        newEvent = null;
                                    }
                                    if (newEvent != null) {
                                        for (NLibEventHandler.EVENT event : NLibEventHandler.EVENTS) {
                                            if (event.eventID == newEvent) {
                                                if (event.side.isPlayerEvent()) {
                                                    EventSaveData save1Data = EventSaveData.get(iCommandSender.getEntityWorld());
                                                    for (EventSaveData.EVENT_PLAYER_DATA playerData : save1Data.playersEventData) {
                                                        if (playerData.player.equals(strings[3])) {
                                                            if (!playerData.playerCompletedEvent(event.eventID)) {
                                                                playerData.eventsEnded = ArrayUtils.add(playerData.eventsEnded, event.eventID);
                                                            }
                                                            iCommandSender.sendMessage(new TextComponentString("Event - \"" + event.name + "\" added to data."));
                                                            return;
                                                        }
                                                    }
                                                    iCommandSender.sendMessage(new TextComponentString("ERROR! (Player not found)"));
                                                } else {
                                                    iCommandSender.sendMessage(new TextComponentString("ERROR! (World Event)"));
                                                }
                                                return;
                                            }
                                        }
                                        iCommandSender.sendMessage(new TextComponentString("ERROR! (Event not found)"));
                                    } else {
                                        for (NLibEventHandler.EVENT event : NLibEventHandler.EVENTS) {
                                            if (event.name.equals(strings[4])) {
                                                if (event.side.isPlayerEvent()) {
                                                    EventSaveData save1Data = EventSaveData.get(iCommandSender.getEntityWorld());
                                                    for (EventSaveData.EVENT_PLAYER_DATA playerData : save1Data.playersEventData) {
                                                        if (playerData.player.equals(strings[3])) {
                                                            if (!playerData.playerCompletedEvent(event.eventID)) {
                                                                playerData.eventsEnded = ArrayUtils.add(playerData.eventsEnded, event.eventID);
                                                            }
                                                            iCommandSender.sendMessage(new TextComponentString("Event - \"" + event.name + "\" added to data."));
                                                            return;
                                                        }
                                                    }
                                                    iCommandSender.sendMessage(new TextComponentString("ERROR! (Player not found)"));
                                                } else {
                                                    iCommandSender.sendMessage(new TextComponentString("ERROR! (World Event)"));
                                                }
                                                return;
                                            }
                                        }
                                        iCommandSender.sendMessage(new TextComponentString("ERROR! (Event not found)"));
                                    }
                                }
                                iCommandSender.sendMessage(new TextComponentString(strings.length == 4 ? "<event_name-id>" : "<player_name>"));
                                break;
                            case "remove":
                                if (strings.length == 5) {
                                    Integer removeEvent;
                                    try {
                                        removeEvent = Integer.parseInt(strings[4]);
                                        if (removeEvent == 0) {
                                            iCommandSender.sendMessage(new TextComponentString("ERROR! (Event ID != 0)"));
                                            return;
                                        }
                                    } catch (NumberFormatException ignored) {
                                        removeEvent = null;
                                    }
                                    if (removeEvent != null) {
                                        for (NLibEventHandler.EVENT event : NLibEventHandler.EVENTS) {
                                            if (event.eventID == removeEvent) {
                                                if (event.side.isPlayerEvent()) {
                                                    EventSaveData save1Data = EventSaveData.get(iCommandSender.getEntityWorld());
                                                    for (EventSaveData.EVENT_PLAYER_DATA playerData : save1Data.playersEventData) {
                                                        if (playerData.player.equals(strings[3])) {
                                                            playerData.eventsEnded = Arrays.stream(playerData.eventsEnded).filter(i -> i != event.eventID).toArray();
                                                            iCommandSender.sendMessage(new TextComponentString("Event - \"" + event.name + "\" removed from data."));
                                                            return;
                                                        }
                                                    }
                                                    iCommandSender.sendMessage(new TextComponentString("ERROR! (Player not found)"));
                                                } else {
                                                    iCommandSender.sendMessage(new TextComponentString("ERROR! (World Event)"));
                                                }
                                                return;
                                            }
                                        }
                                    } else {
                                        for (NLibEventHandler.EVENT event : NLibEventHandler.EVENTS) {
                                            if (event.name.equals(strings[4])) {
                                                if (event.side.isPlayerEvent()) {
                                                    EventSaveData save1Data = EventSaveData.get(iCommandSender.getEntityWorld());
                                                    for (EventSaveData.EVENT_PLAYER_DATA playerData : save1Data.playersEventData) {
                                                        if (playerData.player.equals(strings[3])) {
                                                            playerData.eventsEnded = Arrays.stream(playerData.eventsEnded).filter(i -> i != event.eventID).toArray();
                                                            iCommandSender.sendMessage(new TextComponentString("Event - \"" + event.name + "\" removed from data."));
                                                            return;
                                                        }
                                                    }
                                                    iCommandSender.sendMessage(new TextComponentString("ERROR! (Player not found)"));
                                                } else {
                                                    iCommandSender.sendMessage(new TextComponentString("ERROR! (World Event)"));
                                                }
                                                return;
                                            }
                                        }
                                    }
                                    iCommandSender.sendMessage(new TextComponentString("ERROR! (Event not found)"));
                                    return;
                                }
                                iCommandSender.sendMessage(new TextComponentString(strings.length == 4 ? "<event_name-id>" : "<player_name>"));
                                return;
                        }
                        break;
                }
                break;
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] strings, @Nullable BlockPos pos) {
        List<String> tab = new ArrayList<>();
        if (strings.length == 1) {
            tab.add("world");
            tab.add("player");
            tab.add("list");
            tab.add("help");
            tab.add("debug");
            return tab;
        }
        switch (strings[0]) {
            case "list":

                break;
            case "player":
                if (strings.length == 2) {
                    tab.add("info");
                    tab.add("set");
                    tab.add("data");
                    tab.add("remove");
                    tab.add("clear");
                    break;
                }
                switch (strings[1]) {
                    case "info":
                    case "clear":
                        for (EntityPlayer player : server.getPlayerList().getPlayers()) {
                            tab.add(player.getName());
                        }
                        break;
                    case "set":
                    case "remove":
                        if (strings.length == 4) {
                            for (NLibEventHandler.EVENT event : NLibEventHandler.EVENTS) {
                                if (event.side.isPlayerEvent()) {
                                    tab.add(event.name);
                                }
                            }
                            break;
                        }
                        for (EntityPlayer player : server.getPlayerList().getPlayers()) {
                            tab.add(player.getName());
                        }
                        break;
                    case "data":
                        if (strings.length == 3) {
                            tab.add("info");
                            tab.add("add");
                            tab.add("remove");
                            tab.add("clear");
                            break;
                        }
                        switch (strings[2]) {
                            case "info":
                            case "clear":
                                for (EntityPlayer player : server.getPlayerList().getPlayers()) {
                                    tab.add(player.getName());
                                }
                                break;
                            case "add":
                            case "remove":
                                if (strings.length == 5) {
                                    for (NLibEventHandler.EVENT event : NLibEventHandler.EVENTS) {
                                        if (event.side.isPlayerEvent()) {
                                            tab.add(event.name);
                                        }
                                    }
                                    break;
                                }
                                for (EntityPlayer player : server.getPlayerList().getPlayers()) {
                                    tab.add(player.getName());
                                }
                                break;
                        }
                        break;
                }
                break;
            case "world":
                if (strings.length == 2) {
                    tab.add("info");
                    tab.add("set");
                    tab.add("data");
                    tab.add("remove");
                    tab.add("clear");
                    break;
                }
                switch (strings[1]) {
                    case "info":
                    case "clear":
                        for (int id : DimensionManager.getStaticDimensionIDs()) {
                            tab.add(String.valueOf(id));
                        }
                        break;
                    case "set":
                    case "remove":
                        if (strings.length == 4) {
                            for (NLibEventHandler.EVENT event : NLibEventHandler.EVENTS) {
                                if (event.side.isWorldUpdateEvent()) {
                                    tab.add(event.name);
                                }
                            }
                            break;
                        }
                        for (int id : DimensionManager.getStaticDimensionIDs()) {
                            tab.add(String.valueOf(id));
                        }
                        break;
                    case "data":
                        if (strings.length == 3) {
                            tab.add("info");
                            tab.add("add");
                            tab.add("remove");
                            tab.add("clear");
                            break;
                        }
                        switch (strings[2]) {
                            case "info":
                            case "clear":
                                for (int id : DimensionManager.getStaticDimensionIDs()) {
                                    tab.add(String.valueOf(id));
                                }
                                break;
                            case "add":
                            case "remove":
                                if (strings.length == 5) {
                                    for (NLibEventHandler.EVENT event : NLibEventHandler.EVENTS) {
                                        if (event.side.isWorldUpdateEvent()) {
                                            tab.add(event.name);
                                        }
                                    }
                                    break;
                                }
                                for (int id : DimensionManager.getStaticDimensionIDs()) {
                                    tab.add(String.valueOf(id));
                                }
                                break;
                        }
                        break;
                }
                break;
        }
        return tab;
    }
}
