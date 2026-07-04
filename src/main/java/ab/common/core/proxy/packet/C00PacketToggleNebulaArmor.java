package ab.common.core.proxy.packet;

import ab.common.item.equipment.armor.ItemNebulaArmor;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;

public class C00PacketToggleNebulaArmor implements IMessage, IMessageHandler<C00PacketToggleNebulaArmor, IMessage> {

    private int slotIndex;

    public C00PacketToggleNebulaArmor() {
    }

    public C00PacketToggleNebulaArmor(int slotIndex) {
        this.slotIndex = slotIndex;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.slotIndex);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.slotIndex = buf.readInt();
    }

    @Override
    public IMessage onMessage(C00PacketToggleNebulaArmor message, MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().player;
        MinecraftServer server = player.getServer();
        if (server != null) {
            server.addScheduledTask(() -> {
                Container container = player.openContainer;
                if (container != null && message.slotIndex >= 0 && message.slotIndex < container.inventorySlots.size()) {
                    Slot slot = container.getSlot(message.slotIndex);
                    ItemStack stack = slot.getStack();
                    if (!stack.isEmpty() && stack.getItem() instanceof ItemNebulaArmor) {
                        EntityEquipmentSlot equipSlot = ((ItemNebulaArmor) stack.getItem()).getEquipmentSlot();
                        if (equipSlot != null) {
                            ItemNebulaArmor.toggleAndRefreshAttributes(player, stack, equipSlot);
                        } else {
                            ItemNebulaArmor.setEffectEnabled(stack, !ItemNebulaArmor.enableEffect(stack));
                        }
                    }
                }
            });
        }
        return null;
    }
}
