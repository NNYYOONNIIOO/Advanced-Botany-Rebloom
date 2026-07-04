package ab.common.block;

import ab.AdvancedBotany;
import ab.common.block.tile.TileABSpreader;
import ab.common.lib.register.BlockListAB;
import ab.common.lib.register.RecipeListAB;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import vazkii.botania.api.lexicon.ILexiconable;
import vazkii.botania.api.lexicon.LexiconEntry;
import vazkii.botania.api.mana.ILens;
import vazkii.botania.api.wand.IWandHUD;
import vazkii.botania.api.wand.IWandable;
import vazkii.botania.api.wand.IWireframeAABBProvider;
import vazkii.botania.common.item.ModItems;

import javax.annotation.Nonnull;
import java.util.Random;

public class BlockABSpreader extends BlockContainer implements IWandable, IWandHUD, IWireframeAABBProvider, ILexiconable {

    private static final AxisAlignedBB WIRE_AABB = new AxisAlignedBB(0.0625, 0.0625, 0.0625, 0.9375, 0.9375, 0.9375);

    public BlockABSpreader() {
        super(Material.ROCK);
        this.setCreativeTab(AdvancedBotany.tabAB);
        this.setHardness(7.0f);
        this.setSoundType(SoundType.STONE);
        this.setTranslationKey(AdvancedBotany.modid + "." + "advancedSpreader");
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase liv, ItemStack stack) {
        EnumFacing orientation = EnumFacing.getDirectionFromEntityLiving(pos, liv);
        TileABSpreader spreader = (TileABSpreader) world.getTileEntity(pos);
        if (spreader == null) return;
        switch (orientation) {
            case DOWN:
                spreader.rotationY = -90.0f;
                break;
            case UP:
                spreader.rotationY = 90.0f;
                break;
            case NORTH:
                spreader.rotationX = 270.0f;
                break;
            case SOUTH:
                spreader.rotationX = 90.0f;
                break;
            case WEST:
                return;
            case EAST:
            default:
                spreader.rotationX = 180.0f;
                break;
        }
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        TileEntity tile = world.getTileEntity(pos);
        if (!(tile instanceof TileABSpreader)) {
            return false;
        }
        TileABSpreader spreader = (TileABSpreader) tile;
        ItemStack lens = spreader.getItemHandler().getStackInSlot(0);
        ItemStack heldItem = player.getHeldItem(hand);
        boolean isHeldItemLens = !heldItem.isEmpty() && heldItem.getItem() instanceof ILens;
        boolean wool = !heldItem.isEmpty() && heldItem.getItem() == Item.getItemFromBlock(Blocks.WOOL);

        if (!heldItem.isEmpty() && heldItem.getItem() == ModItems.twigWand) {
            return false;
        }

        if (lens.isEmpty() && isHeldItemLens) {
            if (!player.capabilities.isCreativeMode) {
                player.inventory.setInventorySlotContents(player.inventory.currentItem, ItemStack.EMPTY);
            }
            spreader.getItemHandler().setStackInSlot(0, heldItem.copy());
            spreader.markDirty();
        } else if (!lens.isEmpty() && !wool) {
            ItemStack add = lens.copy();
            if (!player.inventory.addItemStackToInventory(add)) {
                player.dropItem(add, false);
            }
            spreader.getItemHandler().setStackInSlot(0, ItemStack.EMPTY);
            spreader.markDirty();
        }

        if (wool && spreader.paddingColor == -1) {
            spreader.paddingColor = heldItem.getMetadata();
            heldItem.shrink(1);
            if (heldItem.getCount() == 0) {
                player.inventory.setInventorySlotContents(player.inventory.currentItem, ItemStack.EMPTY);
            }
        } else if (heldItem.isEmpty() && spreader.paddingColor != -1 && lens.isEmpty()) {
            ItemStack pad = new ItemStack(Blocks.WOOL, 1, spreader.paddingColor);
            if (!player.inventory.addItemStackToInventory(pad)) {
                player.dropItem(pad, false);
            }
            spreader.paddingColor = -1;
            spreader.markDirty();
        }
        return true;
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileABSpreader) {
            TileABSpreader inv = (TileABSpreader) tile;
            for (int i = 0; i < inv.getItemHandler().getSlots() + 1; i++) {
                ItemStack stack;
                if (i >= inv.getItemHandler().getSlots()) {
                    stack = inv.paddingColor == -1 ? ItemStack.EMPTY : new ItemStack(Blocks.WOOL, 1, inv.paddingColor);
                } else {
                    stack = inv.getItemHandler().getStackInSlot(i);
                }
                if (stack.isEmpty()) continue;
                float spawnX = pos.getX() + world.rand.nextFloat();
                float spawnY = pos.getY() + world.rand.nextFloat();
                float spawnZ = pos.getZ() + world.rand.nextFloat();
                EntityItem droppedItem = new EntityItem(world, spawnX, spawnY, spawnZ, stack);
                float mult = 0.05f;
                droppedItem.motionX = (-0.5f + world.rand.nextFloat()) * mult;
                droppedItem.motionY = (4.0f + world.rand.nextFloat()) * mult;
                droppedItem.motionZ = (-0.5f + world.rand.nextFloat()) * mult;
                if (stack.hasTagCompound()) {
                    droppedItem.getItem().setTagCompound(stack.getTagCompound().copy());
                }
                world.spawnEntity(droppedItem);
            }
            world.updateComparatorOutputLevel(pos, this);
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
        return 12;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileABSpreader();
    }

    @Override
    public boolean onUsedByWand(EntityPlayer player, ItemStack stack, World world, BlockPos pos, EnumFacing side) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileABSpreader) {
            ((TileABSpreader) tile).onWanded(player, stack);
        }
        return true;
    }

    @Override
    public AxisAlignedBB getWireframeAABB(World world, BlockPos pos) {
        return WIRE_AABB.offset(pos);
    }

    @Override
    public void renderHUD(Minecraft mc, ScaledResolution res, World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileABSpreader) {
            ((TileABSpreader) tile).renderHUD(mc, res);
        }
    }

    @Override
    public LexiconEntry getEntry(World world, BlockPos pos, EntityPlayer player, ItemStack stack) {
        return RecipeListAB.lebethronSpreader;
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        return new ItemStack(this);
    }
}
