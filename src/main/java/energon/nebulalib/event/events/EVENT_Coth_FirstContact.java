package energon.nebulalib.event.events;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import energon.nebulalib.handler.NLibSoundHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;

public class EVENT_Coth_FirstContact extends EventBase {
    private Collection<PotionEffect> effects;
    public static ResourceLocation TEXTURE = new ResourceLocation("nebulalib", "textures/gui/tentacle.png");
    public static ResourceLocation TEXTUREN = new ResourceLocation("nebulalib", "textures/gui/tentacle_n.png");
    public static ResourceLocation TEXTURENN = new ResourceLocation("nebulalib", "textures/gui/tentacle_nn.png");
    public EVENT_Coth_FirstContact(@Nullable EntityPlayer p) {
        super(p, 270);
    }

    @Override
    public void serverEventStart() {
        if (this.player != null) {
            this.effects = new ArrayList<>(this.player.getActivePotionEffects());
            this.player.clearActivePotions();
            this.player.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, this.eventTime - 40, 3, false, false));
            this.player.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, this.eventTime - 40, 1, false, false));
            this.player.addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, this.eventTime - 40, 3, false, false));
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
        Minecraft minecraft = Minecraft.getMinecraft();
        minecraft.addScheduledTask(() -> {
            minecraft.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(NLibSoundHandler.presence, 1F));
        });
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
    public void overlayRender(RenderGameOverlayEvent.Pre event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }
        Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution res = new ScaledResolution(mc);
        int screenWidth = res.getScaledWidth();
        int screenHeight = res.getScaledHeight();
        float partialTicks = event.getPartialTicks();
        float progress;
        int tp = this.eventTime / 6;
        if (this.eventProgress < tp) {
            progress = 1F - MathHelper.sin(Math.min((this.eventProgress + partialTicks) * 0.02F, 1F) * 1.57F);
        } else if (this.eventProgress > tp * 5) {
            progress = 1F - MathHelper.sin(Math.min((this.eventTime - this.eventProgress - partialTicks) * 0.02F, 1F) * 1.57F);
        } else {
            progress = 0F;
        }

        this.tentacle(mc, 0 - progress * 300, 40, 110, MathHelper.sin((mc.player.ticksExisted + partialTicks) * 0.1F) * (1.0F - progress * 0.8F), false);
        this.tentacle(mc, 120 - progress * 300, -120 - progress * 120, 140, MathHelper.sin((mc.player.ticksExisted + partialTicks) * 0.13F) * (1.0F - progress * 0.8F), false);

        this.tentacle(mc, 0 - progress * 300, screenHeight - 40, 70, -MathHelper.sin((mc.player.ticksExisted + partialTicks) * 0.12F) * (1.0F - progress * 0.9F), false);
        this.tentacle(mc, 120 - progress * 300, screenHeight + 120 + progress * 120, 30, -MathHelper.sin((mc.player.ticksExisted + partialTicks) * 0.12F) * (1.0F - progress * 0.9F), false);


        this.tentacle(mc, screenWidth + progress * 300, 40, -110, -MathHelper.sin((mc.player.ticksExisted + partialTicks) * 0.11F) * (1.0F - progress * 0.8F), true);
        this.tentacle(mc, screenWidth - 150 + progress * 300, -90 - progress * 120, -150, -MathHelper.sin((mc.player.ticksExisted + partialTicks) * 0.13F) * (1.0F - progress * 0.9F), true);

        this.tentacle(mc, screenWidth + progress * 300, screenHeight - 40, -70, MathHelper.sin((mc.player.ticksExisted + partialTicks) * 0.08F) * (1.0F - progress * 0.7F), true);
        this.tentacle(mc, screenWidth - 120 + progress * 300, screenHeight + 90 + progress * 120, -40, MathHelper.sin((mc.player.ticksExisted + partialTicks) * 0.09F) * (1.0F - progress * 0.8F), true);

    }

    public void tentacle(Minecraft mc, float x, float y, float angle, float sin, boolean re) {
        GlStateManager.pushMatrix();

        mc.getTextureManager().bindTexture(TEXTURE);
        GlStateManager.enableBlend();
        GlStateManager.translate(x, y, 0);
        GlStateManager.rotate(sin * 10 + angle, 0, 0, 1);
        drawTexturedModalRect(-50, -80, 100, 100, re);

        mc.getTextureManager().bindTexture(TEXTUREN);
        GlStateManager.translate(0, -80, 0);
        GlStateManager.rotate(sin * 10 , 0, 0, 1);
        drawTexturedModalRect(-50, -80, 100, 100, re);

        mc.getTextureManager().bindTexture(TEXTURENN);
        GlStateManager.translate(0, -80, 0);
        GlStateManager.rotate(sin * 10 , 0, 0, 1);
        drawTexturedModalRect(-50, -80, 100, 100, re);

        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    protected void drawTexturedModalRect(int x, int y, int width, int height, boolean rev) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        if (rev) {
            buffer.pos(x, y + height, 0)
                    .tex(0D, 1D).endVertex();
            buffer.pos(x + width, y + height, 0)
                    .tex(1F, 1F).endVertex();
            buffer.pos(x + width, y, 0)
                    .tex(1D, 0D).endVertex();
            buffer.pos(x, y, 0)
                    .tex(0D, 0D).endVertex();
        } else {
            buffer.pos(x, y + height, 0)
                    .tex(1D, 1D).endVertex();
            buffer.pos(x + width, y + height, 0)
                    .tex(0D, 1D).endVertex();
            buffer.pos(x + width, y, 0)
                    .tex(0D, 0D).endVertex();
            buffer.pos(x, y, 0)
                    .tex(1D, 0D).endVertex();
        }
        tessellator.draw();
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
