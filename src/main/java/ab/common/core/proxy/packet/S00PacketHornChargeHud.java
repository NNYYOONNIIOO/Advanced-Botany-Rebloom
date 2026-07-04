package ab.common.core.proxy.packet;

import ab.client.core.handler.ItemsRemainingRender;
import ab.common.lib.register.ItemListAB;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;

public class S00PacketHornChargeHud implements IMessage, IMessageHandler<S00PacketHornChargeHud, IMessage> {
    private static final ItemStack horn = new ItemStack(ItemListAB.itemHornPlenty);
    private short chargeLoot;

    public S00PacketHornChargeHud() {
    }

    public S00PacketHornChargeHud(short chargeLoot) {
        this.chargeLoot = chargeLoot;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeShort(this.chargeLoot);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.chargeLoot = buf.readShort();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IMessage onMessage(S00PacketHornChargeHud message, MessageContext ctx) {
        ItemsRemainingRender.set(horn, "" + message.chargeLoot);
        return null;
    }
}
