package energon.nebulalib.event.test;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;

public class Test_DeathFromEntity implements ITestBase {
    public Class<? extends Entity> dexter;
    public Test_DeathFromEntity(Class<? extends Entity> dexter) {
        this.dexter = dexter;
    }

    @Override
    public boolean canStartEvent(EntityLivingBase deadEntity, DamageSource source) {
        return deadEntity instanceof EntityPlayer && source.getTrueSource() != null && source.getTrueSource().getClass().isAssignableFrom(dexter);
    }
}
