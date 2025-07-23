package energon.nebulalib.event.test;

import energon.nebulalib.event.EventSaveData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.world.BlockEvent;

public class TEST_EntityInBiome implements ITestBase {
    public final Biome targetBiome;
    public TEST_EntityInBiome(Biome biome) {
        this.targetBiome = biome;
    }

    @Override
    public boolean canStartEvent(Entity attacker, Entity target) {
        return attacker.world.getBiome(attacker.getPosition()) == this.targetBiome;
    }

    @Override
    public boolean canStartEvent(EntityLivingBase deadEntity, DamageSource source) {
        return deadEntity.world.getBiome(deadEntity.getPosition()) == this.targetBiome;
    }

    @Override
    public boolean canStartEvent(BlockEvent.EntityPlaceEvent event, EventSaveData.EVENT_PLAYER_DATA data) {
        return event.getWorld().getBiome(event.getEntity().getPosition()) == this.targetBiome;
    }

    @Override
    public boolean canStartEvent(BlockEvent.BreakEvent event, EventSaveData.EVENT_PLAYER_DATA data) {
        return event.getWorld().getBiome(event.getPlayer().getPosition()) == this.targetBiome;
    }
}
