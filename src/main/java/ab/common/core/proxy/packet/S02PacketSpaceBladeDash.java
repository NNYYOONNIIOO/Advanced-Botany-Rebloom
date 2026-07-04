package ab.common.core.proxy.packet;

import ab.common.item.equipment.ItemSpaceBlade;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

public class S02PacketSpaceBladeDash implements IMessage, IMessageHandler<S02PacketSpaceBladeDash, IMessage> {
    @Override
    public void fromBytes(ByteBuf buf) {
    }

    @Override
    public void toBytes(ByteBuf buf) {
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IMessage onMessage(S02PacketSpaceBladeDash message, MessageContext ctx) {
        ItemSpaceBlade.onPlayerSpaceDash(Minecraft.getMinecraft().player);
        return null;
    }
}
