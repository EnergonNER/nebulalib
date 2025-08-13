package energon.nebulalib.structure.str_test;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class TEST_Dimension implements IStructureSpawnTest {
    public List<DIM_LOCAL> dimensions;
    public boolean BLB = true;
    public TEST_Dimension(String compact) {
        this(compact.split(";"));
    }

    public TEST_Dimension(String... dimIDs) {
        this.dimensions = new ArrayList<>();
        for (String part : dimIDs) {
            if (!part.isEmpty()) {
                this.dimensions.add(new DIM_LOCAL(part));
                if (this.BLB && part.charAt(0) != '!') {
                    this.BLB = false;
                }
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
        public DIM_LOCAL(String part) {
            if (part.charAt(0) == '!') {
                negative = true;
                dimID = Integer.parseInt(part.substring(1));
            } else {
                negative = false;
                dimID = Integer.parseInt(part);
            }
        }
    }
}
