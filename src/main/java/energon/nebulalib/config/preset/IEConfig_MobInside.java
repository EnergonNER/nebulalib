package energon.nebulalib.config.preset;

import energon.nebulalib.util.NLibMobInside;
import net.minecraft.world.entity.LivingEntity;

public interface IEConfig_MobInside {
    void spawnMobInside(LivingEntity summoner);
    NLibMobInside getMobInside();
}
