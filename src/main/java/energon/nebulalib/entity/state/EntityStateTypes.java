package energon.nebulalib.entity.state;

import java.util.ArrayList;
import java.util.List;

public record EntityStateTypes(int id, String name) {
    public static List<EntityStateTypes> TYPES = new ArrayList<>();
    public static EntityStateTypes NEXT = new EntityStateTypes(1, "next");





    public EntityStateTypes(int id, String name) {
        this.id = id;
        this.name = name;
        TYPES.add(this);
    }

    public boolean isEquals(EntityStateTypes test) {
        return test.id == this.id;
    }
}
