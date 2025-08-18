package energon.nebulalib.structure;

import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.io.File;

public class NLibTemplate {



    public BlockPos getSizes() {
        return BlockPos.ORIGIN;
    }


    private NLibTemplate() {

    }

    @Nullable
    public static NLibTemplate create(File file) {
        return new NLibTemplate();
    }
}
