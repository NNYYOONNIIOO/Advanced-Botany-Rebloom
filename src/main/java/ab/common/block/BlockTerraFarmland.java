package ab.common.block;

import ab.AdvancedBotany;
import ab.api.AdvancedBotanyAPI;
import ab.api.TerraFarmlandList;
import ab.common.lib.register.RecipeListAB;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import vazkii.botania.api.lexicon.ILexiconable;
import vazkii.botania.api.lexicon.LexiconEntry;

import net.minecraft.util.NonNullList;
import java.util.List;
import java.util.Random;

public class BlockTerraFarmland extends Block implements ILexiconable {

    private static final AxisAlignedBB FARMLAND_AABB = new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.9375, 1.0);

    public BlockTerraFarmland() {
        super(Material.GROUND);
        this.setTickRandomly(true);
        this.setTranslationKey(AdvancedBotany.modid + "." + "terraFarmland");
        this.setLightOpacity(255);
        this.setSoundType(SoundType.GROUND);
    }

    @Override
    public boolean canSustainPlant(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing direction, IPlantable plantable) {
        IBlockState plantState = plantable.getPlant(world, pos.up());
        Block plant = plantState.getBlock();
        return plant != Blocks.MELON_STEM && plant != Blocks.PUMPKIN_STEM;
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        if (world.isRemote) {
            return;
        }
        IBlockState blockState = world.getBlockState(pos.up());
        Block block = blockState.getBlock();
        int meta = block.getMetaFromState(blockState);
        if (block instanceof BlockCrops) {
            if (meta == 7) {
                this.refreshSeed(world, pos, block, meta);
                return;
            }
        } else if (block instanceof IPlantable) {
            for (TerraFarmlandList fSeed : AdvancedBotanyAPI.farmlandList) {
                int meta1 = fSeed.getMeta();
                Block block1 = fSeed.getBlock();
                if (block != block1 || meta != meta1) continue;
                this.refreshSeed(world, pos, block1, meta1);
                return;
            }
        } else {
            world.setBlockState(pos, Blocks.DIRT.getDefaultState(), 3);
        }
    }

    private void refreshSeed(World world, BlockPos pos, Block block, int meta) {
        List<EntityItem> items = world.getEntitiesWithinAABB(EntityItem.class,
                new AxisAlignedBB(pos.getX() - 4, pos.getY() - 4, pos.getZ() - 4,
                        pos.getX() + 4, pos.getY() + 4, pos.getZ() + 4));
        if (!items.isEmpty() && items.size() > 7) {
            return;
        }
        IPlantable seed = (IPlantable) block;
        NonNullList<ItemStack> list = NonNullList.create();
        block.getDrops(list, world, pos.up(), world.getBlockState(pos.up()), 0);
        Item seedItem = block.getItemDropped(world.getBlockState(pos.up()), world.rand, 0);
        for (ItemStack stack : list) {
            if (stack == null || stack.isEmpty()) continue;
            if (stack.getItem() == seedItem) {
                if (stack.getCount() <= 1) continue;
                stack.shrink(1);
                continue;
            }
            stack.setCount(Math.min(64, (int)((float)stack.getCount() * 2.5f)));
        }
        for (ItemStack stack : list) {
            if (stack == null || stack.isEmpty()) continue;
            EntityItem itemEnt = new EntityItem(world, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, stack.copy());
            world.spawnEntity(itemEnt);
        }
        world.setBlockState(pos.up(), block.getStateFromMeta(0), 3);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return FARMLAND_AABB;
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
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Item.getItemFromBlock(Blocks.DIRT);
    }

    @net.minecraftforge.fml.relauncher.SideOnly(net.minecraftforge.fml.relauncher.Side.CLIENT)
    public Item getItem(IBlockAccess worldIn, BlockPos pos, IBlockState state) {
        return Item.getItemFromBlock(Blocks.DIRT);
    }

    @Override
    public LexiconEntry getEntry(World world, BlockPos pos, EntityPlayer player, ItemStack lexicon) {
        return RecipeListAB.terraHoe;
    }
}
