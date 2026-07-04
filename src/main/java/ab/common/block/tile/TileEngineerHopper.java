package ab.common.block.tile;

import ab.api.IBoundRender;
import ab.client.core.ClientHelper;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.IHopper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import vazkii.botania.common.item.ModItems;

import java.util.List;

public class TileEngineerHopper extends TileInventory implements IHopper, IBoundRender, net.minecraft.util.ITickable {
    private int cooldown;
    private int[] invPosX = new int[]{0, 0};
    private int[] invPosY = new int[]{-1, -1};
    private int[] invPosZ = new int[]{0, 0};
    private int[] invSide = new int[]{-1, -1};
    private boolean bindType;
    public int redstoneSignal = 0;

    @Override
    public void update() {
        this.redstoneSignal = 0;
        for (EnumFacing dir : EnumFacing.VALUES) {
            int redstoneSide = this.getWorld().getRedstonePower(this.getPos().offset(dir), dir);
            this.redstoneSignal = Math.max(this.redstoneSignal, redstoneSide);
        }
        if (this.redstoneSignal > 0) {
            return;
        }
        if (this.getWorld() != null && !this.getWorld().isRemote) {
            if (this.cooldown > 0) {
                this.cooldown--;
            } else if (this.cooldown == 0 && this.getWorld() != null && !this.getWorld().isRemote) {
                boolean hasUpdate = false;
                ItemStack stack = this.getStackInSlot(0);
                if (!stack.isEmpty()) {
                    hasUpdate = this.canExtractStack();
                }
                if (stack.isEmpty() || stack.getCount() != stack.getMaxStackSize()) {
                    hasUpdate = this.canInsertStack() || hasUpdate;
                }
                if (hasUpdate) {
                    this.cooldown = 8;
                }
                this.markDirty();
            }
        }
    }

    @Override
    public boolean bindTo(EntityPlayer player, ItemStack wand, BlockPos pos, EnumFacing side) {
        boolean isFar = Math.abs(this.getPos().getX() - pos.getX()) >= 10
                || Math.abs(this.getPos().getY() - pos.getY()) >= 10
                || Math.abs(this.getPos().getZ() - pos.getZ()) >= 10;
        if (isFar) {
            return false;
        }
        int invCount = this.bindType ? 0 : 1;
        TileEntity tile = player.getEntityWorld().getTileEntity(pos);
        if (tile instanceof TileEngineerHopper) {
            return false;
        }
        if (tile != null && tile instanceof IInventory) {
            this.setDistantInventory(invCount, pos.getX(), pos.getY(), pos.getZ());
            this.invSide[invCount] = side.getIndex();
            return true;
        }
        this.setDistantInventory(invCount, 0, -1, 0);
        this.invSide[invCount] = -1;
        return false;
    }

    @Override
    public boolean canSelect(EntityPlayer player, ItemStack wand, BlockPos pos, EnumFacing side) {
        return true;
    }

    public void changeBindType() {
        this.bindType = !this.bindType;
    }

    public void setDistantInventory(int count, int posX, int posY, int posZ) {
        this.invPosX[count] = posX;
        this.invPosY[count] = posY;
        this.invPosZ[count] = posZ;
    }

    public IInventory getDistantInventory(int count) {
        IInventory inv = TileEntityHopper.getInventoryAtPosition(this.getWorld(), this.invPosX[count], this.invPosY[count], this.invPosZ[count]);
        if (inv == null) {
            this.setDistantInventory(count, 0, -1, 0);
        }
        return inv;
    }

    @Override
    public BlockPos[] getBlocksCoord() {
        return new BlockPos[]{new BlockPos(this.invPosX[0], this.invPosY[0], this.invPosZ[0]), new BlockPos(this.invPosX[1], this.invPosY[1], this.invPosZ[1])};
    }

    @Override
    public BlockPos getBinding() {
        return null;
    }

