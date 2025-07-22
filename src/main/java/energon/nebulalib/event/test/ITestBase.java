package energon.nebulalib.event.test;

import energon.nebulalib.event.EventSaveData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;

public interface ITestBase {
    /**PLAYER_TICK*/
    default boolean canStartEvent(EntityPlayer player, EventSaveData.EVENT_PLAYER_DATA data) {
        return false;
    }

    /**ATTACK_ENTITY*/
    default boolean canStartEvent(Entity attacker, Entity target) {
        return false;
    }

    /**WORLD_TICK*/
    default boolean canStartEvent(int dimID, EventSaveData.EVENT_WORLD_DATA data) {
        return false;
    }

    /**ENTITY_KILLED*/
    default boolean canStartEvent(EntityLivingBase deadEntity, DamageSource source) {
        return false;
    }
}
