package energon.nebulalib.structure.generator;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import energon.nebulalib.structure.NLibStructureHandler;
import energon.nebulalib.structure.StructureBase;
import energon.nebulalib.structure.StructureGeneratorBase;
import energon.nebulalib.structure.after.IAfterSpawnFunction;
import energon.nebulalib.structure.spawn.DefaultStructureSpawnRules;
import energon.nebulalib.structure.test.structure.IStructureSpawnTest;
import energon.nebulalib.util.NLibStructureUtilities;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import org.apache.commons.lang3.ArrayUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

public class GEN_EveryXZChunk extends StructureGeneratorBase {
    public List<GEN_STRUCTURE_ELEMENT> elements = new ArrayList<>();
    public int globalWight;
    public int everyXChunk;
    public int chunkOffsetX;
    public int chunkOffsetZ;
    public int spawnOffsetX;
    public int spawnOffsetZ;
    public int randomOffsetXZ;
    public GEN_EveryXZChunk(JsonObject object) {
        super(object);
        this.globalWight = object.get("global_weight").getAsInt();
        this.everyXChunk = object.get("every_x_chunk").getAsInt();

        this.chunkOffsetX = object.has("chunk_offset_x") ? object.get("chunk_offset_x").getAsInt() : 0;
        this.chunkOffsetZ = object.has("chunk_offset_z") ? object.get("chunk_offset_z").getAsInt() : 0;
        this.spawnOffsetX = object.has("spawn_offset_x") ? object.get("spawn_offset_x").getAsInt() : 0;
        this.spawnOffsetZ = object.has("spawn_offset_z") ? object.get("spawn_offset_z").getAsInt() : 0;
        this.randomOffsetXZ = object.has("random_offset_xz") ? object.get("random_offset_xz").getAsInt() : 0;

        for (JsonElement element : object.getAsJsonArray("structures")) {
            this.addElement(element.getAsJsonObject());
        }
    }

    @Override
    public void addElement(JsonObject structure) {
        StructureBase base = NLibStructureHandler.getStructureByName(structure.get("structure_name").getAsString());
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

        if (structure.has("rotations")) {
            strElement.rotations = new Rotation[0];
            for (JsonElement element : structure.getAsJsonArray("rotations")) {
                Rotation rotation = NLibStructureUtilities.getTrueRotationByName(element.getAsString());
                if (rotation != null) {
                    strElement.rotations = ArrayUtils.add(strElement.rotations, rotation);
                }
            }
        }

        if (structure.has("spawn_rules")) {
            strElement.locationTests = new ArrayList<>();
            for (JsonElement element : structure.getAsJsonArray("spawn_rules")) {
                JsonObject ruleObj = element.getAsJsonObject();
                if (ruleObj.has("type")) {
                    String type = ruleObj.get("type").getAsString();
                    Function<JsonObject, IStructureSpawnTest> fun = NLibStructureHandler.MAP_TEST.get(type);
                    if (fun != null) {
                        strElement.locationTests.add(fun.apply(ruleObj));
                    }
                }
            }
        }

        if (structure.has("after_spawn")) {
            strElement.afterFunctions = new ArrayList<>();
            for (JsonElement element : structure.getAsJsonArray("after_spawn")) {
                JsonObject ruleObj = element.getAsJsonObject();
                if (ruleObj.has("type")) {
                    String type = ruleObj.get("type").getAsString();
                    Function<JsonObject, IAfterSpawnFunction> fun = NLibStructureHandler.MAP_AFTER.get(type);
                    if (fun != null) {
                        strElement.afterFunctions.add(fun.apply(ruleObj));
                    }
                }
            }
        }

        this.elements.add(strElement);
    }

    @Override
    public boolean canStartSearch(Random random, int chunkX, int chunkZ, World world, IChunkGenerator iChunkGenerator, IChunkProvider iChunkProvider) {
        if ((chunkX - this.chunkOffsetX) % this.everyXChunk == 0 && (chunkZ - this.chunkOffsetZ) % this.everyXChunk == 0) {
            return super.canStartSearch(random, chunkX, chunkZ, world, iChunkGenerator, iChunkProvider);
        }
        return false;
    }

    @Override
    public boolean generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator iChunkGenerator, IChunkProvider iChunkProvider) {
        GEN_STRUCTURE_ELEMENT element = getRandomElement(random);
        BlockPos pos = this.randomOffsetXZ > 0 ?
                new BlockPos(chunkX * 16 + this.spawnOffsetX + (random.nextInt(this.randomOffsetXZ * 2 + 1) - this.randomOffsetXZ), 0, chunkZ * 16 + this.spawnOffsetZ + (random.nextInt(this.randomOffsetXZ * 2 + 1) - this.randomOffsetXZ)) :
                new BlockPos(chunkX * 16 + this.spawnOffsetX, 0, chunkZ * 16 + this.spawnOffsetZ);
        if (element != null && element.canStartSearch(world, pos)) {
            StructureBase structureBase = NLibStructureHandler.getStructureById(element.strId);
            if (structureBase != null) {
                pos = DefaultStructureSpawnRules.getSpawnPos(world, pos, element.spawnType, structureBase);
                if (pos != null && structureBase.canStartSearch(world, pos)) {
                    Rotation rotation = structureBase.defRotation.add(element.getRandomRotation(random));
                    if (structureBase.generate(world, pos, rotation)) {
                        this.runAfterFunctions(world, pos, structureBase, rotation);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Nullable
    public GEN_STRUCTURE_ELEMENT getRandomElement(Random random) {
        int test = 0;
        int randomNumber = random.nextInt(this.globalWight);
        for (GEN_STRUCTURE_ELEMENT element : this.elements) {
            if (randomNumber < (test += element.weight)) {
                return element;
            }
        }
        return null;
    }

    public static class GEN_STRUCTURE_ELEMENT {
        public final String strName;
        public final int strId;
        public final int weight;
        public final int spawnType;
        public List<IStructureSpawnTest> locationTests = null;
        public List<IAfterSpawnFunction> afterFunctions = null;
        public Rotation[] rotations = null;
        public GEN_STRUCTURE_ELEMENT(String strName, int strId, int weight, int spawnType) {
            this.strName = strName;
            this.strId = strId;
            this.weight = weight;
            this.spawnType = spawnType;
        }

        public boolean canStartSearch(World world, BlockPos pos) {
            if (this.locationTests != null) {
                for (IStructureSpawnTest test : this.locationTests) {
                    if (!test.runTest(world, pos)) {
                        return false;
                    }
                }
            }
            return true;
        }

        public Rotation getRandomRotation(Random random) {
            if (this.rotations != null && this.rotations.length != 0) {
                return this.rotations[random.nextInt(this.rotations.length)];
            }
            return Rotation.NONE;
        }
    }
}