    @SideOnly(Side.CLIENT)
    public void renderHUD(Minecraft mc, ScaledResolution res) {
        int x = res.getScaledWidth() / 2 - 7;
        int y = res.getScaledHeight() / 2 + 16;
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        ClientHelper.drawArrow(x - 2, y, true);
        for (int i = 0; i < 2; i++) {
            ItemStack stack = ItemStack.EMPTY;
            boolean hasInv = false;
            int posX = x + (i == 0 ? 32 : -32);
            IInventory distInv = this.getDistantInventory(i);
            if (distInv != null && distInv instanceof TileEntity) {
                TileEntity tile = (TileEntity) distInv;
                BlockPos tPos = tile.getPos();
                Block block = tile.getWorld().getBlockState(tPos).getBlock();
                int meta = block.getMetaFromState(tile.getWorld().getBlockState(tPos));
                stack = new ItemStack(block, 1, meta);
                hasInv = true;
            }
            Gui.drawRect(posX - 4, y - 4, posX + 20, y + 20, 0x44000000);
            Gui.drawRect(posX - 2, y - 2, posX + 18, y + 18, 0x44000000);
            if (!stack.isEmpty()) {
                RenderHelper.enableGUIStandardItemLighting();
                GL11.glEnable(32826);
                mc.getRenderItem().renderItemAndEffectIntoGUI(stack, posX, y);
                RenderHelper.disableStandardItemLighting();
                GL11.glDisable(2929);
            }
            int invCount = this.bindType ? 0 : 1;
            if (invCount == i) {
                GL11.glTranslatef(0.0f, 0.0f, 300.0f);
                RenderHelper.enableGUIStandardItemLighting();
                GL11.glEnable(32826);
                mc.getRenderItem().renderItemOverlays(mc.fontRenderer, new ItemStack(ModItems.twigWand), posX + 10, y + 7);
                mc.getRenderItem().renderItemAndEffectIntoGUI(new ItemStack(ModItems.twigWand), posX + 10, y + 7);
                RenderHelper.disableStandardItemLighting();
                GL11.glDisable(2929);
                GL11.glTranslatef(0.0f, 0.0f, -300.0f);
            }
            boolean unicode = mc.fontRenderer.getUnicodeFlag();
            mc.fontRenderer.setUnicodeFlag(true);
            if (!hasInv) {
                mc.fontRenderer.drawString("\u2717", posX + 5, y + 6, 0x4C0000);
                mc.fontRenderer.drawString("\u2717", posX + 5, y + 5, 13764621);
            }
            mc.fontRenderer.setUnicodeFlag(unicode);
            GL11.glEnable(2929);
        }
        GL11.glDisable(3042);
    }

    @Override
    public void writePacketNBT(NBTTagCompound nbtt) {
        super.writePacketNBT(nbtt);
        nbtt.setInteger("cooldown", this.cooldown);
        nbtt.setBoolean("bindType", this.bindType);
        nbtt.setIntArray("bindingX", this.invPosX);
        nbtt.setIntArray("bindingY", this.invPosY);
        nbtt.setIntArray("bindingZ", this.invPosZ);
        nbtt.setIntArray("bindingSide", this.invSide);
    }

    @Override
    public void readPacketNBT(NBTTagCompound nbtt) {
        super.readPacketNBT(nbtt);
        this.cooldown = nbtt.getInteger("cooldown");
        this.bindType = nbtt.getBoolean("bindType");
        this.invPosX = nbtt.getIntArray("bindingX");
        this.invPosY = nbtt.getIntArray("bindingY");
        this.invPosZ = nbtt.getIntArray("bindingZ");
        this.invSide = nbtt.getIntArray("bindingSide");
    }

    private boolean canExtractStack() {
        IInventory inv = this.getDistantInventory(0);
        if (inv == null) {
            return false;
        }
        int side = this.invSide[0];
        int pullCount = this.getPullCount(inv, side);
        if (pullCount <= 0) {
            return false;
        }
        if (!this.getStackInSlot(0).isEmpty()) {
            ItemStack itemstack = this.getStackInSlot(0).copy();
            ItemStack itemstack1 = TileEntityHopper.putStackInInventoryAllSlots(this, inv, this.decrStackSize(0, pullCount), EnumFacing.byIndex(side));
            if (itemstack1.isEmpty()) {
                inv.markDirty();
                return true;
            }
            this.setInventorySlotContents(0, itemstack);
        }
        return false;
    }

    private int getPullCount(IInventory inv, int side) {
        if (inv instanceof ISidedInventory && side > -1) {
            ISidedInventory sideInv = (ISidedInventory) inv;
            int[] slots = sideInv.getSlotsForFace(EnumFacing.byIndex(side));
            for (int i = 0; i < slots.length; i++) {
                ItemStack stack = sideInv.getStackInSlot(slots[i]);
                if (stack.isEmpty()) {
                    return inv.getInventoryStackLimit();
                }
                if (inv.getInventoryStackLimit() == stack.getCount() || stack.getCount() == stack.getMaxStackSize() || !TileNidavellirForge.isItemEqual(stack, this.getStackInSlot(0)))
                    continue;
                return inv.getInventoryStackLimit() - stack.getCount();
            }
        } else {
            int size = inv.getSizeInventory();
            for (int i = 0; i < size; i++) {
                ItemStack stack = inv.getStackInSlot(i);
                if (stack.isEmpty()) {
                    return inv.getInventoryStackLimit();
                }
                if (inv.getInventoryStackLimit() == stack.getCount() || stack.getCount() == stack.getMaxStackSize() || !TileNidavellirForge.isItemEqual(stack, this.getStackInSlot(0)))
                    continue;
                return inv.getInventoryStackLimit() - stack.getCount();
            }
        }
        return 0;
    }

