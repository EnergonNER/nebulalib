package energon.nebulalib.entity.spawn;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IEntitySpawnRule {
    boolean canSpawn(World world, BlockPos pos);
}
