package energon.nebulalib.event.events;

import energon.nebulalib.event.EventSaveData;
import energon.nebulalib.event.NLibEventHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public abstract class EventBase {
    public EntityPlayer player;
    public World world;
    public int eventProgress = 0;
    public int eventTime;
    public int eventID = 0;
    public boolean fromData = false;
    public EventBase(@Nullable EntityPlayer p, int time) {
        this.player = p;
        this.eventTime = time;
    }

    public void serverEventStart(){}
    public abstract void serverTick();
    public void serverEventEnd(){}

    @SideOnly(Side.CLIENT)
    public void clientEventStart() {}
    @SideOnly(Side.CLIENT)
    public abstract void clientTick();
    @SideOnly(Side.CLIENT)
    public void clientEventEnd() {}
    @SideOnly(Side.CLIENT)
    public void overlayRender(RenderGameOverlayEvent.Pre event) {}
    @SideOnly(Side.CLIENT)
    public void worldRender(RenderWorldLastEvent event) {}

    public boolean disableAttack(AttackEntityEvent event) {
        return false;
    }

    /**Cancels damage to a player affected by the event*/
    public boolean disableGetDamage(AttackEntityEvent event) {
        return false;
    }

    public boolean disableChangeDimension(EntityTravelToDimensionEvent event) {
        return false;
    }

    public boolean disableBreakBlock(BlockEvent.BreakEvent event) {
        return false;
    }

    public boolean disablePlaceBlock(BlockEvent.EntityPlaceEvent event) {
        return false;
    }

    public boolean disableInteractBlock(PlayerInteractEvent.RightClickBlock event) {
        return false;
    }

    public boolean isPlayerEvent() {
        return this.player != null;
    }

    public boolean playerPresent() {
        return this.player != null && FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUsername(this.player.getName()) != null;
    }

    public boolean isWorldEvent() {
        return this.world != null;
    }

    public boolean serverHandler() {
        if (this.eventProgress >= this.eventTime) {
            this.saveData();
            this.serverTick();
            this.serverEventEnd();
            return true;
        } else {
            if (this.eventProgress == 0) {
                if (this.fromData) {
                    if (this.isWorldEvent()) {
                        EventSaveData.EVENT_WORLD_DATA data = NLibEventHandler.DATA.getWorldData(this.world.provider.getDimension(), false);
                        if (data != null) {
                            this.getFromData(data.variable, false);
                        }
                    } else if (this.playerPresent()) {
                        EventSaveData.EVENT_PLAYER_DATA data = NLibEventHandler.DATA.getPlayerData(this.player.getName(), false);
                        if (data != null) {
                            this.getFromData(data.variable, true);
                        }
                    }
                }
                this.serverEventStart();
            }
            this.serverTick();
            this.eventProgress++;
            return false;
        }
    }

    public void saveData() {
        if (this.isWorldEvent()) {
            NLibEventHandler.DATA.setWorldEventEnded(this.world.provider.getDimension());
        } else if (this.playerPresent()) {
            NLibEventHandler.DATA.setPlayerEventEnded(this.player.getName());
        }
    }

    public void getFromData(String data, boolean playerEvent) {}
    public void saveToData(String variables) {
        if (this.isWorldEvent()) {
            EventSaveData.EVENT_WORLD_DATA data = NLibEventHandler.DATA.getWorldData(this.world.provider.getDimension(), false);
            if (data != null) {
                data.variable = variables;
            }
        } else if (this.playerPresent()) {
            EventSaveData.EVENT_PLAYER_DATA data = NLibEventHandler.DATA.getPlayerData(this.player.getName(), false);
            if (data != null) {
                data.variable = variables;
            }
        }
    }

    public boolean clientHandler() {
        if (this.eventProgress >= this.eventTime) {
            this.clientTick();
            this.clientEventEnd();
            return true;
        } else {
            if (this.eventProgress == 0) {
                this.clientEventStart();
            }
            this.clientTick();
            this.eventProgress++;
            return false;
        }
    }
}
