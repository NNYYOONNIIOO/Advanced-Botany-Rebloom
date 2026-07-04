package ab.common.entity;

import ab.common.item.equipment.ItemSprawlRod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import java.awt.Color;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import vazkii.botania.common.Botania;
import vazkii.botania.common.item.ItemGrassSeeds;

public class EntitySeed extends EntityThrowable {
    private static final DataParameter<ItemStack> SEED = EntityDataManager.createKey(EntitySeed.class, DataSerializers.ITEM_STACK);
    private static final DataParameter<Integer> RADIUS = EntityDataManager.createKey(EntitySeed.class, DataSerializers.VARINT);
    private static final DataParameter<String> ATTACKER = EntityDataManager.createKey(EntitySeed.class, DataSerializers.STRING);
    private static final ItemStack AIR_STACK = new ItemStack(Blocks.AIR);

    public EntitySeed(World world) {
        super(world);
    }

    public EntitySeed(World world, EntityPlayer e) {
        super(world, e);
    }

    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(SEED, AIR_STACK.copy());
        this.dataManager.register(RADIUS, 1);
        this.dataManager.register(ATTACKER, "");
    }

    public void onUpdate() {
        super.onUpdate();
        if (this.ticksExisted >= 240) {
            this.setDead();
        }
        if (this.world.isRemote) {
            float m = 0.02f;
            float f1 = 4.0f / ((float) this.getRadius() / 20.0f);
            float size = 1.0f + (float) this.getRadius() / 12.0f;
            for (int i = 0; i < 5; ++i) {
                double posX = this.posX + (Math.random() - 0.5) / (double) f1;
                double posY = this.posY + (Math.random() - 0.5) / (double) f1;
                double posZ = this.posZ + (Math.random() - 0.5) / (double) f1;
                float mx = (float) (Math.random() - 0.5) * m;
                float my = (float) (Math.random() - 0.5) * m;
                float mz = (float) (Math.random() - 0.5) * m;
                Color color = ItemSprawlRod.getSeedColor(this.getSeed());
                Botania.proxy.wispFX(posX, posY, posZ, (float) color.getRed() / 255.0f, (float) color.getGreen() / 255.0f, (float) color.getBlue() / 255.0f, (float) ((double) (0.0625f * size) + Math.random() * (double) 0.12f), mx, my, mz, 0.5f);
            }
        }
    }

    public int getRadius() {
        return this.dataManager.get(RADIUS);
    }

    public void setRadius(int rad) {
        this.dataManager.set(RADIUS, rad);
    }

    public ItemStack getSeed() {
        return this.dataManager.get(SEED);
    }

    public void setSeed(ItemStack stack) {
        stack.setCount(1);
        this.dataManager.set(SEED, stack.isEmpty() ? AIR_STACK.copy() : stack);
    }

    public String getAttacker() {
        return this.dataManager.get(ATTACKER);
    }

    public void setAttacker(String str) {
        this.dataManager.set(ATTACKER, str);
    }

    @SideOnly(Side.CLIENT)
    public void setVelocity(double x, double y, double z) {
        this.motionX = x;
        this.motionY = y;
        this.motionZ = z;
    }

    protected float getGravityVelocity() {
        return 0.03f;
    }

    protected void onImpact(RayTraceResult mov) {
        if (mov.typeOfHit != RayTraceResult.Type.BLOCK) return;
        Block block = this.world.getBlockState(mov.getBlockPos()).getBlock();
        EntityPlayer player = this.world.getPlayerEntityByName(this.getAttacker());
        if (block != null && mov.entityHit == null) {
            if (block instanceof BlockBush || block instanceof BlockLeaves || player == null) {
                return;
            }
            ItemStack seed = this.getSeed();
            if (!seed.isEmpty() && seed.getItem() instanceof ItemGrassSeeds) {
                ItemGrassSeeds itemSeed = (ItemGrassSeeds) seed.getItem();
                for (int i = 0; i < this.getRadius(); ++i) {
                    for (int k = 0; k < this.getRadius(); ++k) {
                        int posX = mov.getBlockPos().getX() + i - this.getRadius() / 2;
                        int posY = mov.getBlockPos().getY();
                        int posZ = mov.getBlockPos().getZ() + k - this.getRadius() / 2;
                        int j = posY;
                        if (this.isTopBlock(posX, posY - 1, posZ)) {
                            j = Math.max(0, j - 20);
                        }
                        while (!this.isTopBlock(posX, j, posZ) && Math.abs(j - posY) <= 40) {
                            ++j;
                        }
                        posY = j;
                        if (!this.world.isRemote && this.isDirt(posX, posY, posZ)) {
                            BlockEvent.BreakEvent event = new BlockEvent.BreakEvent(this.world, new BlockPos(posX, posY, posZ), this.world.getBlockState(new BlockPos(posX, posY, posZ)), player);
                            MinecraftForge.EVENT_BUS.post(event);
                            if (event.isCanceled()) continue;
                            ItemStack originalHeld = player.getHeldItem(net.minecraft.util.EnumHand.MAIN_HAND);
                            player.setHeldItem(net.minecraft.util.EnumHand.MAIN_HAND, seed.copy());
                            itemSeed.onItemUse(player, this.world, new BlockPos(posX, posY, posZ), net.minecraft.util.EnumHand.MAIN_HAND, mov.sideHit, 0.0f, 0.0f, 0.0f);
                            player.setHeldItem(net.minecraft.util.EnumHand.MAIN_HAND, originalHeld);
                            continue;
                        }
                        if (!(Math.random() < (double) 0.15f) && this.getRadius() >= 3 || !this.isDirt(posX, posY, posZ))
                            continue;
                        this.spawnGrowParticle(posX, posY, posZ);
                    }
                }
            }
            this.setDead();
        }
    }

    public void readFromNBT(NBTTagCompound nbtt) {
        super.readFromNBT(nbtt);
        this.ticksExisted = nbtt.getInteger("ticks");
        this.setAttacker(nbtt.getString("attacker"));
        NBTTagCompound stackCmp = nbtt.getCompoundTag("seedStack");
        this.setSeed(new ItemStack(stackCmp));
        this.setRadius(nbtt.getInteger("radius"));
    }

    public NBTTagCompound writeToNBT(NBTTagCompound nbtt) {
        super.writeToNBT(nbtt);
        nbtt.setInteger("ticks", this.ticksExisted);
        nbtt.setString("attacker", this.getAttacker());
        ItemStack stack = this.getSeed();
        NBTTagCompound stackNbt = new NBTTagCompound();
        if (!stack.isEmpty()) {
            stack.writeToNBT(stackNbt);
        }
        nbtt.setInteger("radius", this.getRadius());
        nbtt.setTag("seedStack", stackNbt);
        return nbtt;
    }

    private void spawnGrowParticle(int posX, int posY, int posZ) {
        for (int i = 0; i < 50; ++i) {
            double x = (Math.random() - 0.5) * 3.0;
            double y = Math.random() - 0.5 + 1.0;
            double z = (Math.random() - 0.5) * 3.0;
            float velMul = 0.025f;
            Color color = ItemSprawlRod.getSeedColor(this.getSeed());
            Botania.proxy.wispFX((double) posX + 0.5 + x, (double) posY + 0.5 + y, (double) posZ + 0.5 + z, (float) color.getRed() / 255.0f, (float) color.getGreen() / 255.0f, (float) color.getBlue() / 255.0f, (float) Math.random() * 0.15f + 0.15f, (float) (-x) * velMul, (float) (-y) * velMul, (float) (-z) * velMul);
        }
    }

    private boolean isDirt(int posX, int posY, int posZ) {
        Block block = this.world.getBlockState(new BlockPos(posX, posY, posZ)).getBlock();
        return block == Blocks.DIRT || block == Blocks.GRASS;
    }

    private boolean isTopBlock(int posX, int posY, int posZ) {
        Block blockAbove = this.world.getBlockState(new BlockPos(posX, posY + 1, posZ)).getBlock();
        IBlockState stateAbove = this.world.getBlockState(new BlockPos(posX, posY + 1, posZ));
        return stateAbove.getMaterial() == Material.AIR || blockAbove instanceof BlockBush;
    }
}
