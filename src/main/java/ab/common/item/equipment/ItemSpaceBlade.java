package ab.common.item.equipment;

import ab.AdvancedBotany;
import ab.api.AdvancedBotanyAPI;
import ab.api.IRankItem;
import ab.common.core.handler.ConfigABHandler;
import ab.common.core.handler.NetworkHandler;
import ab.common.entity.EntitySword;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.botania.api.mana.IManaGivingItem;
import vazkii.botania.api.mana.IManaUsingItem;
import vazkii.botania.api.mana.ManaItemHandler;
import vazkii.botania.common.Botania;
import vazkii.botania.common.core.helper.ItemNBTHelper;

import java.util.List;
import java.util.UUID;

public class ItemSpaceBlade extends ItemSword implements IRankItem, IManaUsingItem {

    private static final int recharge = 36;
    public static final int[] LEVELS = new int[]{0, 10000, 1000000, 10000000, 100000000, 1000000000};
    private static final int[] CREATIVE_MANA = new int[]{9999, 999999, 9999999, 99999999, 999999999, 0x7FFFFFFE};

    public ItemSpaceBlade() {
        super(AdvancedBotanyAPI.mithrilToolMaterial);
        this.setCreativeTab(AdvancedBotany.tabAB);
        this.setTranslationKey(AdvancedBotany.modid + "." + "spaceBlade");
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public void getSubItems(CreativeTabs tab, net.minecraft.util.NonNullList<ItemStack> list) {
        if (this.isInCreativeTab(tab)) {
            for (int mana : CREATIVE_MANA) {
                ItemStack stack = new ItemStack(this);
                setMana(stack, mana);
                list.add(stack);
            }
        }
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
        if (!player.world.isRemote) {
            ItemNBTHelper.setInt(stack, "postAttackTick", 3);
            if (getLevel(stack) >= 3 && isEnabledMode(stack)) {
                float size = getLevel(stack) >= 4 ? (getLevel(stack) >= 5 ? 3.5f : 2.5f) : 1.5f;
                AxisAlignedBB axis = entity.getEntityBoundingBox().grow(size, 1.7, size);
                List<EntityLivingBase> entities = entity.world.getEntitiesWithinAABB(EntityLivingBase.class, axis);
                for (EntityLivingBase living : entities) {
                    if (living instanceof EntityPlayer && (((EntityPlayer) living).getName().equals(player.getName()) || player.getServer() != null && !player.getServer().isPVPEnabled()) || living.hurtResistantTime != 0)
                        continue;
                    float damage = getSwordDamage(stack);
                    living.attackEntityFrom(net.minecraft.util.DamageSource.causePlayerDamage(player), damage);
                }
            }
        }
        return super.onLeftClickEntity(stack, player, entity);
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int par4, boolean par5) {
        if (!(entity instanceof EntityPlayer)) {
            return;
        }
        EntityPlayer player = (EntityPlayer) entity;
        int tick = ItemNBTHelper.getInt(stack, "tick", 0);
        if (!world.isRemote) {
            int postAttackTick = ItemNBTHelper.getInt(stack, "postAttackTick", 0);
            if (postAttackTick > 0 && !player.isActiveItemStackBlocking()) {
                ItemNBTHelper.setInt(stack, "postAttackTick", postAttackTick - 1);
            }
            if (tick > 0) {
                ItemNBTHelper.setInt(stack, "tick", tick - 1);
            }
            PotionEffect haste = player.getActivePotionEffect(MobEffects.HASTE);
            float f = haste == null ? 0.16666667f : (haste.getAmplifier() == 1 ? 0.5f : 0.4f);
            if (player.getHeldItemMainhand() == stack && player.getCooledAttackStrength(1.0f) == f && getLevel(stack) >= 1 && postAttackTick == 0 && ManaItemHandler.requestManaExactForTool(stack, player, 120, true)) {
                EntitySword sword = new EntitySword(world, player);
                sword.setDamage(getSwordDamage(stack));
                sword.setAttacker(player.getName());
                sword.motionX *= 0.2;
                sword.motionY *= 0.2;
                sword.motionZ *= 0.2;
                world.spawnEntity(sword);
                player.world.playSound(null, player.posX, player.posY, player.posZ,
                        net.minecraft.util.SoundEvent.REGISTRY.getObject(new net.minecraft.util.ResourceLocation("advanced_botany", "bladeSpace")),
                        SoundCategory.PLAYERS, 0.5f, 3.6f);
            }
        } else if (tick > 26 && par5) {
            for (int i = 0; i < 14; ++i) {
                float r = world.rand.nextBoolean() ? 0.88235295f : 0.39607844f;
                float g = world.rand.nextBoolean() ? 0.2627451f : 0.81960785f;
                float b = world.rand.nextBoolean() ? 0.9411765f : 0.88235295f;
                Botania.proxy.sparkleFX(entity.posX + (Math.random() - 0.5), entity.posY + (Math.random() - 0.5) * 2.0 - 0.5, entity.posZ + (Math.random() - 0.5), r + (float) (Math.random() / 4.0 - 0.125), g + (float) (Math.random() / 4.0 - 0.125), b + (float) (Math.random() / 4.0 - 0.125), 1.8f * (float) (Math.random() - 0.5), 3);
            }
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (ItemNBTHelper.getInt(stack, "tick", 0) != 0) {
            return new ActionResult<>(EnumActionResult.PASS, stack);
        }
        player.setActiveHand(hand);
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase entity, int lastTime) {
        if (!(entity instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) entity;
        int useTime = getMaxItemUseDuration(stack) - lastTime;
        if (useTime < 4) {
            if (!world.isRemote && !player.isSneaking() && ItemNBTHelper.getInt(stack, "tick", 0) == 0 && getLevel(stack) >= 2) {
                NetworkHandler.sendPacketToSpaceDash((EntityPlayerMP) player);
                onPlayerSpaceDash(player);
                ItemNBTHelper.setInt(stack, "tick", 36);
                player.world.playSound(null, player.posX, player.posY, player.posZ,
                        net.minecraft.util.SoundEvent.REGISTRY.getObject(new net.minecraft.util.ResourceLocation("advanced_botany", "bladeSpace")),
                        SoundCategory.PLAYERS, 2.3f, 1.2f);
                return;
            }
            if (player.isSneaking() && getLevel(stack) >= 3) {
                ItemNBTHelper.setBoolean(stack, "isEnabledMode", !isEnabledMode(stack));
                return;
            }
        }
    }

    public static void onPlayerSpaceDash(EntityPlayer player) {
        Vec3d vec3 = player.getLookVec().normalize();
        player.motionX += vec3.x * 3.25;
        player.motionY += vec3.y / 1.6;
        player.motionZ += vec3.z * 3.25;
    }

    @Override
    public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag adv) {
        String rank = I18n.format("botania.rank" + getLevel(stack));
        list.add(I18n.format("botaniamisc.toolRank", rank).replaceAll("&", "\u00a7"));
        if (getMana(stack) == Integer.MAX_VALUE) {
            list.add(TextFormatting.DARK_AQUA + I18n.format("abmisc.swordFull"));
        }
        if (GuiScreen.isShiftKeyDown()) {
            int level = getLevel(stack);
            list.add((level >= 1 ? TextFormatting.GREEN : "") + I18n.format("abmisc.swordInfo.1"));
            list.add((level >= 2 ? TextFormatting.GREEN : "") + I18n.format("abmisc.swordInfo.2"));
            list.add((level >= 3 ? TextFormatting.GREEN : "") + I18n.format("abmisc.swordInfo.LEVEL".replaceAll("LEVEL", "" + (level >= 3 ? level : 3))));
        } else if (getLevel(stack) != 0) {
            list.add(I18n.format("botaniamisc.shiftinfo").replaceAll("&", "\u00a7"));
        }
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return AdvancedBotanyAPI.rarityNebula;
    }

    public int getEntityLifespan(ItemStack itemStack, World world) {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        int tick = ItemNBTHelper.getInt(stack, "tick", 0);
        return (double) tick / 36.0;
    }

    private boolean isEnabledMode(ItemStack stack) {
        return ItemNBTHelper.getBoolean(stack, "isEnabledMode", false);
    }

    @Override
    public int[] getLevels() {
        return LEVELS;
    }

    @Override
    public int getLevel(ItemStack stack) {
        int mana = getMana_(stack);
        for (int i = LEVELS.length - 1; i > 0; --i) {
            if (mana >= LEVELS[i]) {
                return i;
            }
        }
        return 0;
    }

    public static int getMana_(ItemStack stack) {
        return ItemNBTHelper.getInt(stack, "mana", 0);
    }

    public static void setMana(ItemStack stack, int mana) {
        ItemNBTHelper.setInt(stack, "mana", mana);
    }

    public void addMana(ItemStack stack, int mana) {
        setMana(stack, Math.min(getMana(stack) + mana, Integer.MAX_VALUE));
    }

    public boolean canExportManaToItem(ItemStack stack, ItemStack stack1) {
        return false;
    }

    public boolean canExportManaToPool(ItemStack stack, TileEntity tile) {
        return false;
    }

    public boolean canReceiveManaFromItem(ItemStack stack, ItemStack otherStack) {
        return !(otherStack.getItem() instanceof IManaGivingItem);
    }

    public boolean canReceiveManaFromPool(ItemStack stack, TileEntity tile) {
        return true;
    }

    public int getMana(ItemStack stack) {
        return getMana_(stack);
    }

    public int getMaxMana(ItemStack stack) {
        return Integer.MAX_VALUE;
    }

    public boolean isNoExport(ItemStack stack) {
        return true;
    }

    @Override
    public boolean usesMana(ItemStack stack) {
        return true;
    }

    public float getManaFractionForDisplay(ItemStack stack) {
        long mana = getMana(stack) & 0xFFFFFFFFL;
        return (float) ((double) mana / (double) Integer.MAX_VALUE);
    }

    private float getSwordDamage(ItemStack stack) {
        int level = getLevel(stack);
        return 4.0f + (float) Math.round((double) (AdvancedBotanyAPI.mithrilToolMaterial.getAttackDamage() + (float) (level * level) / 1.5f) * ConfigABHandler.damageFactorSpaceSword);
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
        Multimap<String, AttributeModifier> multimap = HashMultimap.create();
        if (slot == EntityEquipmentSlot.MAINHAND) {
            UUID uuid = new UUID(this.getRegistryName().hashCode(), 0L);
            multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", (double) getSwordDamage(stack), 0));
            multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(uuid, "Weapon speed", 0.25, 1));
        }
        return multimap;
    }
}
