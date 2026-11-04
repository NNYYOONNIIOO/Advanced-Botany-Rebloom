package ab.common.core.proxy.packet;

import ab.client.integration.thaumcraft.AspecolusClientHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class S03PacketAspecolus implements IMessage, IMessageHandler<S03PacketAspecolus, IMessage> {
    private int windowId;
    private boolean active;

    public S03PacketAspecolus() { }

    public S03PacketAspecolus(int windowId, boolean active) {
        this.windowId = windowId;
        this.active = active;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        windowId = buf.readInt();
        active = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(windowId);
        buf.writeBoolean(active);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IMessage onMessage(S03PacketAspecolus message, MessageContext ctx) {
        Minecraft.getMinecraft().addScheduledTask(() ->
                AspecolusClientHandler.setActive(message.windowId, message.active));
        return null;
    }
}
