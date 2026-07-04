package ab.common.item.equipment;

import ab.common.item.ItemMod;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import vazkii.botania.api.item.IBlockProvider;
import vazkii.botania.common.core.helper.ItemNBTHelper;
import vazkii.botania.common.item.ItemBlackHoleTalisman;

public class ItemBlackHalo extends ItemMod implements IBlockProvider {

    public ItemBlackHalo() {
        super("blackHalo");
        this.setMaxStackSize(1);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public net.minecraft.util.ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack halo = player.getHeldItem(hand);
        int segment = getSegmentLookedAt(halo, player);
        ItemStack itemForSlot = getItemForSlot(halo, segment);
        if (!itemForSlot.isEmpty()) {
            ItemStack stack = itemForSlot.copy();
            if (player.isSneaking()) {
                if (!player.inventory.addItemStackToInventory(stack)) {
                    player.dropItem(stack, false);
                }
                setItemSlot(halo, ItemStack.EMPTY, segment);
                return new net.minecraft.util.ActionResult<>(net.minecraft.util.EnumActionResult.SUCCESS, halo);
            }
            stack.setItemDamage(stack.getMetadata() == 0 ? 1 : 0);
            if (!world.isRemote) {
                world.playSound(null, player.posX, player.posY, player.posZ, net.minecraft.init.SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, net.minecraft.util.SoundCategory.PLAYERS, 0.3f, 0.1f);
            }
            setItemSlot(halo, stack, segment);
            return new net.minecraft.util.ActionResult<>(net.minecraft.util.EnumActionResult.SUCCESS, halo);
        }
        for (int i = 0; i < 9; ++i) {
            ItemStack invStack = player.inventory.getStackInSlot(i);
            if (invStack.isEmpty() || !(invStack.getItem() instanceof ItemBlackHoleTalisman))
                continue;
            Block talismanBlock = ItemBlackHoleTalisman.getBlock(invStack);
            boolean hasBlock = true;
            if (talismanBlock == null || talismanBlock == Blocks.AIR) {
                hasBlock = false;
            }
            if (!hasBlock) continue;
            if (!getItemForSlot(halo, segment).isEmpty()) {
                return new net.minecraft.util.ActionResult<>(net.minecraft.util.EnumActionResult.PASS, halo);
            }
            setItemSlot(halo, invStack, segment);
            if (player.inventory.getStackInSlot(i).getCount() > 1) {
                player.inventory.getStackInSlot(i).shrink(1);
            } else {
                player.inventory.setInventorySlotContents(i, ItemStack.EMPTY);
            }
            return new net.minecraft.util.ActionResult<>(net.minecraft.util.EnumActionResult.SUCCESS, halo);
        }
        return new net.minecraft.util.ActionResult<>(net.minecraft.util.EnumActionResult.PASS, halo);
    }

    @Override
    public void onUpdate(ItemStack halo, World world, Entity entity, int pos, boolean equipped) {
        boolean eqLastTick = wasEquipped(halo);
        if (!equipped && eqLastTick) {
            setEquipped(halo, equipped);
        }
        if (!eqLastTick && equipped && entity instanceof EntityLivingBase) {
            setEquipped(halo, equipped);
            int angles = 360;
            int segAngles = angles / 12;
            float shift = segAngles / 2;
            setRotationBase(halo, getCheckingAngle((EntityLivingBase) entity) - shift);
        }
        if (world.isRemote) {
            return;
        }
        if (entity.ticksExisted % 10 != 0) {
            return;
        }
        for (int i = 0; i < 12; ++i) {
            ItemStack stack = getItemForSlot(halo, i);
            if (stack.isEmpty()) continue;
            ((ItemBlackHoleTalisman) stack.getItem()).onUpdate(stack, world, entity, pos, equipped);
        }
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack halo = player.getHeldItem(hand);
        for (int i = 0; i < 12; ++i) {
            ItemStack tal = getItemForSlot(halo, i);
            if (tal.isEmpty()) continue;
            ItemBlackHoleTalisman talisman = (ItemBlackHoleTalisman) tal.getItem();
            Block bBlock = ItemBlackHoleTalisman.getBlock(tal);
            int bmeta = ItemBlackHoleTalisman.getBlockMeta(tal);
            TileEntity tile = world.getTileEntity(pos);
            if (tile == null || !(tile instanceof IInventory)) continue;
            IInventory inv = (IInventory) tile;
            int[] slots;
            if (inv instanceof ISidedInventory) {
                slots = ((ISidedInventory) inv).getSlotsForFace(facing);
            } else {
                slots = new int[inv.getSizeInventory()];
                for (int si = 0; si < slots.length; si++) slots[si] = si;
            }
            for (int slot : slots) {
                ItemStack stackInSlot = inv.getStackInSlot(slot);
                if (stackInSlot.isEmpty()) {
                    ItemStack stack = new ItemStack(bBlock, 1, bmeta);
                    int maxSize = stack.getMaxStackSize();
                    stack.setCount(ItemBlackHoleTalisman.remove(tal, maxSize));
                    if (stack.getCount() == 0 || !inv.isItemValidForSlot(slot, stack) || inv instanceof ISidedInventory && !((ISidedInventory) inv).canInsertItem(slot, stack, facing))
                        continue;
                    inv.setInventorySlotContents(slot, stack);
                    inv.markDirty();
                    continue;
                }
                if (stackInSlot.getItem() != Item.getItemFromBlock(bBlock) || stackInSlot.getMetadata() != bmeta)
                    continue;
                int maxSize = stackInSlot.getMaxStackSize();
                int missing = maxSize - stackInSlot.getCount();
                if (!inv.isItemValidForSlot(slot, stackInSlot) || inv instanceof ISidedInventory && !((ISidedInventory) inv).canInsertItem(slot, stackInSlot, facing))
                    continue;
                stackInSlot.grow(ItemBlackHoleTalisman.remove(tal, missing));
                inv.markDirty();
            }
        }
        return EnumActionResult.SUCCESS;
    }

