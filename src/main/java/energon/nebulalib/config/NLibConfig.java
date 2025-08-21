package energon.nebulalib.config;

import energon.nebulalib.NebulaLib;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.File;

public class NLibConfig {
    public static boolean EVENTS_ON = true;
    public static boolean STRUCTURES_ON = false;

    private static void initMainSettings(Configuration config) {
        String category = "+main";
        EVENTS_ON = config.getBoolean("event_on", category, EVENTS_ON, "");
        STRUCTURES_ON = config.getBoolean("structure_spawn", category, STRUCTURES_ON, "");
    }

    public static void registerConfig(FMLPreInitializationEvent event) {
        NebulaLib.config = new Configuration(new File(event.getModConfigurationDirectory(), "nebulalib/nebulalib.cfg"), NebulaLib.VERSION);
        if (!NebulaLib.config.getDefinedConfigVersion().equals(NebulaLib.config.getLoadedConfigVersion()) && NebulaLib.config.getConfigFile().exists() && NebulaLib.config.getConfigFile().delete()) {

        }
        readConfig(NebulaLib.config);
    }

    public static void readConfig(Configuration config) {
        try {
            config.load();
            initMainSettings(config);




        } catch (Exception ignored) {
        } finally {
            if (config.hasChanged()) {
                config.save();
            }
        }
    }
}
