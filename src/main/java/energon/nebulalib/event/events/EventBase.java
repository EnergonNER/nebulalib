package energon.nebulalib.event.events;

import energon.nebulalib.event.EventHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
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

    public EventBase(@Nullable EntityPlayer p, int time) {
        this.player = p;
        this.eventTime = time;
    }

    public boolean serverHandler() {
        if (this.eventProgress >= this.eventTime) {
            this.saveData();
            this.serverTick();
            this.serverEventEnd();
            return true;
        } else {
            if (this.eventProgress == 0) {
                this.serverEventStart();
            }
            this.serverTick();
            this.eventProgress++;
            return false;
        }
    }

    public boolean clientHandler(EntityPlayer player) {
        if (this.eventProgress >= this.eventTime) {
            this.clientTick(player);
            this.clientEventEnd(player);
            return true;
        } else {
            if (this.eventProgress == 0) {
                this.clientEventStart(player);
            }
            this.clientTick(player);
            this.eventProgress++;
            return false;
        }
    }

    public void saveData() {
        if (this.world != null) {
            EventHandler.DATA.setWorldEventEnded(this.world.provider.getDimension());
        } else if (this.player != null && FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUsername(this.player.getName()) != null) {
            EventHandler.DATA.setPlayerEventEnded(this.player.getName());
        }
    }

    public void serverEventStart(){}
    public abstract void serverTick();
    public void serverEventEnd(){}

    @SideOnly(Side.CLIENT)
    public void clientEventStart(EntityPlayer player) {}
    @SideOnly(Side.CLIENT)
    public abstract void clientTick(EntityPlayer player);
    @SideOnly(Side.CLIENT)
    public void clientEventEnd(EntityPlayer player) {}

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
}
