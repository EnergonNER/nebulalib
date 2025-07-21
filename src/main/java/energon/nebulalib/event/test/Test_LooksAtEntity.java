package energon.nebulalib.event.test;

import energon.nebulalib.event.EventSaveData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;

public class Test_LooksAtEntity implements ITestBase {
    public final Class<? extends Entity> target;
    public int radius;
    public int height;
    public Test_LooksAtEntity(Class<? extends Entity> t, int r, int h) {
        this.target = t;
        this.radius = r;
        this.height = h;
    }

    public Test_LooksAtEntity(Class<? extends Entity> t) {
        this(t, 12,6);
    }

    @Override
    public boolean canStartEvent(EntityPlayer player, EventSaveData.EVENT_DATA data) {
        for (Entity target : player.world.getEntitiesWithinAABB(this.target, new AxisAlignedBB(player.posX - radius, player.posY - height, player.posZ - radius, player.posX + radius, player.posY + height, player.posZ + radius))) {
            if (this.playerSeesTarget(player, target)) {
                return true;
            }
        }
        return false;
    }

    public boolean playerSeesTarget(EntityLivingBase player, Entity target) {
        Vec3d vec3d = player.getLook(1.0F).normalize();
        Vec3d vec3d1 = new Vec3d(target.posX - player.posX, target.getEntityBoundingBox().minY + (double)target.getEyeHeight() - (player.posY + (double)player.getEyeHeight()), target.posZ - player.posZ);
        double d0 = vec3d1.lengthVector();
        vec3d1 = vec3d1.normalize();
        double d1 = vec3d.dotProduct(vec3d1);
        return (d1 > (double) 1.0F - 0.025 / d0 && player.canEntityBeSeen(target));
    }
}
