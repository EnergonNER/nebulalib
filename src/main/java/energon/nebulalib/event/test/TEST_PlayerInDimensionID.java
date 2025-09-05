package energon.nebulalib.event.test;

import energon.nebulalib.event.EventSaveData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public class TEST_PlayerInDimensionID implements ITestBase {
    public final int[] dimID;
    public TEST_PlayerInDimensionID(int... id) {
        this.dimID = id;
    }

    private boolean test(int correct) {
        for (int test : this.dimID) {
            if (test == correct) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canStartEvent(EntityPlayer player, EventSaveData.EVENT_PLAYER_DATA data) {
        return this.test(player.dimension);
    }

    @Override
    public boolean canStartEvent(Entity attacker, Entity target, LivingAttackEvent event) {
        return this.test(attacker.dimension);
    }

    @Override
    public boolean canStartEvent(EntityLivingBase deadEntity, DamageSource source) {
        return this.test(deadEntity.dimension);
    }

    @Override
    public boolean canStartEvent(int dimID, EventSaveData.EVENT_WORLD_DATA data) {
        return this.test(dimID);
    }

    @Override
    public boolean canStartEvent(PlayerEvent.PlayerChangedDimensionEvent event, EventSaveData.EVENT_PLAYER_DATA data) {
        return this.test(event.fromDim);
    }

    @Override
    public boolean canStartEvent(BlockEvent.BreakEvent event, EventSaveData.EVENT_PLAYER_DATA data) {
        return this.test(event.getPlayer().dimension);
    }

    @Override
    public boolean canStartEvent(BlockEvent.EntityPlaceEvent event, EventSaveData.EVENT_PLAYER_DATA data) {
        return this.test(event.getWorld().provider.getDimension());
    }
}
