package energon.nebulalib.event.events;

import energon.nebulalib.NebulaLib;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class EVENT_Test extends EventBase {
    private static final CustomPlayer MODEL = new CustomPlayer();
    private static final ResourceLocation TEXTURE = new ResourceLocation(NebulaLib.MODID, "textures/silver.png");
    @SideOnly(Side.CLIENT)
    public List<TEST_UTILS> silverheads = new ArrayList<>();
    public EVENT_Test(@Nullable EntityPlayer p) {
        super(p, 600);
    }

    @Override
    public void serverTick() {

    }

    @Override
    public void clientEventStart() {
        Minecraft mc = Minecraft.getMinecraft();
        BlockPos pos = new BlockPos(mc.player);
        for (int y = 0; y < 33; y++) {
            for (int z = 0; z < 33; z++) {
                for (int x = 0; x < 33; x++) {
                    BlockPos loc = pos.add(x - 16, y - 16, z - 16);
                    if (mc.world.getBlockState(loc).getBlock() == Blocks.REDSTONE_BLOCK) {
                        this.silverheads.add(new TEST_UTILS(loc.getX() + 0.5D, loc.getY() + 2.5D, loc.getZ() + 0.5D));
                    }
                }
            }
        }
    }

    @Override
    public void clientTick() {
        for (TEST_UTILS silver : this.silverheads) {
            silver.update();
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void worldRender(RenderWorldLastEvent event) {
        if (this.silverheads == null) {
            return;
        }
        Minecraft mc = Minecraft.getMinecraft();
        float partialTicks = event.getPartialTicks();

        double interpPlayerX = mc.player.lastTickPosX + (mc.player.posX - mc.player.lastTickPosX) * partialTicks;
        double interpPlayerY = mc.player.lastTickPosY + (mc.player.posY - mc.player.lastTickPosY) * partialTicks;
        double interpPlayerZ = mc.player.lastTickPosZ + (mc.player.posZ - mc.player.lastTickPosZ) * partialTicks;

        for (TEST_UTILS silver : this.silverheads) {
            GlStateManager.pushMatrix();

            double interpX = silver.lastTickPosX + (silver.posX - silver.lastTickPosX) * partialTicks;
            double interpY = silver.lastTickPosY + (silver.posY - silver.lastTickPosY) * partialTicks;
            double interpZ = silver.lastTickPosZ + (silver.posZ - silver.lastTickPosZ) * partialTicks;

            GlStateManager.translate(interpX - interpPlayerX, interpY - interpPlayerY, interpZ - interpPlayerZ);

            mc.getTextureManager().bindTexture(TEXTURE);

            GlStateManager.scale(-1F, -1F, 1F);
            MODEL.renderAll(0.0625F);
            GlStateManager.popMatrix();
        }
    }

    public static class TEST_UTILS {
        public double posX;
        public double lastTickPosX;
        public double posY;
        public double lastTickPosY;
        public double posZ;
        public double lastTickPosZ;
        public TEST_UTILS(double x, double y, double z) {
            this.posX = x;
            this.posY = y;
            this.posZ = z;
            this.lastTickPosX = this.posX;
            this.lastTickPosY = this.posY;
            this.lastTickPosZ = this.posZ;
        }

        public void update() {
            //MOVE
            this.lastTickPosX = this.posX;
            this.lastTickPosY = this.posY;
            this.lastTickPosZ = this.posZ;
            //END
        }
    }
}
