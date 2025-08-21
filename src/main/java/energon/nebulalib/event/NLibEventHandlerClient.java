package energon.nebulalib.event;

import energon.nebulalib.NebulaLib;
import energon.nebulalib.event.events.EventBase;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(modid = NebulaLib.MODID, value = Side.CLIENT)
public class NLibEventHandlerClient {
    public static EventBase CORRECT_EVENT = null;
    public static int preTick = 0;
    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
    public static void playerTick(TickEvent.ClientTickEvent event) {
        if (CORRECT_EVENT != null && event.phase == TickEvent.Phase.END) {
            Minecraft mc = Minecraft.getMinecraft();
            if (preTick != mc.player.ticksExisted) {
                preTick = mc.player.ticksExisted;
                if (CORRECT_EVENT.clientHandler()) {
                    CORRECT_EVENT = null;
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
    public static void onRenderOverlay(RenderGameOverlayEvent.Pre event) {
        if (CORRECT_EVENT != null) {
            CORRECT_EVENT.overlayRender(event);
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.LOW, receiveCanceled = true)
    public static void renderWorldL(RenderWorldLastEvent event){
        if (CORRECT_EVENT != null) {
            CORRECT_EVENT.worldRender(event);
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void onWorldUnload(WorldEvent.Unload event) {
        if (event.getWorld().isRemote) {
            CORRECT_EVENT = null;
        }
    }
}
