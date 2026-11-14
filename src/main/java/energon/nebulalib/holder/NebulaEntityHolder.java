package energon.nebulalib.holder;

import energon.nebulalib.entity.NebulaCategory;

import java.util.ArrayList;
import java.util.List;

public class NebulaEntityHolder {
    public static List<NebulaCategory> CATEGORIES = new ArrayList<>();


    public static void basicInit() {
        CATEGORIES.add(new NebulaCategory("minecraft_hostile", 0));
        CATEGORIES.add(new NebulaCategory("minecraft_animal", 1));
    }
}
