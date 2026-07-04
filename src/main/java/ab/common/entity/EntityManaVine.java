package ab.common.entity;

import ab.client.core.ClientHelper;
import ab.common.core.CommonHelper;
import ab.common.lib.register.BlockListAB;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import java.awt.Color;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.IGrowable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import vazkii.botania.common.Botania;
import vazkii.botania.common.core.handler.ConfigHandler;
import vazkii.botania.common.lib.LibObfuscation;

public class EntityManaVine extends EntityThrowable {
    private static final DataParameter<String> ATTACKER = EntityDataManager.createKey(EntityManaVine.class, DataSerializers.STRING);

    public EntityManaVine(World world) {
        super(world);
    }

    public EntityManaVine(World world, EntityLivingBase entity) {
        super(world, entity);
    }

    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(ATTACKER, "");
    }

    public String getAttacker() {
        return this.dataManager.get(ATTACKER);
    }

    public void setAttacker(String str) {
        this.dataManager.set(ATTACKER, str);
    }

    public void onUpdate() {
        super.onUpdate();
        ++this.ticksExisted;
        if (this.ticksExisted >= 240) {
            this.setDead();
        }
        if (this.world.isRemote) {
            float m = 0.02f;
            float f1 = 6.0f;
            for (int i = 0; i < 4; ++i) {
                double posX = this.posX + (Math.random() / (double) f1 - (double) (0.5f / f1));
                double posY = this.posY + (Math.random() / (double) f1 - (double) (0.5f / f1));
                double posZ = this.posZ + (Math.random() / (double) f1 - (double) (0.5f / f1));
                float mx = (float) (Math.random() - 0.5) * m;
                float my = (float) (Math.random() - 0.5) * m;
                float mz = (float) (Math.random() - 0.5) * m;
                Color color = ClientHelper.getCorporeaRuneColor((int) posX, (int) posY, (int) posZ, 3);
                Botania.proxy.wispFX(posX, posY, posZ, (float) color.getRed() / 255.0f, (float) color.getGreen() / 255.0f, (float) color.getBlue() / 255.0f, (float) ((double) 0.15f + Math.random() * (double) 0.12f), mx, my, mz, 0.7f);
            }
        }
    }

    protected void onImpact(RayTraceResult pos) {
        EntityPlayer player = this.world.getPlayerEntityByName(this.getAttacker());
        if (pos != null && pos.entityHit == null && player != null) {
            World world = this.world;
            int x = pos.getBlockPos().getX();
            int y = pos.getBlockPos().getY();
            int z = pos.getBlockPos().getZ();
            List<EntityLivingBase> list = world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(x - 10, y - 10, z - 10, x + 10, y + 10, z + 10));
            for (EntityLivingBase target : list) {
                if (!(target instanceof EntityAnimal) || !target.attackEntityFrom(DamageSource.causePlayerDamage(player), 0.0f))
                    continue;
                EntityAnimal animal = (EntityAnimal) target;
                ReflectionHelper.setPrivateValue(EntityAnimal.class, animal, 1200, "inLove", "field_70881_d");
                animal.setAttackTarget(null);
                this.world.setEntityState(animal, (byte) 18);
            }
            for (int k = 0; k < 7; ++k) {
                for (int k1 = 0; k1 < 7; ++k1) {
                    for (int k2 = 0; k2 < 7; ++k2) {
                        int xCoord = x + k - 3;
                        int yCoord = y + k1 - 1;
                        int zCoord = z + k2 - 3;
                        Block block = this.world.getBlockState(new BlockPos(xCoord, yCoord, zCoord)).getBlock();
                        if (block instanceof IGrowable && !(block instanceof BlockGrass)) {
                            CommonHelper.fertilizer(world, block, new BlockPos(xCoord, yCoord, zCoord), 12, player);
                            if (!ConfigHandler.blockBreakParticles)
                                continue;
                            this.world.playEvent(2005, new BlockPos(xCoord, yCoord, zCoord), 6 + this.world.rand.nextInt(4));
                            this.world.playSound(null, (double) x, (double) y, (double) z, net.minecraft.util.SoundEvent.REGISTRY.getObject(new net.minecraft.util.ResourceLocation("botania:agricarnation")), net.minecraft.util.SoundCategory.BLOCKS, 0.01f, 0.5f + (float) Math.random() * 0.5f);
                            continue;
                        }
                        if (world.isRemote || !BlockListAB.blockFreyrLiana.canPlaceBlockAt(world, new BlockPos(xCoord, yCoord - 1, zCoord)))
                            continue;
                        Vec3d vec = new Vec3d((double) xCoord, (double) yCoord, (double) zCoord);
                        int distant = (int) vec.distanceTo(new Vec3d((double) x, (double) y, (double) z));
                        --yCoord;
                        if (this.world.rand.nextInt(distant + 1) != 0) continue;
                        while (yCoord > 0 && (block = this.world.getBlockState(new BlockPos(xCoord, yCoord, zCoord)).getBlock()).isAir(this.world.getBlockState(new BlockPos(xCoord, yCoord, zCoord)), world, new BlockPos(xCoord, yCoord, zCoord))) {
                            if (world.rand.nextInt(4) < 3) {
                                CommonHelper.setBlock(world, BlockListAB.blockFreyrLiana, 0, new BlockPos(xCoord, yCoord, zCoord), player, false);
                            } else {
                                CommonHelper.setBlock(world, BlockListAB.blockLuminousFreyrLiana, 0, new BlockPos(xCoord, yCoord, zCoord), player, false);
                            }
                            this.world.playEvent(2001, new BlockPos(xCoord, yCoord, zCoord), Block.getIdFromBlock(BlockListAB.blockFreyrLiana));
                            --yCoord;
                        }
                    }
                }
            }
            this.setDead();
        }
    }

    @SideOnly(Side.CLIENT)
    public void setVelocity(double x, double y, double z) {
        this.motionX = x;
        this.motionY = y;
        this.motionZ = z;
    }

    protected float getGravityVelocity() {
        return 0.0f;
    }

    public void setDead() {
        if (this.world.isRemote) {
            float m = 0.175f;
            for (int i = 0; i < 32; ++i) {
                float mx = (float) (Math.random() - 0.5) * m;
                float my = (float) (Math.random() - 0.5) * m;
                float mz = (float) (Math.random() - 0.5) * m;
                Color color = ClientHelper.getCorporeaRuneColor((int) this.posX, (int) this.posY, (int) this.posZ, 3);
                Botania.proxy.wispFX(this.posX, this.posY, this.posZ, (float) color.getRed() / 255.0f, (float) color.getGreen() / 255.0f, (float) color.getBlue() / 255.0f, (float) ((double) 0.2f + Math.random() * (double) 0.12f), mx, my, mz, 2.0f);
            }
        }
        super.setDead();
    }

    public void readFromNBT(NBTTagCompound nbtt) {
        super.readFromNBT(nbtt);
        this.ticksExisted = nbtt.getInteger("ticks");
        this.setAttacker(nbtt.getString("attacker"));
    }

    public NBTTagCompound writeToNBT(NBTTagCompound nbtt) {
        super.writeToNBT(nbtt);
        nbtt.setInteger("ticks", this.ticksExisted);
        nbtt.setString("attacker", this.getAttacker());
        return nbtt;
    }
}
