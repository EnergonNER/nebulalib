package energon.nebulalib.structure;

import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;

public class TemplateElement {
    public final int id;
    public final String name;
    public Template minecraftTemplate = null;
    public NLibTemplate nLibTemplate = null;
    public TemplateElement(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public TemplateElement(int id, String name, Template mcTemplate) {
        this(id, name);
        this.minecraftTemplate = mcTemplate;
    }

    public TemplateElement(int id, String name, NLibTemplate nLib) {
        this(id, name);
        this.nLibTemplate = nLib;
    }

    public boolean generate(World world, BlockPos pos, Rotation rotation) {
        if (this.minecraftTemplate != null) {
            return this.generateMCTemplate(world, pos, rotation);
        }
        return this.generateNLIBTemplate(world, pos, rotation);
    }

    public BlockPos getSizes() {
        if (this.minecraftTemplate != null) {
            return this.minecraftTemplate.getSize();
        }
        return this.nLibTemplate != null ? this.nLibTemplate.getSizes() : BlockPos.ORIGIN;
    }

    public BlockPos getPosForRotation(BlockPos pos, Rotation rotation, BlockPos sizes) {
        switch (rotation) {
            case CLOCKWISE_90: return pos.add(sizes.getZ() - 1, 0, 0);
            case CLOCKWISE_180: return pos.add(sizes.getX() - 1, 0, sizes.getZ() - 1);
            case COUNTERCLOCKWISE_90: return pos.add(0, 0, sizes.getX() - 1);
            default: return pos;
        }
    }

    public boolean generateMCTemplate(World world, BlockPos pos, Rotation rotation) {
        if (this.minecraftTemplate != null) {
            this.minecraftTemplate.addBlocksToWorldChunk(world, this.getPosForRotation(pos, rotation, this.getSizes()), new PlacementSettings().setRotation(rotation));
            return true;
        }
        return false;
    }

    public boolean generateNLIBTemplate(World world, BlockPos pos, Rotation rotation) {
        if (this.nLibTemplate != null) {


            return true;
        }
        return false;
    }
}
