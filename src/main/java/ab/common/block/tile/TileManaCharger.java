package ab.common.block.tile;

import ab.api.IRenderHud;
import ab.client.core.ClientHelper;
import ab.common.core.handler.ConfigABHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import vazkii.botania.api.internal.VanillaPacketDispatcher;
import vazkii.botania.api.mana.IManaItem;
import vazkii.botania.api.mana.spark.ISparkAttachable;
import vazkii.botania.api.mana.spark.ISparkEntity;
import vazkii.botania.api.wand.IWandBindable;
import vazkii.botania.common.block.ModBlocks;

public class TileManaCharger extends TileInventory implements ISidedInventory, IRenderHud, IWandBindable, net.minecraft.util.ITickable {
    private static final int MANA_SPEED = 11240;
    public boolean requestUpdate;
    int clientMana = -1;
    int receiverPosX = -1;
    int receiverPosY = -1;
    int receiverPosZ = -1;
    public int[] clientTick = new int[]{0, 0, 3, 12, 6};

    @Override
    public void update() {
        ISparkAttachable receiver;
        boolean hasUpdate = false;
        if (!this.getWorld().isRemote && this.requestUpdate) {
            VanillaPacketDispatcher.dispatchTEToNearbyPlayers(this.getWorld(), this.getPos());
        }
        if ((receiver = this.getReceiver()) == null) {
            return;
        }
        for (int i = 0; i < this.getSizeInventory(); i++) {
            ItemStack stack = this.getStackInSlot(i);
            if (stack.isEmpty() || !(stack.getItem() instanceof IManaItem)) continue;
            IManaItem mana = (IManaItem) stack.getItem();
            if (i == 0) {
                if (mana.getMana(stack) <= 0 || receiver.isFull() || !mana.canExportManaToPool(stack, (TileEntity) receiver))
                    continue;
                if (!this.getWorld().isRemote) {
                    int availableMana = receiver.getAvailableSpaceForMana();
                    int manaVal = Math.min(Math.min(mana.getMaxMana(stack) / 256, 11240) * 3, Math.min(availableMana, mana.getMana(stack)));
                    mana.addMana(stack, -manaVal);
                    receiver.recieveMana(manaVal);
                    if (this.getWorld().getTotalWorldTime() % 15L == 0L) {
                        hasUpdate = true;
                    }
                } else {
                    this.clientTick[i]++;
                }
                continue;
            }
            if (receiver.getCurrentMana() <= 0 || mana.getMana(stack) >= mana.getMaxMana(stack) || !mana.canReceiveManaFromPool(stack, (TileEntity) receiver))
                continue;
            if (!this.getWorld().isRemote) {
                int manaVal = Math.min(Math.min(mana.getMaxMana(stack) / 256, 11240), Math.min(receiver.getCurrentMana(), mana.getMaxMana(stack) - mana.getMana(stack)));
                mana.addMana(stack, manaVal);
                receiver.recieveMana(-manaVal);
                if (this.getWorld().getTotalWorldTime() % 15L == 0L) {
                    hasUpdate = true;
                }
            } else if (ConfigABHandler.useManaChargerAnimation) {
                this.clientTick[i]++;
            }
        }
        this.requestUpdate = hasUpdate;
    }

    public ISparkAttachable getReceiver() {
        TileEntity tile;
        ISparkAttachable receiver = null;
        if (this.getWorld() != null && this.receiverPosY != -1 && (tile = this.getWorld().getTileEntity(new BlockPos(this.receiverPosX, this.receiverPosY, this.receiverPosZ))) != null && tile instanceof ISparkAttachable) {
            receiver = (ISparkAttachable) tile;
        }
        if (receiver == null) {
            this.receiverPosX = -1;
            this.receiverPosY = -1;
            this.receiverPosZ = -1;
        }
        return receiver;
    }

    @Override
    public boolean canSelect(EntityPlayer player, ItemStack wand, BlockPos pos, EnumFacing side) {
        return true;
    }

    @Override
    public boolean bindTo(EntityPlayer player, ItemStack wand, BlockPos pos, EnumFacing side) {
        boolean isFar = Math.abs(this.getPos().getX() - pos.getX()) >= 10
                || Math.abs(this.getPos().getY() - pos.getY()) >= 10
                || Math.abs(this.getPos().getZ() - pos.getZ()) >= 10;
        if (isFar) {
            return false;
        }
        TileEntity tile = this.getWorld().getTileEntity(pos);
        if (tile instanceof ISparkAttachable && ((ISparkAttachable) tile).canRecieveManaFromBursts()) {
            if (!this.getWorld().isRemote) {
                this.receiverPosX = pos.getX();
                this.receiverPosY = pos.getY();
                this.receiverPosZ = pos.getZ();
                VanillaPacketDispatcher.dispatchTEToNearbyPlayers(this.getWorld(), this.getPos());
            }
            return true;
        }
        return false;
    }

    @Override
    public BlockPos getBinding() {
        ISparkAttachable receiver = this.getReceiver();
        if (receiver == null) {
            return null;
        }
        TileEntity tile = (TileEntity) receiver;
        return tile.getPos();
    }

