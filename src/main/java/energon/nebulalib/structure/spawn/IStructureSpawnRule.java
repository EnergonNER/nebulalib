package energon.nebulalib.structure.spawn;

import energon.nebulalib.structure.StructureBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IStructureSpawnRule {
    boolean canSpawn(World world, BlockPos pos, StructureBase structure);
    BlockPos getSpawnPos(World world, BlockPos pos, StructureBase structure);
}
