package energon.nebulalib.event.test;

import energon.nebulalib.event.EventSaveData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public class TEST_TRUE implements ITestBase {
    @Override
    public boolean canStartEvent(EntityLivingBase deadEntity, DamageSource source) {
        return true;
    }

    @Override
    public boolean canStartEvent(Entity attacker, Entity target) {
        return true;
    }

    @Override
    public boolean canStartEvent(int dimID, EventSaveData.EVENT_WORLD_DATA data) {
        return true;
    }

    @Override
    public boolean canStartEvent(EntityPlayer player, EventSaveData.EVENT_PLAYER_DATA data) {
        return true;
    }

    @Override
    public boolean canStartEvent(PlayerEvent.PlayerChangedDimensionEvent event, EventSaveData.EVENT_PLAYER_DATA data) {
        return true;
    }

    @Override
    public boolean canStartEvent(BlockEvent.BreakEvent event, EventSaveData.EVENT_PLAYER_DATA data) {
        return true;
    }

    @Override
    public boolean canStartEvent(BlockEvent.EntityPlaceEvent event, EventSaveData.EVENT_PLAYER_DATA data) {
        return true;
    }
}
