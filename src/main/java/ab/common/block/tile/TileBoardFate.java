package ab.common.block.tile;

import ab.api.AdvancedBotanyAPI;
import ab.common.core.handler.ConfigABHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.Style;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.botania.api.internal.VanillaPacketDispatcher;
import vazkii.botania.api.item.IRelic;
import vazkii.botania.common.item.relic.ItemRelic;

import java.util.List;

public class TileBoardFate extends TileInventory implements net.minecraft.util.ITickable {
    public byte[] slotChance = new byte[this.getSizeInventory()];
    public int[] clientTick = new int[]{0, 0, 0, 0};
    public boolean requestUpdate;

    @Override
    public void update() {
        if (!this.getWorld().isRemote) {
            this.updateServer();
        } else {
            this.updateAnimationTicks();
        }
    }

    public void updateAnimationTicks() {
        for (int i = 0; i < this.getSizeInventory(); i++) {
            if (!this.getStackInSlot(i).isEmpty()) {
                this.clientTick[i]++;
            } else {
                this.clientTick[i] = 0;
            }
        }
    }

    protected void updateServer() {
        if (this.requestUpdate) {
            VanillaPacketDispatcher.dispatchTEToNearbyPlayers(this.getWorld(), this.getPos());
        }
        boolean hasUpdate = false;
        if (this.hasFreeSlot()) {
            hasUpdate = this.setDiceFate();
        }
        this.requestUpdate = hasUpdate;
    }

    public boolean spawnRelic(EntityPlayer player) {
        int relicCount = 0;
        for (int i = 0; i < this.getSizeInventory(); i++) {
            if (this.getStackInSlot(i).isEmpty()) {
                this.slotChance[i] = 0;
            } else {
                if (this.getStackInSlot(i).getItem() instanceof IRelic && !((ItemRelic) this.getStackInSlot(i).getItem()).isRightPlayer(player, this.getStackInSlot(i))) {
                    if (!this.getWorld().isRemote) {
                        this.dropRelic(player, i);
                    }
                    return true;
                }
                this.setInventorySlotContents(i, ItemStack.EMPTY);
            }
            relicCount += this.slotChance[i];
        }
        if (relicCount < 1) {
            return false;
        }
        if (!this.getWorld().isRemote) {
            ItemStack relic = AdvancedBotanyAPI.relicList.get(Math.min(relicCount - 1, AdvancedBotanyAPI.relicList.size() - 1)).copy();
            this.getWorld().playSound(null, player.posX, player.posY, player.posZ,
                    net.minecraft.util.SoundEvent.REGISTRY.getObject(new net.minecraft.util.ResourceLocation("random.bow")),
                    net.minecraft.util.SoundCategory.PLAYERS, 0.5f, 0.4f / (this.getWorld().rand.nextFloat() * 0.4f + 0.8f));
            if (hasRelicAdvancement(player, relic) || !ConfigABHandler.fateBoardRelicEnables[relicCount - 1]) {
                player.sendMessage(new TextComponentTranslation("botaniamisc.dudDiceRoll", relicCount).setStyle(new Style().setColor(TextFormatting.DARK_GREEN)));
            } else {
                ((ItemRelic) relic.getItem()).updateRelic(relic, player);
                EntityItem entityItem = new EntityItem(this.getWorld(), this.getPos().getX() + 0.5f, this.getPos().getY() + 0.5f, this.getPos().getZ() + 0.5f, relic);
                player.sendMessage(new TextComponentTranslation("botaniamisc.diceRoll", relicCount).setStyle(new Style().setColor(TextFormatting.DARK_GREEN)));
                this.getWorld().spawnEntity(entityItem);
            }
            this.requestUpdate = true;
        }
        return true;
    }

