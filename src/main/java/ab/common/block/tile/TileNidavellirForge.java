package ab.common.block.tile;

import ab.api.AdvancedBotanyAPI;
import ab.api.recipe.RecipeAdvancedPlate;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.botania.api.internal.VanillaPacketDispatcher;
import vazkii.botania.api.mana.IManaPool;
import vazkii.botania.api.mana.spark.ISparkAttachable;
import vazkii.botania.api.mana.spark.ISparkEntity;
import vazkii.botania.api.mana.spark.SparkHelper;
import vazkii.botania.client.core.handler.ClientTickHandler;
import vazkii.botania.common.Botania;

import java.awt.Color;
import java.util.List;
import java.util.Random;

public class TileNidavellirForge extends TileInventory implements ISparkAttachable, ISidedInventory, net.minecraft.util.ITickable {
    private int mana;
    public int manaToGet;
    private RecipeAdvancedPlate currentRecipe;
    private int recipeID;
    public boolean requestUpdate;

    @Override
    public void update() {
        if (!this.getWorld().isRemote) {
            this.updateServer();
        } else {
            this.updateClient();
        }
        ISparkEntity spark = this.getAttachedSpark();
        if (spark != null) {
            List<ISparkEntity> sparkEntities = SparkHelper.getSparksAround(this.getWorld(), this.getPos().getX() + 0.5, this.getPos().getY() + 0.5, this.getPos().getZ() + 0.5);
            for (ISparkEntity otherSpark : sparkEntities) {
                if (spark == otherSpark || otherSpark.getAttachedTile() == null || !(otherSpark.getAttachedTile() instanceof IManaPool))
                    continue;
                otherSpark.registerTransfer(spark);
            }
        }
    }

    private void updateServer() {
        if (this.requestUpdate) {
            VanillaPacketDispatcher.dispatchTEToNearbyPlayers(this.getWorld(), this.getPos());
        }
        boolean hasUpdate = false;
        List<EntityItem> items = this.getWorld().getEntitiesWithinAABB(EntityItem.class,
                new AxisAlignedBB(this.getPos()));
        for (EntityItem item : items) {
            if (item.getItem().isEmpty() || item.isDead) continue;
            ItemStack stack = item.getItem();
            int splitCount = this.addItemStack(stack);
            stack.shrink(splitCount);
            if (stack.getCount() <= 0) {
                item.setDead();
            }
            if (splitCount <= 0) break;
            hasUpdate = true;
            break;
        }
        int wasManaToGet = this.manaToGet;
        boolean hasCraft = false;
        int recipeID = 0;
        for (RecipeAdvancedPlate recipe : AdvancedBotanyAPI.advancedPlateRecipes) {
            if (recipe.matches(this)) {
                this.recipeID = recipeID;
                if (this.mana > 0 && this.isFull()) {
                    ItemStack output = recipe.getOutput().copy();
                    this.recieveMana(-recipe.getManaUsage());
                    this.manaToGet = 0;
                    for (int i = 1; i < this.getSizeInventory(); i++) {
                        if (this.getStackInSlot(i).getCount() > 1) {
                            this.getStackInSlot(i).shrink(1);
                        } else {
                            this.setInventorySlotContents(i, ItemStack.EMPTY);
                        }
                    }
                    if (!this.getStackInSlot(0).isEmpty()) {
                        this.getStackInSlot(0).grow(1);
                    } else {
                        this.setInventorySlotContents(0, output);
                    }
                    hasUpdate = true;
                    this.getWorld().playSound(null, this.getPos().getX(), this.getPos().getY(), this.getPos().getZ(),
                            net.minecraft.util.SoundEvent.REGISTRY.getObject(new net.minecraft.util.ResourceLocation("botania", "terrasteelCraft")),
                            net.minecraft.util.SoundCategory.BLOCKS, 1.0f, 2.0f);
                    break;
                }
                if (this.getStackInSlot(0).isEmpty()) {
                    this.manaToGet = recipe.getManaUsage();
                    this.currentRecipe = recipe;
                    hasCraft = true;
                    break;
                }
                if (TileNidavellirForge.isItemEqual(recipe.getOutput(), this.getStackInSlot(0)) && this.getStackInSlot(0).getCount() < recipe.getOutput().getMaxStackSize()) {
                    this.manaToGet = recipe.getManaUsage();
                    this.currentRecipe = recipe;
                    hasCraft = true;
                    break;
                }
            }
            recipeID++;
        }
        if (!hasCraft) {
            this.currentRecipe = null;
            this.mana = 0;
            this.manaToGet = 0;
        }
        if (this.manaToGet != wasManaToGet) {
            hasUpdate = true;
        }
        this.requestUpdate = hasUpdate;
    }

