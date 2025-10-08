package energon.nebulalib.entity;

import energon.nebulalib.entity.data.EntityDataBase;
import net.minecraft.world.entity.LivingEntity;

public interface IEData <T extends LivingEntity> {
    EntityDataBase<T> getData();
}
