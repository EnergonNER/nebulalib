package energon.nebulalib.network.packet;

import energon.nebulalib.event.NLibEventHandler;
import energon.nebulalib.event.NLibEventHandlerClient;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class EventPacket implements IMessage {
    public int id;
    public EventPacket() {}

    public EventPacket(int eventID) {
        this.id = eventID;
    }

    @Override
    public void fromBytes(ByteBuf byteBuf) {
        this.id = byteBuf.readInt();
    }

    @Override
    public void toBytes(ByteBuf byteBuf) {
        byteBuf.writeInt(this.id);
    }

    public static class Handler implements IMessageHandler<EventPacket, IMessage> {
        @Override
        public IMessage onMessage(EventPacket message, MessageContext ctx) {
            if (ctx.side.isClient()) {
                Minecraft.getMinecraft().addScheduledTask(() -> sendEvent(message));
            }
            return null;
        }

        public void sendEvent(EventPacket packet) {
            NLibEventHandler.EVENT event = NLibEventHandler.getEventById(packet.id);
            NLibEventHandlerClient.CORRECT_EVENT = event != null ? event.getEvent(null) : null;
        }
    }
}
