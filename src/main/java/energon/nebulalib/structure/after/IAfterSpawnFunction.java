package energon.nebulalib.structure.after;

import energon.nebulalib.structure.StructureBase;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IAfterSpawnFunction {
    void start(World world, BlockPos pos, StructureBase base, Rotation rotation);
}
