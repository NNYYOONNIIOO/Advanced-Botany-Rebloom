package ab.common.core.proxy.packet;

import ab.common.item.equipment.ItemSpaceBlade;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class C03PacketSpaceBladeBurst implements IMessage, IMessageHandler<C03PacketSpaceBladeBurst, IMessage> {
    @Override
    public void fromBytes(ByteBuf buf) {
    }

    @Override
    public void toBytes(ByteBuf buf) {
    }

    @Override
    public IMessage onMessage(C03PacketSpaceBladeBurst message, MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().player;
        player.getServerWorld().addScheduledTask(() -> {
            ItemStack stack = player.getHeldItemMainhand();
            if (stack.getItem() instanceof ItemSpaceBlade) {
                ItemSpaceBlade.fireStarburst(stack, player);
            }
        });
        return null;
    }
}
