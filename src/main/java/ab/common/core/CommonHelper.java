package ab.common.core;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;

public class CommonHelper {
    public static void fertilizer(World world, Block block, BlockPos pos, int count, EntityPlayer player) {
        if (world.isRemote || !(block instanceof IGrowable) || block instanceof BlockSapling || world.getTileEntity(pos) != null) {
            return;
        }
        if (player != null) {
            IBlockState state = world.getBlockState(pos);
            BlockEvent.BreakEvent event = new BlockEvent.BreakEvent(world, pos, state, player);
            MinecraftForge.EVENT_BUS.post(event);
            if (event.isCanceled()) {
                return;
            }
        }
        IGrowable igrowable = (IGrowable) block;
        if (igrowable.canGrow(world, pos, world.getBlockState(pos), world.isRemote) && igrowable.canUseBonemeal(world, world.rand, pos, world.getBlockState(pos))) {
            int meta = block.getMetaFromState(world.getBlockState(pos));
            for (int i = 0; i < count; ++i) {
                IBlockState currentState = world.getBlockState(pos);
                if (currentState.getBlock() != block) {
                    return;
                }
                if (meta != block.getMetaFromState(currentState)) {
                    return;
                }
                igrowable.grow(world, world.rand, pos, currentState);
            }
        }
    }

    public static boolean setBlock(World world, Block block, int meta, BlockPos pos, EntityPlayer player, boolean checkAir) {
        if (!world.isRemote) {
            IBlockState currentState = world.getBlockState(pos);
            if (checkAir && currentState.getMaterial() != Material.AIR) {
                return false;
            }
            BlockEvent.BreakEvent event = new BlockEvent.BreakEvent(world, pos, currentState, player);
            MinecraftForge.EVENT_BUS.post(event);
            if (event.isCanceled()) {
                return false;
            }
            return CommonHelper.setBlockWithY(world, block, meta, pos);
        }
        return false;
    }

    private static boolean setBlockWithY(World world, Block block, int meta, BlockPos pos) {
        if (pos.getY() >= 256) {
            return false;
        }
        IBlockState state = block.getStateFromMeta(meta);
        world.setBlockState(pos, state, 3);
        return true;
    }
}
