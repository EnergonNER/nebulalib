package energon.nebulalib.event.events;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;

public class EVENT_Coth_FirstContact extends EventBase {
    private Collection<PotionEffect> effects;
    public EVENT_Coth_FirstContact(@Nullable EntityPlayer p) {
        super(p, 220);
    }

    @Override
    public void serverEventStart() {
        if (this.player != null) {
            this.effects = new ArrayList<>(this.player.getActivePotionEffects());
            this.player.clearActivePotions();
            this.player.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, this.eventTime - 40, 3));
            this.player.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, this.eventTime - 40, 1));
            this.player.addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, this.eventTime - 40, 3));
            for (ItemStack stack : this.player.inventory.mainInventory) {
                if (!stack.isEmpty()) {
                    this.player.getCooldownTracker().setCooldown(stack.getItem(), this.eventTime - 10);
                }
            }
        }
    }

    @Override
    public void serverTick() {
        if (this.player != null) {
            int radius = 16;
            int height = 8;
            for (EntityLiving target : this.player.world.getEntitiesWithinAABB(EntityLiving.class,
                    new AxisAlignedBB(this.player.posX - radius, this.player.posY - height, this.player.posZ - radius,
                            this.player.posX + radius, this.player.posY + height, this.player.posZ + radius))) {
                target.setAttackTarget(null);
                target.setRevengeTarget(null);
                target.getNavigator().clearPath();
                if (this.eventProgress % 5 == 2) {
                    if (target instanceof EntityParasiteBase) {
                        ((EntityParasiteBase) target).setWait(60);
                    }
                    target.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 60, 255));
                    target.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 60, 255));
                }
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
        /*Minecraft minecraft = Minecraft.getMinecraft();
        minecraft.addScheduledTask(() -> {
            minecraft.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SRPSounds.FLESH_HURT, 1F));
        });*/
    }

    public byte phase = (byte) 0;

    @Override
    @SideOnly(Side.CLIENT)
    public void clientTick(EntityPlayer player) {
        if (this.phase == (byte) 0 && this.eventProgress > 10) {
            Minecraft.getMinecraft().ingameGUI.displayTitle(I18n.translateToLocal("catalyst.event.coth_phase0.title1"), "", 40, 80, 20);
            this.phase++;
        } else if (this.phase == (byte) 1 && this.eventProgress > 150) {
            Minecraft.getMinecraft().ingameGUI.displayTitle(I18n.translateToLocal("catalyst.event.coth_phase0.title2"), "", 20, 80, 40);
            this.phase++;
        }
    }

    @Override
    public boolean disableAttack(AttackEntityEvent event) {
        return true;
    }

    @Override
    public boolean disableGetDamage(AttackEntityEvent event) {
        return true;
    }

    @Override
    public boolean disableChangeDimension(EntityTravelToDimensionEvent event) {
        return true;
    }

    @Override
    public boolean disableBreakBlock(BlockEvent.BreakEvent event) {
        return true;
    }

    @Override
    public boolean disablePlaceBlock(BlockEvent.EntityPlaceEvent event) {
        return true;
    }

    @Override
    public boolean disableInteractBlock(PlayerInteractEvent.RightClickBlock event) {
        return true;
    }
}
