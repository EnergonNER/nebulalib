package energon.nebulalib.event.test;

import energon.nebulalib.event.EventSaveData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.DamageSource;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public class TEST_PlayerHasPotionEffect implements ITestBase {
    public final Potion effect;
    public TEST_PlayerHasPotionEffect(Potion effect) {
        this.effect = effect;
    }

    @Override
    public boolean canStartEvent(EntityPlayer player, EventSaveData.EVENT_PLAYER_DATA data) {
        return player.getActivePotionEffect(this.effect) != null;
    }

    @Override
    public boolean canStartEvent(PlayerEvent.PlayerChangedDimensionEvent event, EventSaveData.EVENT_PLAYER_DATA data) {
        return event.player.getActivePotionEffect(this.effect) != null;
    }

    @Override
    public boolean canStartEvent(EntityLivingBase deadEntity, DamageSource source) {
        return deadEntity.getActivePotionEffect(this.effect) != null;
    }

    @Override
    public boolean canStartEvent(Entity attacker, Entity target) {
        return attacker instanceof EntityPlayer && ((EntityPlayer) attacker).getActivePotionEffect(this.effect) != null
                || target instanceof EntityPlayer && ((EntityPlayer) target).getActivePotionEffect(this.effect) != null;
    }
}
