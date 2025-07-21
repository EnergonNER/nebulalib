package energon.nebulalib.config;

import energon.nebulalib.NebulaLib;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.File;

public class NLibConfig {

    private static void initMainSettings(Configuration config) {
        String category = "+main";
    }

    public static void registerConfig(FMLPreInitializationEvent event) {
        NebulaLib.config = new Configuration(new File(event.getModConfigurationDirectory() + "/nebulalib.cfg"), NebulaLib.VERSION);
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
