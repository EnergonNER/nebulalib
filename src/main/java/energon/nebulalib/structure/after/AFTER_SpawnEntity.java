package energon.nebulalib.structure.after;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import energon.nebulalib.structure.StructureBase;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class AFTER_SpawnEntity implements IAfterSpawnFunction {
    public List<SPAWN_ENTITY_LOCAL> mobList = new ArrayList<>();
    public AFTER_SpawnEntity(JsonObject object) {
        for (JsonElement element : object.getAsJsonArray("mobs")) {
            JsonObject modCompact = element.getAsJsonObject();
            this.mobList.add(new SPAWN_ENTITY_LOCAL(
                    modCompact.get("name").getAsString(),
                    modCompact.get("weight").getAsInt(),
                    modCompact.get("min").getAsInt(),
                    modCompact.get("max").getAsInt(),
                    modCompact.get("spawn_type").getAsInt()
            ));
        }
    }

    @Override
    public void start(World world, BlockPos pos, StructureBase base, Rotation rotation) {
    }

    public static class SPAWN_ENTITY_LOCAL {
        public final String regName;
        public final int wight;
        public final int min;
        public final int max;
        public final int spawnType;
        public SPAWN_ENTITY_LOCAL(String regName, int wight, int min, int max, int spawnType) {
            this.regName = regName;
            this.wight = wight;
            this.min = min;
            this.max = max;
            this.spawnType = spawnType;
        }
    }
}
