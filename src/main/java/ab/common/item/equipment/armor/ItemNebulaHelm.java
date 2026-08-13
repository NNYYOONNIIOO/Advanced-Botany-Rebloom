package ab.common.item.equipment.armor;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.botania.api.mana.IManaDiscountArmor;
import vazkii.botania.api.mana.IManaGivingItem;

import java.util.List;
import java.util.UUID;

public class ItemNebulaHelm extends ItemNebulaArmor implements IManaDiscountArmor, IManaGivingItem {

    public ItemNebulaHelm() {
        this("nebulaHelm");
    }

    public ItemNebulaHelm(String str) {
        super(EntityEquipmentSlot.HEAD, str);
        MinecraftForge.EVENT_BUS.register(new HelmetEventHandler());
    }

    @Override
    public net.minecraft.util.ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        return toggleEffect(world, player, hand);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag flag) {
        super.addInformation(stack, world, list, flag);
        String effectKey = enableEffect(stack) ? "ab.nebula.effect.health.on" : "ab.nebula.effect.health.off";
        addStringToTooltip(I18n.format(effectKey), list);
    }

    @Override
    public void onArmorTick(World world, EntityPlayer player, ItemStack stack) {
        super.onArmorTick(world, player, stack);
        // Full set: dispatch mana to other items
        if (hasArmorSet(player)) {
            dispatchManaExact(stack, player, 2, true);
        }
    }

    public static boolean dispatchManaExact(ItemStack stack, EntityPlayer player, int manaToSend, boolean add) {
        if (stack.isEmpty()) {
            return false;
        }
        net.minecraft.entity.player.InventoryPlayer inventoryPlayer = player.inventory;
        net.minecraft.inventory.IInventory baublesInv = vazkii.botania.api.BotaniaAPI.internalHandler.getBaublesInventory(player);
        int invSize = inventoryPlayer.getSizeInventory();
        int size = invSize;
        if (baublesInv != null) {
            size += baublesInv.getSizeInventory();
        }
        for (int i = 0; i < size; ++i) {
            int slot;
            boolean useBaubles = i >= invSize;
            net.minecraft.inventory.IInventory inv = useBaubles ? baublesInv : inventoryPlayer;
            ItemStack stackInSlot = inv.getStackInSlot(slot = i - (useBaubles ? invSize : 0));
            if (stackInSlot == stack || stackInSlot.isEmpty() || !(stackInSlot.getItem() instanceof vazkii.botania.api.mana.IManaItem))
                continue;
            vazkii.botania.api.mana.IManaItem manaItemSlot = (vazkii.botania.api.mana.IManaItem) stackInSlot.getItem();
            if (manaItemSlot.getMana(stackInSlot) + manaToSend > manaItemSlot.getMaxMana(stackInSlot) || !manaItemSlot.canReceiveManaFromItem(stackInSlot, stack))
                continue;
            if (add) {
                manaItemSlot.addMana(stackInSlot, manaToSend);
            }
            if (useBaubles) {
                vazkii.botania.api.BotaniaAPI.internalHandler.sendBaubleUpdatePacket(player, slot);
            }
            return true;
        }
        return false;
    }

    public static class HelmetEventHandler {
        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public void onLivingAttack(LivingAttackEvent event) {
            if (!(event.getEntityLiving() instanceof EntityPlayer)) return;
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            if (player.world.isRemote || !isCompleteNebulaSet(player)) return;
            if (hasCreativeManaTablet(player)) {
                event.setCanceled(true);
            }
        }

        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public void onLivingHurt(LivingHurtEvent event) {
            if (!(event.getEntityLiving() instanceof EntityPlayer)) return;
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            if (player.world.isRemote || !isCompleteNebulaSet(player)) return;

            if (hasCreativeManaTablet(player)) {
                event.setCanceled(true);
                return;
            }
            if (isFatalDamage(player, event.getAmount()) && consumeAllNebulaMana(player)) {
                rescue(player);
                event.setCanceled(true);
                return;
            }

            // Preserve the existing special immunity for these two sources.
            if (event.getSource() == DamageSource.ANVIL || event.getSource() == DamageSource.FALLING_BLOCK) {
                event.setCanceled(true);
            }
        }

        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public void onLivingDamage(LivingDamageEvent event) {
            if (!(event.getEntityLiving() instanceof EntityPlayer)) return;
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            if (player.world.isRemote || !isCompleteNebulaSet(player)) return;
            if (hasCreativeManaTablet(player)) {
                event.setCanceled(true);
            } else if (isFatalDamage(player, event.getAmount()) && consumeAllNebulaMana(player)) {
                rescue(player);
                event.setCanceled(true);
            }
        }

        /** Fallback for sources that set health directly or bypass attack/hurt events. */
        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public void onLivingDeath(LivingDeathEvent event) {
            if (!(event.getEntityLiving() instanceof EntityPlayer)) return;
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            if (player.world.isRemote || !isCompleteNebulaSet(player)) return;
            if (hasCreativeManaTablet(player) || consumeAllNebulaMana(player)) {
                rescue(player);
                event.setCanceled(true);
            }
        }

        /** Repairs direct setHealth(0) death state, but never revives an empty set. */
        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
            if (!(event.getEntityLiving() instanceof EntityPlayer)) return;
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            if (player.world.isRemote || !isCompleteNebulaSet(player)) return;
            if (player.getHealth() > 0.0F && !player.isDead && player.deathTime <= 0) return;
            if (hasCreativeManaTablet(player) || consumeAllNebulaMana(player)) {
                rescue(player);
            }
        }

        private static boolean isFatalDamage(EntityPlayer player, float amount) {
            return player.getHealth() > 0.0F && amount >= player.getHealth();
        }

        private static boolean isCompleteNebulaSet(EntityPlayer player) {
            return isNebulaPiece(player.getItemStackFromSlot(EntityEquipmentSlot.HEAD), ItemNebulaHelm.class)
                    && isNebulaPiece(player.getItemStackFromSlot(EntityEquipmentSlot.CHEST), ItemNebulaChest.class)
                    && isNebulaPiece(player.getItemStackFromSlot(EntityEquipmentSlot.LEGS), ItemNebulaLegs.class)
                    && isNebulaPiece(player.getItemStackFromSlot(EntityEquipmentSlot.FEET), ItemNebulaBoots.class);
        }

        private static boolean isNebulaPiece(ItemStack stack, Class<? extends ItemNebulaArmor> type) {
            return !stack.isEmpty() && type.isInstance(stack.getItem());
        }

        private static int getNebulaMana(ItemStack stack) {
            if (stack.isEmpty() || !(stack.getItem() instanceof ItemNebulaArmor)) return 0;
            return ((ItemNebulaArmor) stack.getItem()).getMana(stack);
        }

        private static int getTotalNebulaMana(EntityPlayer player) {
            return getNebulaMana(player.getItemStackFromSlot(EntityEquipmentSlot.HEAD))
                    + getNebulaMana(player.getItemStackFromSlot(EntityEquipmentSlot.CHEST))
                    + getNebulaMana(player.getItemStackFromSlot(EntityEquipmentSlot.LEGS))
                    + getNebulaMana(player.getItemStackFromSlot(EntityEquipmentSlot.FEET));
        }

        private static boolean consumeAllNebulaMana(EntityPlayer player) {
            // A lethal interception has a fixed mana cost. Do not rescue a
            // player when the set cannot pay that cost; normal death must win.
            if (getTotalNebulaMana(player) < ItemNebulaArmor.DamageHandler.KILL_MANA_COST) return false;
            ItemNebulaArmor.drainAllArmorMana(player);
            return getTotalNebulaMana(player) == 0;
        }

        private static boolean hasCreativeManaTablet(EntityPlayer player) {
            return ItemNebulaArmor.DamageHandler.hasCreativeManaSource(player);
        }

        private static void rescue(EntityPlayer player) {
            player.setHealth(player.getMaxHealth());
            player.isDead = false;
            player.deathTime = 0;
            player.hurtTime = 0;
            player.maxHurtTime = 0;
        }
    }

    @Override
    public float getDiscount(ItemStack stack, int slot, EntityPlayer player) {
        return hasArmorSet(player) ? 0.3f : 0.0f;
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
        HashMultimap<String, AttributeModifier> hashMultimap = HashMultimap.create();
        if (slot == EntityEquipmentSlot.HEAD) {
            if (enableEffect(stack)) {
                UUID uuid = new UUID(this.getRegistryName().hashCode(), 0L);
                float manaRatio = 1.0f - (float) getDamage(stack) / 1000.0f;
                hashMultimap.put(SharedMonsterAttributes.MAX_HEALTH.getName(),
                        new AttributeModifier(uuid, "NebulaHelm modifier", 20.0 * manaRatio, 0));
            }
            UUID toughnessUuid = new UUID(this.getRegistryName().hashCode() + 1, 0L);
            float manaRatio = 1.0f - (float) getDamage(stack) / 1000.0f;
            hashMultimap.put(SharedMonsterAttributes.ARMOR_TOUGHNESS.getName(),
                    new AttributeModifier(toughnessUuid, "Nebula toughness", 0.15 * manaRatio, 0));
            UUID knockbackUuid = new UUID(this.getRegistryName().hashCode() + 3, 0L);
            hashMultimap.put(SharedMonsterAttributes.KNOCKBACK_RESISTANCE.getName(),
                    new AttributeModifier(knockbackUuid, "Nebula knockback resistance", 0.15, 0));
        }
        return hashMultimap;
    }
}
