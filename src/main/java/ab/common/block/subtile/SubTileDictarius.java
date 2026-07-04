package ab.common.block.subtile;

import ab.common.core.handler.ConfigABHandler;
import ab.common.lib.register.RecipeListAB;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import vazkii.botania.api.lexicon.LexiconEntry;
import vazkii.botania.api.subtile.RadiusDescriptor;
import vazkii.botania.api.subtile.SubTileGenerating;
import vazkii.botania.common.block.tile.TileSpecialFlower;

import java.util.List;

public class SubTileDictarius extends SubTileGenerating {
    private static final int workMana = 480;
    int cooldown;

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (this.supertile.getWorld().isRemote) {
            return;
        }
        int posX = this.supertile.getPos().getX();
        int posY = this.supertile.getPos().getY();
        int posZ = this.supertile.getPos().getZ();
        World world = this.supertile.getWorld();
        if (world.getTotalWorldTime() % 1200L == 0L) {
            this.checkNearDictarius();
        }
        if (this.mana != this.getMaxMana() && this.cooldown == 0) {
            List<EntityLivingBase> livs = world.getEntitiesWithinAABB(EntityLivingBase.class,
                    new AxisAlignedBB(posX, posY, posZ, posX + 1, posY + 1, posZ + 1).grow(2.0, 1.0, 2.0));
            int workMana = 0;
            int villagers = 0;
            if (!livs.isEmpty()) {
                for (int i = 0; i < Math.min(livs.size(), 16); i++) {
                    EntityLivingBase liv = livs.get(i);
                    if (liv instanceof EntityPlayer) {
                        workMana += 480;
                        continue;
                    }
                    if (world.isRemote || !(liv instanceof EntityVillager)) continue;
                    workMana += 80;
                    if (villagers > 15 && world.rand.nextInt(100) <= 4) {
                        liv.setDead();
                    }
                    villagers++;
                }
            }
            if (workMana > 0) {
                this.cooldown = 200;
                workMana = (int) ((double) workMana * Math.random());
                this.mana = Math.min(this.getMaxMana(), this.mana + workMana);
                this.sync();
            }
        }
        if (this.cooldown > 0) {
            this.cooldown--;
        }
    }

    @Override
    public RadiusDescriptor getRadius() {
        return new RadiusDescriptor.Square(this.supertile.getPos(), 2);
    }

    public void checkNearDictarius() {
        int findedFlower = 0;
        for (int z = -4; z < 4; z++) {
            for (int x = -4; x < 4; x++) {
                for (int y = -4; y < 4; y++) {
                    BlockPos checkPos = this.supertile.getPos().add(x, y, z);
                    TileEntity tile = this.supertile.getWorld().getTileEntity(checkPos);
                    if (tile == null || !(tile instanceof TileSpecialFlower)) continue;
                    TileSpecialFlower tileFlower = (TileSpecialFlower) tile;
                    if (!tileFlower.subTileName.equals("dictarius")) continue;
                    if (findedFlower >= ConfigABHandler.maxDictariusCount) {
                        this.supertile.getWorld().playEvent(2001, this.supertile.getPos(), Block.getIdFromBlock(this.supertile.getBlockType()));
                        BlockPos belowPos = this.supertile.getPos().down();
                        if (this.supertile.getWorld().getBlockState(belowPos).isSideSolid(this.supertile.getWorld(), belowPos, EnumFacing.UP)) {
                            this.supertile.getWorld().setBlockState(this.supertile.getPos(), Blocks.DEADBUSH.getDefaultState(), 3);
                        } else {
                            this.supertile.getWorld().setBlockToAir(this.supertile.getPos());
                        }
                        return;
                    }
                    findedFlower++;
                }
            }
        }
    }

    @Override
    public int getMaxMana() {
        return 8000;
    }

    @Override
    public int getColor() {
        return 13815218;
    }

    @Override
    public LexiconEntry getEntry() {
        return RecipeListAB.dictarius;
    }

    @Override
    public void writeToPacketNBT(NBTTagCompound cmp) {
        super.writeToPacketNBT(cmp);
        cmp.setInteger("cooldown", this.cooldown);
    }

    @Override
    public void readFromPacketNBT(NBTTagCompound cmp) {
        super.readFromPacketNBT(cmp);
        this.cooldown = cmp.getInteger("cooldown");
    }
}
