package energon.nebulalib.init;

import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import energon.nebulalib.NebulaLib;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.registries.IForgeRegistry;

public class NLibEntities {
    public static EntityEntry[] ENTITIES;

    private static <T extends Entity> EntityEntry CreateEntityMob(String name, Class<T> cls, int primaryColorIn, int secondaryColorIn, int id, boolean active) {
        if (!active) {
            return null;
        } else {
            EntityEntryBuilder<T> builder = EntityEntryBuilder.create();
            builder.entity(cls);
            builder.name("nebulalib." + name);
            builder.id(new ResourceLocation("srparasites", name), id);
            builder.tracker(64, 3, true);
            if (SRPConfig.vanillaEggs) {
                builder.egg(primaryColorIn, secondaryColorIn);
            }
            return builder.build();
        }
    }

    @Mod.EventBusSubscriber(modid = NebulaLib.MODID)
    public static class RegistrationHandler {
        public RegistrationHandler() {
        }

        @SubscribeEvent
        public static void onEvent(RegistryEvent.Register<EntityEntry> event) {
            IForgeRegistry<EntityEntry> registry = event.getRegistry();
            NLibEntities.ENTITIES = new EntityEntry[]{

            };
            for(int i = 0; i < NLibEntities.ENTITIES.length; ++i) {
                if (NLibEntities.ENTITIES[i] != null) {
                    registry.register(NLibEntities.ENTITIES[i]);
                }
            }
        }
    }
}
