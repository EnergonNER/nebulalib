package energon.nebulalib.event;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.List;

public class EventSaveData extends WorldSavedData {
    public static final String DATA_NAME = "custom_events";
    public List<EVENT_PLAYER_DATA> playersEventData = new ArrayList<>();
    public List<EVENT_WORLD_DATA> worldsEventData = new ArrayList<>();
    public EventSaveData() {
        this(DATA_NAME);
    }

    public EventSaveData(String name) {
        super(name);
    }

    public static EventSaveData get(World world) {
        MapStorage storage = world.getMapStorage();
        EventSaveData instance = (EventSaveData) storage.getOrLoadData(EventSaveData.class, DATA_NAME);
        if (instance == null) {
            instance = new EventSaveData();
            storage.setData(DATA_NAME, instance);
        }
        return instance;
    }

    public EVENT_PLAYER_DATA getPlayerData(String playerName) {
        for (EVENT_PLAYER_DATA data : playersEventData) {
            if (data.player.equals(playerName)) {
                return data;
            }
        }
        EVENT_PLAYER_DATA newData = new EVENT_PLAYER_DATA(playerName, 0, true);
        playersEventData.add(newData);
        this.setDirty(true);
        return newData;
    }

    public EVENT_WORLD_DATA getWorldData(int worldID) {
        for (EVENT_WORLD_DATA data : this.worldsEventData) {
            if (data.worldID == worldID) {
                return data;
            }
        }
        EVENT_WORLD_DATA newData = new EVENT_WORLD_DATA(worldID, 0, true);
        this.worldsEventData.add(newData);
        this.setDirty(true);
        return newData;
    }

    public void addPlayerEvent(String playerName, int id) {
        for (EVENT_PLAYER_DATA data : this.playersEventData) {
            if (data.player.equals(playerName)) {
                data.correctEventEnded = false;
                data.correctEvent = id;
                this.setDirty(true);
                return;
            }
        }
    }

    public void addWorldEvent(int worldID, int id) {
        for (EVENT_WORLD_DATA data : this.worldsEventData) {
            if (data.worldID == worldID) {
                data.correctEventEnded = false;
                data.correctEvent = id;
                this.setDirty(true);
                return;
            }
        }
    }

    public void setPlayerEventEnded(String playerName) {
        for (EVENT_PLAYER_DATA data : this.playersEventData) {
            if (data.player.equals(playerName)) {
                data.correctEventEnded = true;
                data.eventsEnded = ArrayUtils.add(data.eventsEnded, data.correctEvent);
                data.correctEvent = 0;
                this.setDirty(true);
                return;
            }
        }
    }

    public void setWorldEventEnded(int worldID) {
        for (EVENT_WORLD_DATA data : this.worldsEventData) {
            if (data.worldID == worldID) {
                data.correctEventEnded = true;
                data.eventsEnded = ArrayUtils.add(data.eventsEnded, data.correctEvent);
                data.correctEvent = 0;
                this.setDirty(true);
                return;
            }
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound) {
        if (nbtTagCompound.hasKey("player_event_data", 9)) {
            NBTTagList data = nbtTagCompound.getTagList("player_event_data", 10);
            for (int i = 0; i < data.tagCount(); i++) {
                NBTTagCompound tag = data.getCompoundTagAt(i);
                playersEventData.add(new EVENT_PLAYER_DATA(tag.getString("player"), tag.getString("correct_event").split(":"), tag.getIntArray("ended_events")));
            }
        }
        if (nbtTagCompound.hasKey("world_event_data", 9)) {
            NBTTagList data = nbtTagCompound.getTagList("world_event_data", 10);
            for (int i = 0; i < data.tagCount(); i++) {
                NBTTagCompound tag = data.getCompoundTagAt(i);
                worldsEventData.add(new EVENT_WORLD_DATA(tag.getInteger("world_id"), tag.getString("correct_event").split(":"), tag.getIntArray("ended_events")));
            }
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagList data = new NBTTagList();
        for (EVENT_PLAYER_DATA parts : this.playersEventData) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setString("player", parts.player);
            tag.setString("correct_event", parts.getCorrectEventState());
            tag.setIntArray("ended_events", parts.eventsEnded);
            data.appendTag(tag);
        }
        compound.setTag("player_event_data", data);

        data = new NBTTagList();
        for (EVENT_WORLD_DATA parts : this.worldsEventData) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setInteger("world_id", parts.worldID);
            tag.setString("correct_event", parts.getCorrectEventState());
            tag.setIntArray("ended_events", parts.eventsEnded);
            data.appendTag(tag);
        }
        compound.setTag("world_event_data", data);
        return compound;
    }

    public static class EVENT_WORLD_DATA {
        public int worldID;
        public int correctEvent;
        public boolean correctEventEnded;
        public int[] eventsEnded;
        public EVENT_WORLD_DATA(int p, int correct, boolean correct_end, int... ended) {
            this.worldID = p;
            this.correctEvent = correct;
            this.correctEventEnded = correct_end;
            this.eventsEnded = ended;
        }

        public EVENT_WORLD_DATA(int p, String[] both, int... ended) {
            this(p, Integer.parseInt(both[0]), Boolean.parseBoolean(both[1]), ended);
        }

        public String getCorrectEventState() {
            return this.correctEvent + ":" + this.correctEventEnded;
        }

        public boolean worldCompletedEvent(int id) {
            for (int test : this.eventsEnded) {
                if (test == id) {
                    return true;
                }
            }
            return false;
        }

        public boolean canStartSearch() {
            return this.correctEventEnded && this.correctEvent == 0;
        }

        public boolean worldCanStartEvent(int id) {
            for (int test : this.eventsEnded) {
                if (test == id) {
                    return false;
                }
            }
            return true;
        }
    }

    public static class EVENT_PLAYER_DATA {
        public String player;
        public int correctEvent;
        public boolean correctEventEnded;
        public int[] eventsEnded;
        public EVENT_PLAYER_DATA(String p, int correct, boolean correct_end, int... ended) {
            this.player = p;
            this.correctEvent = correct;
            this.correctEventEnded = correct_end;
            this.eventsEnded = ended;
        }

        public EVENT_PLAYER_DATA(String p, String[] both, int... ended) {
            this(p, Integer.parseInt(both[0]), Boolean.parseBoolean(both[1]), ended);
        }

        public String getCorrectEventState() {
            return this.correctEvent + ":" + this.correctEventEnded;
        }

        public boolean playerCompletedEvent(int id) {
            for (int test : this.eventsEnded) {
                if (test == id) {
                    return true;
                }
            }
            return false;
        }

        public boolean canStartSearch() {
            return this.correctEventEnded && this.correctEvent == 0;
        }

        public boolean playerCanStartEvent(int id) {
            if (!this.canStartSearch()) {
                return false;
            }
            for (int test : this.eventsEnded) {
                if (test == id) {
                    return false;
                }
            }
            return true;
        }
    }
}
