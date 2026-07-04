package ab.common.block.tile;

import ab.client.core.ClientHelper;
import ab.common.core.handler.ConfigABHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.botania.api.mana.IManaPool;
import vazkii.botania.api.mana.spark.ISparkAttachable;
import vazkii.botania.api.mana.spark.ISparkEntity;
import vazkii.botania.common.Botania;
import vazkii.botania.common.block.tile.TileMod;

import java.awt.Color;
import java.util.List;

public class TileManaContainer extends TileMod implements IManaPool, ISparkAttachable, net.minecraft.util.ITickable {
    protected int mana = 0;
    int knownMana = -1;

    @Override
    public boolean shouldRefresh(net.minecraft.world.World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
        return oldState.getBlock() != newState.getBlock();
    }

    @Override
    public void update() {
        if (this.getWorld().isRemote) {
            double particleChance = 1.0 - (double) (this.getCurrentMana() / this.getMaxMana()) * 0.1;
            Color color = new Color(50943);
            if (Math.random() > particleChance) {
                Botania.proxy.wispFX(this.getPos().getX() + 0.5f + (Math.random() - 0.5) * 0.4f, this.getPos().getY() + 0.81 + Math.random() * 0.05, this.getPos().getZ() + 0.5f + (Math.random() - 0.5) * 0.4f, (float) color.getRed() / 255.0f, (float) color.getGreen() / 255.0f, (float) color.getBlue() / 255.0f, (float) Math.random() / 4.2f, (float) (-Math.random()) / 50.0f, 2.0f);
            }
        }
    }

    public void onWanded(EntityPlayer player, ItemStack wand) {
        if (player == null) {
            return;
        }
        if (!this.getWorld().isRemote) {
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            this.writePacketNBT(nbttagcompound);
            nbttagcompound.setInteger("knownMana", this.getCurrentMana());
            if (player instanceof EntityPlayerMP) {
                ((EntityPlayerMP) player).connection.sendPacket(new SPacketUpdateTileEntity(this.getPos(), this.getBlockMetadata(), nbttagcompound));
            }
        }
        this.getWorld().playSound(null, player.posX, player.posY, player.posZ,
                net.minecraft.util.SoundEvent.REGISTRY.getObject(new net.minecraft.util.ResourceLocation("botania", "ding")),
                net.minecraft.util.SoundCategory.PLAYERS, 0.11f, 1.0f);
    }

    @SideOnly(Side.CLIENT)
    public void renderHUD(Minecraft mc, ScaledResolution res) {
        IBlockState state = this.getWorld().getBlockState(this.getPos());
        int meta = state.getBlock().getMetaFromState(state);
        String name = net.minecraft.client.resources.I18n.format("ab.manaContainer." + meta + ".hud");
        int color = 3205841;
        ClientHelper.drawPoolManaHUD(res, name, this.knownMana, this.getMaxMana(), color);
    }

    @Override
    public int getCurrentMana() {
        return this.mana;
    }

    @Override
    public boolean canRecieveManaFromBursts() {
        return true;
    }

    public int getMaxMana() {
        IBlockState state = this.getWorld().getBlockState(this.getPos());
        int meta = state.getBlock().getMetaFromState(state);
        return ConfigABHandler.maxContainerMana[Math.min(Math.max(meta, 0), ConfigABHandler.maxContainerMana.length - 1)];
    }

    @Override
    public boolean isFull() {
        return this.mana == this.getMaxMana();
    }

    @Override
    public void recieveMana(int mana) {
        this.mana = Math.max(0, Math.min(this.getCurrentMana() + mana, this.getMaxMana()));
    }

    @Override
    public void writePacketNBT(NBTTagCompound nbtt) {
        nbtt.setInteger("mana", this.mana);
    }

    @Override
    public void readPacketNBT(NBTTagCompound nbtt) {
        this.mana = nbtt.getInteger("mana");
        if (nbtt.hasKey("knownMana")) {
            this.knownMana = nbtt.getInteger("knownMana");
        }
    }

    @Override
    public boolean areIncomingTranfersDone() {
        return false;
    }

    @Override
    public void attachSpark(ISparkEntity entity) {
    }

    @Override
    public boolean canAttachSpark(ItemStack arg0) {
        return true;
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
    public int getAvailableSpaceForMana() {
        return Math.max(0, this.getMaxMana() - this.getCurrentMana());
    }

    @Override
    public boolean isOutputtingPower() {
        return false;
    }

    @Override
    public EnumDyeColor getColor() {
        return EnumDyeColor.WHITE;
    }

    @Override
    public void setColor(EnumDyeColor color) {
    }
}
