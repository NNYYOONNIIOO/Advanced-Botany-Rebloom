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

public class EntitySword extends EntityThrowable {
    private static final DataParameter<Float> DAMAGE = EntityDataManager.createKey(EntitySword.class, DataSerializers.FLOAT);
    private static final DataParameter<String> ATTACKER = EntityDataManager.createKey(EntitySword.class, DataSerializers.STRING);

    public EntitySword(World world) {
        super(world);
    }

    public EntitySword(World world, EntityPlayer e) {
        super(world, e);
    }

    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(DAMAGE, 0.0f);
        this.dataManager.register(ATTACKER, "");
    }

    public void onUpdate() {
        super.onUpdate();
        this.update();
        if (this.ticksExisted < 20) {
            this.motionX *= 1.115f;
            this.motionY *= 1.115f;
            this.motionZ *= 1.115f;
        } else if (this.ticksExisted > 160) {
            this.setDead();
        }
        if (this.world.isRemote) {
            for (int i = 0; i < 12; ++i) {
                float r = this.world.rand.nextBoolean() ? 0.88235295f : 0.39607844f;
                float g = this.world.rand.nextBoolean() ? 0.2627451f : 0.81960785f;
                float b = this.world.rand.nextBoolean() ? 0.9411765f : 0.88235295f;
                Botania.proxy.sparkleFX(this.posX + (Math.random() - 0.5) * 0.25, this.posY + (Math.random() - 0.5) * 0.25, this.posZ + (Math.random() - 0.5) * 0.25, r + (float) (Math.random() / 4.0 - 0.125), g + (float) (Math.random() / 4.0 - 0.125), b + (float) (Math.random() / 4.0 - 0.125), 1.6f * (float) (Math.random() - 0.5), 2);
            }
        }
    }

    public void update() {
        String attacker = this.getAttacker();
        AxisAlignedBB axis = this.getEntityBoundingBox().grow(1.0, 1.0, 1.0);
        List<EntityLivingBase> entities = this.world.getEntitiesWithinAABB(EntityLivingBase.class, axis);
        for (EntityLivingBase living : entities) {
            if (living instanceof EntityPlayer && (((EntityPlayer) living).getName().equals(attacker) || net.minecraftforge.fml.common.FMLCommonHandler.instance().getMinecraftServerInstance() != null && !net.minecraftforge.fml.common.FMLCommonHandler.instance().getMinecraftServerInstance().isPVPEnabled()) || living.hurtTime != 0)
                continue;
            float damage = this.getDamage();
            if (this.world.isRemote) continue;
            EntityPlayer player = living.world.getPlayerEntityByName(attacker);
            living.attackEntityFrom(player == null ? DamageSource.GENERIC : DamageSource.causePlayerDamage(player), damage);
            this.setDead();
            break;
        }
    }

    public String getAttacker() {
        return this.dataManager.get(ATTACKER);
    }

    public void setAttacker(String str) {
        this.dataManager.set(ATTACKER, str);
    }

    public float getDamage() {
        return this.dataManager.get(DAMAGE);
    }

    public void setDamage(float damage) {
        this.dataManager.set(DAMAGE, damage);
    }

    @SideOnly(Side.CLIENT)
    public void setVelocity(double x, double y, double z) {
        this.motionX = x;
        this.motionY = y;
        this.motionZ = z;
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

    public void readFromNBT(NBTTagCompound nbtt) {
        super.readFromNBT(nbtt);
        this.ticksExisted = nbtt.getInteger("ticks");
        this.setDamage(nbtt.getInteger("disDamage"));
        this.setAttacker(nbtt.getString("attacker"));
    }

    public NBTTagCompound writeToNBT(NBTTagCompound nbtt) {
        super.writeToNBT(nbtt);
        nbtt.setInteger("ticks", this.ticksExisted);
        nbtt.setFloat("disDamage", this.getDamage());
        nbtt.setString("attacker", this.getAttacker());
        return nbtt;
    }

    protected float getGravityVelocity() {
        return 0.0f;
    }
}
