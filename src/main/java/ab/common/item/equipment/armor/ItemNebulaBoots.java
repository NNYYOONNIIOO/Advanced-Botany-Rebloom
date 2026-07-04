package ab.common.item.equipment.armor;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumHand;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ItemNebulaBoots extends ItemNebulaArmor {

    public static List<String> playersWithStepup = new ArrayList<String>();

    public ItemNebulaBoots() {
        super(EntityEquipmentSlot.FEET, "nebulaBoots");
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public net.minecraft.util.ActionResult<ItemStack> onItemRightClick(net.minecraft.world.World world, EntityPlayer player, EnumHand hand) {
        return toggleEffect(world, player, hand);
    }

    @Override
    public void addInformation(ItemStack stack, net.minecraft.world.World world, List<String> list, ITooltipFlag flag) {
        super.addInformation(stack, world, list, flag);
        String effectKey = enableEffect(stack) ? "ab.nebula.effect.jump.on" : "ab.nebula.effect.jump.off";
        addStringToTooltip(I18n.format(effectKey), list);
    }

    @Override
    public void onArmorTick(net.minecraft.world.World world, EntityPlayer player, ItemStack stack) {
        super.onArmorTick(world, player, stack);
        // Jump boost scaling with mana ratio (amplifier 0-4, max ~4 blocks)
        if (!world.isRemote && enableEffect(stack)) {
            float manaRatio = 1.0f - (float) getDamage(stack) / 1000.0f;
            int amplifier = (int) (4 * manaRatio);
            if (amplifier > 0) {
                player.addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, 5, amplifier, false, false));
            }
        }
        // Flight particles
        if (world.isRemote && enableEffect(stack) && player.capabilities.isFlying
                && player.getItemStackFromSlot(EntityEquipmentSlot.FEET).getItem() == this) {
            ItemStack chest = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
            if (!chest.isEmpty() && chest.getItem() instanceof ItemNebulaChest && ItemNebulaArmor.enableEffect(chest)) {
                vazkii.botania.common.Botania.proxy.sparkleFX(player.posX + (Math.random() - 0.5), player.posY - 1.25 + (Math.random() / 4.0 - 0.125), player.posZ + (Math.random() - 0.5), (float) (0.6 + Math.random() * 0.4), (float) (0.6 + Math.random() * 0.4), (float) (0.6 + Math.random() * 0.4), (float) ((double) 0.7f + Math.random() / 2.0), 25);
                vazkii.botania.common.Botania.proxy.sparkleFX(player.posX + (Math.random() - 0.5), player.posY - 1.25 + (Math.random() / 4.0 - 0.125), player.posZ + (Math.random() - 0.5), (float) (0.6 + Math.random() * 0.4), (float) (0.6 + Math.random() * 0.4), (float) (0.6 + Math.random() * 0.4), (float) ((double) 0.7f + Math.random() / 2.0), 25);
            }
        }
    }

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {
        if (!(event.getEntityLiving() instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) event.getEntityLiving();
        if (player.world.isRemote) return;
        ItemStack boots = player.getItemStackFromSlot(EntityEquipmentSlot.FEET);
        if (boots.isEmpty() || !(boots.getItem() instanceof ItemNebulaBoots)) return;
        // Immune to fall damage
        if (event.getSource() == DamageSource.FALL) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void updatePlayerStepStatus(LivingEvent.LivingUpdateEvent event) {
        if (!(event.getEntityLiving() instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) event.getEntityLiving();
        String s = playerStr(player);
        if (playersWithStepup.contains(s)) {
            if (shouldPlayerHaveStepup(player)) {
                player.stepHeight = player.isSneaking() ? 0.50001f : 1.0f;
            } else {
                player.stepHeight = 0.5f;
                playersWithStepup.remove(s);
            }
        } else if (shouldPlayerHaveStepup(player)) {
            playersWithStepup.add(s);
            player.stepHeight = 1.0f;
        }
    }

    private boolean shouldPlayerHaveStepup(EntityPlayer player) {
        ItemStack armor = player.getItemStackFromSlot(EntityEquipmentSlot.FEET);
        return !armor.isEmpty() && armor.getItem() instanceof ItemNebulaBoots && enableEffect(armor);
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
        HashMultimap<String, AttributeModifier> hashMultimap = HashMultimap.create();
        if (slot == EntityEquipmentSlot.FEET) {
            UUID toughnessUuid = new UUID(this.getRegistryName().hashCode(), 0L);
            float manaRatio = 1.0f - (float) getDamage(stack) / 1000.0f;
            hashMultimap.put(SharedMonsterAttributes.ARMOR_TOUGHNESS.getName(),
                    new AttributeModifier(toughnessUuid, "Nebula toughness", 0.15 * manaRatio, 0));
            UUID knockbackUuid = new UUID(this.getRegistryName().hashCode() + 2, 0L);
            hashMultimap.put(SharedMonsterAttributes.KNOCKBACK_RESISTANCE.getName(),
                    new AttributeModifier(knockbackUuid, "Nebula knockback resistance", 0.15, 0));
        }
        return hashMultimap;
    }
}
