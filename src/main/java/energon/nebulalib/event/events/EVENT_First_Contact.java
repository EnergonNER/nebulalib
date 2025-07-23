package energon.nebulalib.event.events;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;

public class EVENT_First_Contact extends EventBase {
    private Collection<PotionEffect> effects;
    public EVENT_First_Contact(@Nullable EntityPlayer p) {
        super(p, 1F / (20 * 8));
    }

    @Override
    public void serverEventStart() {
        if (this.player != null) {
            this.effects = new ArrayList<>(this.player.getActivePotionEffects());
            this.player.clearActivePotions();
            this.player.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 160, 3));
            this.player.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 160, 1));
            this.player.addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, 160, 3));
            for (ItemStack stack : this.player.inventory.mainInventory) {
                if (!stack.isEmpty()) {
                    this.player.getCooldownTracker().setCooldown(stack.getItem(), 160);
                }
            }
        }
    }

    @Override
    public void serverTick() {
        if (this.player != null) {
            int radius = 16;
            int height = 6;
            for (EntityLiving target : player.world.getEntitiesWithinAABB(EntityLiving.class, new AxisAlignedBB(player.posX - radius, player.posY - height, player.posZ - radius, player.posX + radius, player.posY + height, player.posZ + radius))) {
                target.setAttackTarget(null);
                target.setRevengeTarget(null);
                target.getNavigator().clearPath();
                if (target instanceof EntityParasiteBase) {
                    ((EntityParasiteBase) target).setWait(80);
                }
                target.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 80, 255));
                target.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 80, 255));
            }
        }
    }

    @Override
    public void serverEventEnd() {
        if (this.player != null) {
            this.player.clearActivePotions();
            for (PotionEffect potionEffect : this.effects) {
                this.player.addPotionEffect(potionEffect);
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void clientEventStart(EntityPlayer player) {
        Minecraft.getMinecraft().ingameGUI.displayTitle("*text display start!", "LOL", 20, 60, 20);
        Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SRPSounds.FLESH_HURT, 1F));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void clientTick(EntityPlayer player) {}

    @Override
    public boolean disableAttack(Entity target) {
        return true;
    }
}
