package ab.common.item.relic;

import ab.common.entity.EntityManaVine;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import vazkii.botania.api.mana.IManaUsingItem;
import vazkii.botania.api.mana.ManaItemHandler;

public class ItemFreyrSlingshot extends ItemModRelic implements IManaUsingItem {

    protected static final int MAX_MANA = 50000;
    private static final String TAG_MANA = "mana";

    public ItemFreyrSlingshot() {
        super("freyrSlingshot");
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase entity, int par4) {
        if (!(entity instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) entity;
        int j = getMaxItemUseDuration(stack) - par4;
        float f = (float) j / 20.0f;
        f = (f * f + f * 2.0f) / 3.0f;
        if (f < 1.0f) {
            return;
        }
        if (!world.isRemote && ManaItemHandler.requestManaExactForTool(stack, player, 5000, true)) {
            EntityManaVine ball = new EntityManaVine(world, (EntityLivingBase) player);
            ball.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, f * 1.5F, 1.0F);
            ball.setAttacker(player.getName());
            world.spawnEntity(ball);
            world.playSound(null, player.posX, player.posY, player.posZ,
                    net.minecraft.util.SoundEvent.REGISTRY.getObject(new net.minecraft.util.ResourceLocation("advanced_botany", "freyrSlingshot")),
                    net.minecraft.util.SoundCategory.PLAYERS, 0.4f, 2.8f);
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (ManaItemHandler.requestManaExactForTool(stack, player, 5000, false)) {
            player.setActiveHand(hand);
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 42000;
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World world, EntityLivingBase entity) {
        return stack;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.BOW;
    }

    @Override
    public boolean usesMana(ItemStack stack) {
        return true;
    }
}
