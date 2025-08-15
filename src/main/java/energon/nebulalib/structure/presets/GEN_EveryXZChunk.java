package energon.nebulalib.structure.presets;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import energon.nebulalib.structure.NLibStructureHandler;
import energon.nebulalib.structure.StructureBase;
import energon.nebulalib.structure.StructureGeneratorBase;
import energon.nebulalib.structure.after.IAfterSpawnFunction;
import energon.nebulalib.structure.test.IStructureSpawnTest;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class GEN_EveryXZChunk extends StructureGeneratorBase {
    public List<GEN_STRUCTURE_ELEMENT> elements = new ArrayList<>();
    public int everyXChunk;
    public int offsetX;
    public int offsetZ;
    public int globalWight;
    public List<IStructureSpawnTest> locationTests = null;
    public List<IAfterSpawnFunction> afterFunctions = null;

    public GEN_EveryXZChunk(JsonObject object) {
        super(object.get("name").getAsString());
        this.everyXChunk = object.get("every_x_chunk").getAsInt();
        this.globalWight = object.get("global_weight").getAsInt();
        this.offsetX = object.get("offset_x").getAsInt();
        this.offsetZ = object.get("offset_z").getAsInt();
        for (JsonElement element : object.getAsJsonArray("structures")) {
            this.addElement(element.getAsJsonObject());
        }
        if (object.has("spawn_rules")) {
            for (JsonElement element : object.getAsJsonArray("spawn_rules")) {
                JsonObject ruleObj = element.getAsJsonObject();
                if (ruleObj.has("type")) {
                    String type = ruleObj.get("type").getAsString();
                    Function<JsonObject, IStructureSpawnTest> fun = NLibStructureHandler.MAP_STR_TEST.get(type);
                    if (fun != null) {
                        this.locationTests.add(fun.apply(ruleObj));
                    }
                }
            }
        }

        if (object.has("after_spawn")) {
            for (JsonElement element : object.getAsJsonArray("after_spawn")) {
                JsonObject ruleObj = element.getAsJsonObject();
                if (ruleObj.has("type")) {
                    String type = ruleObj.get("type").getAsString();
                    Function<JsonObject, IAfterSpawnFunction> fun = NLibStructureHandler.MAP_STR_AFTER.get(type);
                    if (fun != null) {
                        this.afterFunctions.add(fun.apply(ruleObj));
                    }
                }
            }
        }
    }

    @Override
    public void addElement(JsonObject structure) {
        StructureBase base = NLibStructureHandler.getStructureByName(structure.get("name").getAsString());
        if (base != null) {
            this.addElement(base, structure);
        }
    }

    @Override
    public void addElement(StructureBase structureBase, JsonObject structure) {
        GEN_STRUCTURE_ELEMENT strElement = new GEN_STRUCTURE_ELEMENT(
                structureBase.name,
                structureBase.id,
                structure.get("weight").getAsInt(),
                structure.get("spawn_type").getAsInt()
        );

        if (structure.has("spawn_rules")) {
            for (JsonElement element : structure.getAsJsonArray("spawn_rules")) {
                JsonObject ruleObj = element.getAsJsonObject();
                if (ruleObj.has("type")) {
                    String type = ruleObj.get("type").getAsString();
                    Function<JsonObject, IStructureSpawnTest> fun = NLibStructureHandler.MAP_STR_TEST.get(type);
                    if (fun != null) {
                        strElement.locationTests.add(fun.apply(ruleObj));
                    }
                }
            }
        }

        if (structure.has("after_spawn")) {
            for (JsonElement element : structure.getAsJsonArray("after_spawn")) {
                JsonObject ruleObj = element.getAsJsonObject();
                if (ruleObj.has("type")) {
                    String type = ruleObj.get("type").getAsString();
                    Function<JsonObject, IAfterSpawnFunction> fun = NLibStructureHandler.MAP_STR_AFTER.get(type);
                    if (fun != null) {
                        strElement.afterFunctions.add(fun.apply(ruleObj));
                    }
                }
            }
        }

        this.elements.add(strElement);
    }

    @Override
    public void generate(int xChunk, int zChunk, World world) {

    }

    public static class GEN_STRUCTURE_ELEMENT {
        public final String strName;
        public final int strId;
        public final int weight;
        public final int spawnType;
        public List<IStructureSpawnTest> locationTests = null;
        public List<IAfterSpawnFunction> afterFunctions = null;
        public GEN_STRUCTURE_ELEMENT(String strName, int strId, int weight, int spawnType) {
            this.strName = strName;
            this.strId = strId;
            this.weight = weight;
            this.spawnType = spawnType;
        }
    }
}