    @SideOnly(Side.CLIENT)
    private void updateClient() {
        if (this.mana > 0) {
            double worldTime = (float) ClientTickHandler.ticksInGame + ClientTickHandler.partialTicks;
            float indetY = (float) (Math.sin((worldTime += new Random(this.getPos().getX() ^ this.getPos().getY() ^ this.getPos().getZ()).nextInt(360)) / 18.0) / 24.0);
            float ticks = 100.0f * (float) this.getCurrentMana() / (float) this.manaToGet;
            int totalSpiritCount = 3;
            double tickIncrement = 360.0 / (double) totalSpiritCount;
            int speed = 5;
            double wticks = (double) (ticks * (float) speed) - tickIncrement;
            double r = Math.sin((double) (ticks - 100.0f) / 10.0) * 0.5;
            double g = Math.sin(wticks * Math.PI / 180.0 * 0.55);
            float size = 0.4f;
            for (int i = 0; i < totalSpiritCount; i++) {
                double x = this.getPos().getX() + Math.sin(wticks * Math.PI / 180.0) * r + 0.5;
                double y = this.getPos().getY() - indetY + 0.85 + Math.abs(r) * 0.7;
                double z = this.getPos().getZ() + Math.cos(wticks * Math.PI / 180.0) * r + 0.5;
                wticks += tickIncrement;
                int color = 2411744;
                if (this.currentRecipe != null) {
                    color = this.currentRecipe.getColor();
                }
                float[] hsb = Color.RGBtoHSB(color & 0xFF, color >> 8 & 0xFF, color >> 16 & 0xFF, null);
                int color1 = Color.HSBtoRGB(hsb[0], hsb[1], ticks / 100.0f);
                float[] colorsfx = new float[]{(float) (color1 & 0xFF) / 255.0f, (float) (color1 >> 8 & 0xFF) / 255.0f, (float) (color1 >> 16 & 0xFF) / 255.0f};
                Botania.proxy.wispFX(x, y, z, colorsfx[0], colorsfx[1], colorsfx[2], 0.85f * size, (float) g * 0.05f, 0.25f);
                Botania.proxy.wispFX(x, y, z, colorsfx[0], colorsfx[1], colorsfx[2], (float) Math.random() * 0.1f + 0.1f * size, (float) (Math.random() - 0.5) * 0.05f, (float) (Math.random() - 0.5) * 0.05f, (float) (Math.random() - 0.5) * 0.05f, 0.9f);
                if (ticks != 100.0f) continue;
                for (int j = 0; j < 12; j++) {
                    Botania.proxy.wispFX(this.getPos().getX() + 0.5, this.getPos().getY() + 1.1 - indetY, this.getPos().getZ() + 0.5, colorsfx[0], colorsfx[1], colorsfx[2], (float) Math.random() * 0.15f + 0.15f * size, (float) (Math.random() - 0.5) * 0.125f * size, (float) (Math.random() - 0.5) * 0.125f * size, (float) (Math.random() - 0.5) * 0.125f * size, 0.8f);
                }
            }
        }
    }

    public static boolean isItemEqual(ItemStack stack, ItemStack stack1) {
        return stack.isItemEqual(stack1) && ItemStack.areItemStackTagsEqual(stack, stack1);
    }

    private int addItemStack(ItemStack stack) {
        for (int i = 1; i < this.getSizeInventory(); i++) {
            if (this.getStackInSlot(i).isEmpty()) {
                ItemStack stackToAdd = stack.copy();
                this.setInventorySlotContents(i, stackToAdd);
                return stack.getCount();
            }
            if (!TileNidavellirForge.isItemEqual(stack, this.getStackInSlot(i)) || this.getStackInSlot(i).getCount() >= stack.getMaxStackSize())
                continue;
            int count = Math.min(stack.getCount(), stack.getMaxStackSize() - this.getStackInSlot(i).getCount());
            this.getStackInSlot(i).grow(count);
            return count;
        }
        return 0;
    }

    @Override
    public void writePacketNBT(NBTTagCompound cmp) {
        super.writePacketNBT(cmp);
        cmp.setInteger("mana", this.mana);
        cmp.setInteger("manaToGet", this.manaToGet);
        cmp.setBoolean("requestUpdate", this.requestUpdate);
        cmp.setInteger("recipeID", this.currentRecipe == null ? -1 : this.recipeID);
    }

    @Override
    public void readPacketNBT(NBTTagCompound cmp) {
        super.readPacketNBT(cmp);
        this.mana = cmp.getInteger("mana");
        this.manaToGet = cmp.getInteger("manaToGet");
        this.requestUpdate = cmp.getBoolean("requestUpdate");
        int recipeID = cmp.getInteger("recipeID");
        this.currentRecipe = recipeID == -1 ? null : AdvancedBotanyAPI.advancedPlateRecipes.get(recipeID);
    }

    @Override
    public int getSizeInventory() {
        return 4;
    }

    @Override
    public String getName() {
        return "tileAdvancedPlate";
    }

    public int getCurrentMana() {
        return this.mana;
    }

    public boolean isFull() {
        return this.mana >= this.manaToGet;
    }

    public void recieveMana(int mana) {
        this.mana = Math.min(this.mana + mana, this.manaToGet);
    }

    @Override
    public boolean canRecieveManaFromBursts() {
        return !this.isFull();
    }

    @Override
    public boolean canAttachSpark(ItemStack stack) {
        return true;
    }

    @Override
    public void attachSpark(ISparkEntity entity) {
    }

    @Override
    public ISparkEntity getAttachedSpark() {
        AxisAlignedBB box = new AxisAlignedBB(this.getPos().up());
        List<Entity> entities = this.getWorld().getEntitiesWithinAABB(Entity.class, box);
        for (Entity entity : entities) {
            if (entity instanceof ISparkEntity) {
                return (ISparkEntity) entity;
            }
        }
        return null;
    }

    @Override
    public boolean areIncomingTranfersDone() {
        return this.isFull();
    }

    @Override
    public int getAvailableSpaceForMana() {
        return Math.max(0, this.manaToGet - this.getCurrentMana());
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        return new int[]{0, 1, 2, 3};
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack stack, EnumFacing side) {
        for (int i = 1; i < this.getSizeInventory(); i++) {
            ItemStack slotStack = this.getStackInSlot(i);
            if (slotStack.isEmpty() || slotStack.getCount() != slotStack.getMaxStackSize() || !TileNidavellirForge.isItemEqual(stack, slotStack))
                continue;
            return false;
        }
        return side == EnumFacing.UP && slot != 0;
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, EnumFacing side) {
        return side == EnumFacing.DOWN && slot == 0 || side != EnumFacing.DOWN && side != EnumFacing.UP && slot != 0;
    }
}
