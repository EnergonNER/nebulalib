package energon.nebulalib.event.test;

import energon.nebulalib.event.EventHandler;
import energon.nebulalib.event.EventSaveData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public class TEST_Delay implements ITestBase {
    public final int delay;
    public TEST_Delay(int delay) {
        this.delay = delay * 10;
    }

    @Override
    public boolean canStartEvent(BlockEvent.EntityPlaceEvent event, EventSaveData.EVENT_PLAYER_DATA data) {
        return EventHandler.ticks % this.delay == 5;
    }

    @Override
    public boolean canStartEvent(BlockEvent.BreakEvent event, EventSaveData.EVENT_PLAYER_DATA data) {
        return EventHandler.ticks % this.delay == 5;
    }

    @Override
    public boolean canStartEvent(EntityPlayer player, EventSaveData.EVENT_PLAYER_DATA data) {
        return EventHandler.ticks % this.delay == 5;
    }

    @Override
    public boolean canStartEvent(PlayerEvent.PlayerChangedDimensionEvent event, EventSaveData.EVENT_PLAYER_DATA data) {
        return EventHandler.ticks % this.delay == 5;
    }

    @Override
    public boolean canStartEvent(int dimID, EventSaveData.EVENT_WORLD_DATA data) {
        return EventHandler.ticks % (this.delay * 5) == 5;
    }

    @Override
    public boolean canStartEvent(EntityLivingBase deadEntity, DamageSource source) {
        return EventHandler.ticks % this.delay == 5;
    }

    @Override
    public boolean canStartEvent(Entity attacker, Entity target) {
        return EventHandler.ticks % this.delay == 5;
    }
}
