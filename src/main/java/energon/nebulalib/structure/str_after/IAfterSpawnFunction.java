package energon.nebulalib.structure.str_after;

import energon.nebulalib.structure.StructureBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IAfterSpawnFunction {
    boolean start(World world, BlockPos pos, StructureBase base);
}
