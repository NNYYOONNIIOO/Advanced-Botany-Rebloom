package ab.common.item.equipment;

import ab.AdvancedBotany;
import ab.api.AdvancedBotanyAPI;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.EnumAction;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.world.World;
import vazkii.botania.api.mana.ManaItemHandler;
import vazkii.botania.common.Botania;

import java.util.List;

public class ItemAquaSword extends ItemSword {

    public ItemAquaSword() {
        super(AdvancedBotanyAPI.mithrilToolMaterial);
        this.setCreativeTab(AdvancedBotany.tabAB);
        this.setTranslationKey(AdvancedBotany.modid + "." + "itemAquaSword");
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
        AxisAlignedBB axis = entity.getEntityBoundingBox().grow(1.7, 1.7, 1.7);
        List<EntityLivingBase> entities = entity.world.getEntitiesWithinAABB(EntityLivingBase.class, axis);
        double posX = entity.posX;
        double posY = entity.posY + (double) entity.getEyeHeight();
        double posZ = entity.posZ;
        if (!player.world.isRemote) {
            boolean hasWaterSplash = false;
            for (EntityLivingBase living : entities) {
                if (living instanceof EntityPlayer && (((EntityPlayer) living).getName().equals(player.getName()) || player.getServer() != null && !player.getServer().isPVPEnabled()) || !ManaItemHandler.requestManaExactForTool(stack, player, 10, false) || !living.attackEntityFrom(DamageSource.causePlayerDamage(player), AdvancedBotanyAPI.mithrilToolMaterial.getAttackDamage() / 2.0f))
                    continue;
                ManaItemHandler.requestManaExactForTool(stack, player, 10, true);
                if (!hasWaterSplash) {
                    hasWaterSplash = true;
                }
                Vec3d vec3 = player.getLookVec().normalize();
                living.motionX += vec3.x * 1.35;
                living.motionY += vec3.y / 1.8;
                living.motionZ += vec3.z * 1.35;
            }
            if (hasWaterSplash) {
                player.world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_SPLASH, SoundCategory.PLAYERS, 1.2f, 1.2f);
            }
        } else if (ManaItemHandler.requestManaExactForTool(stack, player, 10, false)) {
            for (int i = 0; i < 24; ++i) {
                float mtX = (float) ((Math.random() - 0.5) * 0.12);
                float mtY = (float) ((Math.random() - 0.5) * 0.12);
                float mtZ = (float) ((Math.random() - 0.5) * 0.12);
                Botania.proxy.wispFX(posX, posY, posZ, 0.0f, (float) (Math.random() * 0.35), 1.0f - (float) (Math.random() * 0.4), 0.17f + (float) (Math.random() * 0.3), mtX, mtY, mtZ, 0.512f);
            }
        }
        return super.onLeftClickEntity(stack, player, entity);
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int par4, boolean par5) {
        if (entity instanceof EntityPlayer) {
            EntityPlayer p = (EntityPlayer) entity;
            if (p.isHandActive() && p.getActiveItemStack() == stack) {
                onUsingTick(stack, p, p.getItemInUseCount());
            }
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        player.setActiveHand(hand);
        return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.BLOCK;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 72000;
    }

    private void onUsingTick(ItemStack stack, EntityPlayer p, int time) {
        if (p.world.isRemote) {
            if (!ManaItemHandler.requestManaExactForTool(stack, p, 15, false)) {
                return;
            }
            time = (this.getMaxItemUseDuration(stack) - time) % 120 + 1;
            if (time > 50) {
                time = 120 - time;
            }
            float phase = (float) time / 120.0f;
            int wispCount = 8;
            double tickIncrement = 360.0 / (double) wispCount;
            int speed = 10;
            double wticks = (double) (time * speed) - tickIncrement;
            double r = Math.sin(1.4f);
            for (int i = 0; i < wispCount; ++i) {
                double posX = p.posX + Math.sin(wticks * Math.PI / 180.0) * r;
                double posY = p.posY + wticks * 0.001 + 0.1;
                double posZ = p.posZ + Math.cos(wticks * Math.PI / 180.0) * r;
                Botania.proxy.wispFX(posX, posY, posZ, 0.0f, (float) (Math.random() * 0.35), 1.0f - (float) (Math.random() * 0.4), 0.3f, 0.0f, -0.1f + (float) (Math.random() * 0.05), 0.0f, 0.7f);
                wticks += tickIncrement;
            }
        } else {
            AxisAlignedBB axis = p.getEntityBoundingBox().grow(2.75, 2.75, 2.75);
            List<EntityLivingBase> entities = p.world.getEntitiesWithinAABB(EntityLivingBase.class, axis);
            for (EntityLivingBase living : entities) {
                if (living instanceof EntityPlayer && (((EntityPlayer) living).getName().equals(p.getName()) || p.getServer() != null && !p.getServer().isPVPEnabled()))
                    continue;
                double dist = living.getDistance(p.posX, p.posY, p.posZ) / 2.5;
                if (!ManaItemHandler.requestManaExactForTool(stack, p, 15, false) || !living.attackEntityFrom(DamageSource.causePlayerDamage(p), 1.0f))
                    continue;
                ManaItemHandler.requestManaExactForTool(stack, p, 15, true);
                double d5 = living.posX - p.posX;
                double d7 = living.posZ - p.posZ;
                double d9 = (double) net.minecraft.util.math.MathHelper.sqrt(d5 * d5 + d7 * d7);
                if (!(dist <= 1.0) || d9 == 0.0) continue;
                living.motionX += (d5 /= d9) * 1.2;
                living.motionZ += (d7 /= d9) * 1.2;
            }
        }
    }
}
