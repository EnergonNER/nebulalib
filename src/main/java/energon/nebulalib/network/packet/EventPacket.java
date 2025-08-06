package energon.nebulalib.network.packet;

import energon.nebulalib.NebulaLib;
import energon.nebulalib.proxy.CommonProxy;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class EventPacket implements IMessage {
    public int id;
    public EventPacket() {

    }

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
            CommonProxy proxy = NebulaLib.proxy;
            proxy.eventHandler(message.id);
            return null;
        }
    }
}
