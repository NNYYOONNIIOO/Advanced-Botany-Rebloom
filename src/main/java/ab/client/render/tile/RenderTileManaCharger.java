package ab.client.render.tile;

import ab.client.model.ModelManaCharger;
import ab.common.block.tile.TileManaCharger;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import vazkii.botania.client.core.handler.ClientTickHandler;

public class RenderTileManaCharger extends TileEntitySpecialRenderer<TileEntity> {
    private static final ResourceLocation texture = new ResourceLocation("advanced_botany", "textures/model/manacharger.png");
    private static final ModelManaCharger model = new ModelManaCharger();
    public TileManaCharger charger;

    @Override
    public void render(TileEntity tile, double x, double y, double z, float ticks, int destroyStage, float alpha) {
        double time;
        this.charger = (TileManaCharger)tile;
        time = tile.getWorld() == null ? 0.0 : (double)((float)ClientTickHandler.ticksInGame + ticks);
        if (tile != null) {
            time += (double)new Random(tile.getPos().getX() ^ tile.getPos().getY() ^ tile.getPos().getZ()).nextInt(360);
        }
        GL11.glPushMatrix();
        GL11.glEnable(32826);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glTranslated(x, y, z);
        GL11.glTranslatef(0.5f, 1.65f, 0.5f);
        GL11.glRotatef(180.0f, 1.0f, 0.0f, 1.0f);
        Minecraft.getMinecraft().renderEngine.bindTexture(texture);
        model.render(this, time);
        GL11.glEnable(32826);
        GL11.glPopMatrix();
    }

    public void renderItemStack(ItemStack stack) {
        if (!stack.isEmpty()) {
            Minecraft mc = Minecraft.getMinecraft();
            float s = 0.25f;
            GL11.glScalef(s, s, s);
            GL11.glScalef(2.0f, 2.0f, 2.0f);
            GlStateManager.pushMatrix();
            GlStateManager.disableLighting();
            mc.getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.FIXED);
            GlStateManager.enableLighting();
            GlStateManager.popMatrix();
            GL11.glScalef(1.0f / s, 1.0f / s, 1.0f / s);
            mc.renderEngine.bindTexture(texture);
        }
    }
}
