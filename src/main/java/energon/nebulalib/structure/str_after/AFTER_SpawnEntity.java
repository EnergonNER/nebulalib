package energon.nebulalib.structure.str_after;

import energon.nebulalib.structure.StructureBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class AFTER_SpawnEntity implements IAfterSpawnFunction {
    public List<SPAWN_ENTITY_LOCAL> mobList = new ArrayList<>();
    public AFTER_SpawnEntity(String compact) {
        this(compact.split(","));
    }

    public AFTER_SpawnEntity(String[] mobs) {
        for (String compact : mobs) {
            String[] parts = compact.split(";");
            if (parts.length == 4) {
                this.mobList.add(new SPAWN_ENTITY_LOCAL(parts[0], Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Integer.parseInt(parts[3])));
            }
        }
    }

    @Override
    public boolean start(World world, BlockPos pos, StructureBase base) {
        return true;
    }

    public static class SPAWN_ENTITY_LOCAL {
        public final String regName;
        public final int wight;
        public final int min;
        public final int max;
        public SPAWN_ENTITY_LOCAL(String regName, int wight, int min, int max) {
            this.regName = regName;
            this.wight = wight;
            this.min = min;
            this.max = max;
        }
    }
}