    public static float getManaPercent(ItemStack stack) {
        if (!(stack.getItem() instanceof IManaItem)) {
            return 0.0f;
        }
        IManaItem mana = (IManaItem) stack.getItem();
        return (float) mana.getMana(stack) / ((float) mana.getMaxMana(stack) / 100.0f);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderHud(Minecraft mc, ScaledResolution res) {
        int xc = res.getScaledWidth() / 2;
        int yc = res.getScaledHeight() / 2;
        int radius = 42;
        int amt = 0;
        for (int i = 0; i < this.getSizeInventory(); i++) {
            if (this.getStackInSlot(i).isEmpty()) continue;
            amt++;
        }
        float angle = -90.0f;
        if (amt >= 0) {
            for (int i = 0; i < this.getSizeInventory(); i++) {
                ItemStack stack = this.getStackInSlot(i);
                if (stack.isEmpty()) continue;
                float anglePer = 360.0f / (float) amt;
                double xPos = xc + Math.cos(angle * Math.PI / 180.0) * radius - 8.0;
                double yPos = yc + Math.sin(angle * Math.PI / 180.0) * radius - 8.0;
                net.minecraft.client.renderer.RenderHelper.enableGUIStandardItemLighting();
                GL11.glPushMatrix();
                GL11.glEnable(3042);
                GL11.glBlendFunc(770, 771);
                GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                GL11.glTranslated(xPos, yPos, 0.0);
                vazkii.botania.client.core.helper.RenderHelper.renderProgressPie(0, 0, TileManaCharger.getManaPercent(stack) / 100.0f, stack);
                if (i == 0) {
                    GL11.glScalef(0.75f, 0.75f, 0.75f);
                    GL11.glTranslated(11.0, 10.0, 0.0);
                    net.minecraft.client.renderer.RenderHelper.enableGUIStandardItemLighting();
                    mc.getRenderItem().renderItemAndEffectIntoGUI(new ItemStack(ModBlocks.pool), 0, 0);
                }
                GL11.glDisable(2896);
                GL11.glDisable(3042);
                GL11.glPopMatrix();
                net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
                angle += anglePer;
            }
        }
    }

    public void onWanded(EntityPlayer player, ItemStack wand) {
        ISparkAttachable reciever = this.getReceiver();
        if (player == null) {
            return;
        }
        if (this.getWorld().isRemote && reciever != null) {
            this.clientMana = reciever.getCurrentMana();
        }
        this.getWorld().playSound(null, player.posX, player.posY, player.posZ,
                net.minecraft.util.SoundEvent.REGISTRY.getObject(new net.minecraft.util.ResourceLocation("botania", "ding")),
                net.minecraft.util.SoundCategory.PLAYERS, 0.11f, 1.0f);
    }

    @SideOnly(Side.CLIENT)
    public void renderHUD(Minecraft mc, ScaledResolution res) {
        ISparkAttachable reciever = this.getReceiver();
        if (reciever != null) {
            String name = net.minecraft.client.resources.I18n.format("ab.manaCharger.wandHud");
            TileEntity receiverTile = (TileEntity) reciever;
            BlockPos rPos = receiverTile.getPos();
            ItemStack recieverStack = new ItemStack(this.getWorld().getBlockState(rPos).getBlock(), 1, receiverTile.getBlockMetadata());
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            if (!recieverStack.isEmpty() && recieverStack.getItem() != null) {
                String stackName = recieverStack.getDisplayName();
                int width = 16 + mc.fontRenderer.getStringWidth(stackName) / 2;
                int x = res.getScaledWidth() / 2 - width;
                int y = res.getScaledHeight() / 2 + 48;
                mc.fontRenderer.drawString(stackName, x + 20, y + 5, 0xF4F4F4);
                net.minecraft.client.renderer.RenderHelper.enableGUIStandardItemLighting();
                mc.getRenderItem().renderItemAndEffectIntoGUI(recieverStack, x, y);
                net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
            }
            GL11.glDisable(2896);
            GL11.glDisable(3042);
            ClientHelper.drawPoolManaHUD(res, name, this.clientMana, reciever.getAvailableSpaceForMana() + reciever.getCurrentMana(), 12172206);
        }
    }

    @Override
    public int getSizeInventory() {
        return 5;
    }

    @Override
    public int getInventoryStackLimit() {
        return 1;
    }

    @Override
    public String getName() {
        return "ab.manaCharger";
    }

    @Override
    public void writePacketNBT(NBTTagCompound nbtt) {
        super.writePacketNBT(nbtt);
        nbtt.setInteger("bindingX", this.receiverPosX);
        nbtt.setInteger("bindingY", this.receiverPosY);
        nbtt.setInteger("bindingZ", this.receiverPosZ);
        nbtt.setBoolean("requestUpdate", this.requestUpdate);
    }

    @Override
    public void readPacketNBT(NBTTagCompound nbtt) {
        super.readPacketNBT(nbtt);
        this.receiverPosX = nbtt.getInteger("bindingX");
        this.receiverPosY = nbtt.getInteger("bindingY");
        this.receiverPosZ = nbtt.getInteger("bindingZ");
        this.requestUpdate = nbtt.getBoolean("requestUpdate");
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        return new int[]{1, 2, 3, 4};
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack stack, EnumFacing side) {
        boolean isManaItem = false;
        if (!stack.isEmpty() && stack.getItem() instanceof IManaItem) {
            IManaItem mana = (IManaItem) stack.getItem();
            ISparkAttachable receiver = this.getReceiver();
            if (receiver == null) {
                return false;
            }
            isManaItem = mana.getMana(stack) < mana.getMaxMana(stack) && mana.canReceiveManaFromPool(stack, (TileEntity) receiver);
        }
        return side == EnumFacing.UP && slot != 0 && isManaItem;
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, EnumFacing side) {
        boolean isManaItem = false;
        if (!stack.isEmpty() && stack.getItem() instanceof IManaItem) {
            IManaItem mana = (IManaItem) stack.getItem();
            ISparkAttachable receiver = this.getReceiver();
            if (receiver == null) {
                return false;
            }
            isManaItem = mana.getMana(stack) == mana.getMaxMana(stack);
        }
        return side == EnumFacing.DOWN && slot != 0 && isManaItem;
    }
}
