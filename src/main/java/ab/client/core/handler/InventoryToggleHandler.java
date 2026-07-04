package ab.client.core.handler;

import ab.common.core.handler.NetworkHandler;
import ab.common.core.proxy.packet.C00PacketToggleNebulaArmor;
import ab.common.item.equipment.armor.ItemNebulaArmor;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.client.event.GuiScreenEvent.MouseInputEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Mouse;

@SideOnly(Side.CLIENT)
public class InventoryToggleHandler {

    public InventoryToggleHandler() {
    }

    @SubscribeEvent
    public void onMouseInput(MouseInputEvent.Pre event) {
        if (!(event.getGui() instanceof GuiInventory) && !(event.getGui() instanceof GuiContainerCreative)) {
            return;
        }
        if (Mouse.getEventButton() != 1 || !Mouse.getEventButtonState()) {
            return;
        }

        GuiContainer gui = (GuiContainer) event.getGui();
        Minecraft mc = Minecraft.getMinecraft();
        int mouseX = Mouse.getEventX() * gui.width / mc.displayWidth;
        int mouseY = gui.height - Mouse.getEventY() * gui.height / mc.displayHeight - 1;

        // Find the hovered slot by mouse position (same logic as GuiContainer)
        Slot hoveredSlot = getSlotAtPosition(gui, mouseX, mouseY);
        if (hoveredSlot == null) return;

        ItemStack stack = hoveredSlot.getStack();
        if (stack.isEmpty() || !(stack.getItem() instanceof ItemNebulaArmor)) {
            return;
        }

        NetworkHandler.NETWORK.sendToServer(new C00PacketToggleNebulaArmor(hoveredSlot.slotNumber));
        event.setCanceled(true);
    }

    private static Slot getSlotAtPosition(GuiContainer gui, int mouseX, int mouseY) {
        int guiLeft = gui.getGuiLeft();
        int guiTop = gui.getGuiTop();
        for (Slot slot : gui.inventorySlots.inventorySlots) {
            if (slot.isEnabled()) {
                int slotX = guiLeft + slot.xPos;
                int slotY = guiTop + slot.yPos;
                if (mouseX >= slotX - 1 && mouseX < slotX + 17 && mouseY >= slotY - 1 && mouseY < slotY + 17) {
                    return slot;
                }
            }
        }
        return null;
    }
}
