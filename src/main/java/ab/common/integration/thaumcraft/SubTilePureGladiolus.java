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
    private static final String COOLDOWN_TAG = "advancedBotanyPureGladiolusUntil";
    private static final int COOLDOWN_TICKS = 100;

    @Override
    public void onUpdate() {
        // This functional flower has no specified mana cost or mana storage.
        ++ticksExisted;
        World world = supertile.getWorld();
        if (world.isRemote) return;

        long now = getServerTime(world);
        for (EntityItem item : world.getEntitiesWithinAABB(EntityItem.class,
                new AxisAlignedBB(supertile.getPos()).grow(3))) {
            ItemStack stack = item.getItem();
            if (item.isDead || stack.isEmpty() || stack.getItem() != ItemsTC.crystalEssence
                    || isCoolingDown(stack, now)) continue;

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

    private static void spawnCrystal(World world, Aspect aspect, double x, double y, double z, long until) {
        ItemStack result = ThaumcraftApiHelper.makeCrystal(aspect);
        result.getTagCompound().setLong(COOLDOWN_TAG, until);
        EntityItem output = new EntityItem(world, x, y, z, result);
        output.setDefaultPickupDelay();
        world.spawnEntity(output);
    }

    static boolean isCoolingDown(ItemStack stack, long now) {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null || !tag.hasKey(COOLDOWN_TAG)) return false;
        if (now < tag.getLong(COOLDOWN_TAG)) return true;
        clearExpiredCooldown(stack, now);
        return false;
    }

    static boolean clearExpiredCooldown(ItemStack stack, long now) {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null || !tag.hasKey(COOLDOWN_TAG) || now < tag.getLong(COOLDOWN_TAG)) return false;
        tag.removeTag(COOLDOWN_TAG);
        if (tag.getKeySet().isEmpty()) stack.setTagCompound(null);
        return true;
    }

    static long getServerTime(World world) {
        return world.getMinecraftServer().getWorld(0).getTotalWorldTime();
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
