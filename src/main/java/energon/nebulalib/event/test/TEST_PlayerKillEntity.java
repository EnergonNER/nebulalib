package energon.nebulalib.event.test;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;

public class TEST_PlayerKillEntity implements ITestBase {
    public final Class<? extends Entity> target;
    public TEST_PlayerKillEntity(Class<? extends Entity> target) {
        this.target = target;
    }

    @Override
    public boolean canStartEvent(EntityLivingBase deadEntity, DamageSource source) {
        return source.getTrueSource() != null && source.getTrueSource() instanceof EntityPlayer && this.target.isAssignableFrom(deadEntity.getClass());
    }
}
