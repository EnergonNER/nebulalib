package energon.nebulalib.event.test;

import energon.nebulalib.event.EventSaveData;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;

public interface ITestBase {
    default boolean canStartEvent(EntityPlayer player, EventSaveData.EVENT_DATA data) {
        return false;
    }

    default boolean canStartEvent(EntityLivingBase entity, DamageSource source) {
        return false;
    }
}
