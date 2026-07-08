package ab.common.item.relic;

import ab.AdvancedBotany;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.botania.common.core.helper.ItemNBTHelper;

import java.util.ArrayList;
import java.util.List;

public class ItemTalismanHiddenRiches extends ItemModRelic {

    protected static List<TileEntityChest> chestList = new ArrayList<TileEntityChest>();
    protected static final int segmentCount = 11;
    protected static final int maxSegmentCount = 16;

    public ItemTalismanHiddenRiches() {
        super("talismanHiddenRiches");
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int pos, boolean equipped) {
        super.onUpdate(stack, world, entity, pos, equipped);
        if (entity != null && entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            boolean eqLastTick = wasEquipped(stack);
            if (!equipped && eqLastTick) {
                setEquipped(stack, equipped);
            }
            if (!eqLastTick && equipped) {
                setEquipped(stack, equipped);
                float segAngles = 360.0f / 16.0f;
                float shift = segAngles / 2.0f + (segmentCount / 2) * segAngles;
                setRotationBase(stack, getCheckingAngle((EntityLivingBase) entity) - shift);
            }
            if (world.isRemote && equipped) {
                for (int i = 0; i < 11; ++i) {
                    TileEntityChest chest = getChestForSegment(i);
                    if (chest == null) continue;
                    chest.prevLidAngle = chest.lidAngle;
                    float lidAngel = chest.lidAngle;
                    if (i == getOpenChest(stack) && chest.lidAngle < 1.0f) {
                        if (lidAngel == 0.0f) {
                            playChestSoundClient(world, player.posX, player.posY - 0.5, player.posZ, SoundEvents.BLOCK_CHEST_OPEN, world.rand.nextFloat() * 0.1f + 0.9f);
                        }
                        chest.lidAngle = Math.min(1.0f, lidAngel + 0.1f);
                        continue;
                    }
                    if (i == getOpenChest(stack) || !(lidAngel > 0.0f)) continue;
                    if ((int) (lidAngel * 10.0f) == 5) {
                        playChestSoundClient(world, player.posX, player.posY - 0.5, player.posZ, SoundEvents.BLOCK_CHEST_CLOSE, world.rand.nextFloat() * 0.1f + 0.9f);
                    }
                    chest.lidAngle = Math.max(0.0f, lidAngel - 0.1f);
                }
            }
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        int segment = getSegmentLookedAt(stack, player);
        if (segment == -1) {
            return new ActionResult<>(EnumActionResult.PASS, stack);
        }
        setOpenChest(stack, segment);
        player.openGui(AdvancedBotany.instance, 0, world, 0, 0, 0);
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    public static int getSegmentLookedAt(ItemStack stack, EntityLivingBase player) {
        float yaw = getCheckingAngle(player, getRotationBase(stack));
        float segAngles = 360.0f / 16.0f;
        for (int seg = 0; seg < 11; ++seg) {
            float calcAngle = seg * segAngles;
            if (yaw >= calcAngle && yaw < calcAngle + segAngles) {
                return seg;
            }
        }
        return -1;
    }

    protected static float getCheckingAngle(EntityLivingBase player, float base) {
        float yaw = MathHelper.wrapDegrees(player.rotationYaw) + 90.0f;
        float segAngles = 360.0f / 16.0f;
        float shift = segAngles / 2.0f + (segmentCount / 2) * segAngles;
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

    public static void setOpenChest(ItemStack stack, int segment) {
        ItemNBTHelper.setInt(stack, "openChest", segment);
    }

    public static int getOpenChest(ItemStack stack) {
        return ItemNBTHelper.getInt(stack, "openChest", -1);
    }

    public static TileEntityChest getChestForSegment(int segment) {
        if (chestList.isEmpty()) {
            for (int i = 0; i < 11; ++i) {
                chestList.add(new TileEntityChest());
            }
        }
        if (segment < 0 || segment >= 11) {
            return null;
        }
        return chestList.get(segment);
    }

    public static void setChestLoot(ItemStack stack, ItemStack[] loot, int segment) {
        NBTTagList nbtList = new NBTTagList();
        int i = -1;
        for (ItemStack item : loot) {
            ++i;
            if (item == null || item.isEmpty()) continue;
            NBTTagCompound cmp = new NBTTagCompound();
            cmp.setByte("slot", (byte) i);
            item.copy().writeToNBT(cmp);
            nbtList.appendTag(cmp);
        }
        ItemNBTHelper.setList(stack, "chestLoot" + segment, nbtList);
    }

    public static ItemStack[] getChestLoot(ItemStack stack, int segment) {
        if (segment >= 11) {
            return null;
        }
        ItemStack[] loot = new ItemStack[27];
        NBTTagList nbtList = ItemNBTHelper.getList(stack, "chestLoot" + segment, 10, false);
        for (int i = 0; i < nbtList.tagCount(); ++i) {
            NBTTagCompound cmp = nbtList.getCompoundTagAt(i);
            byte slotCount = cmp.getByte("slot");
            if (slotCount < 0) continue;
            loot[slotCount] = new ItemStack(cmp);
        }
        return loot;
    }

    @SideOnly(Side.CLIENT)
    private static void playChestSoundClient(World world, double x, double y, double z, net.minecraft.util.SoundEvent sound, float pitch) {
        net.minecraft.client.Minecraft.getMinecraft().world.playSound(x, y, z, sound, SoundCategory.BLOCKS, 0.5f, pitch, false);
    }
}
