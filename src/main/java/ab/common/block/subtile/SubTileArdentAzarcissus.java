package ab.common.block.subtile;

import ab.common.block.tile.TileGameBoard;
import ab.common.lib.register.RecipeListAB;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import vazkii.botania.api.lexicon.LexiconEntry;
import vazkii.botania.api.subtile.RadiusDescriptor;
import vazkii.botania.api.subtile.SubTileGenerating;
import vazkii.botania.common.item.block.ItemBlockSpecialFlower;

public class SubTileArdentAzarcissus extends SubTileGenerating {
    public static final String playerName = "ArdentAzarcissus#21sda2gaj91*21df#111sfq3jrns@#";
    public static final ItemStack flowerStack = ItemBlockSpecialFlower.ofType("ardentAzarcissus");
    int cooldown;
    int workMana = 320;

    @Override
    public void onUpdate() {
        super.onUpdate();
        World world = this.supertile.getWorld();
        int posX = this.supertile.getPos().getX();
        int posY = this.supertile.getPos().getY();
        int posZ = this.supertile.getPos().getZ();
        if (world.isRemote) {
            return;
        }
        if (this.cooldown > 0) {
            this.cooldown--;
            return;
        }
        if (this.mana == this.getMaxMana()) {
            return;
        }
        boolean needSync = false;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int x = posX + i;
                int z = posZ + j;
                TileEntity tile = world.getTileEntity(new BlockPos(x, posY, z));
                if (tile == null || !(tile instanceof TileGameBoard)) continue;
                TileGameBoard board = (TileGameBoard) tile;
                if (!board.isSingleGame) continue;
                if (!board.hasGame()) {
                    board.setPlayer(playerName, true);
                    this.cooldown = 120;
                } else {
                    if (!board.playersName[0].equals(playerName)) {
                        board.playersName[0] = playerName;
                    }
                    if (!board.isCustomGame) {
                        board.isCustomGame = true;
                    }
                    if (board.endGameTick == 0) {
                        int winCount = board.slotChance[0] + board.slotChance[1] - (board.slotChance[2] + board.slotChance[3]);
                        if (winCount > 0) {
                            this.mana = Math.min(this.getMaxMana(), this.mana + this.workMana * winCount);
                            needSync = true;
                        }
                        board.finishGame(false);
                    } else {
                        board.dropDice(playerName);
                    }
                    this.cooldown = 120;
                }
                board.changeCustomStack(flowerStack);
            }
        }
        if (needSync) {
            this.sync();
        }
    }

    @Override
    public RadiusDescriptor getRadius() {
        return new RadiusDescriptor.Square(this.supertile.getPos(), 1);
    }

    @Override
    public int getMaxMana() {
        return 16000;
    }

    @Override
    public int getColor() {
        return 14628246;
    }

    @Override
    public LexiconEntry getEntry() {
        return RecipeListAB.azartFlower;
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
