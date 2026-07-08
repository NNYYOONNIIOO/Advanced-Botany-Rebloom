package ab.common.item.equipment.armor;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.common.MinecraftForge;
import vazkii.botania.common.Botania;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ItemNebulaChest extends ItemNebulaArmor {

    public static List<String> playersWithFlight = new ArrayList<String>();

    public ItemNebulaChest() {
        super(EntityEquipmentSlot.CHEST, "nebulaChest");
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public net.minecraft.util.ActionResult<ItemStack> onItemRightClick(net.minecraft.world.World world, EntityPlayer player, EnumHand hand) {
        return toggleEffect(world, player, hand);
    }

    @Override
    public void onArmorTick(net.minecraft.world.World world, EntityPlayer player, ItemStack stack) {
        super.onArmorTick(world, player, stack);
        if (world.isRemote && enableEffect(stack) && player.capabilities.isFlying
                && player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() == this) {
            float w = 0.6f;
            float c = 1.0f - w;
            float r = w + (float) Math.random() * c;
            float g = w + (float) Math.random() * c;
            float b = w + (float) Math.random() * c;
            for (int i = 0; i < 2; ++i) {
                Botania.proxy.sparkleFX(player.posX + (Math.random() - 0.5), player.posY - 0.5 + (Math.random() / 4.0 - 0.125), player.posZ + (Math.random() - 0.5), r, g, b, (float) ((double) 0.7f + Math.random() / 2.0), 25);
            }
        }
    }

    @Override
    public void addInformation(ItemStack stack, net.minecraft.world.World world, List<String> list, ITooltipFlag flag) {
        super.addInformation(stack, world, list, flag);
        String effectKey = enableEffect(stack) ? "ab.nebula.effect.flight.on" : "ab.nebula.effect.flight.off";
        addStringToTooltip(I18n.format(effectKey), list);
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
        HashMultimap<String, AttributeModifier> hashMultimap = HashMultimap.create();
        if (slot == EntityEquipmentSlot.CHEST) {
            UUID toughnessUuid = new UUID(this.getRegistryName().hashCode(), 0L);
            float manaRatio = 1.0f - (float) getDamage(stack) / 1000.0f;
            hashMultimap.put(SharedMonsterAttributes.ARMOR_TOUGHNESS.getName(),
                    new AttributeModifier(toughnessUuid, "Nebula toughness", 0.4 * manaRatio, 0));
            UUID knockbackUuid = new UUID(this.getRegistryName().hashCode() + 2, 0L);
            hashMultimap.put(SharedMonsterAttributes.KNOCKBACK_RESISTANCE.getName(),
                    new AttributeModifier(knockbackUuid, "Nebula knockback resistance", 0.4, 0));
        }
        return hashMultimap;
    }

    @SubscribeEvent
    public void updatePlayerFlyStatus(LivingEvent.LivingUpdateEvent event) {
        if (event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            // Only handle on server side; client syncs via S39PacketPlayerAbilities
            if (player.world.isRemote) return;
            String key = player.getName();
            if (shouldPlayerHaveFlight(player)) {
                if (!playersWithFlight.contains(key)) {
                    playersWithFlight.add(key);
                }
                player.capabilities.allowFlying = true;
                ItemStack chestStack = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
                float manaRatio = chestStack.getItem() instanceof ItemNebulaArmor ?
                    1.0f - (float) getDamage(chestStack) / 1000.0f : 1.0f;
                player.capabilities.setFlySpeed(0.05f + 0.1f * manaRatio);
                if (player instanceof EntityPlayerMP) {
                    ((EntityPlayerMP) player).sendPlayerAbilities();
                }
            } else {
                player.capabilities.setFlySpeed(0.05f); // Always reset fly speed
                if (!player.capabilities.isCreativeMode) {
                    player.capabilities.allowFlying = false;
                    player.capabilities.isFlying = false;
                }
                if (player instanceof EntityPlayerMP) {
                    ((EntityPlayerMP) player).sendPlayerAbilities();
                }
                playersWithFlight.remove(key);
            }
        }
    }

    @SubscribeEvent
    public void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        EntityPlayer player = event.getEntityPlayer();
        if (player.capabilities.isFlying && shouldPlayerHaveFlight(player)) {
            event.setNewSpeed(event.getNewSpeed() * 5.0f);
        }
    }

    private static boolean shouldPlayerHaveFlight(EntityPlayer player) {
        ItemStack armor = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
        return !armor.isEmpty() && armor.getItem() instanceof ItemNebulaChest && enableEffect(armor);
    }
}
