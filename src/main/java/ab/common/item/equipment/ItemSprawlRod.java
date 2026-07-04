package ab.common.item.equipment;

import ab.common.core.handler.ConfigABHandler;
import ab.common.entity.EntitySeed;
import ab.common.item.ItemMod;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import vazkii.botania.api.mana.IManaUsingItem;
import vazkii.botania.api.mana.ManaItemHandler;
import vazkii.botania.common.Botania;
import vazkii.botania.common.item.ModItems;

import java.awt.*;

public class ItemSprawlRod extends ItemMod implements IManaUsingItem {

    public ItemSprawlRod() {
        super("sprawlRod");
        this.setMaxStackSize(1);
        this.setNoRepair();
        this.setMaxDamage(100);
    }

    @Override
    public boolean usesMana(ItemStack stack) {
        return true;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public boolean isFull3D() {
        return true;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.BOW;
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity player, int par4, boolean par5) {
        if (!world.isRemote && player instanceof EntityPlayer && stack.getMetadata() > 0 && ManaItemHandler.requestManaExactForTool(stack, (EntityPlayer) player, 760, true)) {
            stack.setItemDamage(stack.getMetadata() - 1);
        }
        if (player instanceof EntityPlayer) {
            EntityPlayer p = (EntityPlayer) player;
            if (p.isHandActive() && p.getActiveItemStack() == stack) {
                int count = p.getItemInUseCount();
                onUsingTick(stack, p, count);
            }
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer p, EnumHand hand) {
        ItemStack rod = p.getHeldItem(hand);
        if (rod.getMetadata() != 0) {
            return new ActionResult<>(EnumActionResult.PASS, rod);
        }
        ItemStack seed = null;
        for (int i = 0; i < p.inventory.getSizeInventory(); ++i) {
            ItemStack slot = p.inventory.getStackInSlot(i);
            if (slot.isEmpty() || slot.getItem() != ModItems.grassSeeds) continue;
            seed = slot;
            break;
        }
        if (seed == null) {
            return new ActionResult<>(EnumActionResult.PASS, rod);
        }
        p.setActiveHand(hand);
        return new ActionResult<>(EnumActionResult.SUCCESS, rod);
    }

    public void onUsingTick(ItemStack stack, EntityPlayer p, int time) {
        if (p.world.isRemote) {
            time = getMaxItemUseDuration(stack) - time;
            if (time % 2 == 0 && time != 0) {
                return;
            }
            ItemStack seed = null;
            for (int i = 0; i < p.inventory.getSizeInventory(); ++i) {
                ItemStack slot = p.inventory.getStackInSlot(i);
                if (slot.isEmpty() || slot.getItem() != ModItems.grassSeeds) continue;
                seed = slot;
                break;
            }
            if (seed == null) {
                return;
            }
            int ticks = Math.min(128, time);
            float fTicks = (float) ticks / 128.0f;
            Vec3d look = p.getLook(1.0f);
            double posX = p.posX + look.x * 1.4 + (Math.random() - 0.5) * fTicks * 0.3;
            double posY = p.posY + look.y * 1.4 + (Math.random() - 0.5) * fTicks * 0.3;
            double posZ = p.posZ + look.z * 1.4 + (Math.random() - 0.5) * fTicks * 0.3;
            Color color = getSeedColor(seed);
            Botania.proxy.wispFX(posX, posY, posZ, (float) color.getRed() / 255.0f, (float) color.getGreen() / 255.0f, (float) color.getBlue() / 255.0f, 0.5f * fTicks - (float) (Math.random() * 0.1), 0.0f, 0.5f);
        }
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack rod, World world, EntityLivingBase entity, int lastTime) {
        if (!(entity instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) entity;
        int useTime = getMaxItemUseDuration(rod) - lastTime;
        for (int i = 0; i < player.inventory.getSizeInventory(); ++i) {
            ItemStack stack = player.inventory.getStackInSlot(i);
            if (stack.isEmpty() || stack.getItem() != ModItems.grassSeeds) continue;
            if (!world.isRemote) {
                EntitySeed entitySeed = new EntitySeed(world, player);
                entitySeed.setSeed(stack.copy());
                entitySeed.setRadius((int) (Math.min((float) useTime, 128.0f) / 128.0f * ConfigABHandler.sprawlRodMaxArea));
                entitySeed.setAttacker(player.getName());
                float f = (float) useTime / 20.0f;
                f = (f * f + f * 2.0f) / 3.0f;
                if (f > 1.0f) f = 1.0f;
                entitySeed.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, f * 1.5F, 1.0F);
                world.spawnEntity(entitySeed);
            }
            if (stack.getCount() > 1) {
                stack.shrink(1);
            } else {
                player.inventory.setInventorySlotContents(i, ItemStack.EMPTY);
            }
            if (player.capabilities.isCreativeMode) break;
            rod.setItemDamage(Math.min(100, (int) ((float) useTime / 128.0f * 100.0f)));
            break;
        }
    }

    public static Color getSeedColor(ItemStack seed) {
        int meta = seed.getMetadata();
        float r = 0.0f;
        float g = 0.4f;
        float b = 0.0f;
        switch (meta) {
            case 1:
                r = 0.5f; g = 0.37f; b = 0.0f; break;
            case 2:
                r = 0.27f; g = 0.0f; b = 0.33f; break;
            case 3:
                r = 0.4f; g = 0.5f; b = 0.05f; break;
            case 4:
                r = 0.75f; g = 0.7f; b = 0.0f; break;
            case 5:
                r = 0.0f; g = 0.5f; b = 0.1f; break;
            case 6:
                r = 0.75f; g = 0.0f; b = 0.0f; break;
            case 7:
                r = 0.0f; g = 0.55f; b = 0.55f; break;
            case 8:
                r = 0.4f; g = 0.1f; b = 0.4f; break;
        }
        return new Color(r, g, b);
    }
}
