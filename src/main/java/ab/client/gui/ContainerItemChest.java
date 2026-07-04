package ab.client.gui;

import ab.common.item.relic.ItemTalismanHiddenRiches;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import vazkii.botania.client.gui.SlotLocked;

public class ContainerItemChest extends Container {
    public InventoryItemChest itemChestInv;
    private int numRows;

    public ContainerItemChest(EntityPlayer player) {
        int i;
        int j;
        int slot = player.inventory.currentItem;
        InventoryPlayer inventoryPlayer = player.inventory;
        this.itemChestInv = new InventoryItemChest(player, slot, ItemTalismanHiddenRiches.getOpenChest(player.getHeldItemMainhand()));
        this.numRows = this.itemChestInv.getSizeInventory() / 9;
        for (j = 0; j < this.numRows; ++j) {
            for (int k = 0; k < 9; ++k) {
                this.addSlotToContainer(new SlotItemChest(this.itemChestInv, k + j * 9, 8 + k * 18, 18 + j * 18));
            }
        }
        for (i = 0; i < 3; ++i) {
            for (j = 0; j < 9; ++j) {
                this.addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9, 8 + j * 18, 85 + i * 18));
            }
        }
        for (i = 0; i < 9; ++i) {
            if (player.inventory.currentItem == i) {
                this.addSlotToContainer(new SlotLocked(inventoryPlayer, i, 8 + i * 18, 143));
                continue;
            }
            this.addSlotToContainer(new Slot(inventoryPlayer, i, 8 + i * 18, 143));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        boolean can = this.itemChestInv.isUsableByPlayer(player);
        if (!can) {
            this.onContainerClosed(player);
        }
        return can;
    }

    @Override
    public void onContainerClosed(EntityPlayer player) {
        super.onContainerClosed(player);
        this.itemChestInv.pushInventory();
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int i) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(i);
        if (slot != null && slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            itemstack = stack.copy();
            if (!InventoryItemChest.isRelicTalisman(stack)) {
                if (i < this.itemChestInv.getSizeInventory()) {
                    if (!this.mergeItemStack(stack, this.itemChestInv.getSizeInventory(), this.inventorySlots.size(), true)) {
                        this.itemChestInv.pushInventory();
                        return ItemStack.EMPTY;
                    }
                } else if (!this.mergeItemStack(stack, 0, this.itemChestInv.getSizeInventory(), false)) {
                    this.itemChestInv.pushInventory();
                    return ItemStack.EMPTY;
                }
            } else {
                this.itemChestInv.pushInventory();
                return ItemStack.EMPTY;
            }
            if (stack.getCount() == 0) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
        }
        this.itemChestInv.pushInventory();
        return itemstack;
    }
}
