package ab.common.block.tile;

import ab.api.IRenderHud;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.Style;
import net.minecraft.world.World;
import vazkii.botania.api.internal.VanillaPacketDispatcher;
import vazkii.botania.common.block.tile.TileMod;

import java.util.List;

public class TileGameBoard extends TileMod implements IRenderHud, net.minecraft.util.ITickable {
    public String[] playersName = new String[]{"", ""};
    public byte[] slotChance = new byte[]{0, 0, 0, 0};
    protected int botTick = -1;
    public int endGameTick = -1;
    protected boolean requestUpdate;
    public boolean isSingleGame = true;
    public boolean isCustomGame = false;
    public int[] clientTick = new int[]{0, 0, 0, 0};
    public static final ItemStack headRender = new ItemStack(Items.SKULL, 1, 3);
    private ItemStack customStack;

    @Override
    public void update() {
        if (this.botTick > 0) {
            this.botTick--;
        }
        if (this.endGameTick > 0) {
            this.endGameTick--;
        }
        if (!this.getWorld().isRemote) {
            this.updateServer();
        } else {
            this.updateAnimationTicks();
        }
    }

    public void updateAnimationTicks() {
        for (int i = 0; i < this.slotChance.length; i++) {
            if (this.slotChance[i] > 0) {
                this.clientTick[i]++;
            } else {
                this.clientTick[i] = 0;
            }
        }
    }

    protected void updateServer() {
        if (this.hasGame() && this.endGameTick == 0 && !this.isCustomGame) {
            this.finishGame();
        }
        if (this.requestUpdate) {
            VanillaPacketDispatcher.dispatchTEToNearbyPlayers(this.getWorld(), this.getPos());
        }
        if (this.getWorld().getTotalWorldTime() % 20L == 0L && this.hasFullDice() && this.endGameTick == -1) {
            this.endGameTick = 28;
        }
        boolean hasUpdate = false;
        if (this.isSingleGame) {
            if (this.botTick == 0 && this.hasGame()) {
                for (int i = 2; i < 4; i++) {
                    if (this.slotChance[i] != 0) continue;
                    this.slotChance[i] = (byte) (this.getWorld().rand.nextInt(6) + 1);
                    this.botTick = -1;
                    hasUpdate = true;
                    this.getWorld().playSound(null, this.getPos().getX(), this.getPos().getY(), this.getPos().getZ(),
                            net.minecraft.util.SoundEvent.REGISTRY.getObject(new net.minecraft.util.ResourceLocation("ab", "boardCube")),
                            net.minecraft.util.SoundCategory.BLOCKS, 0.6f, 1.0f);
                    break;
                }
            }
        } else if (this.botTick == 0 && this.hasGame()) {
            this.endGameTick = 0;
        }
        this.requestUpdate = hasUpdate;
    }

    public void setPlayer(String name, boolean isCustomGame) {
        this.isCustomGame = isCustomGame;
        if (this.isSingleGame) {
            this.playersName[0] = name;
            this.playersName[1] = "";
            this.requestUpdate = true;
            this.botTick = 8;
        } else {
            if (this.playersName[0].isEmpty()) {
                this.playersName[0] = name;
            } else if (!this.playersName[0].equals(name)) {
                this.playersName[1] = name;
            }
            this.requestUpdate = true;
        }
    }

    public void setPlayer(EntityPlayer player) {
        this.setPlayer(player.getName(), false);
    }

    public boolean dropDice(String name) {
        if (this.isSingleGame) {
            if (name.equals(this.playersName[0]) && this.botTick == -1) {
                boolean hasDrop = false;
                for (int i = 0; i < 2; i++) {
                    if (this.slotChance[i] != 0) continue;
                    hasDrop = true;
                    if (this.getWorld().isRemote) break;
                    this.slotChance[i] = (byte) (this.getWorld().rand.nextInt(6) + 1);
                    this.botTick = 18;
                    this.getWorld().playSound(null, this.getPos().getX(), this.getPos().getY(), this.getPos().getZ(),
                            net.minecraft.util.SoundEvent.REGISTRY.getObject(new net.minecraft.util.ResourceLocation("ab", "boardCube")),
                            net.minecraft.util.SoundCategory.BLOCKS, 0.6f, 1.0f);
                    this.requestUpdate = true;
                    break;
                }
                return hasDrop;
            }
        } else {
            for (int i = 0; i < this.playersName.length; i++) {
                if (!name.equals(this.playersName[i])) continue;
                boolean hasDrop = false;
                for (int j = i * 2; j < (i + 1) * 2; j++) {
                    if (this.slotChance[j] != 0) continue;
                    hasDrop = true;
                    if (this.getWorld().isRemote) break;
                    this.slotChance[j] = (byte) (this.getWorld().rand.nextInt(6) + 1);
                    this.botTick = this.playersName[1].isEmpty() ? 240 : 1200;
                    this.getWorld().playSound(null, this.getPos().getX(), this.getPos().getY(), this.getPos().getZ(),
                            net.minecraft.util.SoundEvent.REGISTRY.getObject(new net.minecraft.util.ResourceLocation("ab", "boardCube")),
                            net.minecraft.util.SoundCategory.BLOCKS, 0.6f, 1.0f);
                    this.requestUpdate = true;
                    break;
                }
                return hasDrop;
            }
        }
        return false;
    }