    private boolean canInsertStack() {
        IInventory inv = this.getDistantInventory(1);
        if (inv == null) return false;
        int side = this.invSide[1];
        int takeCount = this.getTakeCount(inv, side);
        if (takeCount <= 0) {
            return false;
        }
        if (inv instanceof ISidedInventory && side > -1) {
            ISidedInventory sidedInv = (ISidedInventory) inv;
            int[] slots = sidedInv.getSlotsForFace(EnumFacing.byIndex(side));
            for (int k = 0; k < slots.length; k++) {
                if (!this.tryExtractFromSlot(inv, slots[k], side, takeCount)) continue;
                return true;
            }
        } else {
            int i = inv.getSizeInventory();
            for (int j = 0; j < i; j++) {
                if (!this.tryExtractFromSlot(inv, j, side, takeCount)) continue;
                return true;
            }
        }
        return false;
    }

    private int getTakeCount(IInventory inv, int side) {
        if (inv instanceof ISidedInventory && side > -1) {
            ISidedInventory sideInv = (ISidedInventory) inv;
            int[] slots = sideInv.getSlotsForFace(EnumFacing.byIndex(side));
            for (int i = 0; i < slots.length; i++) {
                ItemStack stack = sideInv.getStackInSlot(slots[i]);
                if (stack.isEmpty()) continue;
                if (this.getStackInSlot(0).isEmpty()) {
                    return Math.min(stack.getMaxStackSize(), this.getInventoryStackLimit());
                }
                if (this.getInventoryStackLimit() == this.getStackInSlot(0).getCount() || this.getStackInSlot(0).getCount() == this.getStackInSlot(0).getMaxStackSize() || !TileNidavellirForge.isItemEqual(stack, this.getStackInSlot(0)))
                    continue;
                return this.getInventoryStackLimit() - this.getStackInSlot(0).getCount();
            }
        } else {
            for (int i = 0; i < inv.getSizeInventory(); i++) {
                ItemStack stack = inv.getStackInSlot(i);
                if (stack.isEmpty()) continue;
                if (this.getStackInSlot(0).isEmpty()) {
                    return Math.min(stack.getMaxStackSize(), this.getInventoryStackLimit());
                }
                if (this.getInventoryStackLimit() == this.getStackInSlot(0).getCount() || this.getStackInSlot(0).getCount() == this.getStackInSlot(0).getMaxStackSize() || !TileNidavellirForge.isItemEqual(stack, this.getStackInSlot(0)))
                    continue;
                return this.getInventoryStackLimit() - this.getStackInSlot(0).getCount();
            }
        }
        return 0;
    }

    private boolean tryExtractFromSlot(IInventory inv, int slot, int side, int takeCount) {
        ItemStack itemstack = inv.getStackInSlot(slot);
        if (!itemstack.isEmpty() && TileEngineerHopper.canExtractItemFromSlot(inv, itemstack, slot, side)) {
            ItemStack itemstack1 = itemstack.copy();
            ItemStack itemstack2 = TileEntityHopper.putStackInInventoryAllSlots(inv, this, inv.decrStackSize(slot, takeCount), EnumFacing.UP);
            if (itemstack2.isEmpty()) {
                inv.markDirty();
                return true;
            }
            inv.setInventorySlotContents(slot, itemstack1);
        }
        return false;
    }

    private static boolean canExtractItemFromSlot(IInventory inv, ItemStack stack, int slot, int side) {
        return !(inv instanceof ISidedInventory) || ((ISidedInventory) inv).canExtractItem(slot, stack, EnumFacing.byIndex(side));
    }

    @Override
    public int getSizeInventory() {
        return 1;
    }

    @Override
    public String getName() {
        return "engineerHopper";
    }

    @Override
    public double getXPos() {
        return this.getPos().getX();
    }

    @Override
    public double getYPos() {
        return this.getPos().getY();
    }

    @Override
    public double getZPos() {
        return this.getPos().getZ();
    }
}
