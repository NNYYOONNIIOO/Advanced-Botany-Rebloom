package ab.common.item;

import ab.AdvancedBotany;
import ab.api.AdvancedBotanyAPI;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import vazkii.botania.api.recipe.IFlowerComponent;

import javax.annotation.Nullable;
import java.util.List;

public class ItemABResource extends Item implements IFlowerComponent {
    public static final int SUB_TYPES = 7;

    public ItemABResource() {
        this.setHasSubtypes(true);
        this.setCreativeTab(AdvancedBotany.tabAB);
        this.setTranslationKey(AdvancedBotany.modid + "." + "resourceAB");
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        if (stack.getMetadata() == 5 || stack.getMetadata() == 6) {
            return AdvancedBotanyAPI.rarityNebula;
        }
        return super.getRarity(stack);
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return stack.getMetadata() == 2;
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> list) {
        if (this.isInCreativeTab(tab)) {
            for (int i = 0; i < SUB_TYPES; ++i) {
                list.add(new ItemStack(this, 1, i));
            }
        }
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        return "item." + AdvancedBotany.modid + ".resourceAB_" + stack.getMetadata();
    }

    @Override
    public boolean canFit(ItemStack stack, vazkii.botania.api.item.IPetalApothecary apothecary) {
        int meta = stack.getMetadata();
        return meta == 3 || meta == 2 || meta == 4;
    }

    @Override
    public int getParticleColor(ItemStack stack) {
        return 0x9B0000;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (stack.getMetadata() != 3) {
            return super.onItemRightClick(world, player, hand);
        }
        player.setActiveHand(hand);
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World world, EntityLivingBase entity) {
        if (stack.getMetadata() != 3) {
            return super.onItemUseFinish(stack, world, entity);
        }
        if (!world.isRemote && entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            player.addPotionEffect(new PotionEffect(net.minecraft.init.MobEffects.REGENERATION, 120, 3));
            if (!player.inventory.addItemStackToInventory(new ItemStack(Items.GLASS_BOTTLE))) {
                player.dropItem(new ItemStack(Items.GLASS_BOTTLE), false);
            }
        }
        stack.shrink(1);
        return stack;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return stack.getMetadata() != 3 ? super.getMaxItemUseDuration(stack) : 24;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return stack.getMetadata() != 3 ? super.getItemUseAction(stack) : EnumAction.DRINK;
    }
}
