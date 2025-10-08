package energon.nebulalib.config.preset;

import energon.nebulalib.util.NLibLootTable;
import net.minecraft.world.entity.LivingEntity;

public interface IEConfig_Loot {
    void spawnDeathLoot(LivingEntity summoner);
    NLibLootTable getDeathLoot();
}
