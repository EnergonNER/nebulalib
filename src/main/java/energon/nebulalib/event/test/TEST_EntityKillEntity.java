package energon.nebulalib.event.test;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;

public class TEST_EntityKillEntity implements ITestBase {
    public final Class<? extends EntityLivingBase> dead;
    public final Class<? extends Entity> killer;
    public TEST_EntityKillEntity(Class<? extends EntityLivingBase> dead, Class<? extends Entity> killer) {
        this.dead = dead;
        this.killer = killer;
    }

    @Override
    public boolean canStartEvent(EntityLivingBase deadEntity, DamageSource source) {
        return this.dead.isAssignableFrom(deadEntity.getClass()) && source.getTrueSource() != null && this.killer.isAssignableFrom(source.getTrueSource().getClass());
    }
}
