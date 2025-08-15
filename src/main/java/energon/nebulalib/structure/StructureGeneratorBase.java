package energon.nebulalib.structure;

import com.google.gson.JsonObject;
import net.minecraft.world.World;

public abstract class StructureGeneratorBase {
    public final String name;
    public StructureGeneratorBase(String name) {
        this.name = name;
    }

    public abstract void addElement(JsonObject structureCompact);
    public abstract void addElement(StructureBase structureBase, JsonObject structureCompact);
    public abstract void generate(int xChunk, int zChunk, World world);
}
