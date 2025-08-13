package energon.nebulalib.structure.presets;

import energon.nebulalib.config.Config;
import energon.nebulalib.structure.NLibStructureHandler;
import energon.nebulalib.structure.StructureBase;
import energon.nebulalib.structure.StructureGeneratorBase;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class GEN_EveryXZChunk extends StructureGeneratorBase {
    public List<ELEMENT> elements = new ArrayList<>();
    public int everyChunk;
    public int offsetX;
    public int offsetZ;
    public int globalWight;
    public GEN_EveryXZChunk(StructureGeneratorBase.COMPACT_GEN_INPUT input) {
        this(input.name, input.config, input.category);
    }

    public GEN_EveryXZChunk(String name, Config config, String category) {
        this(name,
                config.getInt("global_weight", category, 100, 1, 999999999, "Global Wight."),
                config.getInt("every", category, 3, 0, 99999, ""),
                config.getInt("offsetX", category, 2, 0, 99999, ""),
                config.getInt("offsetZ", category, 1, 0, 99999, ""),
                config.getStringList("structures", category, new String[0], "")
        );
    }

    public GEN_EveryXZChunk(String name, int globalWight, int every, int offsetX, int offsetZ, String[] structures) {
        super(name);
        this.globalWight = globalWight;
        this.everyChunk = every;
        this.offsetX = offsetX;
        this.offsetZ = offsetZ;
        for (String str : structures) {
            this.addElement(str);
        }
    }

    @Override
    public void addElement(String compact) {
        String[] parts = compact.split(";");
        if (parts.length == 2) {
            StructureBase base = NLibStructureHandler.getStructureByName(parts[0]);
            if (base != null) {
                this.addElement(base, parts[1]);
            }
        }
    }

    @Override
    public void addElement(StructureBase structureBase, String compact) {
        this.elements.add(new ELEMENT(structureBase.name, structureBase.id, Integer.parseInt(compact)));
    }

    @Override
    public void generate(int xChunk, int zChunk, World world) {

    }

    public static class ELEMENT {
        public final String strName;
        public final int strId;
        public final int weight;
        public ELEMENT(String strName, int strId, int weight) {
            this.strName = strName;
            this.strId = strId;
            this.weight = weight;
        }
    }
}
