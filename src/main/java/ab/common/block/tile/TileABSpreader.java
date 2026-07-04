package ab.common.block.tile;

import ab.common.core.handler.ConfigABHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.botania.api.mana.BurstProperties;
import vazkii.botania.api.mana.ILensEffect;
import vazkii.botania.api.mana.IManaReceiver;
import vazkii.botania.api.mana.IManaSpreader;
import vazkii.botania.client.core.handler.HUDHandler;
import vazkii.botania.common.block.tile.mana.TileSpreader;
import vazkii.botania.common.entity.EntityManaBurst;

public class TileABSpreader extends TileSpreader {
    protected boolean requestsClientUpdate = false;
    protected int knownMana = -1;
    protected IManaReceiver receiver = null;

    @Override
    public void readPacketNBT(NBTTagCompound cmp) {
        super.readPacketNBT(cmp);
        this.requestsClientUpdate = cmp.getBoolean("requestUpdate");
        if (cmp.hasKey("knownMana")) {
            this.knownMana = cmp.getInteger("knownMana");
        }
        if (this.requestsClientUpdate && this.getWorld() != null) {
            int x = cmp.getInteger("forceClientBindingX");
            int y = cmp.getInteger("forceClientBindingY");
            int z = cmp.getInteger("forceClientBindingZ");
            if (y != -1) {
                TileEntity tile = this.getWorld().getTileEntity(new BlockPos(x, y, z));
                this.receiver = tile instanceof IManaReceiver ? (IManaReceiver) tile : null;
            } else {
                this.receiver = null;
            }
        }
    }

    @Override
    public EntityManaBurst getBurst(boolean fake) {
        EntityManaBurst burst = new EntityManaBurst(this, fake);
        int maxMana = ConfigABHandler.spreaderBurstMana;
        int color = 13489177;
        int ticksBeforeManaLoss = 35;
        float manaLossPerTick = (float) ConfigABHandler.spreaderBurstMana / 4.5f;
        float motionModifier = 2.5f;
        float gravity = 0.0f;
        BurstProperties props = new BurstProperties(maxMana, ticksBeforeManaLoss, manaLossPerTick, gravity, motionModifier, color);
        ItemStack lens = this.itemHandler.getStackInSlot(0);
        if (!lens.isEmpty() && lens.getItem() instanceof ILensEffect) {
            ((ILensEffect) lens.getItem()).apply(lens, props);
        }
        burst.setSourceLens(lens);
        if (this.getCurrentMana() >= props.maxMana || fake) {
            burst.setColor(props.color);
            burst.setMana(props.maxMana);
            burst.setStartingMana(props.maxMana);
            burst.setMinManaLoss(props.ticksBeforeManaLoss);
            burst.setManaLossPerTick(props.manaLossPerTick);
            burst.setGravity(props.gravity);
            burst.setMotion(burst.motionX * props.motionModifier, burst.motionY * props.motionModifier, burst.motionZ * props.motionModifier);
            return burst;
        }
        return null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderHUD(Minecraft mc, ScaledResolution res) {
        String name = net.minecraft.client.resources.I18n.format("tile.advancedSpreader.name");
        int color = 13489177;
        HUDHandler.drawSimpleManaHUD(color, this.knownMana, this.getMaxMana(), name, res);
        ItemStack lens = this.itemHandler.getStackInSlot(0);
        if (!lens.isEmpty()) {
            net.minecraft.client.renderer.RenderHelper.enableGUIStandardItemLighting();
            String lensName = lens.getDisplayName();
            int width = 16 + mc.fontRenderer.getStringWidth(lensName) / 2;
            int x = res.getScaledWidth() / 2 - width;
            int y = res.getScaledHeight() / 2 + 50;
            mc.fontRenderer.drawString(lensName, x + 20, y + 5, color);
            net.minecraft.client.renderer.RenderHelper.enableGUIStandardItemLighting();
            mc.getRenderItem().renderItemAndEffectIntoGUI(lens, x, y);
            net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
        }
        if (this.receiver != null) {
            TileEntity receiverTile = (TileEntity) this.receiver;
            BlockPos rPos = receiverTile.getPos();
            ItemStack recieverStack = new ItemStack(this.getWorld().getBlockState(rPos).getBlock(), 1, receiverTile.getBlockMetadata());
            if (!recieverStack.isEmpty() && recieverStack.getItem() != null) {
                String stackName = recieverStack.getDisplayName();
                int width = 16 + mc.fontRenderer.getStringWidth(stackName) / 2;
                int x = res.getScaledWidth() / 2 - width;
                int y = res.getScaledHeight() / 2 + 30;
                mc.fontRenderer.drawString(stackName, x + 20, y + 5, color);
                net.minecraft.client.renderer.RenderHelper.enableGUIStandardItemLighting();
                mc.getRenderItem().renderItemAndEffectIntoGUI(recieverStack, x, y);
                net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
            }
        }
    }

    @Override
    public int getMaxMana() {
        return ConfigABHandler.spreaderMaxMana;
    }
}
