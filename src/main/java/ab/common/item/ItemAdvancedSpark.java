package ab.common.item;

import ab.common.entity.EntityAdvancedSpark;
import com.google.common.base.Predicates;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import vazkii.botania.api.internal.VanillaPacketDispatcher;
import vazkii.botania.api.mana.IManaGivingItem;
import vazkii.botania.api.mana.spark.ISparkAttachable;
import vazkii.botania.api.mana.spark.ISparkEntity;

import java.util.List;

public class ItemAdvancedSpark extends ItemMod implements IManaGivingItem {
    public static Object worldIcon;

    public ItemAdvancedSpark() {
        super("advancedSpark");
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof ISparkAttachable) {
            ISparkAttachable attach = (ISparkAttachable) tile;
            // Direct check: scan for any existing spark entities above this block
            List<Entity> sparks = world.getEntitiesWithinAABB(Entity.class,
                    new AxisAlignedBB(pos.up(), pos.up().add(1, 1, 1)),
                    Predicates.instanceOf(ISparkEntity.class));
            if (attach.canAttachSpark(player.getHeldItem(hand)) && sparks.isEmpty()) {
                if (!world.isRemote) {
                    player.getHeldItem(hand).shrink(1);
                    EntityAdvancedSpark spark = new EntityAdvancedSpark(world);
                    spark.setPosition(pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5);
                    world.spawnEntity(spark);
                    attach.attachSpark((ISparkEntity) spark);
                    VanillaPacketDispatcher.dispatchTEToNearbyPlayers(world, pos);
                }
                return EnumActionResult.SUCCESS;
            }
        }
        return EnumActionResult.PASS;
    }
}
