package energon.nebulalib.structure;

import com.google.gson.*;
import energon.nebulalib.NebulaLib;
import energon.nebulalib.structure.presets.GEN_EveryXZChunk;
import energon.nebulalib.structure.after.AFTER_SpawnEntity;
import energon.nebulalib.structure.after.IAfterSpawnFunction;
import energon.nebulalib.structure.test.generator.IGeneratorStartTest;
import energon.nebulalib.structure.test.structure.*;
import energon.nebulalib.util.NLibFileUtilities;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.minecraftforge.fml.common.Loader;

import javax.annotation.Nullable;
import java.io.*;
import java.util.*;
import java.util.function.Function;

public class NLibStructureHandler implements IWorldGenerator {
    public static int ASTR = 0;
    public static int ATEM = 0;

    public static HashMap<String, Function<JsonObject, IStructureSpawnTest>> MAP_TEST;
    public static HashMap<String, Function<JsonObject, IGeneratorStartTest>> MAP_GEN_TEST;
    public static HashMap<String, Function<JsonObject, IAfterSpawnFunction>> MAP_AFTER;
    public static HashMap<String, Function<JsonObject, StructureGeneratorBase>> MAP_GENERATORS;

    public static List<TEMPLATE> LIST_TEMPLATES = new ArrayList<>();
    public static List<StructureBase> LIST_STRUCTURES = new ArrayList<>();
    public static List<StructureGeneratorBase> LIST_GENERATORS = new ArrayList<>();

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator iChunkGenerator, IChunkProvider iChunkProvider) {
        boolean spawned = false;
        for (StructureGeneratorBase generator : LIST_GENERATORS) {
            if (!(generator.skip && spawned) && generator.canStartSearch(random, chunkX, chunkZ, world, iChunkGenerator, iChunkProvider)) {
                generator.generate(random, chunkX, chunkZ, world, iChunkGenerator, iChunkProvider);
                spawned = true;
            }
        }
    }

    public static void writeStaticValues() {}

    public static void copy() {
        File configDir = Loader.instance().getConfigDir();
        File nebulaGenDir = new File(configDir, "nebulalib/generators/nebula");
        if (!nebulaGenDir.exists()) {
            info("NEBULA: LIB <> COPY FROM MOD START   ");
            for (String resource : new String[]{"simple.json"}) {
                switch (NLibFileUtilities.copyFromMod("nebulalib/custom/", resource, nebulaGenDir, false)) {
                    case 0:
                        debug("NEBULA: LIB > " + resource + " SUCCESSFUL   ");
                        break;
                    case 1:
                        alert("NEBULA: LIB > " + resource + " ERROR!!!   ");
                        break;
                    case 2:
                        alert("NEBULA: LIB > " + resource + " FILE EXIST!!!   ");
                        break;
                    case 3:
                        alert("NEBULA: LIB > " + resource + " FILE PATH ERROR!!!   ");
                        break;
                    case 4:
                        alert("NEBULA: LIB > " + resource + " INPUT = NULL!!!   ");
                        break;
                }
            }
            info("NEBULA: LIB <> COPY FROM MOD END   ");
        }
    }

    public static void init() {
        info("NEBULA: LIB <> STRUCTURE GENERATOR START   ");


        File configDir = Loader.instance().getConfigDir();

        File templateDir = new File(configDir, "nebulalib/template");
        info("NEBULA: LIB > START READ TEMPLATE   ");
        readTemplate(templateDir);
        info("NEBULA: LIB > END READ TEMPLATE   ");

        File structureDir = new File(configDir, "nebulalib/structures");
        info("NEBULA: LIB > START READ STRUCTURES   ");
        readStructures(structureDir);
        info("NEBULA: LIB > END READ STRUCTURES   ");

        File generatorDir = new File(configDir, "nebulalib/generators");
        info("NEBULA: LIB > START READ GENERATORS   ");
        readGenerators(generatorDir);
        info("NEBULA: LIB > END READ GENERATORS   ");

        info("NEBULA: LIB > START STRUCTURE DATA PROCESSING > GENERATOR LINK   ");
        for (StructureBase base : LIST_STRUCTURES) {
            if (base.generatorLink != null) {
                StructureGeneratorBase generator = getGeneratorByName(base.generatorLink.get("generator_name").getAsString());
                if (generator != null) {
                    generator.addElement(base, base.generatorLink);
                    debug("STRUCTURE LINK: " + base.name + " SUCCESSFUL   ");
                } else {
                    alert("STRUCTURE LINK: " + base.name + " SKIP: GENERATOR NOT FOUND   ");
                }
                base.generatorLink = null;
            }
        }
        info("NEBULA: LIB > END STRUCTURE DATA PROCESSING > GENERATOR LINK   ");

        info("NEBULA: LIB > START GENERATOR DATA PROCESSING > GENERATOR PRIORITY   ");
        LIST_GENERATORS.sort(Comparator.comparingInt(StructureGeneratorBase::getPriority).thenComparing(StructureGeneratorBase::getName));
        info("NEBULA: LIB > END GENERATOR DATA PROCESSING > GENERATOR PRIORITY   ");

        info("NEBULA: LIB <> STRUCTURE GENERATOR END   ");
    }

    public static void alert(String error) {
        System.out.println(error);
    }

    public static void debug(String text) {
        System.out.println(text);
    }

    public static void info(String text) {
        System.out.println(text);
    }

    public static void readGenerators(File dir) {
        if (!dir.exists() && !dir.mkdirs()) {
            alert("FILE_ERROR: " + dir.getName() + " !!!");
            return;
        }
        File[] files = dir.listFiles();
        if (files != null) {
            for (File testFile : files) {
                if (testFile.isDirectory()) {
                    readGenerators(testFile);
                    continue;
                }
                String fileName = testFile.getName();
                if (fileName.endsWith(".json")) {
                    switch (CreateGenerator(testFile)) {
                        case 0:
                            debug("GENERATOR: " + fileName + "  SUCCESSFUL   ");
                            break;
                        case 1:
                            alert("GENERATOR: " + fileName + "  SKIP: GENERATOR NOT FOUND!!!   ");
                            break;
                        case 2:
                            alert("GENERATOR: " + fileName + "  SKIP: REQUIRED MODS!!!   ");
                            break;
                        case 3:
                            alert("GENERATOR: " + fileName + "  ERROR!!!   ");
                            break;
                        case 4:
                            alert("GENERATOR: " + fileName + "  WRONG SYNTAX!!!   ");
                            break;
                    }
                }
            }
        }
    }

    public static int CreateGenerator(File file) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileReader reader = new FileReader(file)) {
            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
            if (jsonObject.has("required_mods")) {
                JsonArray requiredArray = jsonObject.getAsJsonArray("required_mods");
                for (JsonElement mod : requiredArray) {
                    if (!Loader.isModLoaded(mod.getAsString())) {
                        return 2;
                    }
                }
            }
            if (jsonObject.has("generator_type")) {
                Function<JsonObject, StructureGeneratorBase> fun = MAP_GENERATORS.get(jsonObject.get("generator_type").getAsString());
                if (fun != null) {
                    LIST_GENERATORS.add(fun.apply(jsonObject));
                    return 0;
                }
                return 1;
            }
            return 4;
        } catch (IOException e) {
            return 3;
        }
    }

    public static void readTemplate(File dir) {
        if (!dir.exists() && !dir.mkdirs()) {
            alert("FILE_ERROR: " + dir.getName() + " !!!");
            return;
        }
        File[] files = dir.listFiles();
        if (files != null) {
            for (File testFile : files) {
                if (testFile.isDirectory()) {
                    readTemplate(testFile);
                    continue;
                }
                String fileName = testFile.getName();
                if (fileName.endsWith(".nbt")) {
                    Template template = NLibFileUtilities.getTemplateFromFile(testFile);
                    if (template != null) {
                        LIST_TEMPLATES.add(new TEMPLATE(ATEM++, NLibFileUtilities.getNameWithoutExt(fileName), template));
                        debug("TEMPLATE: " + fileName + "  SUCCESSFUL   ");
                    } else {
                        alert("TEMPLATE: " + fileName + "  ERROR!!!   ");
                    }
                } else if (fileName.endsWith(".cfg")) {
                    NLibTemplate nLibTemplate = NLibTemplate.create(testFile);
                    if (nLibTemplate != null) {
                        LIST_TEMPLATES.add(new TEMPLATE(ATEM++, NLibFileUtilities.getNameWithoutExt(fileName), nLibTemplate));
                    }
                }
            }
        }
    }

    public static void readStructures(File dir) {
        if (!dir.exists() && !dir.mkdirs()) {
            alert("FILE_ERROR: " + dir.getName() + " !!!");
            return;
        }
        File[] files = dir.listFiles();
        if (files != null) {
            for (File testFile : files) {
                if (testFile.isDirectory()) {
                    readStructures(testFile);
                    continue;
                }
                String fileName = testFile.getName();
                if (fileName.endsWith(".json")) {
                    switch (Create_Structure(testFile)) {
                        case 0:
                            debug("STRUCTURE: " + fileName + "  SUCCESSFUL   ");
                            break;
                        case 1:
                            alert("STRUCTURE: " + fileName + "  ERROR!!!   ");
                            break;
                        case 2:
                            alert("STRUCTURE: " + fileName + "  SKIP: REQUIRED MODS!!!   ");
                            break;
                        case 3:
                            alert("STRUCTURE: " + fileName + "  SKIP: STRUCTURE NOT FOUND!!!   ");
                            break;
                    }
                }
            }
        }
    }

    public static int Create_Structure(File file) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileReader reader = new FileReader(file)) {
            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
            if (jsonObject.has("required_mods")) {
                JsonArray requiredArray = jsonObject.getAsJsonArray("required_mods");
                for (JsonElement mod : requiredArray) {
                    if (!Loader.isModLoaded(mod.getAsString())) {
                        return 2;
                    }
                }
            }

            String structureName = NLibFileUtilities.getFileNameWithoutExt(file);
            TEMPLATE strLinkTemplate;
            String structureLocation;
            if (jsonObject.has("structure_location")) {
                String strLocWithoutExt;
                structureLocation = jsonObject.get("structure_location").getAsString();
                if (structureLocation.endsWith(".nbt")) {
                    strLocWithoutExt = NLibFileUtilities.getNameWithoutExt(structureLocation);
                } else {
                    strLocWithoutExt = structureLocation;
                    structureLocation += ".nbt";
                }
                strLinkTemplate = getTemplateByName(strLocWithoutExt);
                if (strLinkTemplate == null) {
                    Template template = NLibFileUtilities.getTemplateFromMod(structureLocation);
                    if (template != null) {
                        strLinkTemplate = new TEMPLATE(ATEM++, strLocWithoutExt, template);
                        LIST_TEMPLATES.add(strLinkTemplate);
                    } else {
                        return 3;
                    }
                }
            } else {
                strLinkTemplate = getTemplateByName(structureName);
                if (strLinkTemplate == null) {
                    return 3;
                }
            }

            if (jsonObject.has("name")) {
                structureName = jsonObject.get("name").getAsString();
            }

            StructureBase structure = new StructureBase(
                    ASTR++, structureName, strLinkTemplate.name, strLinkTemplate.id,
                    //jsonObject.has("spawn_type") ? jsonObject.get("spawn_type").getAsInt() : 0,
                    jsonObject.has("offsetX") ? jsonObject.get("offsetX").getAsInt() : 0,
                    jsonObject.has("offsetY") ? jsonObject.get("offsetY").getAsInt() : 0,
                    jsonObject.has("offsetZ") ? jsonObject.get("offsetZ").getAsInt() : 0
            );

            if (jsonObject.has("default_rotation")) {
                String testRotation = jsonObject.get("default_rotation").getAsString();
                for (Rotation rot : Rotation.values()) {
                    if (rot.name().equals(testRotation)) {
                        structure.changeDefaultRotation(rot);
                        break;
                    }
                }
            }

            if (jsonObject.has("spawn_rules")) {
                for (JsonElement element : jsonObject.getAsJsonArray("spawn_rules")) {
                    JsonObject ruleObj = element.getAsJsonObject();
                    if (ruleObj.has("type")) {
                        String type = ruleObj.get("type").getAsString();
                        Function<JsonObject, IStructureSpawnTest> fun = MAP_TEST.get(type);
                        if (fun != null) {
                            structure.locationTests.add(fun.apply(ruleObj));
                        }
                    }
                }
            }

            if (jsonObject.has("after_spawn")) {
                for (JsonElement element : jsonObject.getAsJsonArray("after_spawn")) {
                    JsonObject ruleObj = element.getAsJsonObject();
                    if (ruleObj.has("type")) {
                        String type = ruleObj.get("type").getAsString();
                        Function<JsonObject, IAfterSpawnFunction> fun = MAP_AFTER.get(type);
                        if (fun != null) {
                            structure.afterFunctions.add(fun.apply(ruleObj));
                        }
                    }
                }
            }

            if (jsonObject.has("generator_link")) {
                structure.generatorLink = jsonObject.get("generator_link").getAsJsonObject();
            }

            LIST_STRUCTURES.add(structure);
            return 0;
        } catch (IOException e) {
            return 1;
        }
    }

    @Nullable
    public static TEMPLATE getTemplateByName(String searchName) {
        for (TEMPLATE template : LIST_TEMPLATES) {
            if (template.name.equals(searchName)) {
                return template;
            }
        }
        return null;
    }

    @Nullable
    public static TEMPLATE getTemplateById(int id) {
        for (TEMPLATE template : LIST_TEMPLATES) {
            if (template.id == id) {
                return template;
            }
        }
        return null;
    }

    @Nullable
    public static StructureBase getStructureByName(String searchName) {
        for (StructureBase base : LIST_STRUCTURES) {
            if (base.name.equals(searchName)) {
                return base;
            }
        }
        return null;
    }

    @Nullable
    public static StructureBase getStructureById(int id) {
        for (StructureBase base : LIST_STRUCTURES) {
            if (base.id == id) {
                return base;
            }
        }
        return null;
    }

    @Nullable
    public static StructureGeneratorBase getGeneratorByName(String searchName) {
        for (StructureGeneratorBase base : LIST_GENERATORS) {
            if (base.name.equals(searchName)) {
                return base;
            }
        }
        return null;
    }

    static {
        //init
        MAP_TEST = new HashMap<>();
        MAP_GEN_TEST = new HashMap<>();
        MAP_AFTER = new HashMap<>();
        MAP_GENERATORS = new HashMap<>();

        //test`s
        MAP_TEST.put("biome", TEST_Biome::new);
        MAP_TEST.put("dimension", TEST_Dimension::new);
        //after`s
        MAP_AFTER.put("spawn_entity", AFTER_SpawnEntity::new);
        //generator`s
        MAP_GENERATORS.put("simple", GEN_EveryXZChunk::new);
        //compatibility
        if (NebulaLib.srparasites) {
            MAP_TEST.put("evo_phase", TEST_EvoPhase::new);
            MAP_TEST.put("node_colony", TEST_NodeColony::new);
        }
    }

    public static class TEMPLATE {
        public final int id;
        public final String name;
        public Template minecraftTemplate = null;
        public NLibTemplate nLibTemplate = null;
        public TEMPLATE(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public TEMPLATE(int id, String name, Template mcTemplate) {
            this(id, name);
            this.minecraftTemplate = mcTemplate;
        }

        public TEMPLATE(int id, String name, NLibTemplate nLib) {
            this(id, name);
            this.nLibTemplate = nLib;
        }

        public boolean generate(World world, BlockPos pos, Rotation rotation) {
            return true;
        }
    }
}
