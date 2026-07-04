package ab.common.entity;

import ab.common.lib.register.ItemListAB;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import vazkii.botania.api.mana.IManaItem;
import vazkii.botania.api.mana.IManaPool;
import vazkii.botania.api.mana.spark.ISparkAttachable;
import vazkii.botania.api.mana.spark.ISparkEntity;
import vazkii.botania.api.mana.spark.SparkHelper;
import vazkii.botania.api.mana.spark.SparkUpgradeType;
import vazkii.botania.common.Botania;
import vazkii.botania.common.core.helper.Vector3;
import vazkii.botania.common.item.ModItems;

public class EntityAdvancedSpark extends Entity implements ISparkEntity {
    private static final DataParameter<Integer> INVIS = EntityDataManager.createKey(EntityAdvancedSpark.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> UPGRADE = EntityDataManager.createKey(EntityAdvancedSpark.class, DataSerializers.VARINT);

    Set<ISparkEntity> transfers = Collections.newSetFromMap(new WeakHashMap<>());
    public int transferSpeed = 48000;
    public int removeTransferants = 2;
    public boolean firstTick = false;

    public EntityAdvancedSpark(World world) {
        super(world);
        this.isImmuneToFire = true;
        this.setNoGravity(true);
    }

    protected void entityInit() {
        this.setSize(0.1f, 0.5f);
        this.dataManager.register(INVIS, 0);
        this.dataManager.register(UPGRADE, 0);
    }

    public void onUpdate() {
        super.onUpdate();
        ISparkAttachable tile = this.getAttachedTile();
        if (tile == null) {
            if (!this.world.isRemote) {
                this.setDead();
            }
            return;
        }
        boolean first = this.world.isRemote && !this.firstTick;
        SparkUpgradeType upgrade = this.getUpgrade();
        int upgradeOrdinal = upgrade.ordinal();
        List<ISparkEntity> allSparks = null;
        if (first || upgradeOrdinal == 0 || upgradeOrdinal == 2 || upgradeOrdinal == 3) {
            allSparks = SparkHelper.getSparksAround(this.world, this.posX, this.posY, this.posZ);
        }
        if (first) {
            this.firstTick = true;
        }
        Collection<ISparkEntity> transfers = this.getTransfers();
        switch (upgradeOrdinal) {
            case 0: {
                // NONE: bidirectional - actively send to nearby sparks (like RECESSIVE) while also receiving from DOMINANT
                for (ISparkEntity spark : allSparks) {
                    SparkUpgradeType supgr = spark.getUpgrade();
                    int supgrOrd = supgr.ordinal();
                    if (spark == this || supgrOrd == 2 || supgrOrd == 3 || supgrOrd == 4)
                        continue;
                    transfers.add(spark);
                }
                break;
            }
            case 1: {
                List<EntityPlayer> players = SparkHelper.getEntitiesAround(EntityPlayer.class, this.world, this.posX, this.posY, this.posZ);
                HashMap<EntityPlayer, HashMap<ItemStack, Integer>> receivingPlayers = new HashMap<>();
                ItemStack input = new ItemStack(ModItems.spark);
                for (EntityPlayer player2 : players) {
                    ArrayList<ItemStack> stacks = new ArrayList<>();
                    stacks.addAll(player2.inventory.mainInventory);
                    stacks.addAll(player2.inventory.armorInventory);
                    stacks.addAll(player2.inventory.offHandInventory);
                    for (ItemStack stack : stacks) {
                        IManaItem manaItem;
                        if (stack.isEmpty() || !(stack.getItem() instanceof IManaItem) || !(manaItem = (IManaItem) stack.getItem()).canReceiveManaFromItem(stack, input))
                            continue;
                        boolean add = false;
                        HashMap<ItemStack, Integer> receivingStacks;
                        if (!receivingPlayers.containsKey(player2)) {
                            add = true;
                            receivingStacks = new HashMap<>();
                        } else {
                            receivingStacks = receivingPlayers.get(player2);
                        }
                        int recv = Math.min(this.getAttachedTile().getCurrentMana(), Math.min(this.transferSpeed, manaItem.getMaxMana(stack) - manaItem.getMana(stack)));
                        if (recv <= 0) continue;
                        receivingStacks.put(stack, recv);
                        if (!add) continue;
                        receivingPlayers.put(player2, receivingStacks);
                    }
                }
                if (!receivingPlayers.isEmpty()) {
                    ArrayList<EntityPlayer> keys = new ArrayList<>(receivingPlayers.keySet());
                    Collections.shuffle(keys);
                    EntityPlayer player2 = keys.iterator().next();
                    Map<ItemStack, Integer> items = receivingPlayers.get(player2);
                    ItemStack stack = items.keySet().iterator().next();
                    int cost = items.get(stack);
                    int manaToPut = Math.min(this.getAttachedTile().getCurrentMana(), cost);
                    ((IManaItem) stack.getItem()).addMana(stack, manaToPut);
                    this.getAttachedTile().recieveMana(-manaToPut);
                    this.particlesTowards(player2);
                }
                break;
            }
            case 2: {
                ArrayList<ISparkEntity> validSparks = new ArrayList<>();
                for (ISparkEntity spark : allSparks) {
                    if (spark == this || spark.getUpgrade() != SparkUpgradeType.NONE || !(spark.getAttachedTile() instanceof IManaPool))
                        continue;
                    validSparks.add(spark);
                }
                if (validSparks.size() > 0) {
                    validSparks.get(this.world.rand.nextInt(validSparks.size())).registerTransfer(this);
                }
                break;
            }
            case 3: {
                for (ISparkEntity spark : allSparks) {
                    SparkUpgradeType supgr = spark.getUpgrade();
                    int supgrOrd = supgr.ordinal();
                    if (spark == this || supgrOrd == 2 || supgrOrd == 3 || supgrOrd == 4)
                        continue;
                    transfers.add(spark);
                }
                break;
            }
        }
        if (!transfers.isEmpty()) {
            int manaTotal = Math.min(this.transferSpeed * transfers.size(), tile.getCurrentMana());
            int manaForEach = manaTotal / transfers.size();
            int manaSpent = 0;
            if (manaForEach > transfers.size()) {
                for (ISparkEntity spark : transfers) {
                    if (spark.getAttachedTile() == null || spark.getAttachedTile().isFull() || spark.areIncomingTransfersDone()) {
                        manaTotal -= manaForEach;
                        continue;
                    }
                    ISparkAttachable attached = spark.getAttachedTile();
                    int spend = Math.min(attached.getAvailableSpaceForMana(), manaForEach);
                    attached.recieveMana(spend);
                    manaSpent += spend;
                    this.particlesTowards((Entity) spark);
                }
                tile.recieveMana(-manaSpent);
            }
        }
        if (this.removeTransferants > 0) {
            --this.removeTransferants;
        }
        this.getTransfers();
    }

    public void particlesTowards(Entity e) {
        Vector3 thisVec = Vector3.fromEntityCenter(this).add(0.0, 0.0, 0.0);
        Vector3 receiverVec = Vector3.fromEntityCenter(e).add(0.0, 0.0, 0.0);
        double rc = 0.45;
        thisVec = thisVec.add(new Vector3((Math.random() - 0.5) * rc, (Math.random() - 0.5) * rc, (Math.random() - 0.5) * rc));
        receiverVec = receiverVec.add(new Vector3((Math.random() - 0.5) * rc, (Math.random() - 0.5) * rc, (Math.random() - 0.5) * rc));
        Vector3 motion = receiverVec.subtract(thisVec);
        motion = motion.multiply(0.04);
        float r = 0.4f + 0.3f * (float) Math.random();
        float g = 0.4f + 0.3f * (float) Math.random();
        float b = 0.4f + 0.3f * (float) Math.random();
        float size = 0.125f + 0.125f * (float) Math.random();
        Botania.proxy.wispFX(thisVec.x, thisVec.y, thisVec.z, r, g, b, size, (float) motion.x, (float) motion.y, (float) motion.z);
    }

    public static void particleBeam(Entity e1, Entity e2) {
        if (e1 == null || e2 == null) {
            return;
        }
        Vector3 orig = new Vector3(e1.posX, e1.posY + 0.25, e1.posZ);
        Vector3 end = new Vector3(e2.posX, e2.posY + 0.25, e2.posZ);
        Vector3 diff = end.subtract(orig);
        Vector3 movement = diff.normalize().multiply(0.1);
        int iters = (int) (diff.mag() / movement.mag());
        float huePer = 1.0f / (float) iters;
        float hueSum = (float) Math.random();
        Vector3 currentPos = orig;
        for (int i = 0; i < iters; ++i) {
            float hue = (float) i * huePer + hueSum;
            Color color = Color.getHSBColor(hue, 1.0f, 1.0f);
            float r = Math.min(1.0f, (float) color.getRed() / 255.0f + 0.4f);
            float g = Math.min(1.0f, (float) color.getGreen() / 255.0f + 0.4f);
            float b = Math.min(1.0f, (float) color.getBlue() / 255.0f + 0.4f);
            Botania.proxy.setSparkleFXNoClip(true);
            Botania.proxy.sparkleFX(currentPos.x, currentPos.y, currentPos.z, r, g, b, 1.0f, 12);
            Botania.proxy.setSparkleFXNoClip(false);
            currentPos = currentPos.add(movement);
        }
    }

    public boolean canBeCollidedWith() {
        return true;
    }

    public boolean processInitialInteract(EntityPlayer player, net.minecraft.util.EnumHand hand) {
        ItemStack stack = player.getHeldItemMainhand();
        if (!stack.isEmpty()) {
            SparkUpgradeType upgrade = this.getUpgrade();
            if (stack.getItem() == ModItems.twigWand) {
                if (player.isSneaking()) {
                    if (upgrade != SparkUpgradeType.NONE) {
                        if (!this.world.isRemote) {
                            this.entityDropItem(new ItemStack(ModItems.sparkUpgrade, 1, upgrade.ordinal()), 0.0f);
                        }
                        this.setUpgrade(SparkUpgradeType.NONE);
                        this.transfers.clear();
                        this.removeTransferants = 2;
                    } else {
                        this.setDead();
                    }
                    if (player.world.isRemote) {
                        player.swingArm(net.minecraft.util.EnumHand.MAIN_HAND);
                    }
                    return true;
                }
                List<ISparkEntity> allSparks = SparkHelper.getSparksAround(this.world, this.posX, this.posY, this.posZ);
                for (ISparkEntity spark : allSparks) {
                    EntityAdvancedSpark.particleBeam(this, (Entity) spark);
                }
                return true;
            }
            if (stack.getItem() == ModItems.sparkUpgrade && upgrade == SparkUpgradeType.NONE) {
                int newUpgrade = stack.getMetadata();
                SparkUpgradeType[] types = SparkUpgradeType.values();
                if (newUpgrade >= 0 && newUpgrade < types.length) {
                    this.setUpgrade(types[newUpgrade]);
                }
                stack.shrink(1);
                if (player.world.isRemote) {
                    player.swingArm(net.minecraft.util.EnumHand.MAIN_HAND);
                }
                return true;
            }
        }
        return this.doPhantomInk(stack);
    }

    public boolean doPhantomInk(ItemStack stack) {
        if (!stack.isEmpty() && stack.getItem() == ModItems.phantomInk && !this.world.isRemote) {
            int invis = this.dataManager.get(INVIS);
            this.dataManager.set(INVIS, ~invis & 1);
            return true;
        }
        return false;
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound cmp) {
        this.setUpgradeFromInt(cmp.getInteger("upgrade"));
        this.dataManager.set(INVIS, cmp.getInteger("invis"));
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound cmp) {
        cmp.setInteger("upgrade", this.getUpgrade().ordinal());
        cmp.setInteger("invis", this.dataManager.get(INVIS));
    }

    public void setDead() {
        super.setDead();
        if (!this.world.isRemote) {
            SparkUpgradeType upgrade = this.getUpgrade();
            this.entityDropItem(new ItemStack(ItemListAB.itemAdvancedSpark), 0.0f);
            if (upgrade != SparkUpgradeType.NONE) {
                this.entityDropItem(new ItemStack(ModItems.sparkUpgrade, 1, upgrade.ordinal()), 0.0f);
            }
        }
    }

    public ISparkAttachable getAttachedTile() {
        int x = MathHelper.floor(this.posX);
        int y = MathHelper.floor(this.posY) - 1;
        int z = MathHelper.floor(this.posZ);
        TileEntity tile = this.world.getTileEntity(new net.minecraft.util.math.BlockPos(x, y, z));
        if (tile instanceof ISparkAttachable) {
            return (ISparkAttachable) tile;
        }
        return null;
    }

    public Collection<ISparkEntity> getTransfers() {
        ArrayList<ISparkEntity> removals = new ArrayList<>();
        Iterator<ISparkEntity> iterator = this.transfers.iterator();
        while (iterator.hasNext()) {
            ISparkEntity spark = iterator.next();
            SparkUpgradeType upgr = this.getUpgrade();
            SparkUpgradeType supgr = spark.getUpgrade();
            ISparkAttachable atile = spark.getAttachedTile();
            if (spark != this && !spark.areIncomingTransfersDone() && atile != null && !atile.isFull() && (upgr == SparkUpgradeType.NONE && (supgr == SparkUpgradeType.DOMINANT || supgr == SparkUpgradeType.NONE || supgr == SparkUpgradeType.DISPERSIVE) || upgr == SparkUpgradeType.RECESSIVE && (supgr == SparkUpgradeType.NONE || supgr == SparkUpgradeType.DISPERSIVE) || !(atile instanceof IManaPool)))
                continue;
            removals.add(spark);
        }
        if (!removals.isEmpty()) {
            this.transfers.removeAll(removals);
        }
        return this.transfers;
    }

    private boolean hasTransfer(ISparkEntity entity) {
        return this.transfers.contains(entity);
    }

    public void registerTransfer(ISparkEntity entity) {
        if (this.hasTransfer(entity)) {
            return;
        }
        this.transfers.add(entity);
    }

    @Override
    public SparkUpgradeType getUpgrade() {
        int val = this.dataManager.get(UPGRADE);
        SparkUpgradeType[] types = SparkUpgradeType.values();
        if (val >= 0 && val < types.length) {
            return types[val];
        }
        return SparkUpgradeType.NONE;
    }

    public void setUpgrade(SparkUpgradeType upgrade) {
        this.dataManager.set(UPGRADE, upgrade.ordinal());
    }

    private void setUpgradeFromInt(int val) {
        SparkUpgradeType[] types = SparkUpgradeType.values();
        if (val >= 0 && val < types.length) {
            this.dataManager.set(UPGRADE, val);
        } else {
            this.dataManager.set(UPGRADE, 0);
        }
    }

    public boolean areIncomingTransfersDone() {
        ISparkAttachable tile = this.getAttachedTile();
        if (tile instanceof IManaPool) {
            return this.removeTransferants > 0;
        }
        return tile != null && tile.areIncomingTranfersDone();
    }
}
