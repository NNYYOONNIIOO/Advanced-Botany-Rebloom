package ab.common.item.relic;

import ab.AdvancedBotany;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.botania.api.item.IRelic;
import vazkii.botania.common.core.helper.ItemNBTHelper;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class ItemModRelic extends Item implements IRelic {

    public ItemModRelic(String name) {
        super();
        this.setTranslationKey(AdvancedBotany.modid + "." + name);
        this.setCreativeTab(AdvancedBotany.tabAB);
        this.setNoRepair();
        this.setMaxStackSize(1);
    }

    @Override
    public void onUpdate(ItemStack stack, World world, net.minecraft.entity.Entity entity, int slot, boolean selected) {
        if (!world.isRemote && entity instanceof EntityPlayer) {
            updateRelic(stack, (EntityPlayer) entity);
        }
    }

    public static void updateRelic(ItemStack stack, EntityPlayer player) {
        IRelic relic = (IRelic) stack.getItem();
        if (!relic.hasUUID(stack)) {
            relic.bindToUUID(player.getUniqueID(), stack);
        } else if (!relic.getSoulbindUUID(stack).equals(player.getUniqueID())) {
            // Damage wrong player every 10 ticks
            if (player.ticksExisted % 10 == 0 && shouldDamageWrongPlayer(stack)) {
                player.attackEntityFrom(damageSource(), 2);
            }
        }
    }

    public static boolean shouldDamageWrongPlayer(ItemStack stack) {
        return true;
    }

    public static DamageSource damageSource() {
        return new DamageSource("botania-relic");
    }

    public static boolean isRightPlayer(EntityPlayer player, ItemStack stack) {
        IRelic relic = (IRelic) stack.getItem();
        return relic.hasUUID(stack) && relic.getSoulbindUUID(stack).equals(player.getUniqueID());
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flags) {
        addBindInfo(tooltip, stack);
    }

    @SideOnly(Side.CLIENT)
    public void addBindInfo(List<String> list, ItemStack stack) {
        if (GuiScreen.isShiftKeyDown()) {
            if (!hasUUID(stack)) {
                addStringToTooltip(I18n.format("botaniamisc.relicUnbound"), list);
            } else {
                if (!getSoulbindUUID(stack).equals(Minecraft.getMinecraft().player.getUniqueID()))
                    addStringToTooltip(I18n.format("botaniamisc.notYourSagittarius"), list);
                else addStringToTooltip(I18n.format("botaniamisc.relicSoulbound", Minecraft.getMinecraft().player.getName()), list);
            }
        } else addStringToTooltip(I18n.format("botaniamisc.shiftinfo"), list);
    }

    private static void addStringToTooltip(String s, List<String> tooltip) {
        tooltip.add(s.replaceAll("&", "\u00a7"));
    }

    @Override
    public int getEntityLifespan(ItemStack itemStack, World world) {
        return Integer.MAX_VALUE;
    }

    @Override
    public void bindToUUID(UUID uuid, ItemStack stack) {
        ItemNBTHelper.setString(stack, "soulbindUUID", uuid.toString());
    }

    @Nullable
    @Override
    public UUID getSoulbindUUID(ItemStack stack) {
        String uuidStr = ItemNBTHelper.getString(stack, "soulbindUUID", "");
        if (uuidStr.isEmpty()) return null;
        try {
            return UUID.fromString(uuidStr);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public boolean hasUUID(ItemStack stack) {
        return getSoulbindUUID(stack) != null;
    }

    @Nullable
    @Override
    public ResourceLocation getAdvancement() {
        return null;
    }
}
