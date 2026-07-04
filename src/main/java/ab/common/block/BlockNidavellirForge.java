package ab.common.block;

import ab.AdvancedBotany;
import ab.common.block.tile.TileInventory;
import ab.common.block.tile.TileNidavellirForge;
import ab.common.lib.register.BlockListAB;
import ab.common.lib.register.RecipeListAB;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.AxisAlignedBB;
import vazkii.botania.api.lexicon.LexiconEntry;
import vazkii.botania.api.lexicon.ILexiconable;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class BlockNidavellirForge extends BlockContainer implements ILexiconable {

    private static final AxisAlignedBB[] FORGE_AABBS = new AxisAlignedBB[]{
            new AxisAlignedBB(0.1875, 0.0, 0.0625, 0.8125, 0.75, 1.0),
            new AxisAlignedBB(0.0, 0.0, 0.1875, 0.9375, 0.75, 0.8125),
            new AxisAlignedBB(0.1875, 0.0, 0.0, 0.8125, 0.75, 0.9375),
            new AxisAlignedBB(0.0625, 0.0, 0.1875, 1.0, 0.75, 0.8125)
    };

    public BlockNidavellirForge() {
        super(Material.IRON);
        this.setCreativeTab(AdvancedBotany.tabAB);
        this.setHardness(3.0f);
        this.setResistance(10.0f);
        this.setSoundType(SoundType.METAL);
        this.setTranslationKey(AdvancedBotany.modid + "." + "ABPlate");
    }

    @Override
    @Nonnull
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        int meta = this.getMetaFromState(state);
        if (meta >= 0 && meta < FORGE_AABBS.length) {
            return FORGE_AABBS[meta];
        }
        return FULL_BLOCK_AABB;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase liv, ItemStack stack) {
        int meta = MathHelper.floor((double)(liv.rotationYaw * 4.0f / 360.0f) + 0.5) & 3;
        world.setBlockState(pos, this.getStateFromMeta(meta), 3);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntity te = world.getTileEntity(pos);
        if (!(te instanceof TileNidavellirForge)) return false;
        TileNidavellirForge tile = (TileNidavellirForge) te;
        if (player.isSneaking()) {
            if (!tile.getStackInSlot(0).isEmpty()) {
                ItemStack copy = tile.getStackInSlot(0).copy();
                if (!world.isRemote) {
                    Vec3d vec3 = player.getLookVec();
                    EntityItem entityitem = new EntityItem(world, player.posX + vec3.x, player.posY + 1.2, player.posZ + vec3.z, copy);
                    world.spawnEntity(entityitem);
                    tile.requestUpdate = true;
                }
                tile.setInventorySlotContents(0, ItemStack.EMPTY);
                world.updateComparatorOutputLevel(pos, this);
                return true;
            }
            for (int i = tile.getSizeInventory() - 1; i > 0; i--) {
                ItemStack stack = tile.getStackInSlot(i);
                if (stack.isEmpty()) continue;
                ItemStack copy = stack.copy();
                if (!world.isRemote) {
                    Vec3d vec3 = player.getLookVec();
                    EntityItem entityitem = new EntityItem(world, player.posX + vec3.x, player.posY + 1.2, player.posZ + vec3.z, copy);
                    world.spawnEntity(entityitem);
                    tile.requestUpdate = true;
                }
                tile.setInventorySlotContents(i, ItemStack.EMPTY);
                world.updateComparatorOutputLevel(pos, this);
                return true;
            }
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
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
        return BlockListAB.blockABPlateRI;
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
    public boolean isFullBlock(IBlockState state) {
        return false;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileNidavellirForge();
    }

    @Override
    public LexiconEntry getEntry(World world, BlockPos pos, EntityPlayer player, ItemStack stack) {
        return RecipeListAB.advandedAgglomerationPlate;
    }
}
