package energon.nebulalib.event;

import energon.nebulalib.event.events.EventBase;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(value = Side.CLIENT)
public class EventHandlerClient {
    public static EventBase CORRECT_EVENT = null;
    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.LOW, receiveCanceled = true)
    public static void playerTick(TickEvent.PlayerTickEvent event) {
        if (CORRECT_EVENT != null) {
            if (CORRECT_EVENT.clientHandler(event.player)) {
                CORRECT_EVENT = null;
            }
        }
    }
}
