package energon.nebulalib.structure;

import energon.nebulalib.config.Config;
import net.minecraft.world.World;

public abstract class StructureGeneratorBase {
    public final String name;
    public StructureGeneratorBase(String name) {
        this.name = name;
    }

    public abstract void addElement(String compact);
    public abstract void addElement(StructureBase structureBase, String compact);
    public abstract void generate(int xChunk, int zChunk, World world);

    public static class COMPACT_GEN_INPUT {
        public final String name;
        public final Config config;
        public final String category;
        public COMPACT_GEN_INPUT(String name, Config config, String category) {
            this.name = name;
            this.config = config;
            this.category = category;
        }
    }
}
