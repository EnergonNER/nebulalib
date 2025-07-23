package energon.nebulalib.event.test;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;

public class TEST_EntityKillPlayer implements ITestBase {
    public final Class<? extends Entity> mob;
    public TEST_EntityKillPlayer(Class<? extends Entity> dexterMob) {
        this.mob = dexterMob;
    }

    @Override
    public boolean canStartEvent(EntityLivingBase deadEntity, DamageSource source) {
        return deadEntity instanceof EntityPlayer && source.getTrueSource() != null && this.mob.isAssignableFrom(source.getTrueSource().getClass());
    }
}
