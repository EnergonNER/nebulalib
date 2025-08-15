package energon.nebulalib.structure.test;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IStructureSpawnTest {
    boolean runTest(World world, BlockPos pos);
    default String getTestInfo() {
        return "";
    }
}
