package ab.common.block;

import ab.AdvancedBotany;
import ab.common.core.CommonHelper;
import ab.common.lib.register.BlockListAB;
import ab.common.lib.register.RecipeListAB;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.SoundType;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import vazkii.botania.api.lexicon.ILexiconable;
import vazkii.botania.api.lexicon.LexiconEntry;

import java.util.Random;

public class BlockFreyrLiana extends BlockBush implements ILexiconable {

    public BlockFreyrLiana() {
        super(Material.PLANTS);
        this.setTranslationKey(AdvancedBotany.modid + "." + "BlockFreyrLiana");
        this.setSoundType(SoundType.PLANT);
    }

    @Override
    public LexiconEntry getEntry(World world, BlockPos pos, EntityPlayer player, ItemStack lexicon) {
        return RecipeListAB.freyrSlingshot;
    }

    @Override
    public boolean canBlockStay(World world, BlockPos pos, IBlockState state) {
        IBlockState upState = world.getBlockState(pos.up());
        Material upMat = upState.getMaterial();
        return upMat == Material.LEAVES || upMat == Material.ROCK || upMat == Material.PLANTS
                || upMat == Material.WOOD || upMat == Material.CACTUS || upMat == Material.GRASS
                || upState.getBlock() instanceof BlockFreyrLiana;
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        this.checkAndDropBlock(world, pos, state);
        world.notifyNeighborsOfStateChange(pos, this, true);
        for (int i1 = 1; i1 < 5; i1++) {
            Block block = world.getBlockState(pos.down(i1)).getBlock();
            if (block instanceof IGrowable) {
                CommonHelper.fertilizer(world, block, pos.down(i1), 18, null);
                if (world.getBlockState(pos.down(i1 + 1)).getBlock() != BlockListAB.blockTerraFarmland) break;
                world.getBlockState(pos.down(i1 + 1)).getBlock().updateTick(world, pos.down(i1 + 1), world.getBlockState(pos.down(i1 + 1)), rand);
                break;
            }
        }
    }

    @Override
    protected void checkAndDropBlock(World world, BlockPos pos, IBlockState state) {
        if (!this.canBlockStay(world, pos, state)) {
            this.dropBlockAsItem(world, pos, state, 0);
            world.setBlockToAir(pos);
        }
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return null;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
        return 100;
    }

    @Override
    public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face) {
        return 60;
    }
}
