package ab.common.block;

import ab.AdvancedBotany;
import ab.common.block.tile.TileInventory;
import ab.common.block.tile.TileManaCharger;
import ab.common.lib.register.BlockListAB;
import ab.common.lib.register.RecipeListAB;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import vazkii.botania.api.lexicon.ILexiconable;
import vazkii.botania.api.lexicon.LexiconEntry;
import vazkii.botania.api.mana.IManaItem;
import vazkii.botania.api.wand.IWandHUD;
import vazkii.botania.api.wand.IWandable;

import javax.annotation.Nonnull;

public class BlockManaCharger extends BlockContainer implements IWandHUD, IWandable, ILexiconable {

    private static final AxisAlignedBB CHARGER_AABB = new AxisAlignedBB(0.1875, 0.1875, 0.1875, 0.8125, 0.875, 0.8125);

    public BlockManaCharger() {
        super(Material.IRON);
        this.setCreativeTab(AdvancedBotany.tabAB);
        this.setHardness(6.0f);
        this.setTranslationKey(AdvancedBotany.modid + "." + "ABManaCharger");
    }

    @Override
    @Nonnull
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return CHARGER_AABB;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        TileEntity te = world.getTileEntity(pos);
        if (!(te instanceof TileManaCharger)) return false;
        TileManaCharger tile = (TileManaCharger) te;
        int slotSide = side.getIndex() - 1;
        if (slotSide < 0) {
            return false;
        }
        ItemStack heldItem = player.getHeldItem(hand);
        ItemStack stackInSlot = tile.getStackInSlot(slotSide);
        if (player.isSneaking()) {
            if (!stackInSlot.isEmpty()) {
                ItemStack copy = stackInSlot.copy();
                tile.setInventorySlotContents(slotSide, ItemStack.EMPTY);
                if (!world.isRemote) {
                    Vec3d vec3 = player.getLookVec();
                    EntityItem entityitem = new EntityItem(world, player.posX + vec3.x, player.posY + 1.2, player.posZ + vec3.z, copy);
                    world.spawnEntity(entityitem);
                    tile.requestUpdate = true;
                }
                world.updateComparatorOutputLevel(pos, this);
                return true;
            }
        } else if (!heldItem.isEmpty() && heldItem.getItem() instanceof IManaItem && stackInSlot.isEmpty() && heldItem.getMaxStackSize() == 1) {
            ItemStack copy = heldItem.copy();
            copy.setCount(1);
            heldItem.shrink(1);
            if (heldItem.getCount() == 0) {
                player.inventory.setInventorySlotContents(player.inventory.currentItem, ItemStack.EMPTY);
            }
            player.inventoryContainer.detectAndSendChanges();
            if (!world.isRemote) {
                tile.setInventorySlotContents(slotSide, copy);
                tile.requestUpdate = true;
            }
            return true;
        }
        return false;
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntity te = world.getTileEntity(pos);
        if (!world.isRemote && te instanceof TileInventory) {
            TileInventory inv = (TileInventory) te;
            for (int i = 0; i < inv.getSizeInventory(); i++) {
                ItemStack stack = inv.getStackInSlot(i);
                if (stack.isEmpty()) continue;
                float f = world.rand.nextFloat() * 0.8f + 0.1f;
                float f1 = world.rand.nextFloat() * 0.8f + 0.1f;
                float f2 = world.rand.nextFloat() * 0.8f + 0.1f;
                EntityItem entityitem = new EntityItem(world,
                        pos.getX() + f, pos.getY() + f1, pos.getZ() + f2,
                        stack.copy());
                float f3 = 0.05f;
                entityitem.motionX = world.rand.nextGaussian() * f3;
                entityitem.motionY = world.rand.nextGaussian() * f3 + 0.2f;
                entityitem.motionZ = world.rand.nextGaussian() * f3;
                if (stack.hasTagCompound()) {
                    entityitem.getItem().setTagCompound(stack.getTagCompound().copy());
                }
                world.spawnEntity(entityitem);
            }
        }
        super.breakBlock(world, pos, state);
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
        return BlockListAB.blockManaChargerRI;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileManaCharger();
    }

    @Override
    public void renderHUD(Minecraft mc, ScaledResolution res, World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileManaCharger) {
            ((TileManaCharger) tile).renderHUD(mc, res);
        }
    }

    @Override
    public boolean onUsedByWand(EntityPlayer player, ItemStack stack, World world, BlockPos pos, EnumFacing side) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileManaCharger) {
            ((TileManaCharger) tile).onWanded(player, stack);
        }
        return true;
    }

    @Override
    public LexiconEntry getEntry(World world, BlockPos pos, EntityPlayer player, ItemStack stack) {
        return RecipeListAB.manaCharger;
    }
}
