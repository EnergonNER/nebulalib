package energon.nebulalib.structure.test;

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
        for (JsonElement element : object.getAsJsonArray("biomes")) {
            String test = element.getAsString();
            if (!test.isEmpty()) {
                this.biomeTests.add(new BIOME_LOCAL(test));
                if (this.BLB && test.charAt(0) != '!') {
                    this.BLB = false;
                }
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
        public BIOME_LOCAL(String part) {
            if (part.contains(":")) {
                if (part.charAt(0) == '!') {
                    this.test = TEST_TYPE.N_REG;
                    this.part = part.substring(1);
                } else {
                    this.test = TEST_TYPE.REG;
                    this.part = part;
                }
            } else {
                if (part.charAt(0) == '!') {
                    this.test = TEST_TYPE.N_TYPE;
                    this.part = part.substring(1);
                } else {
                    this.test = TEST_TYPE.TYPE;
                    this.part = part;
                }
            }
        }
    }

    public enum TEST_TYPE {
        REG,
        N_REG,
        TYPE,
        N_TYPE;
    }
}
