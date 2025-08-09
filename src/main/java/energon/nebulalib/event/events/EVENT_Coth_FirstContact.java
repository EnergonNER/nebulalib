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
import net.minecraft.potion.Potion;
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
import java.util.regex.Pattern;

public class EVENT_Coth_FirstContact extends EventBase {
    private Collection<PotionEffect> effects;
    public static ResourceLocation TEXTURE = new ResourceLocation("nebulalib", "textures/gui/tentacle.png");
    public static ResourceLocation TEXTUREN = new ResourceLocation("nebulalib", "textures/gui/tentacle_n.png");
    public static ResourceLocation TEXTURENN = new ResourceLocation("nebulalib", "textures/gui/tentacle_nn.png");
    public EVENT_Coth_FirstContact(@Nullable EntityPlayer p) {
        super(p, 320);
    }

    /**effects|reg_name;duration;amplifier;isAmbient;showParticles,...*/
    @Override
    public void getFromData(String data, boolean playerEvent) {
        if (data.startsWith("effects")) {
            this.effects = new ArrayList<>();
            String[] parts = data.substring(8).split(Pattern.quote(","));
            for (String effectInstance : parts) {
                String[] effect = effectInstance.split(Pattern.quote(";"));
                if (effect.length == 5) {
                    Potion potion = Potion.getPotionFromResourceLocation(effect[0]);
                    if (potion != null) {
                        this.effects.add(new PotionEffect(potion, Integer.parseInt(effect[1]), Integer.parseInt(effect[2]), Boolean.parseBoolean(effect[3]), Boolean.parseBoolean(effect[4])));
                    }
                }
            }
        }
    }

