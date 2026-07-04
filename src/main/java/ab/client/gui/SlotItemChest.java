package ab.client.gui;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotItemChest extends Slot {
    InventoryItemChest itemChestInv;

    public SlotItemChest(InventoryItemChest inv, int index, int x, int y) {
        super(inv, index, x, y);
        this.itemChestInv = inv;
    }

    @Override
    public void putStack(ItemStack stack) {
        this.itemChestInv.setInventorySlotContents(this.getSlotIndex(), stack);
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return !InventoryItemChest.isRelicTalisman(stack);
    }
}
