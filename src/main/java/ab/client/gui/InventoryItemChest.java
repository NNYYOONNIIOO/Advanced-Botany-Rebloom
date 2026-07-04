package ab.client.gui;

import ab.common.item.relic.ItemTalismanHiddenRiches;
import ab.common.lib.register.ItemListAB;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nullable;

public class InventoryItemChest implements IInventory {
    EntityPlayer player;
    int slot;
    ItemStack[] stacks = null;
    boolean invPushed = false;
    ItemStack storedInv = null;
    int openChest;

    public InventoryItemChest(EntityPlayer player, int slot, int openChest) {
        this.player = player;
        this.slot = slot;
        this.openChest = openChest;
    }

    public static boolean isRelicTalisman(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() == ItemListAB.itemTalismanHiddenRiches;
    }

    protected ItemStack getStack() {
        ItemStack stack = this.player.inventory.getStackInSlot(this.slot);
        if (!stack.isEmpty()) {
            this.storedInv = stack;
        }
        return stack;
    }

    protected ItemStack[] getInventory() {
        if (this.stacks != null) {
            return this.stacks;
        }
        ItemStack stack = this.getStack();
        if (InventoryItemChest.isRelicTalisman(this.getStack())) {
            this.stacks = ItemTalismanHiddenRiches.getChestLoot(stack, this.openChest);
            return this.stacks;
        }
        return new ItemStack[this.getSizeInventory()];
    }

    public void pushInventory() {
        if (this.invPushed) {
            return;
        }
        ItemStack stack = this.getStack();
        if (stack.isEmpty()) {
            stack = this.storedInv;
        }
        if (!stack.isEmpty()) {
            ItemStack[] inv = this.getInventory();
            ItemTalismanHiddenRiches.setChestLoot(stack, inv, this.openChest);
            ItemTalismanHiddenRiches.setOpenChest(stack, -1);
        }
        this.invPushed = true;
    }

    @Override
    public int getSizeInventory() {
        return 27;
    }

    @Override
    public boolean isEmpty() {
        for (int i = 0; i < this.getSizeInventory(); i++) {
            if (!getStackInSlot(i).isEmpty()) return false;
        }
        return true;
    }

    @Override
    public ItemStack getStackInSlot(int i) {
        ItemStack stack = this.getInventory()[i];
        return stack != null ? stack : ItemStack.EMPTY;
    }

    @Override
    public ItemStack decrStackSize(int i, int j) {
        ItemStack[] inventorySlots = this.getInventory();
        ItemStack stackAtSlot = inventorySlots[i];
        if (stackAtSlot == null || stackAtSlot.isEmpty()) {
            return ItemStack.EMPTY;
        }
        if (stackAtSlot.getCount() <= j) {
            ItemStack itemStack = stackAtSlot;
            inventorySlots[i] = ItemStack.EMPTY;
            return itemStack;
        }
        ItemStack stack = stackAtSlot.splitStack(j);
        if (stackAtSlot.getCount() == 0) {
            inventorySlots[i] = ItemStack.EMPTY;
        }
        return stack;
    }

    @Override
    public ItemStack removeStackFromSlot(int i) {
        ItemStack stack = this.getStackInSlot(i);
        this.setInventorySlotContents(i, ItemStack.EMPTY);
        return stack;
    }

    @Override
    public void setInventorySlotContents(int i, ItemStack itemstack) {
        ItemStack[] inventorySlots = this.getInventory();
        inventorySlots[i] = itemstack;
    }

    @Override
    public int getInventoryStackLimit() {
        return InventoryItemChest.isRelicTalisman(this.getStack()) ? 64 : 0;
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer entityplayer) {
        return InventoryItemChest.isRelicTalisman(this.getStack());
    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemstack) {
        return InventoryItemChest.isRelicTalisman(this.getStack());
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public void markDirty() {
    }

    @Override
    public void openInventory(EntityPlayer player) {
    }

    @Override
    public void closeInventory(EntityPlayer player) {
    }

    @Override
    public String getName() {
        return "itemChestTalisman";
    }

    @Override
    public int getField(int id) {
        return 0;
    }

    @Override
    public void setField(int id, int value) {
    }

    @Override
    public int getFieldCount() {
        return 0;
    }

    @Override
    public void clear() {
    }

    @Nullable
    @Override
    public ITextComponent getDisplayName() {
        return new TextComponentString(getName());
    }
}
