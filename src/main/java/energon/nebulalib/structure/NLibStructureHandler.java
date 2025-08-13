package energon.nebulalib.structure;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import energon.nebulalib.NebulaLib;
import energon.nebulalib.config.Config;
import energon.nebulalib.structure.presets.GEN_EveryXZChunk;
import energon.nebulalib.structure.str_after.AFTER_SpawnEntity;
import energon.nebulalib.structure.str_after.IAfterSpawnFunction;
import energon.nebulalib.structure.str_test.*;
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
import java.util.regex.Pattern;

public class NLibStructureHandler {
    public static String VERSION = "1.0";
    public static int ASTR = 0;
    public static int ATEM = 0;
    //test_name|tests
    public static HashMap<String, Function<String, IStructureSpawnTest>> MAP_STR_TEST;
    //after_name|afters
    public static HashMap<String, Function<String, IAfterSpawnFunction>> MAP_STR_AFTER;
    public static HashMap<String, Function<StructureGeneratorBase.COMPACT_GEN_INPUT, StructureGeneratorBase>> MAP_GENERATORS;


    public static List<TEMPLATE> LIST_TEMPLATES = new ArrayList<>();
    public static List<StructureBase> LIST_STRUCTURES = new ArrayList<>();
    public static List<StructureGeneratorBase> LIST_GENERATORS = new ArrayList<>();
    public static void init() {
        File configDir = Loader.instance().getConfigDir();

        File structureDir = new File(configDir, "nebulalib/structures");
        readTemplate(structureDir);
        readStructures(structureDir);

        File generatorDir = new File(configDir, "nebulalib/generators");
        readGenerators(generatorDir);

        for (StructureBase base : LIST_STRUCTURES) {
            if (base.generatorLink != null) {
                String[] parts = base.generatorLink.split(Pattern.quote("|"));
                if (parts.length == 2) {
                    StructureGeneratorBase generator = getGeneratorByName(parts[0]);
                    if (generator != null) {
                        generator.addElement(base, parts[1]);
                    }
                }
            }
        }
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
                if (fileName.endsWith(".cfg")) {
                    switch (CreateGenerator(Config.ReadConfig(testFile, VERSION), NLibFileUtilities.getNameWithoutExt(fileName))) {
                        case 0:
                            alert("GENERATOR:" + fileName + "  SUCCESSFUL.");
                            break;
                        case 1:
                            alert("GENERATOR:" + fileName + "  SKIP: GENERATOR NOT FOUND !!!");
                            break;
                        case 2:
                            alert("GENERATOR:" + fileName + "  SKIP: REQUIRED MODS !!!");
                            break;
                    }
                }
            }
        }
    }

    public static int CreateGenerator(Config config, String name) {
        String category = "main";
        config.addCategoryInfo(category, "");
        String[] req = config.getStringList("required_mods", category, new String[0], "");
        for (String mod : req) {
            if (!Loader.isModLoaded(mod)) {
                config.clear();
                return 2;
            }
        }
        String genType = config.getString("generator_type", category, "simple", "");
        Function<StructureGeneratorBase.COMPACT_GEN_INPUT, StructureGeneratorBase> fun = MAP_GENERATORS.get(genType);
        if (fun != null) {
            LIST_GENERATORS.add(fun.apply(new StructureGeneratorBase.COMPACT_GEN_INPUT(name, config, category)));
            config.save();
            config.clear();
            return 0;
        }
        return 1;
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
                        alert("TEMPLATE:" + fileName + "  SUCCESSFUL.");
                    } else {
                        alert("TEMPLATE:" + fileName + "  ERROR!!!");
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
                            alert("STRUCTURE:" + fileName + "  SUCCESSFUL.");
                            break;
                        case 1:
                            alert("STRUCTURE:" + fileName + "  ERROR!!!");
                            break;
                        case 2:
                            alert("STRUCTURE:" + fileName + "  SKIP: REQUIRED MODS !!!");
                            break;
                        case 3:
                            alert("STRUCTURE:" + fileName + "  SKIP: STRUCTURE NOT FOUND !!!");
                            break;
                    }
                }
            }
        }
    }

    @Deprecated
    public static void CODE_STRUCTURES(File fileDir) throws IOException {
        if (!fileDir.exists() && !fileDir.mkdirs()) {
            alert("FILE_ERROR: " + fileDir.getName() + " !!!");
            return;
        }
        fileDir = new File(fileDir, "config.json");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("false", 0);
        try (FileWriter writer = new FileWriter(fileDir)) {
            gson.toJson(jsonObject, writer);
        }
    }

    public static int Create_Structure(File file) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileReader reader = new FileReader(file)) {
            String fileNameWithoutExt = NLibFileUtilities.getFileNameWithoutExt(file);
            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
            if (jsonObject.has("required_mods")) {
                //String[] required = jsonObject.get("required_mod").getAsString().replaceAll(" ", "").split(";");
                for (String mod : jsonObject.get("required_mods").getAsString().replaceAll(" ", "").split(";")) {
                    if (!Loader.isModLoaded(mod)) {
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
                structure.locationTests = new ArrayList<>();
                for (JsonElement element : jsonObject.getAsJsonArray("spawn_rules")) {
                    String[] parts = element.getAsString().split(Pattern.quote("|"));
                    if (parts.length == 2) {
                        Function<String, IStructureSpawnTest> fun = MAP_STR_TEST.get(parts[0]);
                        if (fun != null) {
                            structure.locationTests.add(fun.apply(parts[1]));
                        }
                    }
                }
            }

            if (jsonObject.has("after_spawn")) {
                structure.afterFunctions = new ArrayList<>();
                for (JsonElement element : jsonObject.getAsJsonArray("after_spawn")) {
                    String[] parts = element.getAsString().split(Pattern.quote("|"));
                    if (parts.length == 2) {
                        Function<String, IAfterSpawnFunction> fun = MAP_STR_AFTER.get(parts[0]);
                        if (fun != null) {
                            structure.afterFunctions.add(fun.apply(parts[1]));
                        }
                    }
                }
            }

            if (jsonObject.has("generator_link")) {
                structure.generatorLink = jsonObject.get("generator_link").getAsString();
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
        MAP_STR_TEST = new HashMap<>();
        MAP_STR_AFTER = new HashMap<>();
        MAP_GENERATORS = new HashMap<>();


        MAP_STR_TEST.put("biomes", TEST_Biome::new);
        MAP_STR_TEST.put("dim_ids", TEST_Dimension::new);
        MAP_STR_AFTER.put("spawn_entity", AFTER_SpawnEntity::new);
        MAP_GENERATORS.put("simple", GEN_EveryXZChunk::new);
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