    @Override
    public void serverEventStart() {
        if (this.player != null) {
            if (!this.fromData) {
                this.effects = new ArrayList<>(this.player.getActivePotionEffects());
                StringBuilder builder = new StringBuilder("effects|");
                for (PotionEffect effect : this.effects) {
                    ResourceLocation loc = effect.getPotion().getRegistryName();
                    if (loc != null) {
                        builder.append(loc.toString()).append(";").append(effect.getDuration()).append(";").append(effect.getAmplifier()).append(";").append(effect.getIsAmbient()).append(";").append(effect.doesShowParticles()).append(",");
                    }
                }
                this.saveToData(builder.toString());
                this.player.clearActivePotions();
            }
            this.player.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, this.eventTime - 20, 4, false, false));
            this.player.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, this.eventTime - 20, 1, false, false));
            this.player.addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, this.eventTime - 20, 4, false, false));
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
            //this.player.clearActivePotions();
            for (PotionEffect potionEffect : this.effects) {
                this.player.addPotionEffect(potionEffect);
            }
        }
        effects.clear();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void clientEventStart() {
        Minecraft mc = Minecraft.getMinecraft();
        mc.addScheduledTask(() -> {
            mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(NLibSoundHandler.presence, 1F));
        });
    }

    public byte phase = (byte) 0;

    @Override
    @SideOnly(Side.CLIENT)
    public void clientTick() {
        if (this.phase == (byte) 0 && this.eventProgress > 10) {
            Minecraft.getMinecraft().ingameGUI.displayTitle(I18n.translateToLocal("catalyst.event.coth_phase0.title1"), "", 40, 100, 20);
            this.phase++;
        } else if (this.phase == (byte) 1 && this.eventProgress > 170) {
            Minecraft.getMinecraft().ingameGUI.displayTitle(I18n.translateToLocal("catalyst.event.coth_phase0.title2"), "", 20, 100, 60);
            this.phase++;
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void overlayRender(RenderGameOverlayEvent.Pre event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }
        Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution res = new ScaledResolution(mc);
        //mc.player.sendMessage(new TextComponentString("FACTOR: " + res.getScaleFactor() + "  WEIGHT: " + res.getScaledWidth() + "  HEIGHT: " + res.getScaledHeight()));
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

        float f3 = mc.player.ticksExisted + partialTicks;
        float resScale;
        switch (res.getScaleFactor()) {
            case 1:
                resScale = 1.5F;
                break;
            case 2:
                resScale = 1F;
                break;
            case 3:
                resScale = 0.75F;
                break;
            default:
                resScale = 0.5F;
                break;
        }
        //UL
        //1
        this.tentacle(mc, -30 * resScale - progress * 300, 40 * resScale, 110, MathHelper.sin(f3 * 0.09F) * (1.0F - progress * 0.8F), false, 0.7F * resScale);
        //2
        this.tentacle(mc, 180 * resScale - progress * 350, -30 * resScale - progress * 150, 150, MathHelper.sin(f3 * 0.071F) * (1.0F - progress * 0.8F), false, 0.9F * resScale);
        //3
        this.tentacle(mc, 320 * resScale - progress * 300, -50 * resScale - progress * 120, 150, MathHelper.sin(f3 * 0.082F) * (1.0F - progress * 0.8F), false, 0.7F * resScale);


        //DL
        //4
        this.tentacle(mc, -30 * resScale - progress * 300, screenHeight - 180 * resScale, 80, -MathHelper.sin(f3 * 0.043F) * (1.0F - progress * 0.9F), false, resScale);
        //5
        this.tentacle(mc, 30 * resScale - progress * 300, screenHeight + 50 * resScale + progress * 120, 30, -MathHelper.sin(f3 * 0.083F) * (1.0F - progress * 0.9F), false, 0.6F * resScale);
        //6
        this.tentacle(mc, 180 * resScale - progress * 350, screenHeight + 30 * resScale + progress * 180, 30, -MathHelper.sin(f3 * 0.067F) * (1.0F - progress * 0.9F), false, 0.75F * resScale);
        //7
        this.tentacle(mc, 290 * resScale - progress * 350, screenHeight + 50 * resScale + progress * 120, 20, -MathHelper.sin(f3 * 0.075F) * (1.0F - progress * 0.9F), false, 0.7F * resScale);


        //UR
        //8
        this.tentacle(mc, screenWidth + 40 * resScale + progress * 300, -20 * resScale, -130, -MathHelper.sin(f3 * 0.091F) * (1.0F - progress * 0.8F), true, 0.6F * resScale);
        //9
        this.tentacle(mc, screenWidth - 240 * resScale + progress * 350, -40 * resScale - progress * 120, -150, -MathHelper.sin(f3 * 0.081F) * (1.0F - progress * 0.9F), true, 0.7F * resScale);
        //10
        this.tentacle(mc, screenWidth + 20 * resScale + progress * 300, 120 * resScale - progress * 40, -100, -MathHelper.sin(f3 * 0.073F) * (1.0F - progress * 0.9F), true, 0.6F * resScale);


        //DR
        //11
        this.tentacle(mc, screenWidth + 40 * resScale + progress * 300, screenHeight - 120 * resScale, -70, -MathHelper.sin(f3 * 0.065F) * (1.0F - progress * 0.7F), true, resScale);
        //12
        this.tentacle(mc, screenWidth - 180 * resScale + progress * 350, screenHeight + 30 * resScale + progress * 120, -30, MathHelper.sin(f3 * 0.082F) * (1.0F - progress * 0.8F), true, 0.7F * resScale);
        //13
        this.tentacle(mc, screenWidth + 30 * resScale + progress * 300, screenHeight + 30 * resScale + progress * 120, -30, MathHelper.sin(f3 * 0.073F) * (1.0F - progress * 0.8F), true, 0.6F * resScale);
    }

    public void tentacle(Minecraft mc, float x, float y, float angle, float sin, boolean re, float scale) {
        GlStateManager.pushMatrix();

        mc.getTextureManager().bindTexture(TEXTURE);
        GlStateManager.enableBlend();
        GlStateManager.translate(x, y, 0);
        GlStateManager.rotate(sin * 10 + angle, 0, 0, 1);
        drawTexturedModalRect((int) (-50 * scale), (int) (-80 * scale), (int) (100 * scale), (int) (100 * scale), re);

        mc.getTextureManager().bindTexture(TEXTUREN);
        GlStateManager.translate(0, -80 * scale, 0);
        GlStateManager.rotate(sin * 10 , 0, 0, 1);
        drawTexturedModalRect((int) (-50 * scale), (int) (-80 * scale), (int) (100 * scale), (int) (100 * scale), re);

        mc.getTextureManager().bindTexture(TEXTURENN);
        GlStateManager.translate(0, -80 * scale, 0);
        GlStateManager.rotate(sin * 10 , 0, 0, 1);
        drawTexturedModalRect((int) (-50 * scale), (int) (-80 * scale), (int) (100 * scale), (int) (100 * scale), re);

        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public void drawTexturedModalRect(int x, int y, int width, int height, boolean rev) {
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
