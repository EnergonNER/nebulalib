package energon.nebulalib.structure;

import javax.annotation.Nullable;
import java.io.File;

public class NLibTemplate {



    private NLibTemplate() {

    }

    @Nullable
    public static NLibTemplate create(File file) {
        return new NLibTemplate();
    }
}
