package energon.nebulalib.event.test;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

public class TEST_EntityInBiome implements ITestBase {
    public final Biome targetBiome;
    public TEST_EntityInBiome(Biome biome) {
        this.targetBiome = biome;
    }

    @Override
    public boolean canStartEvent(Entity attacker, Entity target, LivingAttackEvent event) {
        return attacker.world.getBiome(attacker.getPosition()) == this.targetBiome;
    }

    @Override
    public boolean canStartEvent(EntityLivingBase deadEntity, DamageSource source) {
        return deadEntity.world.getBiome(deadEntity.getPosition()) == this.targetBiome;
    }
}
