package energon.nebulalib;

import energon.nebulalib.entity.IEData;
import energon.nebulalib.entity.data.EntityDataBase;
import energon.nebulalib.entity.data.EntityDataHandler;
import energon.nebulalib.entity.data.EntityDataEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = NebulaLibMain.MODID)
public class NLibCommonEventBus {
    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        EntityDataEvent.NLibRegisterAttributes_Pre.BUS.fire(new EntityDataEvent.NLibRegisterAttributes_Pre());
        for (EntityDataBase<?> data : EntityDataHandler.ENTITIES) {
            event.put(data.entityObject.get(), data.createAttributes().build());
        }
        EntityDataEvent.NLibRegisterAttributes_Post.BUS.fire(new EntityDataEvent.NLibRegisterAttributes_Post());
    }

    @SubscribeEvent
    public static void onEntityDeathMain(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (!entity.level().isClientSide() && entity instanceof IEData<?> data) {
            data.getData().onDead(entity);
        }
    }

    @SubscribeEvent
    public static void onEntityFirstJoin(MobSpawnEvent.FinalizeSpawn event) {
        LivingEntity entity = event.getEntity();
        if (!entity.level().isClientSide() && entity instanceof IEData<?> data) {
            data.getData().onSpawn(entity);
        }
    }
}
