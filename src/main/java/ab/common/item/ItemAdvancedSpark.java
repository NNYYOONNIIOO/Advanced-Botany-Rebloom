package ab.common.item;

import ab.common.entity.EntityAdvancedSpark;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import vazkii.botania.api.internal.VanillaPacketDispatcher;
import vazkii.botania.api.mana.IManaGivingItem;
import vazkii.botania.api.mana.spark.ISparkAttachable;
import vazkii.botania.api.mana.spark.ISparkEntity;

public class ItemAdvancedSpark extends ItemMod implements IManaGivingItem {
    public static TextureAtlasSprite worldIcon;

    public ItemAdvancedSpark() {
        super("advancedSpark");
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof ISparkAttachable) {
            ISparkAttachable attach = (ISparkAttachable) tile;
            if (attach.canAttachSpark(stack) && attach.getAttachedSpark() == null) {
                stack.shrink(1);
                if (!world.isRemote) {
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
