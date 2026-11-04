package ab.common.integration.thaumcraft;

import ab.common.lib.register.RecipeListAB;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.api.items.ItemGenericEssentiaContainer;
import thaumcraft.api.items.ItemsTC;
import vazkii.botania.api.lexicon.LexiconEntry;
import vazkii.botania.api.subtile.RadiusDescriptor;
import vazkii.botania.api.subtile.SubTileFunctional;

public class SubTilePureGladiolus extends SubTileFunctional {
    private static final String COOLDOWN_TAG = "advancedBotanyPureGladiolusCooldownUntil";
    private static final String LEGACY_COOLDOWN_TAG = "advancedBotanyPureGladiolusUntil";
    private static final String MERGE_LOCK_TAG = "advancedBotanyPureGladiolusMergeLock";
    private static final int COOLDOWN_TICKS = 100;

    @Override
    public void onUpdate() {
        // This functional flower has no specified mana cost or mana storage.
        ++ticksExisted;
        World world = supertile.getWorld();
        if (world.isRemote) return;

        long now = world.getTotalWorldTime();
        for (EntityItem item : world.getEntitiesWithinAABB(EntityItem.class,
                new AxisAlignedBB(supertile.getPos()).grow(3))) {
            ItemStack stack = item.getItem();
            if (item.isDead || stack.isEmpty() || stack.getItem() != ItemsTC.crystalEssence) continue;
            migrateLegacyCooldown(item, now);
            if (isCoolingDown(item, now)) continue;

            AspectList aspects = ((ItemGenericEssentiaContainer) ItemsTC.crystalEssence).getAspects(stack);
            if (aspects == null || aspects.size() != 1 || aspects.getAspects()[0] == null) continue;

            Aspect[] components = aspects.getAspects()[0].getComponents();
            double x = item.posX;
            double y = item.posY;
            double z = item.posZ;
            stack.shrink(1);
            if (stack.isEmpty()) item.setDead();
            else item.setItem(stack);

            if (components == null || components.length != 2 || components[0] == null || components[1] == null) {
                AuraHelper.polluteAura(world, new BlockPos(x, y, z), 1.0F, true);
            } else {
                spawnCrystal(world, components[0], x, y, z, now + COOLDOWN_TICKS);
                spawnCrystal(world, components[1], x, y, z, now + COOLDOWN_TICKS);
            }
        }
    }

    private static void spawnCrystal(World world, Aspect aspect, double x, double y, double z, long cooldownUntil) {
        ItemStack result = ThaumcraftApiHelper.makeCrystal(aspect);
        NBTTagCompound stackTag = result.getTagCompound();
        if (stackTag == null) stackTag = new NBTTagCompound();
        stackTag.setLong(MERGE_LOCK_TAG, cooldownUntil);
        result.setTagCompound(stackTag);
        EntityItem output = new EntityItem(world, x, y, z, result);
        output.getEntityData().setLong(COOLDOWN_TAG, cooldownUntil);
        world.spawnEntity(output);
    }

    static boolean isCoolingDown(EntityItem item, long now) {
        NBTTagCompound entityData = item.getEntityData();
        if (!entityData.hasKey(COOLDOWN_TAG)) {
            clearMergeLock(item);
            return false;
        }
        if (now < entityData.getLong(COOLDOWN_TAG)) return true;
        entityData.removeTag(COOLDOWN_TAG);
        clearMergeLock(item);
        return false;
    }

    static void migrateLegacyCooldown(EntityItem item, long now) {
        ItemStack stack = item.getItem();
        NBTTagCompound stackTag = stack.getTagCompound();
        if (stackTag == null || !stackTag.hasKey(LEGACY_COOLDOWN_TAG)) return;

        long cooldownUntil = stackTag.getLong(LEGACY_COOLDOWN_TAG);
        stackTag.removeTag(LEGACY_COOLDOWN_TAG);
        if (cooldownUntil > now) {
            NBTTagCompound entityData = item.getEntityData();
            if (!entityData.hasKey(COOLDOWN_TAG) || entityData.getLong(COOLDOWN_TAG) < cooldownUntil) {
                entityData.setLong(COOLDOWN_TAG, cooldownUntil);
            }
            stackTag.setLong(MERGE_LOCK_TAG, cooldownUntil);
        }
        if (stackTag.getKeySet().isEmpty()) stack.setTagCompound(null);
        item.setItem(stack);
    }

    private static void clearMergeLock(EntityItem item) {
        if (clearMergeLock(item.getItem())) item.setItem(item.getItem());
    }

    static boolean clearMergeLock(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null || !tag.hasKey(MERGE_LOCK_TAG)) return false;
        tag.removeTag(MERGE_LOCK_TAG);
        if (tag.getKeySet().isEmpty()) stack.setTagCompound(null);
        return true;
    }

    static boolean clearLegacyCooldown(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null || !tag.hasKey(LEGACY_COOLDOWN_TAG)) return false;
        tag.removeTag(LEGACY_COOLDOWN_TAG);
        if (tag.getKeySet().isEmpty()) stack.setTagCompound(null);
        return true;
    }

    @Override
    public boolean isOvergrowthAffected() {
        return false;
    }

    @Override
    public int getMaxMana() {
        return 0;
    }

    @Override
    public boolean canSelect(EntityPlayer player, ItemStack wand, BlockPos pos, EnumFacing side) {
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void renderHUD(Minecraft mc, ScaledResolution res) {
        String name = I18n.format("tile.botania:flower." + getUnlocalizedName() + ".name");
        mc.fontRenderer.drawStringWithShadow(name,
                res.getScaledWidth() / 2 - mc.fontRenderer.getStringWidth(name) / 2,
                res.getScaledHeight() / 2 + 10, getColor());
    }

    @Override
    public int getColor() {
        return 0xF8EFFF;
    }

    @Override
    public RadiusDescriptor getRadius() {
        return new RadiusDescriptor.Square(supertile.getPos(), 3);
    }

    @Override
    public LexiconEntry getEntry() {
        return RecipeListAB.pureGladiolus;
    }
}
