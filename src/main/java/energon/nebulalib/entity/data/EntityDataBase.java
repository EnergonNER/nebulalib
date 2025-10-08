package energon.nebulalib.entity.data;

import energon.nebulalib.config.preset.IEConfigBase;
import energon.nebulalib.config.preset.IEConfig_Loot;
import energon.nebulalib.config.preset.IEConfig_MobInside;
import energon.nebulalib.entity.state.IEStateBase;
import energon.nebulalib.util.NLibLootTable;
import energon.nebulalib.util.NLibMobInside;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;

public abstract class EntityDataBase<T extends LivingEntity> {
    public static NLibLootTable EMPTY_LOOT = new NLibLootTable();
    public static NLibMobInside EMPTY_MOB_INSIDE = new NLibMobInside();
    public final RegistryObject<EntityType<T>> entityObject;
    public IEConfigBase config;
    public List<IEStateBase> STATES = new ArrayList<>();
    public EntityDataBase(RegistryObject<EntityType<T>> entityObject, IEConfigBase config) {
        this.entityObject = entityObject;
        this.config = config;
    }

    public T getEntity(Level level, EntitySpawnReason reason) {
        return this.entityObject.get().create(level, reason);
    }

    public String getRegName() {
        return this.entityObject.getId().toString();
    }

    public AttributeSupplier.Builder createAttributes() {
        return config.createAttributes();
    }

    public void spawnDeathLoot(LivingEntity summoner) {
        if (config instanceof IEConfig_Loot loot) {
            loot.spawnDeathLoot(summoner);
        }
    }

    public NLibLootTable getDeathLoot() {
        return config instanceof IEConfig_Loot loot ? loot.getDeathLoot() : EMPTY_LOOT;
    }

    public void spawnMobInside(LivingEntity summoner) {
        if (config instanceof IEConfig_MobInside inside) {
            inside.spawnMobInside(summoner);
        }
    }

    public NLibMobInside getMobInside() {
        return config instanceof IEConfig_MobInside inside ? inside.getMobInside() : EMPTY_MOB_INSIDE;
    }

    public void onSpawn(LivingEntity entity) {

    }

    public void onLoad(LivingEntity entity) {

    }

    public void onAILoad(LivingEntity entity) {

    }

    public void onDead(LivingEntity entity) {

    }
}
