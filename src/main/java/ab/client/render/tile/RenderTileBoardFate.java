package ab.client.render.tile;

import ab.client.model.ModelDiceFate;
import ab.common.block.tile.TileBoardFate;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import vazkii.botania.client.core.handler.ClientTickHandler;

public class RenderTileBoardFate extends TileEntitySpecialRenderer<TileEntity> {
    private static final ModelDiceFate model = new ModelDiceFate();
    private static final ResourceLocation texture = new ResourceLocation("advanced_botany", "textures/model/dicefate.png");

    @Override
    public void render(TileEntity tile, double x, double y, double z, float ticks, int destroyStage, float alpha) {
        double time = tile.getWorld() == null ? 0.0 : (double)((float)ClientTickHandler.ticksInGame + ticks);
        if (tile != null) {
            time += (double)new Random(tile.getPos().getX() ^ tile.getPos().getY() ^ tile.getPos().getZ()).nextInt(360);
        }
        TileBoardFate board = (TileBoardFate)tile;
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glEnable(32826);
        GL11.glBlendFunc(770, 771);
        GL11.glPushMatrix();
        GL11.glTranslated(x + 0.5, y, z + 0.5);
        GL11.glRotatef(90.0f - Minecraft.getMinecraft().getRenderManager().playerViewY, 0.0f, 1.0f, 0.0f);
        for (int i = 0; i < board.getSizeInventory(); ++i) {
            if (board.getStackInSlot(i).isEmpty()) continue;
            time += (double)((float)i * 83.256f);
            GL11.glPushMatrix();
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            float dropAnim = 1.0f - Math.min(150.0f, (float)(board.clientTick[i] * board.clientTick[i]) * 1.42f + ticks) / 150.0f;
            dropAnim = Math.min(1.0f, Math.max(dropAnim, 0.0f));
            float alphaVal = (float)Math.cos(dropAnim);
            float indet = i == 0 ? 0.16f + 0.08f * dropAnim : -0.16f - 0.08f * dropAnim;
            GL11.glTranslated(indet, 0.02f + Math.sin(time / 12.0) / 48.0 + (0.28f * dropAnim), indet);
            GL11.glScalef(0.25f, 0.25f, 0.25f);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, alphaVal);
            Minecraft.getMinecraft().renderEngine.bindTexture(texture);
            float dropAngel = 70.0f * dropAnim;
            switch (board.slotChance[i]) {
                case 1:
                    model.render(180.0f + dropAngel, 0.0f + dropAngel, 0.0f);
                    break;
                case 2:
                    model.render(0.0f + dropAngel, 0.0f + dropAngel, 0.0f);
                    break;
                case 3:
                    model.render(90.0f + dropAngel, 0.0f + dropAngel, 0.0f);
                    break;
                case 4:
                    model.render(270.0f + dropAngel, 0.0f + dropAngel, 0.0f);
                    break;
                case 5:
                    model.render(0.0f + dropAngel, 0.0f + dropAngel, 270.0f);
                    break;
                case 6:
                    model.render(0.0f + dropAngel, 0.0f + dropAngel, 90.0f);
                    break;
                default:
                    model.render(0.0f + dropAngel, 0.0f + dropAngel, 0.0f);
            }
            GL11.glEnable(32826);
            GL11.glPopMatrix();
        }
        GL11.glPopMatrix();
        GL11.glEnable(32826);
        GL11.glDisable(GL11.GL_BLEND);
    }
}
