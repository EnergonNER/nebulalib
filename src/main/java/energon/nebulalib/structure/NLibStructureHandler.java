package energon.nebulalib.structure;

import com.google.gson.*;
import energon.nebulalib.NebulaLib;
import energon.nebulalib.structure.presets.GEN_EveryXZChunk;
import energon.nebulalib.structure.after.AFTER_SpawnEntity;
import energon.nebulalib.structure.after.IAfterSpawnFunction;
import energon.nebulalib.structure.test.*;
import energon.nebulalib.util.NLibFileUtilities;
import net.minecraft.util.Rotation;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraftforge.fml.common.Loader;

import javax.annotation.Nullable;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

public class NLibStructureHandler {
    public static String VERSION = "1.0";
    public static int ASTR = 0;
    public static int ATEM = 0;

    public static HashMap<String, Function<JsonObject, IStructureSpawnTest>> MAP_STR_TEST;
    public static HashMap<String, Function<JsonObject, IAfterSpawnFunction>> MAP_STR_AFTER;
    public static HashMap<String, Function<JsonObject, StructureGeneratorBase>> MAP_GENERATORS;

    public static List<TEMPLATE> LIST_TEMPLATES = new ArrayList<>();
    public static List<StructureBase> LIST_STRUCTURES = new ArrayList<>();
    public static List<StructureGeneratorBase> LIST_GENERATORS = new ArrayList<>();
    public static void init() {
        System.out.println("NEBULA: LIB STRUCTURE GENERATOR START   ");


        File configDir = Loader.instance().getConfigDir();

        File structureDir = new File(configDir, "nebulalib/structures");
        System.out.println("NEBULA: LIB > START READ TEMPLATE   ");
        readTemplate(structureDir);
        System.out.println("NEBULA: LIB > END READ TEMPLATE   ");

        System.out.println("NEBULA: LIB > START READ STRUCTURES   ");
        readStructures(structureDir);
        System.out.println("NEBULA: LIB > END READ STRUCTURES   ");

        File generatorDir = new File(configDir, "nebulalib/generators");
        System.out.println("NEBULA: LIB > START READ GENERATORS   ");
        readGenerators(generatorDir);
        System.out.println("NEBULA: LIB > END READ GENERATORS   ");

        System.out.println("NEBULA: LIB > START READ STRUCTURE > GENERATOR LINK   ");
        for (StructureBase base : LIST_STRUCTURES) {
            if (base.generatorLink != null) {
                StructureGeneratorBase generator = getGeneratorByName(base.generatorLink.get("generator_name").getAsString());
                if (generator != null) {
                    generator.addElement(base, base.generatorLink);
                }
            }
        }
        System.out.println("NEBULA: LIB > END READ STRUCTURE > GENERATOR LINK   ");


        System.out.println("NEBULA: LIB STRUCTURE GENERATOR END   ");
    }

    public static void alert(String error) {
        System.out.println(error);
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
                            alert("GENERATOR:" + fileName + "  SUCCESSFUL   ");
                            break;
                        case 1:
                            alert("GENERATOR:" + fileName + "  SKIP: GENERATOR NOT FOUND!!!   ");
                            break;
                        case 2:
                            alert("GENERATOR:" + fileName + "  SKIP: REQUIRED MODS!!!   ");
                            break;
                        case 3:
                            alert("GENERATOR:" + fileName + "  ERROR!!!   ");
                            break;
                        case 4:
                            alert("GENERATOR:" + fileName + "  WRONG SYNTAX!!!   ");
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
                        alert("TEMPLATE:" + fileName + "  SUCCESSFUL   ");
                    } else {
                        alert("TEMPLATE:" + fileName + "  ERROR!!!   ");
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
                            alert("STRUCTURE:" + fileName + "  SUCCESSFUL   ");
                            break;
                        case 1:
                            alert("STRUCTURE:" + fileName + "  ERROR!!!   ");
                            break;
                        case 2:
                            alert("STRUCTURE:" + fileName + "  SKIP: REQUIRED MODS!!!   ");
                            break;
                        case 3:
                            alert("STRUCTURE:" + fileName + "  SKIP: STRUCTURE NOT FOUND!!!   ");
                            break;
                    }
                }
            }
        }
    }

    public static int Create_Structure(File file) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileReader reader = new FileReader(file)) {
            String fileNameWithoutExt = NLibFileUtilities.getFileNameWithoutExt(file);
            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
            if (jsonObject.has("required_mods")) {
                JsonArray requiredArray = jsonObject.getAsJsonArray("required_mods");
                for (JsonElement mod : requiredArray) {
                    if (!Loader.isModLoaded(mod.getAsString())) {
                        return 2;
                    }
                }
            }

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
                if (getTemplateByName(strLocWithoutExt) == null) {
                    Template template = NLibFileUtilities.getTemplateFromMod(structureLocation);
                    if (template != null) {
                        structureLocation = strLocWithoutExt;
                        LIST_TEMPLATES.add(new TEMPLATE(ATEM++, strLocWithoutExt, template));
                    } else {
                        return 3;
                    }
                }
            } else if (getTemplateByName(fileNameWithoutExt) != null) {
                structureLocation = fileNameWithoutExt;
            } else {
                return 3;
            }

            StructureBase structure = new StructureBase(
                    ASTR++, fileNameWithoutExt, structureLocation,
                    //jsonObject.has("spawn_type") ? jsonObject.get("spawn_type").getAsInt() : 0,
                    jsonObject.has("offsetX") ? jsonObject.get("offsetX").getAsInt() : 0,
                    jsonObject.has("offsetY") ? jsonObject.get("offsetY").getAsInt() : 0,
                    jsonObject.has("offsetZ") ? jsonObject.get("offsetZ").getAsInt() : 0
            );

            if (jsonObject.has("default_rotation")) {
                String testRotation = jsonObject.get("default_rotation").getAsString();
                for (Rotation rot : Rotation.values()) {
                    if (rot.name().equals(testRotation)) {
                        structure.defRotation = rot;
                        break;
                    }
                }
            }

            if (jsonObject.has("spawn_rules")) {
                for (JsonElement element : jsonObject.getAsJsonArray("spawn_rules")) {
                    JsonObject ruleObj = element.getAsJsonObject();
                    if (ruleObj.has("type")) {
                        String type = ruleObj.get("type").getAsString();
                        Function<JsonObject, IStructureSpawnTest> fun = MAP_STR_TEST.get(type);
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
                        Function<JsonObject, IAfterSpawnFunction> fun = MAP_STR_AFTER.get(type);
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
        MAP_STR_TEST = new HashMap<>();
        MAP_STR_AFTER = new HashMap<>();
        MAP_GENERATORS = new HashMap<>();

        //test`s
        MAP_STR_TEST.put("biome_test", TEST_Biome::new);
        MAP_STR_TEST.put("dimension_test", TEST_Dimension::new);
        //after`s
        MAP_STR_AFTER.put("spawn_entity", AFTER_SpawnEntity::new);
        //generator`s
        MAP_GENERATORS.put("simple", GEN_EveryXZChunk::new);
        //compatibility
        if (NebulaLib.srparasites) {
            MAP_STR_TEST.put("evo_phase", TEST_EvoPhase::new);
            MAP_STR_TEST.put("node_colony", TEST_NodeColony::new);
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
    }

    public static class SPAWN_TYPE {
        public final int id;
        public final String name;
        public final SpawnType spawnType;
        public SPAWN_TYPE(int id, String name, SpawnType spawnType) {
            this.id = id;
            this.name = name;
            this.spawnType = spawnType;
        }
    }
}
