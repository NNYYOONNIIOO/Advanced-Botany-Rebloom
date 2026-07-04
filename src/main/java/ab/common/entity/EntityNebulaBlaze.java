package ab.common.entity;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockLeaves;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import vazkii.botania.common.Botania;

public class EntityNebulaBlaze extends EntityThrowable {
    private static final DataParameter<String> ATTACKER = EntityDataManager.createKey(EntityNebulaBlaze.class, DataSerializers.STRING);

    public EntityNebulaBlaze(World world) {
        super(world);
    }

    public EntityNebulaBlaze(World world, EntityPlayer e) {
        super(world, e);
    }

    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(ATTACKER, "");
    }

    public void onUpdate() {
        if (this.ticksExisted >= 240) {
            this.setDead();
        }
        double posX = this.posX;
        double posY = this.posY;
        double posZ = this.posZ;
        this.update();
        super.onUpdate();
        AxisAlignedBB axis = new AxisAlignedBB(this.posX, this.posY, this.posZ, this.posX + 1.0, this.posY + 1.0, this.posZ + 1.0).grow(3.75, 3.75, 3.75);
        List<EntityLivingBase> livs = this.world.getEntitiesWithinAABB(EntityLivingBase.class, axis);
        for (EntityLivingBase liv : livs) {
            double d3;
            if (liv instanceof EntityPlayer || liv.getHealth() <= 0.0f) continue;
            double d1 = liv.posX - this.posX;
            double d2 = liv.posY - this.posY;
            double d4 = Math.sqrt(d1 * d1 + d2 * d2 + (d3 = liv.posZ - this.posZ) * d3);
            if (d4 < 1.0) {
                d4 = 1.0;
            }
            double d5 = 0.5 + 0.5 / Math.max(1.0, d4);
            this.motionX = d1 / d4 * d5 * 0.325 + liv.motionX * 0.85f;
            this.motionY = d2 / d4 * d5 * 0.325 + liv.motionY * 0.85f;
            this.motionZ = d3 / d4 * d5 * 0.325 + liv.motionZ * 0.85f;
            break;
        }
        if (this.world.isRemote) {
            for (int i = 0; i < 12; ++i) {
                Botania.proxy.sparkleFX(posX + (Math.random() - 0.5) * 0.15f, posY + (Math.random() - 0.5) * 0.15f, posZ + (Math.random() - 0.5) * 0.15f, 1.0f - (float) (Math.random() / 5.0), 0.0f + (float) (Math.random() / 5.0), 1.0f - (float) (Math.random() / 5.0), 1.2f * (float) (Math.random() - 0.5), 3);
            }
        }
    }

    public void update() {
        String attacker = this.getAttacker();
        AxisAlignedBB axis = this.getEntityBoundingBox().grow(1.5, 1.5, 1.5);
        List<EntityLivingBase> entities = this.world.getEntitiesWithinAABB(EntityLivingBase.class, axis);
        EntityLivingBase closest = null;
        double closestDist = Double.MAX_VALUE;
        for (EntityLivingBase living : entities) {
            if (living instanceof EntityPlayer && (((EntityPlayer) living).getName().equals(attacker) || net.minecraftforge.fml.common.FMLCommonHandler.instance().getMinecraftServerInstance() != null && !net.minecraftforge.fml.common.FMLCommonHandler.instance().getMinecraftServerInstance().isPVPEnabled()))
                continue;
            if (living.getHealth() <= 0.0f) continue;
            double dist = living.getDistanceSq(this);
            if (dist < closestDist) {
                closestDist = dist;
                closest = living;
            }
        }
        if (closest != null && !this.world.isRemote) {
            EntityPlayer player = closest.world.getPlayerEntityByName(attacker);
            closest.attackEntityFrom(player == null ? DamageSource.GENERIC : DamageSource.causePlayerDamage(player), 18.0f);
            this.setDead();
        }
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
        return 0.0f;
    }

    protected void onImpact(RayTraceResult mov) {
        if (mov.typeOfHit == RayTraceResult.Type.BLOCK) {
            Block block = this.world.getBlockState(mov.getBlockPos()).getBlock();
            if (block instanceof BlockBush || block instanceof BlockLeaves) {
                return;
            }
            this.setDead();
        }
    }
}
