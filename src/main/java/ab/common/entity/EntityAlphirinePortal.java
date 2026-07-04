package ab.common.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;

public class EntityAlphirinePortal extends Entity {
    private static final DataParameter<ItemStack> STACK = EntityDataManager.createKey(EntityAlphirinePortal.class, DataSerializers.ITEM_STACK);
    private static final ItemStack AIR_STACK = new ItemStack(Blocks.AIR);

    public EntityAlphirinePortal(World world) {
        super(world);
        this.dataManager.register(STACK, AIR_STACK.copy());
    }

    protected void entityInit() {
    }

    public void onUpdate() {
        super.onUpdate();
        if (this.ticksExisted >= 40) {
            if (this.getStack().isEmpty()) {
                this.setDead();
            }
            if (!this.world.isRemote) {
                EntityItem itemResult = new EntityItem(this.world, this.posX, this.posY, this.posZ, this.getStack());
                this.world.spawnEntity(itemResult);
                this.setDead();
            }
        }
    }

    public void readEntityFromNBT(NBTTagCompound nbtt) {
        this.ticksExisted = nbtt.getInteger("portalTick");
        NBTTagCompound stackCmp = nbtt.getCompoundTag("dropStack");
        ItemStack stack = new ItemStack(stackCmp);
        this.setStack(stack);
    }

    public void writeEntityToNBT(NBTTagCompound nbtt) {
        nbtt.setInteger("portalTick", this.ticksExisted);
        ItemStack stack = this.getStack();
        NBTTagCompound stackNbt = new NBTTagCompound();
        if (!stack.isEmpty()) {
            stack.writeToNBT(stackNbt);
        }
        nbtt.setTag("dropStack", stackNbt);
    }

    public ItemStack getStack() {
        return this.dataManager.get(STACK);
    }

    public void setStack(ItemStack stack) {
        this.dataManager.set(STACK, stack);
    }
}
