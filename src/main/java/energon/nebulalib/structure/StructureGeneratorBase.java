package energon.nebulalib.structure;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import energon.nebulalib.structure.after.IAfterSpawnFunction;
import energon.nebulalib.structure.spawn.DefaultStructureSpawnRules;
import energon.nebulalib.structure.spawn.IStructureSpawnRule;
import energon.nebulalib.structure.test.generator.IGeneratorStartTest;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

public abstract class StructureGeneratorBase {
    public final String name;
    public boolean skip;
    public int priority;
    public List<IGeneratorStartTest> generatorStartTest = null;
    public List<IAfterSpawnFunction> afterFunctions = null;
    public StructureGeneratorBase(String name) {
        this.name = name;
    }

    public StructureGeneratorBase(JsonObject object) {
        this(object.get("name").getAsString());
        this.skip = object.has("skip") && object.get("skip").getAsBoolean();
        this.priority = object.has("priority") ? object.get("priority").getAsInt() : 5;

        if (object.has("generator_start_tests")) {
            for (JsonElement element : object.getAsJsonArray("generator_start_tests")) {
                JsonObject ruleObj = element.getAsJsonObject();
                if (ruleObj.has("type")) {
                    String type = ruleObj.get("type").getAsString();
                    Function<JsonObject, IGeneratorStartTest> fun = NLibStructureHandler.MAP_GEN_TEST.get(type);
                    if (fun != null) {
                        this.generatorStartTest.add(fun.apply(ruleObj));
                    }
                }
            }
        }

        if (object.has("after_spawn")) {
            for (JsonElement element : object.getAsJsonArray("after_spawn")) {
                JsonObject ruleObj = element.getAsJsonObject();
                if (ruleObj.has("type")) {
                    String type = ruleObj.get("type").getAsString();
                    Function<JsonObject, IAfterSpawnFunction> fun = NLibStructureHandler.MAP_AFTER.get(type);
                    if (fun != null) {
                        this.afterFunctions.add(fun.apply(ruleObj));
                    }
                }
            }
        }
    }

    public int getPriority() {
        return this.priority;
    }

    public String getName() {
        return this.name;
    }

    public abstract void addElement(JsonObject structureCompact);
    public abstract void addElement(StructureBase structureBase, JsonObject structureCompact);

    public boolean canStartSearch(Random random, int chunkX, int chunkZ, World world, IChunkGenerator iChunkGenerator, IChunkProvider iChunkProvider) {
        if (this.generatorStartTest != null) {
            for (IGeneratorStartTest test : this.generatorStartTest) {
                if (!test.canStart(random, chunkX, chunkZ, world, iChunkGenerator, iChunkProvider)) {
                    return false;
                }
            }
        }
        return true;
    }

    public abstract void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator iChunkGenerator, IChunkProvider iChunkProvider);

    @Nullable
    public BlockPos getSpawnPos(World world, BlockPos pos, int spawnType, StructureBase structureBase) {
        IStructureSpawnRule rule = DefaultStructureSpawnRules.STRUCTURE_SPAWN_RULES.get(spawnType);
        if (rule != null) {
            return rule.getSpawnPos(world, pos, structureBase);
        }
        return null;
    }

    public void runAfterFunctions(World world, BlockPos pos, StructureBase base) {
        if (this.afterFunctions != null) {
            for (IAfterSpawnFunction function : this.afterFunctions) {
                function.start(world, pos, base);
            }
        }
        base.runAfterFunction(world, pos);
    }
}
