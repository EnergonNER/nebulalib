package energon.nebulalib.structure.test.structure;

import energon.nebulalib.structure.test.IChance;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class TEST_Chance implements IStructureSpawnTest, IChance {
    public int weight;
    public Integer globalWeight = null;
    public TEST_Chance(String compact) {
        String[] parts = compact.split(";");
        if (parts.length > 0) {
            this.weight = Integer.parseInt(parts[0]);
            if (parts.length == 2) {
                this.globalWeight = Integer.parseInt(parts[1]);
            }
        } else {
            this.weight = 0;
        }
    }



    @Override
    public boolean runTest(World world, BlockPos pos) {
        return world.rand.nextInt(this.globalWeight == null ? 100 : this.globalWeight) < this.weight;
    }

    @Override
    public int getWeight() {
        return this.weight;
    }

    @Override
    @Nullable
    public Integer getGlobalWeight() {
        return this.globalWeight;
    }
}
