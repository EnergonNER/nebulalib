package energon.nebulalib.event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class Network {
    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel("custom_events");

    public static void init() {
        INSTANCE.registerMessage(EventPacket.Handler.class, EventPacket.class, 1, Side.CLIENT);
    }

    public static void sendPlayerEvent(EntityPlayer player, int eventID) {
        INSTANCE.sendTo(new EventPacket(eventID), (EntityPlayerMP) player);
    }
}
