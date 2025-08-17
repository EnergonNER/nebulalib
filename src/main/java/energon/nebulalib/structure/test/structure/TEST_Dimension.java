package energon.nebulalib.structure.test.structure;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class TEST_Dimension implements IStructureSpawnTest {
    public List<DIM_LOCAL> dimensions;
    public boolean BLB = true;
    public TEST_Dimension(JsonObject object) {
        this.dimensions = new ArrayList<>();
        for (JsonElement element : object.getAsJsonArray("whitelist")) {
            String test = element.getAsString();
            if (!test.isEmpty()) {
                this.dimensions.add(new DIM_LOCAL(test, false));
                if (this.BLB) {
                    this.BLB = false;
                }
            }
        }
        for (JsonElement element : object.getAsJsonArray("blacklist")) {
            String test = element.getAsString();
            if (!test.isEmpty()) {
                this.dimensions.add(new DIM_LOCAL(test, true));
            }
        }
    }

    @Override
    public boolean runTest(World world, BlockPos pos) {
        int testDim = world.provider.getDimension();
        boolean result = this.BLB;
        for (DIM_LOCAL test : this.dimensions) {
            if (test.negative) {
                if (test.dimID == testDim) {
                    return false;
                }
            } else {
                if (test.dimID == testDim) {
                    result = true;
                }
            }
        }
        return result;
    }

    public static class DIM_LOCAL {
        public boolean negative;
        public int dimID;
        public DIM_LOCAL(String part, boolean negative) {
            this.dimID = Integer.parseInt(part);
            this.negative = negative;
        }
    }
}
