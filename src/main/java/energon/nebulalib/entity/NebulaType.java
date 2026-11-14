package energon.nebulalib.entity;

import net.minecraft.world.entity.EntityType;

public class NebulaType {
    public final EntityType<?> type;
    public final int categoryId;
    public NebulaType(EntityType<?> type, int id) {
        this.type = type;
        this.categoryId = id;
    }
}
