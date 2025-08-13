package energon.nebulalib.util;

import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;

public class NLibWorldUtilities {
    public static BiomeDictionary.Type getBiomeType(String typeName) {
        for (BiomeDictionary.Type test : BiomeDictionary.Type.getAll()) {
            if (test.getName().equalsIgnoreCase(typeName)) {
                return test;
            }
        }
        return null;
    }

    public static boolean biomeHasType(Biome biome, String typeName) {
        BiomeDictionary.Type type = getBiomeType(typeName);
        return type != null && BiomeDictionary.hasType(biome, type);
    }
}