    public void setItemSlot(ItemStack halo, ItemStack stack, int slot) {
        NBTTagCompound cmp = new NBTTagCompound();
        if (!stack.isEmpty()) {
            stack.copy().writeToNBT(cmp);
        }
        ItemNBTHelper.setCompound(halo, "itemSlot" + slot, cmp);
    }

    public static ItemStack getItemForSlot(ItemStack halo, int slot) {
        if (slot >= 12) {
            return ItemStack.EMPTY;
        }
        NBTTagCompound cmp = ItemNBTHelper.getCompound(halo, "itemSlot" + slot, true);
        if (cmp != null && !cmp.isEmpty()) {
            return new ItemStack(cmp);
        }
        return ItemStack.EMPTY;
    }

    public static int getSegmentLookedAt(ItemStack stack, EntityLivingBase player) {
        float yaw = getCheckingAngle(player, getRotationBase(stack));
        int angles = 360;
        int segAngles = angles / 12;
        for (int seg = 0; seg < 12; ++seg) {
            float calcAngle = seg * segAngles;
            if (yaw >= calcAngle && yaw < calcAngle + segAngles) {
                return seg;
            }
        }
        return -1;
    }

    public static void setRotationBase(ItemStack stack, float rotation) {
        ItemNBTHelper.setFloat(stack, "rotationBase", rotation);
    }

    public static float getRotationBase(ItemStack stack) {
        return ItemNBTHelper.getFloat(stack, "rotationBase", 0.0f);
    }

    public static float getCheckingAngle(EntityLivingBase player, float base) {
        float yaw = MathHelper.wrapDegrees(player.rotationYaw) + 90.0f;
        int angles = 360;
        int segAngles = angles / 12;
        float shift = segAngles / 2;
        if (yaw < 0.0f) {
            yaw = 360.0f + yaw;
        }
        float angle = 360.0f - (yaw -= 360.0f - base) + shift;
        if (angle < 0.0f) {
            angle = 360.0f + angle;
        }
        return angle;
    }

    private static float getCheckingAngle(EntityLivingBase player) {
        return getCheckingAngle(player, 0.0f);
    }

    public static void setEquipped(ItemStack stack, boolean equipped) {
        ItemNBTHelper.setBoolean(stack, "equipped", equipped);
    }

    public static boolean wasEquipped(ItemStack stack) {
        return ItemNBTHelper.getBoolean(stack, "equipped", false);
    }

    @Override
    public boolean provideBlock(EntityPlayer player, ItemStack requestor, ItemStack halo, Block block, int meta, boolean doit) {
        for (int i = 0; i < 12; ++i) {
            ItemStack tal = getItemForSlot(halo, i);
            if (tal.isEmpty()) continue;
            Block stored = ItemBlackHoleTalisman.getBlock(tal);
            int storedMeta = ItemBlackHoleTalisman.getBlockMeta(tal);
            if (stored != block || storedMeta != meta) continue;
            int count = ItemBlackHoleTalisman.getBlockCount(tal);
            if (count <= 0) continue;
            if (doit) {
                ItemNBTHelper.setInt(tal, "blockCount", count - 1);
            }
            return true;
        }
        return false;
    }

    @Override
    public int getBlockCount(EntityPlayer player, ItemStack requestor, ItemStack halo, Block block, int meta) {
        for (int i = 0; i < 12; ++i) {
            ItemStack tal = getItemForSlot(halo, i);
            if (tal.isEmpty()) continue;
            Block stored = ItemBlackHoleTalisman.getBlock(tal);
            int storedMeta = ItemBlackHoleTalisman.getBlockMeta(tal);
            if (stored != block || storedMeta != meta) continue;
            return ItemBlackHoleTalisman.getBlockCount(tal);
        }
        return 0;
    }
}
