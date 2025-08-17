package energon.nebulalib.structure.test.structure;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import energon.nebulalib.util.NLibWorldUtilities;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import java.util.ArrayList;
import java.util.List;

public class TEST_Biome implements IStructureSpawnTest {
    public List<BIOME_LOCAL> biomeTests;
    public boolean BLB = true;
    public TEST_Biome(JsonObject object) {
        this.biomeTests = new ArrayList<>();
        for (JsonElement element : object.getAsJsonArray("whitelist")) {
            String test = element.getAsString();
            if (!test.isEmpty()) {
                this.biomeTests.add(new BIOME_LOCAL(test, false));
                if (this.BLB) {
                    this.BLB = false;
                }
            }
        }
        for (JsonElement element : object.getAsJsonArray("blacklist")) {
            String test = element.getAsString();
            if (!test.isEmpty()) {
                this.biomeTests.add(new BIOME_LOCAL(test, true));
            }
        }
    }

    @Override
    public boolean runTest(World world, BlockPos pos) {
        Biome testBiome = world.getBiome(pos);
        ResourceLocation location = testBiome.getRegistryName();
        if (location != null) {
            String testBiomeRegName = location.toString();
            boolean result = this.BLB;
            for (BIOME_LOCAL check : biomeTests) {
                switch (check.test) {
                    case N_REG:
                        if (check.part.equals(testBiomeRegName)) {
                            return false;
                        }
                        break;
                    case N_TYPE:
                        if (NLibWorldUtilities.biomeHasType(testBiome, check.part)) {
                            return false;
                        }
                        break;
                    case REG:
                        if (check.part.equals(testBiomeRegName)) {
                            result = true;
                        }
                        break;
                    case TYPE:
                        if (NLibWorldUtilities.biomeHasType(testBiome, check.part)) {
                            result = true;
                        }
                        break;
                }
            }
            return result;
        }
        return false;
    }

    public static class BIOME_LOCAL {
        public TEST_TYPE test;
        public String part;
        public BIOME_LOCAL(String part, boolean negative) {
            if (negative) {
                this.test = part.contains(":") ? TEST_TYPE.N_REG : TEST_TYPE.N_TYPE;
            } else {
                this.test = part.contains(":") ? TEST_TYPE.REG : TEST_TYPE.TYPE;
            }
            this.part = part;
        }
    }

    public enum TEST_TYPE {
        REG,
        N_REG,
        TYPE,
        N_TYPE;
    }
}
