package energon.nebulalib.event.test;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

public class TEST_EntityHurtPlayer implements ITestBase {
    public final Class<? extends Entity> attackerEntityClass;
    public TEST_EntityHurtPlayer(Class<? extends Entity> attacker) {
        this.attackerEntityClass = attacker;
    }

    @Override
    public boolean canStartEvent(Entity attacker, Entity target, LivingAttackEvent event) {
        return target instanceof EntityPlayer && this.attackerEntityClass.isAssignableFrom(attacker.getClass());
    }
}
