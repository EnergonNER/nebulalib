package energon.nebulalib.event.events;

import energon.nebulalib.event.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public abstract class EventBase {
    public final EntityPlayer player;
    public World world;
    public float eventProgress = 0F;
    public final float eventProgressStep;

    public EventBase(@Nullable EntityPlayer p, float step) {
        this.player = p;
        this.eventProgressStep = step;
    }

    public boolean serverHandler() {
        if (this.eventProgress >= 1F) {
            this.saveData();
            this.serverTick();
            this.serverEventEnd();
            return true;
        } else {
            if (this.eventProgress == 0F) {
                this.serverEventStart();
            }
            this.serverTick();
            this.eventProgress += this.eventProgressStep;
            return false;
        }
    }

    public boolean clientHandler(EntityPlayer player) {
        if (this.eventProgress >= 1F) {
            this.clientTick(player);
            this.clientEventEnd(player);
            return true;
        } else {
            if (this.eventProgress == 0F) {
                this.clientEventStart(player);
            }
            this.clientTick(player);
            this.eventProgress += this.eventProgressStep;
            return false;
        }
    }

    private void saveData() {
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

    public boolean canAttack(Entity target) {
        return true;
    }
}
