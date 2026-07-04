package ab.common.item.equipment;

import ab.api.AdvancedBotanyAPI;
import ab.common.entity.EntityNebulaBlaze;
import ab.common.item.ItemMod;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import vazkii.botania.api.mana.IManaUsingItem;
import vazkii.botania.api.mana.ManaItemHandler;

public class ItemNebulaBlaze extends ItemMod implements IManaUsingItem {

    public ItemNebulaBlaze() {
        super("nebulaBlaze");
        this.setMaxStackSize(1);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        player.setActiveHand(hand);
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            if (player.isHandActive() && player.getActiveItemStack() == stack) {
                int count = player.getItemInUseCount();
                onUsingTick(stack, player, count);
            }
        }
    }

    public void onUsingTick(ItemStack stack, EntityPlayer player, int count) {
        if (player.world.isRemote) {
            return;
        }
        if (count % 5 == 2 && ManaItemHandler.requestManaExactForTool(stack, player, 125, true)) {
            EntityNebulaBlaze blaze = new EntityNebulaBlaze(player.world, player);
            blaze.setAttacker(player.getName());
            player.world.spawnEntity(blaze);
            player.world.playSound(null, player.posX, player.posY, player.posZ,
                    net.minecraft.util.SoundEvent.REGISTRY.getObject(new net.minecraft.util.ResourceLocation("advanced_botany", "nebulaBlaze")),
                    net.minecraft.util.SoundCategory.PLAYERS, 0.4f, 1.4f);
        }
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return AdvancedBotanyAPI.rarityNebula;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.BLOCK;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public boolean usesMana(ItemStack stack) {
        return true;
    }
}
