package energon.nebulalib.handler;

import energon.nebulalib.NebulaLib;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class NLibSoundHandler {
    public static SoundEvent presence;

    public static void registerSounds() {
        presence = registerSound("presence");
    }

    private static SoundEvent registerSound(String name) {
        ResourceLocation location = new ResourceLocation(NebulaLib.MODID, name);
        SoundEvent event = new SoundEvent(location);
        event.setRegistryName(name);
        ForgeRegistries.SOUND_EVENTS.register(event);
        return event;
    }
}
