package ab.common.item.relic;

import ab.AdvancedBotany;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.botania.common.core.helper.ItemNBTHelper;

public class ItemPocketWardrobe extends ItemModRelic {

    protected static final int segmentCount = 5;
    protected static final int maxSegmentCount = 12;

    public ItemPocketWardrobe() {
        super("autoPocketWardrobe");
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void onUpdate(ItemStack pocketWardrobe, World world, Entity entity, int pos, boolean equipped) {
        super.onUpdate(pocketWardrobe, world, entity, pos, equipped);
        boolean eqLastTick = wasEquipped(pocketWardrobe);
        if (!equipped && eqLastTick) {
            setEquipped(pocketWardrobe, equipped);
        }
        if (!eqLastTick && equipped && entity instanceof EntityLivingBase) {
            setEquipped(pocketWardrobe, equipped);
            int angles = 360;
            int segAngles = angles / 12;
            float shift = (float) segAngles / 2.0f + (segmentCount / 2) * (float) segAngles;
            setRotationBase(pocketWardrobe, getCheckingAngle((EntityLivingBase) entity) - shift);
        }
        int tick = getFightingTick(pocketWardrobe);
        if (tick > 0) {
            setFightingTick(pocketWardrobe, tick - 1);
        } else if (!world.isRemote && tick == 0 && getFightingMode(pocketWardrobe) && entity != null && entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            setFightingMode(pocketWardrobe, false);
            swapArmorSet(pocketWardrobe, player, getPrioritySet(pocketWardrobe));
        }
    }

    public void setArmorSet(ItemStack pocketWardrobe, ItemStack[] armorSet, int segment) {
        NBTTagList nbtList = new NBTTagList();
        int i = -1;
        for (ItemStack armor : armorSet) {
            ++i;
            if (armor.isEmpty()) continue;
            NBTTagCompound cmp = new NBTTagCompound();
            cmp.setByte("slot", (byte) i);
            armor.copy().writeToNBT(cmp);
            nbtList.appendTag(cmp);
        }
        ItemNBTHelper.setList(pocketWardrobe, "armorSet" + segment, nbtList);
    }

    public static ItemStack[] getArmorSet(ItemStack pocketWardrobe, int segment) {
        if (segment >= 5) {
            return null;
        }
        ItemStack[] armorSet = new ItemStack[4];
        NBTTagList nbtList = ItemNBTHelper.getList(pocketWardrobe, "armorSet" + segment, 10, false);
        for (int i = 0; i < nbtList.tagCount(); ++i) {
            NBTTagCompound cmp = nbtList.getCompoundTagAt(i);
            byte slotCount = cmp.getByte("slot");
            if (slotCount < 0 || slotCount > 5) continue;
            armorSet[slotCount] = new ItemStack(cmp);
        }
        return armorSet;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack pocketWardrobe = player.getHeldItem(hand);
        int segment = getSegmentLookedAt(pocketWardrobe, player);
        if (segment == -1) {
            return new ActionResult<>(EnumActionResult.PASS, pocketWardrobe);
        }
        if (player.isSneaking()) {
            setPrioritySet(pocketWardrobe, segment);
        } else {
            swapArmorSet(pocketWardrobe, player, segment);
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, pocketWardrobe);
    }

    public void swapArmorSet(ItemStack stack, EntityPlayer player, int segment) {
        ItemStack[] currentSet = new ItemStack[4];
        for (int i = 0; i < 4; i++) {
            currentSet[i] = player.inventory.armorInventory.get(i);
        }
        ItemStack[] newSet = getArmorSet(stack, segment);
        net.minecraft.util.NonNullList<ItemStack> newArmor = net.minecraft.util.NonNullList.withSize(4, ItemStack.EMPTY);
        for (int i = 0; i < 4; i++) {
            if (newSet != null && newSet[i] != null) {
                newArmor.set(i, newSet[i]);
            }
        }
        for (int i = 0; i < 4; i++) {
            player.inventory.armorInventory.set(i, newArmor.get(i));
        }
        setArmorSet(stack, currentSet, segment);
        // Sync armor to client
        if (player instanceof net.minecraft.entity.player.EntityPlayerMP) {
            for (int i = 0; i < 4; i++) {
                ((net.minecraft.entity.player.EntityPlayerMP) player).connection.sendPacket(new net.minecraft.network.play.server.SPacketSetSlot(-2, i + 36, player.inventory.armorInventory.get(i)));
            }
            player.inventoryContainer.detectAndSendChanges();
        }
        if (!player.world.isRemote) {
            player.world.playSound(null, player.posX, player.posY, player.posZ,
                    net.minecraft.util.SoundEvent.REGISTRY.getObject(new net.minecraft.util.ResourceLocation("advanced_botany", "lokiCubeArmor")),
                    net.minecraft.util.SoundCategory.PLAYERS, 0.3f, 0.86f);
        }
    }

    @SubscribeEvent
    public void onPlayerAttack(LivingAttackEvent event) {
        if (event.getEntityLiving() != null && event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            if (player.capabilities.isCreativeMode) {
                return;
            }
            for (int i = 0; i < player.inventory.getSizeInventory(); ++i) {
                ItemStack stack = player.inventory.getStackInSlot(i);
                if (stack.isEmpty() || !(stack.getItem() instanceof ItemPocketWardrobe)) continue;
                ItemPocketWardrobe item = (ItemPocketWardrobe) stack.getItem();
                if (!getFightingMode(stack)) {
                    int armorPrioritySlot = getPrioritySet(stack);
                    ItemStack[] armorSet = getArmorSet(stack, armorPrioritySlot);
                    boolean hasArmor = false;
                    for (int j = 0; j < armorSet.length; ++j) {
                        if (armorSet[j] == null || armorSet[j].isEmpty()) continue;
                        hasArmor = true;
                        break;
                    }
                    if (!hasArmor) continue;
                    setFightingTick(stack, 32);
                    setFightingMode(stack, true);
                    item.swapArmorSet(stack, player, armorPrioritySlot);
                    return;
                }
                setFightingTick(stack, 32);
                return;
            }
        }
    }

    public static int getSegmentLookedAt(ItemStack stack, EntityLivingBase player) {
        float yaw = getCheckingAngle(player, getRotationBase(stack));
        int angles = 360;
        int segAngles = angles / 12;
        for (int seg = 0; seg < 5; ++seg) {
            float calcAngle = seg * segAngles;
            if (yaw >= calcAngle && yaw < calcAngle + segAngles) {
                return seg;
            }
        }
        return -1;
    }

    protected static float getCheckingAngle(EntityLivingBase player, float base) {
        float yaw = MathHelper.wrapDegrees(player.rotationYaw) + 90.0f;
        int angles = 360;
        int segAngles = angles / 12;
        float shift = (float) segAngles / 2.0f + (segmentCount / 2) * (float) segAngles;
        if (yaw < 0.0f) {
            yaw = 360.0f + yaw;
        }
        float angle = 360.0f - (yaw -= 360.0f - base) + shift;
        if (angle > 360.0f) {
            angle %= 360.0f;
        }
        return angle;
    }

    protected static float getCheckingAngle(EntityLivingBase player) {
        return getCheckingAngle(player, 0.0f);
    }

    public static void setEquipped(ItemStack stack, boolean equipped) {
        ItemNBTHelper.setBoolean(stack, "equipped", equipped);
    }

    public static boolean wasEquipped(ItemStack stack) {
        return ItemNBTHelper.getBoolean(stack, "equipped", false);
    }

    public static void setRotationBase(ItemStack stack, float rotation) {
        ItemNBTHelper.setFloat(stack, "rotationBase", rotation);
    }

    public static float getRotationBase(ItemStack stack) {
        return ItemNBTHelper.getFloat(stack, "rotationBase", 0.0f);
    }

    public static void setFightingMode(ItemStack stack, boolean mode) {
        ItemNBTHelper.setBoolean(stack, "fightingMode", mode);
    }

    public static boolean getFightingMode(ItemStack stack) {
        return ItemNBTHelper.getBoolean(stack, "fightingMode", false);
    }

    public static void setFightingTick(ItemStack stack, int tick) {
        ItemNBTHelper.setInt(stack, "fightingTick", tick);
    }

    public static int getFightingTick(ItemStack stack) {
        return ItemNBTHelper.getInt(stack, "fightingTick", 0);
    }

    public static void setPrioritySet(ItemStack stack, int segment) {
        ItemNBTHelper.setInt(stack, "prioritySet", segment);
    }

    public static int getPrioritySet(ItemStack stack) {
        return ItemNBTHelper.getInt(stack, "prioritySet", 2);
    }
}
