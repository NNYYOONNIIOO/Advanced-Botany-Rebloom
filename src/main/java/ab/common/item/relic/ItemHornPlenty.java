package ab.common.item.relic;

import ab.common.core.handler.ConfigABHandler;
import ab.common.core.handler.NetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import vazkii.botania.api.mana.ManaItemHandler;
import vazkii.botania.common.core.helper.ItemNBTHelper;

public class ItemHornPlenty extends ItemModRelic {

    public static final String[] dropFewItems = new String[]{"dropFewItems", "func_70628_a"};
    private static final short maxChargeLoot = 16;
    private static final int manaCost = 64000;

    public ItemHornPlenty() {
        super("hornPlenty");
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int pos, boolean equipped) {
        super.onUpdate(stack, world, entity, pos, equipped);
        if (!world.isRemote && entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            short lastChargeLoot = getLastChargeLoot(stack);
            short chargeLoot = getChargeLoot(stack);
            if (lastChargeLoot != chargeLoot) {
                setLastChargeLoot(stack, chargeLoot);
                NetworkHandler.sendPacketToHornHud((EntityPlayerMP) player, chargeLoot);
            }
        }
        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            if (player.isHandActive() && player.getActiveItemStack() == stack) {
                int count = player.getItemInUseCount();
                onUsingTick(stack, player, count);
            }
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (!hasChargeLoot(stack) && ManaItemHandler.requestManaExactForTool(stack, player, 64000, false)) {
            player.setActiveHand(hand);
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    public void onUsingTick(ItemStack stack, EntityPlayer player, int time) {
        time = getMaxItemUseDuration(stack) - time;
        if (time > 48) {
            if (!player.world.isRemote && ManaItemHandler.requestManaExactForTool(stack, player, 64000, true)) {
                setChargeLoot(stack, (short) 16);
                player.world.playSound(null, player.posX, player.posY, player.posZ, net.minecraft.init.SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, net.minecraft.util.SoundCategory.PLAYERS, 1.2f, 4.0f);
            }
            player.stopActiveHand();
        }
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return hasChargeLoot(stack);
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        return 1.0 - (double) getChargeLoot(stack) / 16.0;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 42000;
    }

    @Override
    public boolean isFull3D() {
        return true;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.BOW;
    }

    @SubscribeEvent
    public void onPlayerAttack(LivingDropsEvent event) {
        if (event.getSource() != null && event.getSource().getTrueSource() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getSource().getTrueSource();
            ItemStack horn = null;
            for (int i = 0; i < player.inventory.getSizeInventory(); ++i) {
                ItemStack stack = player.inventory.getStackInSlot(i);
                if (stack.isEmpty() || !(stack.getItem() instanceof ItemHornPlenty) || !hasChargeLoot(stack))
                    continue;
                horn = stack;
                break;
            }
            if (!player.world.isRemote && horn != null && hasChargeLoot(horn) && player.world.rand.nextInt(100) < 20 && event.getEntityLiving() != null && isVallidEntity(event.getEntityLiving())) {
                try {
                    EntityLivingBase liv = event.getEntityLiving();
                    ReflectionHelper.findMethod(EntityLivingBase.class, "dropFewItems", "func_70628_a", boolean.class, int.class).invoke(liv, true, (int) ((float) event.getLootingLevel() * 1.5f));
                    setChargeLoot(horn, (short) (getChargeLoot(horn) - 1));
                    player.world.playSound(null, player.posX, player.posY, player.posZ, net.minecraft.init.SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, net.minecraft.util.SoundCategory.PLAYERS, 1.9f, 0.8f);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setLastChargeLoot(ItemStack stack, short count) {
        ItemNBTHelper.setShort(stack, "lastChargeLoot", count);
    }

    public short getLastChargeLoot(ItemStack stack) {
        return ItemNBTHelper.getShort(stack, "lastChargeLoot", (short) 0);
    }

    public void setChargeLoot(ItemStack stack, short count) {
        ItemNBTHelper.setShort(stack, "chargeLoot", count);
    }

    public short getChargeLoot(ItemStack stack) {
        return ItemNBTHelper.getShort(stack, "chargeLoot", (short) 0);
    }

    public boolean hasChargeLoot(ItemStack stack) {
        return ItemNBTHelper.getInt(stack, "chargeLoot", 0) > 0;
    }

    public static boolean isVallidEntity(EntityLivingBase liv) {
        for (String entityName : ConfigABHandler.lockEntityListToHorn) {
            if (liv.getClass().getSimpleName().equals(entityName)) {
                return false;
            }
        }
        return true;
    }
}
