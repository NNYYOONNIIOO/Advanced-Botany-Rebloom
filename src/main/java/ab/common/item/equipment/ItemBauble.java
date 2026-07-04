package ab.common.item.equipment;

import ab.AdvancedBotany;
import baubles.api.IBauble;
import baubles.api.cap.BaublesCapabilities;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import vazkii.botania.api.item.ICosmeticAttachable;
import vazkii.botania.api.item.IPhantomInkable;
import vazkii.botania.common.core.helper.ItemNBTHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public abstract class ItemBauble extends Item implements IBauble, ICosmeticAttachable, IPhantomInkable {

    public ItemBauble(String name) {
        this.setMaxStackSize(1);
        this.setTranslationKey(AdvancedBotany.modid + "." + name);
        this.setCreativeTab(AdvancedBotany.tabAB);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (canEquip(stack, player)) {
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }
        return new ActionResult<>(EnumActionResult.PASS, stack);
    }

    @Override
    public void addInformation(ItemStack stack, World world, List<String> tooltip, net.minecraft.client.util.ITooltipFlag flags) {
        if (GuiScreen.isShiftKeyDown()) {
            this.addHiddenTooltip(stack, world, tooltip, flags);
        } else {
            this.addStringToTooltip(I18n.format("botaniamisc.shiftinfo"), tooltip);
        }
    }

    public void addHiddenTooltip(ItemStack stack, World world, List<String> tooltip, net.minecraft.client.util.ITooltipFlag flags) {
        ItemStack cosmetic = this.getCosmeticItem(stack);
        if (!cosmetic.isEmpty()) {
            this.addStringToTooltip(String.format(I18n.format("botaniamisc.hasCosmetic"), cosmetic.getDisplayName()), tooltip);
        }
        if (this.hasPhantomInk(stack)) {
            this.addStringToTooltip(I18n.format("botaniamisc.hasPhantomInk"), tooltip);
        }
    }

    void addStringToTooltip(String s, List<String> tooltip) {
        tooltip.add(s.replaceAll("&", "\u00a7"));
    }

    @Override
    public boolean canEquip(ItemStack stack, EntityLivingBase player) {
        return true;
    }

    @Override
    public boolean canUnequip(ItemStack stack, EntityLivingBase player) {
        return true;
    }

    @Override
    public void onWornTick(ItemStack stack, EntityLivingBase player) {
        if (getLastPlayerHashcode(stack) != player.hashCode()) {
            this.onEquippedOrLoadedIntoWorld(stack, player);
            setLastPlayerHashcode(stack, player.hashCode());
        }
    }

    @Override
    public void onEquipped(ItemStack stack, EntityLivingBase player) {
        if (player != null) {
            if (!player.world.isRemote) {
                player.world.playSound(null, player.posX, player.posY, player.posZ,
                        net.minecraft.util.SoundEvent.REGISTRY.getObject(new net.minecraft.util.ResourceLocation("botania", "equipBauble")),
                        net.minecraft.util.SoundCategory.PLAYERS, 0.1f, 1.3f);
            }
            this.onEquippedOrLoadedIntoWorld(stack, player);
            setLastPlayerHashcode(stack, player.hashCode());
        }
    }

    public void onEquippedOrLoadedIntoWorld(ItemStack stack, EntityLivingBase player) {
    }

    @Override
    public void onUnequipped(ItemStack stack, EntityLivingBase player) {
    }

    @Override
    public ItemStack getCosmeticItem(ItemStack stack) {
        NBTTagCompound cmp = ItemNBTHelper.getCompound(stack, "cosmeticItem", true);
        if (cmp == null || cmp.isEmpty()) {
            return ItemStack.EMPTY;
        }
        return new ItemStack(cmp);
    }

    @Override
    public void setCosmeticItem(ItemStack stack, ItemStack cosmetic) {
        NBTTagCompound cmp = new NBTTagCompound();
        if (!cosmetic.isEmpty()) {
            cosmetic.copy().writeToNBT(cmp);
        }
        ItemNBTHelper.setCompound(stack, "cosmeticItem", cmp);
    }

    @Override
    public boolean hasContainerItem(ItemStack stack) {
        return !getContainerItem(stack).isEmpty();
    }

    @Override
    public ItemStack getContainerItem(ItemStack itemStack) {
        return this.getCosmeticItem(itemStack);
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return false;
    }

    public static UUID getBaubleUUID(ItemStack stack) {
        long most = ItemNBTHelper.getLong(stack, "baubleUUIDMost", 0L);
        if (most == 0L) {
            UUID uuid = UUID.randomUUID();
            ItemNBTHelper.setLong(stack, "baubleUUIDMost", uuid.getMostSignificantBits());
            ItemNBTHelper.setLong(stack, "baubleUUIDLeast", uuid.getLeastSignificantBits());
            return getBaubleUUID(stack);
        }
        long least = ItemNBTHelper.getLong(stack, "baubleUUIDLeast", 0L);
        return new UUID(most, least);
    }

    public static int getLastPlayerHashcode(ItemStack stack) {
        return ItemNBTHelper.getInt(stack, "playerHashcode", 0);
    }

    public static void setLastPlayerHashcode(ItemStack stack, int hash) {
        ItemNBTHelper.setInt(stack, "playerHashcode", hash);
    }

    @Override
    public boolean hasPhantomInk(ItemStack stack) {
        return ItemNBTHelper.getBoolean(stack, "phantomInk", false);
    }

    @Override
    public void setPhantomInk(ItemStack stack, boolean ink) {
        ItemNBTHelper.setBoolean(stack, "phantomInk", ink);
    }

    @Nullable
    @Override
    public net.minecraftforge.common.capabilities.ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        return new net.minecraftforge.common.capabilities.ICapabilityProvider() {
            @Override
            public boolean hasCapability(@Nonnull net.minecraftforge.common.capabilities.Capability<?> capability, @Nullable net.minecraft.util.EnumFacing facing) {
                return capability == BaublesCapabilities.CAPABILITY_ITEM_BAUBLE;
            }

            @Nullable
            @Override
            public <T> T getCapability(@Nonnull net.minecraftforge.common.capabilities.Capability<T> capability, @Nullable net.minecraft.util.EnumFacing facing) {
                return capability == BaublesCapabilities.CAPABILITY_ITEM_BAUBLE ? BaublesCapabilities.CAPABILITY_ITEM_BAUBLE.cast((IBauble)ItemBauble.this) : null;
            }
        };
    }
}
