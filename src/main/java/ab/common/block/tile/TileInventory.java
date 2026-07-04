package ab.common.block.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import vazkii.botania.common.block.tile.TileMod;

import javax.annotation.Nonnull;

public abstract class TileInventory extends TileMod implements IInventory {
    ItemStack[] inventorySlots;

    private void initSlots() {
        inventorySlots = new ItemStack[this.getSizeInventory()];
        for (int i = 0; i < inventorySlots.length; i++) {
            inventorySlots[i] = ItemStack.EMPTY;
        }
    }

    {
        initSlots();
    }

    @Override
    public void readPacketNBT(NBTTagCompound par1NBTTagCompound) {
        NBTTagList var2 = par1NBTTagCompound.getTagList("Items", 10);
        initSlots();
        for (int var3 = 0; var3 < var2.tagCount(); var3++) {
            NBTTagCompound var4 = var2.getCompoundTagAt(var3);
            byte var5 = var4.getByte("Slot");
            if (var5 < 0 || var5 >= this.inventorySlots.length) continue;
            this.inventorySlots[var5] = new ItemStack(var4);
        }
    }

    @Override
    public void writePacketNBT(NBTTagCompound par1NBTTagCompound) {
        NBTTagList var2 = new NBTTagList();
        for (int var3 = 0; var3 < this.inventorySlots.length; var3++) {
            if (this.inventorySlots[var3] == null || this.inventorySlots[var3].isEmpty()) continue;
            NBTTagCompound var4 = new NBTTagCompound();
            var4.setByte("Slot", (byte) var3);
            this.inventorySlots[var3].writeToNBT(var4);
            var2.appendTag(var4);
        }
        par1NBTTagCompound.setTag("Items", var2);
    }

    @Override
    @Nonnull
    public ItemStack getStackInSlot(int i) {
        if (i < 0 || i >= this.inventorySlots.length) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = this.inventorySlots[i];
        return stack != null ? stack : ItemStack.EMPTY;
    }

    @Override
    @Nonnull
    public ItemStack decrStackSize(int i, int j) {
        if (this.inventorySlots[i] != null && !this.inventorySlots[i].isEmpty()) {
            if (!this.getWorld().isRemote) {
                this.getWorld().notifyNeighborsOfStateChange(this.getPos(), this.getBlockType(), true);
            }
            if (this.inventorySlots[i].getCount() <= j) {
                ItemStack itemStack = this.inventorySlots[i];
                this.inventorySlots[i] = ItemStack.EMPTY;
                this.markDirty();
                return itemStack;
            }
            ItemStack stackAt = this.inventorySlots[i].splitStack(j);
            if (this.inventorySlots[i].getCount() == 0) {
                this.inventorySlots[i] = ItemStack.EMPTY;
            }
            this.markDirty();
            return stackAt;
        }
        return ItemStack.EMPTY;
    }

    @Override
    @Nonnull
    public ItemStack removeStackFromSlot(int i) {
        return this.getStackInSlot(i);
    }

    @Override
    public void setInventorySlotContents(int i, ItemStack itemstack) {
        this.inventorySlots[i] = itemstack == null ? ItemStack.EMPTY : itemstack;
        this.markDirty();
        if (!this.getWorld().isRemote) {
            this.getWorld().notifyNeighborsOfStateChange(this.getPos(), this.getBlockType(), true);
        }
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer entityplayer) {
        return this.getWorld().getTileEntity(this.getPos()) != this ? false : entityplayer.getDistanceSq(this.getPos().getX() + 0.5, this.getPos().getY() + 0.5, this.getPos().getZ() + 0.5) <= 64.0;
    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemstack) {
        return true;
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public void openInventory(EntityPlayer player) {
    }

    @Override
    public void closeInventory(EntityPlayer player) {
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

    @Override
    public String getName() {
        return null;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : this.inventorySlots) {
            if (stack != null && !stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }
}
