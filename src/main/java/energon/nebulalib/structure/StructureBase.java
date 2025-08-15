package energon.nebulalib.structure;

import com.google.gson.JsonObject;
import energon.nebulalib.structure.after.IAfterSpawnFunction;
import energon.nebulalib.structure.test.IStructureSpawnTest;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class StructureBase {
    public final int id;
    public final String name;
    public final String structureLink;
    public JsonObject generatorLink = null;
    public final int offsetX;
    public final int offsetY;
    public final int offsetZ;
    public Rotation defRotation = Rotation.NONE;
    public List<IStructureSpawnTest> locationTests = null;
    public List<IAfterSpawnFunction> afterFunctions = null;

    public StructureBase(int id, String name, String structureLink, int offsetX, int offsetY, int offsetZ) {
        this.id = id;
        this.name = name;
        this.structureLink = structureLink;

        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
    }

    public void changeDefaultRotation(Rotation rotation) {
        this.defRotation = rotation;
    }

    //FIRST
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
}
