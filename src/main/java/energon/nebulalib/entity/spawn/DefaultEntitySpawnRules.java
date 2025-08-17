package energon.nebulalib.entity.spawn;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;

public class DefaultEntitySpawnRules {
    public static HashMap<Integer, IEntitySpawnRule> STRUCTURE_SPAWN_RULES;
    public static void writeStaticValues(){}
    static {
        STRUCTURE_SPAWN_RULES = new HashMap<>();
        STRUCTURE_SPAWN_RULES.put(-8, new ABOVE_EIGHT());
        STRUCTURE_SPAWN_RULES.put(-7, new ABOVE_SEVEN());
        STRUCTURE_SPAWN_RULES.put(-6, new ABOVE_SIX());
        STRUCTURE_SPAWN_RULES.put(-5, new ABOVE_FIVE());
        STRUCTURE_SPAWN_RULES.put(-4, new BELOW_FOUR());
        STRUCTURE_SPAWN_RULES.put(-3, new BELOW_THREE());
        STRUCTURE_SPAWN_RULES.put(-2, new BELOW_TWO());
        STRUCTURE_SPAWN_RULES.put(-1, new BELOW_ONE());
        STRUCTURE_SPAWN_RULES.put(0, new ZERO());
        STRUCTURE_SPAWN_RULES.put(1, new ONE());
        STRUCTURE_SPAWN_RULES.put(2, new TWO());
        STRUCTURE_SPAWN_RULES.put(3, new THREE());
        STRUCTURE_SPAWN_RULES.put(4, new FOUR());
    }

    public static class ABOVE_EIGHT implements IEntitySpawnRule {
        @Override
        public boolean canSpawn(World world, BlockPos pos) {
            return world.getBlockState(pos.down()).isOpaqueCube()
                    && world.getBlockState(pos).getMaterial().isLiquid()
                    && world.getBlockState(pos.up(1)).getMaterial().isLiquid()
                    && world.getBlockState(pos.up(2)).getMaterial().isLiquid()
                    && world.getBlockState(pos.up(3)).getMaterial().isLiquid();
        }
    }

    public static class ABOVE_SEVEN implements IEntitySpawnRule {
        @Override
        public boolean canSpawn(World world, BlockPos pos) {
            return world.getBlockState(pos.down()).isOpaqueCube()
                    && world.getBlockState(pos).getMaterial().isLiquid()
                    && world.getBlockState(pos.up(1)).getMaterial().isLiquid()
                    && world.getBlockState(pos.up(2)).getMaterial().isLiquid();
        }
    }

    public static class ABOVE_SIX implements IEntitySpawnRule {
        @Override
        public boolean canSpawn(World world, BlockPos pos) {
            return world.getBlockState(pos.down()).isOpaqueCube()
                    && world.getBlockState(pos).getMaterial().isLiquid()
                    && world.getBlockState(pos.up()).getMaterial().isLiquid();
        }
    }

    public static class ABOVE_FIVE implements IEntitySpawnRule {
        @Override
        public boolean canSpawn(World world, BlockPos pos) {
            return world.getBlockState(pos.down()).isOpaqueCube() && world.getBlockState(pos).getMaterial().isLiquid();
        }
    }

    public static class BELOW_FOUR implements IEntitySpawnRule {
        @Override
        public boolean canSpawn(World world, BlockPos pos) {
            return world.getBlockState(pos).getMaterial().isLiquid()
                    && world.getBlockState(pos.up(1)).getMaterial().isLiquid()
                    && world.getBlockState(pos.up(2)).getMaterial().isLiquid()
                    && world.getBlockState(pos.up(3)).getMaterial().isLiquid();
        }
    }

    public static class BELOW_THREE implements IEntitySpawnRule {
        @Override
        public boolean canSpawn(World world, BlockPos pos) {
            return world.getBlockState(pos).getMaterial().isLiquid()
                    && world.getBlockState(pos.up(1)).getMaterial().isLiquid()
                    && world.getBlockState(pos.up(2)).getMaterial().isLiquid();
        }
    }

    public static class BELOW_TWO implements IEntitySpawnRule {
        @Override
        public boolean canSpawn(World world, BlockPos pos) {
            return world.getBlockState(pos).getMaterial().isLiquid()
                    && world.getBlockState(pos.up()).getMaterial().isLiquid();
        }
    }

    public static class BELOW_ONE implements IEntitySpawnRule {
        @Override
        public boolean canSpawn(World world, BlockPos pos) {
            return world.getBlockState(pos).getMaterial().isLiquid();
        }
    }

    public static class ZERO implements IEntitySpawnRule {
        @Override
        public boolean canSpawn(World world, BlockPos pos) {
            return true;
        }
    }

    public static class ONE implements IEntitySpawnRule {
        @Override
        public boolean canSpawn(World world, BlockPos pos) {
            if (world.getBlockState(pos.down()).isOpaqueCube()) {
                IBlockState state = world.getBlockState(pos);
                return !state.getMaterial().isLiquid()
                        && !state.isOpaqueCube()
                        && state.getMaterial().isReplaceable();
            }
            return false;
        }
    }

    public static class TWO implements IEntitySpawnRule {
        @Override
        public boolean canSpawn(World world, BlockPos pos) {
            if (world.getBlockState(pos.down()).isOpaqueCube()) {
                IBlockState state = world.getBlockState(pos);
                if (!state.getMaterial().isLiquid() && !state.isOpaqueCube() && state.getMaterial().isReplaceable()) {
                    state = world.getBlockState(pos.up(1));
                    return !state.getMaterial().isLiquid() && !state.isOpaqueCube() && state.getMaterial().isReplaceable();
                }
            }
            return false;
        }
    }

    public static class THREE implements IEntitySpawnRule {
        @Override
        public boolean canSpawn(World world, BlockPos pos) {
            if (world.getBlockState(pos.down()).isOpaqueCube()) {
                IBlockState state = world.getBlockState(pos);
                if (!state.getMaterial().isLiquid() && !state.isOpaqueCube() && state.getMaterial().isReplaceable()) {
                    state = world.getBlockState(pos.up(1));
                    if (!state.getMaterial().isLiquid() && !state.isOpaqueCube() && state.getMaterial().isReplaceable()) {
                        state = world.getBlockState(pos.up(2));
                        return !state.isOpaqueCube() && state.getMaterial().isReplaceable();
                    }
                }
            }
            return false;
        }
    }

    public static class FOUR implements IEntitySpawnRule {
        @Override
        public boolean canSpawn(World world, BlockPos pos) {
            if (world.getBlockState(pos.down()).isOpaqueCube()) {
                IBlockState state = world.getBlockState(pos);
                if (!state.getMaterial().isLiquid() && !state.isOpaqueCube() && state.getMaterial().isReplaceable()) {
                    state = world.getBlockState(pos.up(1));
                    if (!state.getMaterial().isLiquid() && !state.isOpaqueCube() && state.getMaterial().isReplaceable()) {
                        state = world.getBlockState(pos.up(2));
                        if (!state.getMaterial().isLiquid() && !state.isOpaqueCube() && state.getMaterial().isReplaceable()) {
                            state = world.getBlockState(pos.up(3));
                            return !state.isOpaqueCube() && state.getMaterial().isReplaceable();
                        }
                    }
                }
            }
            return false;
        }
    }
}
