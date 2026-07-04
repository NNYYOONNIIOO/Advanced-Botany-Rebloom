package ab.common.block;

import ab.AdvancedBotany;
import ab.common.block.tile.TileEngineerHopper;
import ab.common.block.tile.TileInventory;
import ab.common.lib.register.BlockListAB;
import ab.common.lib.register.RecipeListAB;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
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
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import vazkii.botania.api.lexicon.ILexiconable;
import vazkii.botania.api.lexicon.LexiconEntry;
import vazkii.botania.api.wand.IWandHUD;
import vazkii.botania.api.wand.IWandable;

import javax.annotation.Nonnull;

public class BlockEngineerHopper extends BlockContainer implements IWandable, IWandHUD, ILexiconable {

    private static final AxisAlignedBB HOPPER_AABB = new AxisAlignedBB(0.125, 0.0625, 0.125, 0.875, 0.9375, 0.875);

    public BlockEngineerHopper() {
        super(Material.IRON);
        this.setCreativeTab(AdvancedBotany.tabAB);
        this.setTranslationKey(AdvancedBotany.modid + "." + "engineerHopper");
    }

    @Override
    @Nonnull
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return HOPPER_AABB;
    }

    @Override
    public LexiconEntry getEntry(World world, BlockPos pos, EntityPlayer player, ItemStack lexicon) {
        return RecipeListAB.engineerHopper;
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
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEngineerHopper();
    }

    @Override
    public void renderHUD(Minecraft mc, ScaledResolution res, World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEngineerHopper) {
            ((TileEngineerHopper) tile).renderHUD(mc, res);
        }
    }

    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
        return BlockListAB.blockEngineerHopperRI;
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
    public boolean onUsedByWand(EntityPlayer player, ItemStack wand, World world, BlockPos pos, EnumFacing side) {
        if (!player.isSneaking()) {
            TileEntity tile = world.getTileEntity(pos);
            if (tile instanceof TileEngineerHopper) {
                ((TileEngineerHopper) tile).changeBindType();
                if (!world.isRemote) {
                    world.playSound(null, player.posX, player.posY, player.posZ,
                            net.minecraft.util.SoundEvent.REGISTRY.getObject(new net.minecraft.util.ResourceLocation("botania", "ding")),
                            net.minecraft.util.SoundCategory.PLAYERS, 0.11f, 1.0f);
                }
            }
        }
        return true;
    }
}
