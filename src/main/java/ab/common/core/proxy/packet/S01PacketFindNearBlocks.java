package ab.common.core.proxy.packet;

import ab.common.item.relic.ItemSphereNavigation;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class S01PacketFindNearBlocks implements IMessage, IMessageHandler<S01PacketFindNearBlocks, IMessage> {
    private int blockID;
    private int meta;

    public S01PacketFindNearBlocks() {
    }

    public S01PacketFindNearBlocks(Block block, int meta) {
        this.blockID = Block.getIdFromBlock(block);
        this.meta = meta;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.blockID);
        buf.writeInt(this.meta);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.blockID = buf.readInt();
        this.meta = buf.readInt();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IMessage onMessage(S01PacketFindNearBlocks message, MessageContext ctx) {
        ItemSphereNavigation.findBlocks(Minecraft.getMinecraft().world, Block.getBlockById(message.blockID), message.meta, Minecraft.getMinecraft().player);
        return null;
    }
}
