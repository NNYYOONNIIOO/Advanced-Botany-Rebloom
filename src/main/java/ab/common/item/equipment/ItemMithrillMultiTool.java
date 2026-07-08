package ab.common.item.equipment;

import ab.AdvancedBotany;
import ab.api.AdvancedBotanyAPI;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import vazkii.botania.common.core.helper.ItemNBTHelper;

import java.util.Collections;

public class ItemMithrillMultiTool extends ItemTool {

    public ItemMithrillMultiTool() {
        super(AdvancedBotanyAPI.mithrilToolMaterial, Collections.<Block>emptySet());
        this.setCreativeTab(AdvancedBotany.tabAB);
        this.setTranslationKey(AdvancedBotany.modid + "." + "mithrillMultiTool");
        this.setHasSubtypes(true);
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public boolean canHarvestBlock(net.minecraft.block.state.IBlockState state, ItemStack stack) {
        return state.getBlock() != Blocks.BEDROCK;
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return true;
    }

    @Override
    public float getDestroySpeed(ItemStack stack, net.minecraft.block.state.IBlockState state) {
        if (isEnabled(stack)) {
            return 9999.0f;
        }
        return 0.0f;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (player.isSneaking()) {
            setEnabled(stack, !isEnabled(stack));
            if (!world.isRemote) {
                world.playSound(null, player.posX, player.posY, player.posZ,
                        net.minecraft.util.SoundEvent.REGISTRY.getObject(new net.minecraft.util.ResourceLocation("botania", "terraPickMode")),
                        net.minecraft.util.SoundCategory.PLAYERS, 0.5f, 0.4f);
            }
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    boolean isEnabled(ItemStack stack) {
        return ItemNBTHelper.getBoolean(stack, "enabled", false);
    }

    void setEnabled(ItemStack stack, boolean enabled) {
        ItemNBTHelper.setBoolean(stack, "enabled", enabled);
    }

    @Override
    public int getMetadata(ItemStack stack) {
        return isEnabled(stack) ? 1 : 0;
    }
}
