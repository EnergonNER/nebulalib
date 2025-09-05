package energon.nebulalib.event.events;

import energon.nebulalib.event.EventSaveData;
import energon.nebulalib.event.NLibEventHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public abstract class EventBase {
    @Nullable
    public EntityPlayer player;
    @Nullable
    public World world;
    /**Event progress: if it reaches or exceeds eventTime, the event will be marked as completed.*/
    public int eventProgress = 0;
    /**Lifetime in ticks after which the event will be marked for removal.*/
    public int eventTime;
    public int eventID = 0;
    /**Indicates if the event was loaded from saved data.
     * This value is set automatically before the event’s lifecycle starts and is accessible only on the server.*/
    public boolean fromData = false;
    public EventBase(@Nullable EntityPlayer p, int time) {
        this.player = p;
        this.eventTime = time;
    }

    public void serverEventStart(){}
    /**The main method where all actions to occur during the event’s progression are defined (server-side)*/
    public abstract void serverTick();
    public void serverEventEnd(){}

    @SideOnly(Side.CLIENT)
    public void clientEventStart() {}
    /**The main method where all actions to occur during the event’s progression are defined (client-side)*/
    @SideOnly(Side.CLIENT)
    public abstract void clientTick();
    @SideOnly(Side.CLIENT)
    public void clientEventEnd() {}
    @SideOnly(Side.CLIENT)
    public void overlayRender(RenderGameOverlayEvent.Pre event) {}
    @SideOnly(Side.CLIENT)
    public void worldRender(RenderWorldLastEvent event) {}

    /**If true, prevents the player from dealing damage to entities.*/
    public boolean disableAttack(LivingAttackEvent event) {
        return false;
    }

    public boolean disableAttack_Damage(LivingHurtEvent event) {
        return true;
    }

    /**If true, disables damage from entities to the player.*/
    public boolean disableGetDamage(LivingAttackEvent event) {
        return false;
    }

    public boolean disableGetDamage_Damage(LivingHurtEvent event) {
        return false;
    }

    public boolean disableDeath(LivingDeathEvent event) {
        return false;
    }

    /**If true, disables the ability to travel between dimensions.*/
    public boolean disableChangeDimension(EntityTravelToDimensionEvent event) {
        return false;
    }

    /**If true, prevents breaking blocks.*/
    public boolean disableBreakBlock(BlockEvent.BreakEvent event) {
        return false;
    }

    /**If true, prevents placing blocks.*/
    public boolean disablePlaceBlock(BlockEvent.EntityPlaceEvent event) {
        return false;
    }

    /**If true, prevents interaction with blocks using the right mouse button.*/
    public boolean disableInteractBlock(PlayerInteractEvent.RightClickBlock event) {
        return false;
    }

    public boolean isPlayerEvent() {
        return this.player != null;
    }

    public boolean playerPresent() {
        return this.isPlayerEvent() && FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUsername(this.player.getName()) != null;
    }

    public boolean isWorldEvent() {
        return this.world != null;
    }

    /**Core method managing event execution and server-side progression.
     * If it returns true, the event is marked for removal. (server-side)*/
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

    /**Marks the player or world as having completed the event.
     * If neither is available, no action is taken.*/
    public void saveData() {
        if (this.isWorldEvent()) {
            NLibEventHandler.DATA.setWorldEventEnded(this.world.provider.getDimension());
        } else if (this.playerPresent()) {
            NLibEventHandler.DATA.setPlayerEventEnded(this.player.getName());
        }
    }

    /**Invoked when the event is flagged as loaded from memory.
     * Called prior to serverEventStart and used to restore saved data.*/
    public void getFromData(String data, boolean playerEvent) {}

    /**Method used to store data (automatically restored by getFromData).
     * Data is automatically cleared if the event finishes correctly.*/
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

    /**Core method managing event execution and client-side progression.
     * If it returns true, the event is removed. (client-side)*/
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
