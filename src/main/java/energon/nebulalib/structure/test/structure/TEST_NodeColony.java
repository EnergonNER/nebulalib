package energon.nebulalib.structure.test.structure;

import com.google.gson.JsonObject;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TEST_NodeColony implements IStructureSpawnTest {
    public TEST_NodeColony(JsonObject object) {

    }

    public TEST_NodeColony(String compact) {

    }

    @Override
    public boolean runTest(World world, BlockPos pos) {
        return false;
    }
}
