package energon.nebulalib.event.test;

import energon.nebulalib.event.EventSaveData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public class TEST_EntityCount implements ITestBase {
    public final Class<? extends Entity> target;
    public final int count;
    public TEST_EntityCount(Class<? extends Entity> target, int count) {
        this.target = target;
        this.count = count;
    }

    private boolean customTest(World world) {
        int test = 0;
        for (Entity entity : world.loadedEntityList) {
            if (this.target.isAssignableFrom(entity.getClass())) {
                ++test;
            }
        }
        return this.count <= test;
    }

    @Override
    public boolean canStartEvent(EntityPlayer player, EventSaveData.EVENT_PLAYER_DATA data) {
        return this.customTest(player.world);
    }

    @Override
    public boolean canStartEvent(Entity attacker, Entity target) {
        return this.customTest(attacker.world);
    }

    @Override
    public boolean canStartEvent(EntityLivingBase deadEntity, DamageSource source) {
        return this.customTest(deadEntity.world);
    }

    @Override
    public boolean canStartEvent(PlayerEvent.PlayerChangedDimensionEvent event, EventSaveData.EVENT_PLAYER_DATA data) {
        return this.customTest(event.player.world);
    }

    @Override
    public boolean canStartEvent(int dimID, EventSaveData.EVENT_WORLD_DATA data) {
        World world = DimensionManager.getWorld(dimID);
        return world != null && this.customTest(world);
    }
}
