package ab.common.block.tile;

import ab.api.AdvancedBotanyAPI;
import ab.common.core.handler.ConfigABHandler;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.Style;
import net.minecraft.world.World;
import vazkii.botania.api.internal.VanillaPacketDispatcher;
import vazkii.botania.api.item.IRelic;
import vazkii.botania.common.advancements.RelicBindTrigger;
import vazkii.botania.common.item.ModItems;

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
        World world = this.getWorld();
        if (world.isRemote) return true;

        int relicCount = 0;
        for (int i = 0; i < this.getSizeInventory(); i++) {
            ItemStack stack = this.getStackInSlot(i);
            if (stack.isEmpty()) {
                this.slotChance[i] = 0;
            } else {
                if (stack.getItem() instanceof IRelic
                        && !player.getUniqueID().equals(((IRelic) stack.getItem()).getSoulbindUUID(stack))) {
                    this.dropRelic(player, i);
                    return true;
                }
                relicCount += this.slotChance[i];
            }
        }
        if (relicCount < 1) {
            return false;
        }

        ItemStack reward = ItemStack.EMPTY;
        boolean dudRoll = false;
        if (relicCount != 12) {
            if (AdvancedBotanyAPI.relicList.isEmpty()) return false;
            int relicIndex = Math.min(relicCount - 1, AdvancedBotanyAPI.relicList.size() - 1);
            reward = AdvancedBotanyAPI.relicList.get(relicIndex).copy();
            dudRoll = reward.isEmpty() || hasRelicAdvancement(player, reward)
                    || relicIndex >= ConfigABHandler.fateBoardRelicEnables.length
                    || !ConfigABHandler.fateBoardRelicEnables[relicIndex];
            if (!dudRoll && reward.getItem() instanceof IRelic) {
                // Addon relics implement IRelic without extending Botania's ItemRelic.
                IRelic relic = (IRelic) reward.getItem();
                if (!relic.hasUUID(reward)) {
                    relic.bindToUUID(player.getUniqueID(), reward);
                    if (player instanceof EntityPlayerMP) {
                        RelicBindTrigger.INSTANCE.trigger((EntityPlayerMP) player, reward);
                    }
                }
            }
        }

        for (int i = 0; i < this.getSizeInventory(); i++) {
            this.setInventorySlotContents(i, ItemStack.EMPTY);
            this.slotChance[i] = 0;
        }
        this.requestUpdate = true;
        world.playSound(null, player.posX, player.posY, player.posZ,
                SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS,
                0.5f, 0.4f / (world.rand.nextFloat() * 0.4f + 0.8f));
        if (relicCount == 12) {
            for (int i = 0; i < 3; i++) {
                this.spawnReward(new ItemStack(ModItems.dice));
            }
            player.sendMessage(new TextComponentTranslation("abmisc.fateBoard.bonusDice")
                    .setStyle(new Style().setColor(TextFormatting.DARK_GREEN)));
        } else if (dudRoll) {
            player.sendMessage(new TextComponentTranslation("botaniamisc.dudDiceRoll", relicCount)
                    .setStyle(new Style().setColor(TextFormatting.DARK_GREEN)));
        } else {
            this.spawnReward(reward);
            player.sendMessage(new TextComponentTranslation("botaniamisc.diceRoll", relicCount)
                    .setStyle(new Style().setColor(TextFormatting.DARK_GREEN)));
        }
        return true;
    }

    private void spawnReward(ItemStack reward) {
        this.getWorld().spawnEntity(new EntityItem(this.getWorld(),
                this.getPos().getX() + 0.5, this.getPos().getY() + 0.5, this.getPos().getZ() + 0.5, reward));
    }

    private void dropRelic(EntityPlayer player, int slot) {
        EntityItem entityItem = new EntityItem(this.getWorld(), this.getPos().getX() + 0.5f, this.getPos().getY() + 0.8f, this.getPos().getZ() + 0.5f, this.getStackInSlot(slot).copy());
        float f3 = 0.15f;
        Vec3d vec = player.getLookVec();
        entityItem.motionX = vec.x * f3;
        entityItem.motionY = 0.25;
        entityItem.motionZ = vec.z * f3;
        this.setInventorySlotContents(slot, ItemStack.EMPTY);
        this.slotChance[slot] = 0;
        this.requestUpdate = true;
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
