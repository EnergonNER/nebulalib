package energon.nebulalib.event.test;

import com.dhanantry.scapeandrunparasites.world.SRPWorldData;
import energon.nebulalib.event.EventSaveData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public class TEST_ColonyExists implements ITestBase {
    @Override
    public boolean canStartEvent(int dimID, EventSaveData.EVENT_WORLD_DATA data) {
        World world = DimensionManager.getWorld(dimID);
        if (world != null) {
            return !SRPWorldData.get(world).getColonies("a").isEmpty();
        }
        return false;
    }

    @Override
    public boolean canStartEvent(EntityLivingBase deadEntity, DamageSource source) {
        return !SRPWorldData.get(deadEntity.world).getColonies("a").isEmpty();
    }

    @Override
    public boolean canStartEvent(Entity attacker, Entity target) {
        return !SRPWorldData.get(attacker.world).getColonies("a").isEmpty();
    }

    @Override
    public boolean canStartEvent(EntityPlayer player, EventSaveData.EVENT_PLAYER_DATA data) {
        return !SRPWorldData.get(player.world).getColonies("a").isEmpty();
    }

    @Override
    public boolean canStartEvent(PlayerEvent.PlayerChangedDimensionEvent event, EventSaveData.EVENT_PLAYER_DATA data) {
        return !SRPWorldData.get(event.player.world).getColonies("a").isEmpty();
    }

    @Override
    public boolean canStartEvent(BlockEvent.BreakEvent event, EventSaveData.EVENT_PLAYER_DATA data) {
        return !SRPWorldData.get(event.getWorld()).getColonies("a").isEmpty();
    }

    @Override
    public boolean canStartEvent(BlockEvent.EntityPlaceEvent event, EventSaveData.EVENT_PLAYER_DATA data) {
        return !SRPWorldData.get(event.getWorld()).getColonies("a").isEmpty();
    }
}
