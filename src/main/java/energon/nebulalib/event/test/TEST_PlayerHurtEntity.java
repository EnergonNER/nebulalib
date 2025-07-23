package energon.nebulalib.event.test;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public class TEST_PlayerHurtEntity implements ITestBase {
    public final Class<? extends Entity> targetEntityClass;
    public TEST_PlayerHurtEntity(Class<? extends Entity> target) {
        this.targetEntityClass = target;
    }

    @Override
    public boolean canStartEvent(Entity attacker, Entity target) {
        return attacker instanceof EntityPlayer && this.targetEntityClass.isAssignableFrom(target.getClass());
    }
}
