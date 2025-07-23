package energon.nebulalib.event.test;

import energon.nebulalib.event.EventSaveData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;

public class TEST_EntityNearPlayer implements ITestBase {
    public final Class<? extends Entity> target;
    public final int radius;
    public final int height;
    public final int count;
    public TEST_EntityNearPlayer(Class<? extends Entity> target, int radius, int height, int count) {
        this.target = target;
        this.radius = radius;
        this.height = height;
        this.count = count;
    }

    public TEST_EntityNearPlayer(Class<? extends Entity> target) {
        this(target, 24, 8, 1);
    }

    @Override
    public boolean canStartEvent(EntityPlayer player, EventSaveData.EVENT_PLAYER_DATA data) {
        return this.count <= player.world.getEntitiesWithinAABB(this.target, new AxisAlignedBB(player.posX - radius, player.posY - height, player.posZ - radius, player.posX + radius, player.posY + height, player.posZ + radius)).size();
    }
}
