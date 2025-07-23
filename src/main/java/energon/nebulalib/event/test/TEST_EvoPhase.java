package energon.nebulalib.event.test;

import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import energon.nebulalib.event.EventSaveData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public class TEST_EvoPhase implements ITestBase {
    public final byte min;
    public final byte max;
    public TEST_EvoPhase(int minPhase, int maxPhase) {
        this.min = (byte) minPhase;
        this.max = (byte) maxPhase;
    }

    @Override
    public boolean canStartEvent(EntityLivingBase deadEntity, DamageSource source) {
        byte phase = SRPSaveData.get(deadEntity.world).getEvolutionPhase(deadEntity.dimension);
        return this.min <= phase && this.max >= phase;
    }

    @Override
    public boolean canStartEvent(int dimID, EventSaveData.EVENT_WORLD_DATA data) {
        World world = DimensionManager.getWorld(dimID);
        if (world != null) {
            byte phase = SRPSaveData.get(world).getEvolutionPhase(dimID);
            return this.min <= phase && this.max >= phase;
        }
        return false;
    }

    @Override
    public boolean canStartEvent(Entity attacker, Entity target) {
        byte phase = SRPSaveData.get(attacker.world).getEvolutionPhase(attacker.dimension);
        return this.min <= phase && this.max >= phase;
    }

    @Override
    public boolean canStartEvent(EntityPlayer player, EventSaveData.EVENT_PLAYER_DATA data) {
        byte phase = SRPSaveData.get(player.world).getEvolutionPhase(player.dimension);
        return this.min <= phase && this.max >= phase;
    }

    @Override
    public boolean canStartEvent(PlayerEvent.PlayerChangedDimensionEvent event, EventSaveData.EVENT_PLAYER_DATA data) {
        byte phase = SRPSaveData.get(event.player.world).getEvolutionPhase(event.fromDim);
        return this.min <= phase && this.max >= phase;
    }

    @Override
    public boolean canStartEvent(BlockEvent.EntityPlaceEvent event, EventSaveData.EVENT_PLAYER_DATA data) {
        byte phase = SRPSaveData.get(event.getWorld()).getEvolutionPhase(event.getWorld().provider.getDimension());
        return this.min <= phase && this.max >= phase;
    }

    @Override
    public boolean canStartEvent(BlockEvent.BreakEvent event, EventSaveData.EVENT_PLAYER_DATA data) {
        byte phase = SRPSaveData.get(event.getWorld()).getEvolutionPhase(event.getWorld().provider.getDimension());
        return this.min <= phase && this.max >= phase;
    }
}
