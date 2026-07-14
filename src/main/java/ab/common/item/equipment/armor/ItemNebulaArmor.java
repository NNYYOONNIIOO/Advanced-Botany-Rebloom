package ab.common.item.equipment.armor;

import ab.AdvancedBotany;
import ab.api.AdvancedBotanyAPI;
import ab.common.lib.register.ItemListAB;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.mana.IManaItem;
import vazkii.botania.api.mana.IManaTooltipDisplay;
import vazkii.botania.api.mana.ManaItemHandler;
import vazkii.botania.common.core.helper.ItemNBTHelper;
import vazkii.botania.common.item.equipment.armor.manasteel.ItemManasteelArmor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ItemNebulaArmor extends ItemManasteelArmor implements IManaItem, IManaTooltipDisplay {

    public static Object nebulaEyes;
    public static final String TAG_MANA = "mana";
    public static final String TAG_ENABLE_EFFECT = "enableEffect";
    private static final int MAX_MANA = 250000;
    static ItemStack[] armorset;

    public ItemNebulaArmor(EntityEquipmentSlot type, String name) {
        super(type, name, AdvancedBotanyAPI.nebulaArmorMaterial);
        this.setCreativeTab(AdvancedBotany.tabAB);
        this.setMaxDamage(1000);
        this.setNoRepair();
    }

    public static boolean enableEffect(ItemStack stack) {
        return ItemNBTHelper.getBoolean(stack, TAG_ENABLE_EFFECT, true);
    }

    public static void setEffectEnabled(ItemStack stack, boolean enabled) {
        ItemNBTHelper.setBoolean(stack, TAG_ENABLE_EFFECT, enabled);
    }

    protected net.minecraft.util.ActionResult<ItemStack> toggleEffect(World world, EntityPlayer player, net.minecraft.util.EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        setEffectEnabled(stack, !enableEffect(stack));
        return new net.minecraft.util.ActionResult<>(net.minecraft.util.EnumActionResult.SUCCESS, stack);
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return AdvancedBotanyAPI.rarityNebula;
    }

    @Override
    public ISpecialArmor.ArmorProperties getProperties(EntityLivingBase player, ItemStack armor, DamageSource source, double damage, int slot) {
        if (player instanceof EntityPlayer && !isWearingFullSet((EntityPlayer) player)) {
            return new ISpecialArmor.ArmorProperties(0, 0, 0);
        }
        return new ISpecialArmor.ArmorProperties(0, (double) this.getArmorMaterial().getDamageReductionAmount(armorStackToSlot(slot)) * ((double) 0.03f + 0.0725 * (double) (1.0f - (float) getDamage(armor) / 1000.0f)), Integer.MAX_VALUE);
    }

    @Override
    public int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot) {
        float manaRatio = 1.0f - (float) getDamage(armor) / 1000.0f;
        EntityEquipmentSlot slotFor = armorStackToSlot(slot);
        return (int) (this.getArmorMaterial().getDamageReductionAmount(slotFor) * manaRatio);
    }

    private EntityEquipmentSlot armorStackToSlot(int slot) {
        switch (slot) {
            case 0: return EntityEquipmentSlot.FEET;
            case 1: return EntityEquipmentSlot.LEGS;
            case 2: return EntityEquipmentSlot.CHEST;
            case 3: return EntityEquipmentSlot.HEAD;
            default: return EntityEquipmentSlot.FEET;
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ModelBiped provideArmorModelForSlot(ItemStack stack, EntityEquipmentSlot slot) {
        this.models.put(slot, new ab.client.model.armor.ModelArmorNebula(slot));
        return this.models.get(slot);
    }

    @Override
    public String getArmorTextureAfterInk(ItemStack stack, EntityEquipmentSlot slot) {
        return "advanced_botany:textures/model/nebulaarmor.png";
    }

    @Override
    public ItemStack[] getArmorSetStacks() {
        if (armorset == null) {
            armorset = new ItemStack[]{new ItemStack(ItemListAB.itemNebulaHelm), new ItemStack(ItemListAB.itemNebulaChest), new ItemStack(ItemListAB.itemNebulaLegs), new ItemStack(ItemListAB.itemNebulaBoots)};
        }
        return armorset;
    }

    @Override
    public boolean hasArmorSetItem(EntityPlayer player, int i) {
        EntityEquipmentSlot slot;
        switch (i) {
            case 0: slot = EntityEquipmentSlot.HEAD; break;
            case 1: slot = EntityEquipmentSlot.CHEST; break;
            case 2: slot = EntityEquipmentSlot.LEGS; break;
            case 3: slot = EntityEquipmentSlot.FEET; break;
            default: return false;
        }
        ItemStack stack = player.getItemStackFromSlot(slot);
        if (stack.isEmpty()) return false;
        switch (i) {
            case 0: return stack.getItem() == ItemListAB.itemNebulaHelm;
            case 1: return stack.getItem() == ItemListAB.itemNebulaChest;
            case 2: return stack.getItem() == ItemListAB.itemNebulaLegs;
            case 3: return stack.getItem() == ItemListAB.itemNebulaBoots;
        }
        return false;
    }

    @Override
    public void damageArmor(EntityLivingBase entity, ItemStack stack, DamageSource source, int damage, int slot) {
        // Mana drain handled entirely by DamageHandler - prevent ISpecialArmor double drain
    }

    @Override
    public void onArmorTick(World world, EntityPlayer player, ItemStack stack) {
        if (!world.isRemote && getMana(stack) != getMaxMana(stack)
                && !DamageHandler.lethalCooldown.contains(player.getName())
                && ManaItemHandler.requestManaExactForTool(stack, player, 1000, true)) {
            addMana(stack, 1000);
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addArmorSetDescription(ItemStack stack, List<String> list) {
        addStringToTooltip(I18n.format("ab.armorset.nebula.desc0"), list);
        addStringToTooltip(I18n.format("ab.armorset.nebula.desc1"), list);
        addStringToTooltip(I18n.format("ab.armorset.nebula.desc2"), list);
        addStringToTooltip(I18n.format("ab.armorset.nebula.desc3"), list);
    }

    public int getDamage(ItemStack stack) {
        return 1000 - (int) (getMana(stack) / (float) MAX_MANA * 1000.0f);
    }

    public int getDisplayDamage(ItemStack stack) {
        return getDamage(stack);
    }

    public static String playerStr(EntityPlayer player) {
        return player.getGameProfile().getName() + ":" + player.world.isRemote;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public String getArmorSetName() {
        return I18n.format("ab.armorset.nebula.name");
    }

    @Override
    public float getManaFractionForDisplay(ItemStack stack) {
        return (float) getMana(stack) / (float) MAX_MANA;
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return true;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        return 1.0 - getManaFractionForDisplay(stack);
    }

    @Override
    public boolean isDamaged(ItemStack stack) {
        return false;
    }

    public static void setMana(ItemStack stack, int mana) {
        ItemNBTHelper.setInt(stack, TAG_MANA, mana);
    }

    @Override
    public void addMana(ItemStack stack, int mana) {
        setMana(stack, Math.min(getMana(stack) + mana, MAX_MANA));
        stack.setItemDamage(getDamage(stack));
    }

    @Override
    public boolean canExportManaToItem(ItemStack stack, ItemStack stack1) {
        return false;
    }

    @Override
    public boolean canExportManaToPool(ItemStack arg0, TileEntity arg1) {
        return false;
    }

    @Override
    public boolean canReceiveManaFromItem(ItemStack arg0, ItemStack arg1) {
        return true;
    }

    @Override
    public boolean canReceiveManaFromPool(ItemStack arg0, TileEntity arg1) {
        return true;
    }

    @Override
    public int getMana(ItemStack stack) {
        return ItemNBTHelper.getInt(stack, TAG_MANA, 0);
    }

    @Override
    public int getMaxMana(ItemStack arg0) {
        return MAX_MANA;
    }

    @Override
    public boolean isNoExport(ItemStack arg0) {
        return true;
    }

    public static boolean isWearingFullSet(EntityPlayer player) {
        return !player.getItemStackFromSlot(EntityEquipmentSlot.HEAD).isEmpty() && player.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() == ItemListAB.itemNebulaHelm
                && !player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).isEmpty() && player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() == ItemListAB.itemNebulaChest
                && !player.getItemStackFromSlot(EntityEquipmentSlot.LEGS).isEmpty() && player.getItemStackFromSlot(EntityEquipmentSlot.LEGS).getItem() == ItemListAB.itemNebulaLegs
                && !player.getItemStackFromSlot(EntityEquipmentSlot.FEET).isEmpty() && player.getItemStackFromSlot(EntityEquipmentSlot.FEET).getItem() == ItemListAB.itemNebulaBoots;
    }

    public static int getTotalArmorMana(EntityPlayer player) {
        int total = 0;
        for (ItemStack stack : player.inventory.armorInventory) {
            if (!stack.isEmpty() && stack.getItem() instanceof ItemNebulaArmor) {
                total += ((ItemNebulaArmor) stack.getItem()).getMana(stack);
            }
        }
        return total;
    }

    public static void drainArmorMana(EntityPlayer player, int amount) {
        int remaining = amount;
        for (ItemStack stack : player.inventory.armorInventory) {
            if (remaining <= 0) break;
            if (!stack.isEmpty() && stack.getItem() instanceof ItemNebulaArmor) {
                ItemNebulaArmor armor = (ItemNebulaArmor) stack.getItem();
                int toDrain = Math.min(remaining, armor.getMana(stack));
                armor.addMana(stack, -toDrain);
                remaining -= toDrain;
            }
        }
    }

    public static void drainAllArmorMana(EntityPlayer player) {
        for (ItemStack stack : player.inventory.armorInventory) {
            if (!stack.isEmpty() && stack.getItem() instanceof ItemNebulaArmor) {
                ((ItemNebulaArmor) stack.getItem()).setMana(stack, 0);
                stack.setItemDamage(1000);
            }
        }
    }

    public static int getBaublesMana(EntityPlayer player) {
        int total = 0;
        Map<Integer, ItemStack> baubles = ManaItemHandler.getManaBaubles(player);
        for (ItemStack stack : baubles.values()) {
            if (stack.getItem() instanceof ItemNebulaArmor) continue;
            IManaItem manaItem = (IManaItem) stack.getItem();
            if (manaItem.isNoExport(stack)) continue;
            total += manaItem.getMana(stack);
        }
        return total;
    }

    public static int drainBaublesMana(EntityPlayer player, int amount) {
        int remaining = amount;
        Map<Integer, ItemStack> baubles = ManaItemHandler.getManaBaubles(player);
        for (Map.Entry<Integer, ItemStack> entry : baubles.entrySet()) {
            if (remaining <= 0) break;
            ItemStack stack = entry.getValue();
            if (stack.getItem() instanceof ItemNebulaArmor) continue;
            IManaItem manaItem = (IManaItem) stack.getItem();
            if (manaItem.isNoExport(stack)) continue;
            int toDrain = Math.min(remaining, manaItem.getMana(stack));
            manaItem.addMana(stack, -toDrain);
            remaining -= toDrain;
            BotaniaAPI.internalHandler.sendBaubleUpdatePacket(player, entry.getKey());
        }
        return remaining;
    }

    public static int getInventoryMana(EntityPlayer player) {
        int total = 0;
        List<ItemStack> items = ManaItemHandler.getManaItems(player);
        for (ItemStack stack : items) {
            if (stack.getItem() instanceof ItemNebulaArmor) continue;
            IManaItem manaItem = (IManaItem) stack.getItem();
            if (manaItem.isNoExport(stack)) continue;
            total += manaItem.getMana(stack);
        }
        return total;
    }

    public static int drainInventoryMana(EntityPlayer player, int amount) {
        int remaining = amount;
        List<ItemStack> items = ManaItemHandler.getManaItems(player);
        for (ItemStack stack : items) {
            if (remaining <= 0) break;
            if (stack.getItem() instanceof ItemNebulaArmor) continue;
            IManaItem manaItem = (IManaItem) stack.getItem();
            if (manaItem.isNoExport(stack)) continue;
            int toDrain = Math.min(remaining, manaItem.getMana(stack));
            manaItem.addMana(stack, -toDrain);
            remaining -= toDrain;
        }
        return remaining;
    }

    public static int getTotalAvailableMana(EntityPlayer player) {
        return getBaublesMana(player) + getInventoryMana(player) + getTotalArmorMana(player);
    }

    public static void drainManaByPriority(EntityPlayer player, int amount) {
        int remaining = drainBaublesMana(player, amount);
        if (remaining > 0) remaining = drainInventoryMana(player, remaining);
        if (remaining > 0) drainArmorMana(player, remaining);
    }

    public static void toggleAndRefreshAttributes(EntityPlayer player, ItemStack stack, EntityEquipmentSlot slot) {
        Multimap<String, AttributeModifier> oldMods = stack.getAttributeModifiers(slot);
        setEffectEnabled(stack, !enableEffect(stack));
        if (!player.world.isRemote) {
            player.getAttributeMap().removeAttributeModifiers(oldMods);
            player.getAttributeMap().applyAttributeModifiers(stack.getAttributeModifiers(slot));
        }
    }

    public static class DamageHandler {
        private static final int KILL_MANA_COST = 1000000;
        private static final int HUNGER_MANA_COST = 2500;
        private static final int INVULN_TICKS = 10;

        public static final Set<String> lethalCooldown = new HashSet<>();
        private static final Map<String, Long> invulnerableUntil = new HashMap<>();
        private static final Map<String, Float> lastBlockedDamage = new HashMap<>();

        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public void onLivingAttack(LivingAttackEvent event) {
            EntityPlayer player = getPlayerIfFullSet(event);
            if (player == null) return;

            float damage = event.getAmount();
            DamageSource source = event.getSource();

            // Cancel on BOTH sides to prevent vanilla pipeline (visual effects, sounds, hurt animation)
            event.setCanceled(true);

            if (player.world.isRemote) return;

            // Check invulnerability frames
            if (isInvulnerable(player, damage)) return;

            // If player has a creative mana source, skip all mana checks and absorb directly
            if (hasCreativeManaSource(player)) {
                setInvulnerability(player, damage);
                return;
            }

            // Apply ISpecialArmor reduction manually (since we cancelled the vanilla pipeline)
            float postArmorDamage = applyArmorReduction(player, source, damage);

            // Calculate mana cost based on post-armor damage
            float manaCostFloat = postArmorDamage * 1000.0f;
            int manaCost = manaCostFloat > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) manaCostFloat;

            // Step 1: Drain baubles + inventory mana first (preserve armor mana for lethal save)
            int remainingCost = drainBaublesMana(player, manaCost);
            remainingCost = drainInventoryMana(player, remainingCost);

            if (remainingCost <= 0) {
                // Fully absorbed by external mana
                setInvulnerability(player, damage);
                return;
            }

            // Step 2: Check if remaining damage is lethal
            float remainingDamage = remainingCost / 1000.0f;
            int armorMana = getTotalArmorMana(player);
            boolean lethal = remainingDamage >= player.getHealth();

            if (lethal && armorMana >= KILL_MANA_COST) {
                if (armorMana >= remainingCost) {
                    // Can fully absorb with armor mana (cheaper than lethal save)
                    drainArmorMana(player, remainingCost);
                } else {
                    // Lethal save: sacrifice all armor mana, keep current health
                    drainAllArmorMana(player);
                    lethalCooldown.add(player.getName());
                }
                setInvulnerability(player, damage);
                return;
            }

            // Step 3: Try to absorb remaining with armor mana (non-lethal or can't do lethal save)
            int armorDrain = Math.min(armorMana, remainingCost);
            drainArmorMana(player, armorDrain);
            remainingCost -= armorDrain;

            float finalDamage = remainingCost / 1000.0f;
            if (finalDamage <= 0) {
                setInvulnerability(player, damage);
            } else {
                // Apply remaining damage directly (avoid double armor reduction from attackEntityFrom)
                player.setHealth(player.getHealth() - finalDamage);
                setInvulnerability(player, damage);
                if (player.getHealth() <= 0) {
                    player.onDeath(source);
                }
            }
        }

        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public void onLivingDeath(LivingDeathEvent event) {
            EntityPlayer player = getPlayerIfFullSet(event);
            if (player == null) return;

            // Safety net: if damage bypassed onLivingAttack somehow
            if (getTotalArmorMana(player) >= KILL_MANA_COST) {
                drainAllArmorMana(player);
                lethalCooldown.add(player.getName());
                // Health is already <= 0, set to 1 HP to survive
                player.setHealth(1.0f);
                player.isDead = false;
                player.deathTime = 0;
                event.setCanceled(true);
                setInvulnerability(player, player.getMaxHealth());
            }
        }

        @SubscribeEvent
        public void onPlayerTick(TickEvent.PlayerTickEvent event) {
            if (event.phase != TickEvent.Phase.END) return;
            EntityPlayer player = event.player;
            if (!isWearingFullSet(player)) return;

            // Clear hurt visual (both client and server)
            player.hurtTime = 0;
            player.maxHurtTime = 0;

            String key = player.getName();

            // Clear lethal cooldown after 1 tick
            lethalCooldown.remove(key);

            // Clean up expired invulnerability entries
            Long until = invulnerableUntil.get(key);
            if (until != null && player.world.getTotalWorldTime() >= until) {
                invulnerableUntil.remove(key);
                lastBlockedDamage.remove(key);
            }

            // Server-only logic below
            if (player.world.isRemote) return;

            // Clear harmful potion effects (armor mana only)
            for (PotionEffect effect : player.getActivePotionEffects()) {
                if (isHarmfulPotionEffect(effect)) {
                    int cost = getEffectRemovalCost(effect);
                    if (getTotalArmorMana(player) >= cost) {
                        drainArmorMana(player, cost);
                        player.removePotionEffect(effect.getPotion());
                    }
                    break;
                }
            }

            // Restore hunger (only when food level is actually below 20, armor mana only)
            net.minecraft.util.FoodStats foodStats = player.getFoodStats();
            if (foodStats.getFoodLevel() < 20) {
                if (getTotalArmorMana(player) >= HUNGER_MANA_COST) {
                    drainArmorMana(player, HUNGER_MANA_COST);
                    foodStats.addStats(20 - foodStats.getFoodLevel(), 20.0f);
                }
            }
        }

        private static float applyArmorReduction(EntityPlayer player, DamageSource source, float damage) {
            if (source.isUnblockable()) return damage;
            List<ISpecialArmor.ArmorProperties> props = new ArrayList<>();
            for (int i = 0; i < player.inventory.armorInventory.size(); i++) {
                ItemStack armor = player.inventory.armorInventory.get(i);
                if (armor.isEmpty()) continue;
                if (armor.getItem() instanceof ISpecialArmor) {
                    ISpecialArmor.ArmorProperties prop =
                        ((ISpecialArmor) armor.getItem()).getProperties(player, armor, source, damage, i);
                    prop.Slot = i;
                    props.add(prop);
                }
            }
            Collections.sort(props);
            float remaining = damage;
            for (ISpecialArmor.ArmorProperties prop : props) {
                float absorb = remaining * (float) prop.AbsorbRatio;
                absorb = Math.min(absorb, (float) prop.AbsorbMax);
                remaining -= absorb;
                if (remaining <= 0) break;
            }
            return Math.max(0, remaining);
        }

        private static void setInvulnerability(EntityPlayer player, float damage) {
            String key = player.getName();
            invulnerableUntil.put(key, player.world.getTotalWorldTime() + INVULN_TICKS);
            lastBlockedDamage.put(key, damage);
        }

        private static boolean isInvulnerable(EntityPlayer player, float damage) {
            String key = player.getName();
            Long until = invulnerableUntil.get(key);
            if (until != null && player.world.getTotalWorldTime() < until) {
                Float last = lastBlockedDamage.get(key);
                return last != null && damage <= last;
            }
            return false;
        }

        private static boolean hasCreativeManaSource(EntityPlayer player) {
            Map<Integer, ItemStack> baubles = ManaItemHandler.getManaBaubles(player);
            for (ItemStack stack : baubles.values()) {
                if (stack.getItem() instanceof vazkii.botania.api.mana.ICreativeManaProvider
                        && ((vazkii.botania.api.mana.ICreativeManaProvider) stack.getItem()).isCreative(stack)) {
                    return true;
                }
            }
            List<ItemStack> items = ManaItemHandler.getManaItems(player);
            for (ItemStack stack : items) {
                if (stack.getItem() instanceof vazkii.botania.api.mana.ICreativeManaProvider
                        && ((vazkii.botania.api.mana.ICreativeManaProvider) stack.getItem()).isCreative(stack)) {
                    return true;
                }
            }
            return false;
        }

        private static EntityPlayer getPlayerIfFullSet(LivingAttackEvent event) {
            if (!(event.getEntityLiving() instanceof EntityPlayer)) return null;
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            return isWearingFullSet(player) ? player : null;
        }

        private static EntityPlayer getPlayerIfFullSet(LivingDeathEvent event) {
            if (!(event.getEntityLiving() instanceof EntityPlayer) || event.getEntity().world.isRemote) return null;
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            return isWearingFullSet(player) ? player : null;
        }

        private static boolean isHarmfulPotionEffect(PotionEffect effect) {
            return effect.getPotion() == MobEffects.WITHER
                    || effect.getPotion() == MobEffects.POISON
                    || effect.getPotion() == MobEffects.HUNGER
                    || effect.getPotion() == MobEffects.SLOWNESS
                    || effect.getPotion() == MobEffects.MINING_FATIGUE
                    || effect.getPotion() == MobEffects.NAUSEA
                    || effect.getPotion() == MobEffects.BLINDNESS
                    || effect.getPotion() == MobEffects.LEVITATION
                    || effect.getPotion() == MobEffects.WEAKNESS;
        }

        private static int getEffectRemovalCost(PotionEffect effect) {
            if (effect.getPotion() == MobEffects.WITHER) return (effect.getAmplifier() + 1) * 2000;
            if (effect.getPotion() == MobEffects.POISON) return (effect.getAmplifier() + 1) * 1000;
            return 1000;
        }
    }
}
