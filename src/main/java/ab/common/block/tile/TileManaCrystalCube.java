package ab.common.block.tile;

import ab.api.IRenderHud;
import ab.client.core.ClientHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.botania.api.mana.spark.ISparkAttachable;
import vazkii.botania.api.mana.spark.ISparkEntity;
import vazkii.botania.api.mana.spark.SparkHelper;
import vazkii.botania.common.block.tile.TileMod;

import java.util.List;

public class TileManaCrystalCube extends TileMod implements IRenderHud, net.minecraft.util.ITickable {
    int knownMana = -1;
    int knownMaxMana = -1;

    @Override
    public boolean shouldRefresh(net.minecraft.world.World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
        return oldState.getBlock() != newState.getBlock();
    }

    @Override
    public void update() {
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderHud(Minecraft mc, ScaledResolution res) {
        String name = net.minecraft.client.resources.I18n.format("ab.manaCrystalCube.hud");
        int color = 3205841;
        ClientHelper.drawPoolManaHUD(res, name, this.knownMana, this.knownMaxMana, color);
    }

    public int[] getManaAround() {
        int[] mana = new int[]{0, 0};
        List<ISparkEntity> allSparks = SparkHelper.getSparksAround(this.getWorld(), this.getPos().getX(), this.getPos().getY(), this.getPos().getZ());
        for (ISparkEntity spark : allSparks) {
            ISparkAttachable tileMana = spark.getAttachedTile();
            mana[1] += (tileMana.getCurrentMana() + tileMana.getAvailableSpaceForMana());
            mana[0] += tileMana.getCurrentMana();
        }
        return mana;
    }

    @Override
    public void readPacketNBT(NBTTagCompound nbtt) {
        if (nbtt.hasKey("knownMana")) {
            this.knownMana = nbtt.getInteger("knownMana");
        }
        if (nbtt.hasKey("knownMaxMana")) {
            this.knownMaxMana = nbtt.getInteger("knownMaxMana");
        }
    }

    @Override
    public void writePacketNBT(NBTTagCompound nbtt) {
    }
}
