package ab.common.block;

import ab.AdvancedBotany;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.botania.common.Botania;

import java.util.Random;

public class BlockLuminousFreyrLiana extends BlockFreyrLiana {

    public BlockLuminousFreyrLiana() {
        this.setTranslationKey(AdvancedBotany.modid + "." + "BlockLuminousFreyrLiana");
        this.setTickRandomly(true);
    }

    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
        return 11;
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        super.updateTick(world, pos, state, rand);
        if (rand.nextInt(11) == 0) {
            world.setBlockState(pos, this.getStateFromMeta(1), 3);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
        if (this.getMetaFromState(state) == 1 && rand.nextInt(3) == 0) {
            Botania.proxy.wispFX(
                    pos.getX() + 0.1 + Math.random() * 0.8,
                    pos.getY() + Math.random() * 0.5,
                    pos.getZ() + 0.1 + Math.random() * 0.8,
                    0.9764706f, 0.9019608f, 0.011764706f, 0.14f, -0.04f, 2.0f);
        }
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        Random rand = new Random();
        if (this.getMetaFromState(state) == 1) {
            if (!world.isRemote) {
                EntityItem entity = new EntityItem(world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5,
                        new ItemStack(Items.GLOWSTONE_DUST, 1 + rand.nextInt(3)));
                entity.setDefaultPickupDelay();
                world.spawnEntity(entity);
            }
            world.setBlockState(pos, this.getStateFromMeta(0), 3);
            return true;
        }
        return false;
    }
}
