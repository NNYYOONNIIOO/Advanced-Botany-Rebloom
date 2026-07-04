package ab.common.item;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.botania.common.core.helper.ItemNBTHelper;

import java.util.List;

public class ItemAntigravityCharm extends ItemMod {

    public ItemAntigravityCharm() {
        super("antigravityCharm");
        this.setMaxStackSize(1);
        this.setNoRepair();
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (player.isSneaking()) {
            ItemNBTHelper.setBoolean(stack, "isActive", !ItemNBTHelper.getBoolean(stack, "isActive", true));
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (event.getWorld().isRemote) return;
        if (!(event.getEntity() instanceof EntityFallingBlock)) return;

        EntityFallingBlock fallingBlock = (EntityFallingBlock) event.getEntity();
        IBlockState state = fallingBlock.getBlock();
        if (state == null) return;

        List<EntityPlayer> players = event.getWorld().playerEntities;
        for (EntityPlayer player : players) {
            if (!hasActiveCharm(player)) continue;

            double dx = player.posX - fallingBlock.posX;
            double dy = player.posY - fallingBlock.posY;
            double dz = player.posZ - fallingBlock.posZ;
            if (dx * dx + dy * dy + dz * dz > 64.0) continue;

            event.setCanceled(true);
            BlockPos pos = new BlockPos(fallingBlock.posX, fallingBlock.posY, fallingBlock.posZ);
            if (event.getWorld().isAirBlock(pos) || event.getWorld().getBlockState(pos).getBlock().isReplaceable(event.getWorld(), pos)) {
                event.getWorld().setBlockState(pos, state, 18);
            }
            return;
        }
    }

    private boolean hasActiveCharm(EntityPlayer player) {
        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
            ItemStack stack = player.inventory.getStackInSlot(i);
            if (!stack.isEmpty() && stack.getItem() instanceof ItemAntigravityCharm) {
                if (ItemNBTHelper.getBoolean(stack, "isActive", true)) {
                    return true;
                }
            }
        }
        return false;
    }
}
