package energon.nebulalib.event.test;

import energon.nebulalib.event.EventSaveData;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;

public interface ITestBase {
    /**PLAYER_TICK*/
    default boolean canStartEvent(EntityPlayer player, EventSaveData.EVENT_PLAYER_DATA data) {
        return false;
    }

    /**WORLD_TICK*/
    default boolean canStartEvent(int dimID) {
        return false;
    }

    /**ENTITY_KILLED*/
    default boolean canStartEvent(EntityLivingBase entity, DamageSource source) {
        return false;
    }
}
