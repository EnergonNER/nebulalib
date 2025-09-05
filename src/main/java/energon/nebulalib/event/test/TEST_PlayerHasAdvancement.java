package energon.nebulalib.event.test;

import energon.nebulalib.event.EventSaveData;
import net.minecraft.advancements.Advancement;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public class TEST_PlayerHasAdvancement implements ITestBase {
    public final ResourceLocation advancement;
    public TEST_PlayerHasAdvancement(String advancement) {
        this.advancement = new ResourceLocation(advancement);
    }

    @Override
    public boolean canStartEvent(Entity attacker, Entity target, LivingAttackEvent event) {
        if (attacker instanceof EntityPlayerMP) {
            EntityPlayerMP playerMP = (EntityPlayerMP) attacker;
            Advancement adv = playerMP.getServerWorld().getAdvancementManager().getAdvancement(this.advancement);
            return adv != null && playerMP.getAdvancements().getProgress(adv).isDone();
        } else if (target instanceof EntityPlayerMP) {
            EntityPlayerMP playerMP = (EntityPlayerMP) target;
            Advancement adv = playerMP.getServerWorld().getAdvancementManager().getAdvancement(this.advancement);
            return adv != null && playerMP.getAdvancements().getProgress(adv).isDone();
        }
        return false;
    }

    @Override
    public boolean canStartEvent(BlockEvent.EntityPlaceEvent event, EventSaveData.EVENT_PLAYER_DATA data) {
        if (event.getEntity() instanceof EntityPlayerMP) {
            EntityPlayerMP playerMP = (EntityPlayerMP) event.getEntity();
            Advancement adv = playerMP.getServerWorld().getAdvancementManager().getAdvancement(this.advancement);
            return adv != null && playerMP.getAdvancements().getProgress(adv).isDone();
        }
        return false;
    }

    @Override
    public boolean canStartEvent(BlockEvent.BreakEvent event, EventSaveData.EVENT_PLAYER_DATA data) {
        EntityPlayerMP playerMP = (EntityPlayerMP) event.getPlayer();
        Advancement adv = playerMP.getServerWorld().getAdvancementManager().getAdvancement(this.advancement);
        return adv != null && playerMP.getAdvancements().getProgress(adv).isDone();
    }

    @Override
    public boolean canStartEvent(EntityPlayer player, EventSaveData.EVENT_PLAYER_DATA data) {
        EntityPlayerMP playerMP = (EntityPlayerMP) player;
        Advancement adv = playerMP.getServerWorld().getAdvancementManager().getAdvancement(this.advancement);
        return adv != null && playerMP.getAdvancements().getProgress(adv).isDone();
    }

    @Override
    public boolean canStartEvent(EntityLivingBase deadEntity, DamageSource source) {
        if (deadEntity instanceof EntityPlayerMP) {
            EntityPlayerMP playerMP = (EntityPlayerMP) deadEntity;
            Advancement adv = playerMP.getServerWorld().getAdvancementManager().getAdvancement(this.advancement);
            return adv != null && playerMP.getAdvancements().getProgress(adv).isDone();
        } else if (source.getTrueSource() instanceof EntityPlayerMP) {
            EntityPlayerMP playerMP = (EntityPlayerMP) source.getTrueSource();
            Advancement adv = playerMP.getServerWorld().getAdvancementManager().getAdvancement(this.advancement);
            return adv != null && playerMP.getAdvancements().getProgress(adv).isDone();
        }
        return false;
    }

    @Override
    public boolean canStartEvent(PlayerEvent.PlayerChangedDimensionEvent event, EventSaveData.EVENT_PLAYER_DATA data) {
        EntityPlayerMP playerMP = (EntityPlayerMP) event.player;
        Advancement adv = playerMP.getServerWorld().getAdvancementManager().getAdvancement(this.advancement);
        return adv != null && playerMP.getAdvancements().getProgress(adv).isDone();
    }
}