    public boolean dropDice(EntityPlayer player) {
        return this.dropDice(player.getName());
    }

    public boolean hasFullDice() {
        for (int i = 0; i < this.slotChance.length; i++) {
            if (this.slotChance[i] <= 0) {
                return false;
            }
        }
        return true;
    }

    public boolean hasGame() {
        if (this.isSingleGame) {
            return !this.playersName[0].isEmpty();
        }
        for (int i = 0; i < this.playersName.length; i++) {
            if (!this.playersName[i].isEmpty()) return true;
        }
        return false;
    }

    public void finishGame(boolean hasChatMessage) {
        if (this.getWorld().isRemote) {
            return;
        }
        if (!hasChatMessage) {
            this.resetGame();
            return;
        }
        if (!this.hasFullDice()) {
            this.sendNearMessage("ab.gameBoard.misc.notPlayer");
            this.resetGame();
            return;
        }
        String str = this.isSingleGame ? "" : ".mult";
        if (this.slotChance[0] + this.slotChance[1] > this.slotChance[2] + this.slotChance[3]) {
            this.sendNearMessage("ab.gameBoard.misc.0" + str, this.playersName[0]);
        } else if (this.slotChance[0] + this.slotChance[1] == this.slotChance[2] + this.slotChance[3]) {
            this.sendNearMessage("ab.gameBoard.misc.1" + str);
        } else {
            this.sendNearMessage("ab.gameBoard.misc.2" + str, this.playersName[this.isSingleGame ? 0 : 1]);
        }
        this.resetGame();
    }

    public void finishGame() {
        this.finishGame(true);
    }

    public void sendNearMessage(String text, Object... obj) {
        List<EntityPlayer> players = this.getWorld().getEntitiesWithinAABB(EntityPlayer.class,
                new AxisAlignedBB(this.getPos()).grow(3.5));
        for (EntityPlayer player : players) {
            if (player == null) continue;
            player.sendMessage(new TextComponentTranslation(text, obj).setStyle(new Style().setColor(TextFormatting.DARK_GREEN)));
        }
    }

    public boolean changeCustomStack(ItemStack stack) {
        if (!this.isCustomGame) {
            return false;
        }
        if (this.customStack == null || !ItemStack.areItemStacksEqual(this.customStack, stack)) {
            this.customStack = stack;
            this.requestUpdate = true;
            return true;
        }
        return false;
    }

    public void resetGame() {
        this.playersName[0] = "";
        this.playersName[1] = "";
        for (int i = 0; i < this.slotChance.length; i++) {
            this.slotChance[i] = 0;
        }
        this.botTick = -1;
        this.endGameTick = -1;
        this.isCustomGame = false;
        this.requestUpdate = true;
    }

    @Override
    @net.minecraftforge.fml.relauncher.SideOnly(net.minecraftforge.fml.relauncher.Side.CLIENT)
    public void renderHud(Minecraft mc, ScaledResolution res) {
        int x = res.getScaledWidth() / 2 - 7;
        int y = res.getScaledHeight() / 2 + 12;
        RenderHelper.enableGUIStandardItemLighting();
        mc.getRenderItem().renderItemAndEffectIntoGUI(this.isCustomGame ? (this.customStack == null || this.customStack.isEmpty() ? headRender : this.customStack) : headRender, x - (this.isSingleGame ? 0 : 1), y - (this.isSingleGame ? 0 : 1));
        if (!this.isSingleGame) {
            mc.getRenderItem().renderItemAndEffectIntoGUI(headRender, x + 3, y + 3);
        }
        RenderHelper.disableStandardItemLighting();
    }

    @Override
    public void writePacketNBT(NBTTagCompound cmp) {
        for (int i = 0; i < this.playersName.length; i++) {
            cmp.setString("playerName" + i, this.playersName[i]);
        }
        cmp.setByteArray("slotChance", this.slotChance);
        cmp.setInteger("botTick", this.botTick);
        cmp.setInteger("endGameTick", this.endGameTick);
        cmp.setBoolean("requestUpdate", this.requestUpdate);
        cmp.setBoolean("isSingleGame", this.isSingleGame);
        cmp.setBoolean("isAnonimGame", this.isCustomGame);
        if (this.customStack != null && !this.customStack.isEmpty()) {
            this.customStack.writeToNBT(cmp);
        }
    }

    @Override
    public void readPacketNBT(NBTTagCompound cmp) {
        for (int i = 0; i < this.playersName.length; i++) {
            this.playersName[i] = cmp.getString("playerName" + i);
        }
        this.botTick = cmp.getInteger("botTick");
        this.endGameTick = cmp.getInteger("endGameTick");
        this.slotChance = cmp.getByteArray("slotChance");
        this.requestUpdate = cmp.getBoolean("requestUpdate");
        this.isSingleGame = cmp.getBoolean("isSingleGame");
        this.isCustomGame = cmp.getBoolean("isAnonimGame");
        this.customStack = new ItemStack(cmp);
    }
}
