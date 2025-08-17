package energon.nebulalib.structure.spawn;

import energon.nebulalib.structure.StructureBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;

public class DefaultStructureSpawnRules {
    public static HashMap<Integer, IStructureSpawnRule> STRUCTURE_SPAWN_RULES;
    public static void writeStaticValues(){}
    static {
        STRUCTURE_SPAWN_RULES = new HashMap<>();
        STRUCTURE_SPAWN_RULES.put(-1, new OnWater());
        STRUCTURE_SPAWN_RULES.put(0, new SeaLevel());
        STRUCTURE_SPAWN_RULES.put(1, new ONE());
        STRUCTURE_SPAWN_RULES.put(2, new TWO());
    }

    public static class OnWater implements IStructureSpawnRule {
        @Override
        public boolean canSpawn(World world, BlockPos pos, StructureBase structure) {
            return world.getBlockState(pos).getMaterial().isLiquid();
        }

        @Override
        public BlockPos getSpawnPos(World world, BlockPos pos, StructureBase structure) {
            if (this.canSpawn(world, pos.up(world.getSeaLevel()), structure)) {
                return pos.up(world.getSeaLevel());
            }
            return null;
        }
    }

    public static class SeaLevel implements IStructureSpawnRule {
        @Override
        public boolean canSpawn(World world, BlockPos pos, StructureBase structure) {
            return true;
        }

        @Override
        public BlockPos getSpawnPos(World world, BlockPos pos, StructureBase structure) {
            return new BlockPos(pos.getX(), world.getSeaLevel(), pos.getZ());
        }
    }

    public static class ONE implements IStructureSpawnRule {
        @Override
        public boolean canSpawn(World world, BlockPos pos, StructureBase structure) {
            IBlockState downState = world.getBlockState(pos.down());
            if (downState.isOpaqueCube() && downState.getMaterial().isSolid()) {
                IBlockState state = world.getBlockState(pos);
                return !state.getMaterial().isLiquid()
                        && !state.isOpaqueCube()
                        && state.getMaterial().isReplaceable();
            }
            return false;
        }

        @Override
        public BlockPos getSpawnPos(World world, BlockPos pos, StructureBase structure) {
            for (int y = world.getSeaLevel() + 16; y > world.getSeaLevel(); y--) {
                if (this.canSpawn(world, pos.up(y), structure)) {
                    return pos.up(y);
                }
            }
            return null;
        }
    }

    public static class TWO implements IStructureSpawnRule {
        @Override
        public boolean canSpawn(World world, BlockPos pos, StructureBase structure) {
            IBlockState downState = world.getBlockState(pos.down());
            if (downState.isOpaqueCube() && downState.getMaterial().isSolid()) {
                IBlockState state = world.getBlockState(pos);
                if (!state.getMaterial().isLiquid()
                        && !state.isOpaqueCube()
                        && state.getMaterial().isReplaceable()) {
                    state = world.getBlockState(pos.up(1));
                    return !state.isOpaqueCube() && state.getMaterial().isReplaceable();
                }
            }
            return false;
        }

        @Override
        public BlockPos getSpawnPos(World world, BlockPos pos, StructureBase structure) {
            for (int y = world.getSeaLevel() + 32; y > world.getSeaLevel(); y--) {
                if (this.canSpawn(world, pos.up(y), structure)) {
                    return pos.up(y);
                }
            }
            return null;
        }
    }
}
