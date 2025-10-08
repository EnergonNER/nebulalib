package energon.nebulalib.config.preset;

import energon.nebulalib.config.Config;
import energon.nebulalib.util.NLibLootTable;
import net.minecraft.world.entity.LivingEntity;

public class EntityConfig_L extends EntityConfigBase implements IEConfig_Loot {
    public String[] lootTableDefault;
    public NLibLootTable lootTable;
    public EntityConfig_L(boolean active, float health, float damage, float armor, float armorToughness, float movementSpeed, float followRange, float knockback, String[] lootTableDefault) {
        super(active, health, damage, armor, armorToughness, movementSpeed, followRange, knockback);
        this.lootTableDefault = lootTableDefault;
    }

    @Override
    public void init(Config config, String category) {
        super.init(config, category);
        lootTableDefault = config.getStringList("loot_table", category, lootTableDefault, "Loot Table.");
        lootTable = NLibLootTable.generate(category, lootTableDefault);
    }

    @Override
    public void spawnDeathLoot(LivingEntity summoner) {
        lootTable.spawnLoot(summoner, 100);
    }

    @Override
    public NLibLootTable getDeathLoot() {
        return lootTable;
    }
}
