package energon.nebulalib.structure;

import com.google.gson.JsonObject;
import energon.nebulalib.structure.after.IAfterSpawnFunction;
import energon.nebulalib.structure.test.structure.IStructureSpawnTest;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class StructureBase {
    public final int id;
    public final String name;
    public final String structureLinkName;
    public final int structureLinkID;
    public JsonObject generatorLink = null;
    public final int offsetX;
    public final int offsetY;
    public final int offsetZ;
    public Rotation defRotation = Rotation.NONE;
    public List<IStructureSpawnTest> locationTests = null;
    public List<IAfterSpawnFunction> afterFunctions = null;

    public StructureBase(int id, String name, String structureLink, int structureLinkID, int offsetX, int offsetY, int offsetZ) {
        this.id = id;
        this.name = name;
        this.structureLinkName = structureLink;
        this.structureLinkID = structureLinkID;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
    }

    @Override
    public String toString() {
        return this.getInfo(false);
    }

    public String getInfo(boolean all) {
        if (all) {
            return "Structure name: " + this.name + "  ";
        }
        return "Structure name: " + this.name + "  ";
    }

    public void changeDefaultRotation(Rotation rotation) {
        this.defRotation = rotation;
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

    public boolean generate(World world, BlockPos pos, Rotation rotation) {
        TemplateElement template = NLibStructureHandler.getTemplateById(this.structureLinkID);
        if (template != null) {
            return template.generate(world, pos, rotation);
        }
        return false;
    }

    public void runAfterFunctions(World world, BlockPos pos, Rotation rotation) {
        if (this.afterFunctions != null) {
            for (IAfterSpawnFunction function : this.afterFunctions) {
                function.start(world, pos, this, rotation);
            }
        }
    }
}
