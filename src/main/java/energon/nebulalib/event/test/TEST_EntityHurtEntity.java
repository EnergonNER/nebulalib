package energon.nebulalib.event.test;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

public class TEST_EntityHurtEntity implements ITestBase {
    public final Class<? extends EntityLivingBase> targetEntity;
    public final Class<? extends Entity> attackerEntityClass;
    public TEST_EntityHurtEntity(Class<? extends EntityLivingBase> target, Class<? extends Entity> attacker) {
        this.targetEntity = target;
        this.attackerEntityClass = attacker;
    }

    @Override
    public boolean canStartEvent(Entity attacker, Entity target, LivingAttackEvent event) {
        return this.targetEntity.isAssignableFrom(target.getClass()) && this.attackerEntityClass.isAssignableFrom(attacker.getClass());
    }
}