    private void dropRelic(EntityPlayer player, int slot) {
        EntityItem entityItem = new EntityItem(this.getWorld(), this.getPos().getX() + 0.5f, this.getPos().getY() + 0.8f, this.getPos().getZ() + 0.5f, this.getStackInSlot(slot).copy());
        float f3 = 0.15f;
        Vec3d vec = player.getLookVec();
        entityItem.motionX = vec.x * f3;
        entityItem.motionY = 0.25;
        entityItem.motionZ = vec.z * f3;
        this.setInventorySlotContents(slot, ItemStack.EMPTY);
        this.getWorld().spawnEntity(entityItem);
    }

    public static boolean hasRelicAdvancement(EntityPlayer player, ItemStack rStack) {
        if (!(rStack.getItem() instanceof IRelic)) {
            return false;
        }
        IRelic irelic = (IRelic) rStack.getItem();
        ResourceLocation adv = irelic.getAdvancement();
        if (adv == null) {
            return false;
        }
        if (player instanceof EntityPlayerMP) {
            EntityPlayerMP mpPlayer = (EntityPlayerMP) player;
            net.minecraft.advancements.Advancement advancement = mpPlayer.getServer().getAdvancementManager().getAdvancement(adv);
            if (advancement != null) {
                return mpPlayer.getAdvancements().getProgress(advancement).isDone();
            }
        }
        return false;
    }

    protected boolean hasFreeSlot() {
        for (int i = 0; i < this.getSizeInventory(); i++) {
            if (this.getStackInSlot(i).isEmpty()) return true;
        }
        return false;
    }

    protected boolean setDiceFate() {
        boolean hasUpdate = false;
        List<EntityItem> items = this.getWorld().getEntitiesWithinAABB(EntityItem.class,
                new AxisAlignedBB(this.getPos().getX(), this.getPos().getY(), this.getPos().getZ(),
                        this.getPos().getX() + 1, this.getPos().getY() + 0.7f, this.getPos().getZ() + 1));
        for (EntityItem item : items) {
            ItemStack stack;
            if (item.isDead || item.getItem().isEmpty() || !TileBoardFate.isDice(stack = item.getItem())) continue;
            for (int s = 0; s < this.getSizeInventory(); s++) {
                ItemStack slotStack = this.getStackInSlot(s);
                if (!slotStack.isEmpty()) continue;
                ItemStack copy = stack.copy();
                copy.setCount(1);
                this.setInventorySlotContents(s, copy);
                this.slotChance[s] = (byte) (this.getWorld().rand.nextInt(6) + 1);
                stack.shrink(1);
                if (stack.getCount() == 0) {
                    item.setDead();
                }
                hasUpdate = true;
                this.getWorld().playSound(null, this.getPos().getX(), this.getPos().getY(), this.getPos().getZ(),
                        net.minecraft.util.SoundEvent.REGISTRY.getObject(new net.minecraft.util.ResourceLocation("ab", "boardCube")),
                        net.minecraft.util.SoundCategory.BLOCKS, 0.6f, 1.0f);
                return hasUpdate;
            }
        }
        return hasUpdate;
    }

    public static boolean isDice(ItemStack stack) {
        for (ItemStack dice : AdvancedBotanyAPI.diceList) {
            if (dice.getItem() != stack.getItem() || (dice.getMetadata() != stack.getMetadata() && dice.getMetadata() != Short.MAX_VALUE))
                continue;
            return true;
        }
        return false;
    }

    @Override
    public void writePacketNBT(NBTTagCompound cmp) {
        super.writePacketNBT(cmp);
        cmp.setByteArray("slotChance", this.slotChance);
        cmp.setBoolean("requestUpdate", this.requestUpdate);
    }

    @Override
    public void readPacketNBT(NBTTagCompound cmp) {
        super.readPacketNBT(cmp);
        this.slotChance = cmp.getByteArray("slotChance");
        this.requestUpdate = cmp.getBoolean("requestUpdate");
    }

    @Override
    public int getInventoryStackLimit() {
        return 1;
    }

    @Override
    public String getName() {
        return "inv.boardFate";
    }

    @Override
    public int getSizeInventory() {
        return 2;
    }
}
