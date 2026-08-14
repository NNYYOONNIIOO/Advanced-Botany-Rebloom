package ab.client.render.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;
import vazkii.botania.common.core.helper.ItemNBTHelper;

public class RenderItemAntigravityCharm extends TileEntityItemStackRenderer {
    private void renderLayer(ItemStack source, int modelMetadata) {
        ItemStack layer = source.copy();
        layer.setItemDamage(modelMetadata);
        Minecraft.getMinecraft().getRenderItem().renderItem(layer, ItemCameraTransforms.TransformType.NONE);
    }

    private void renderLayerOnTop(ItemStack source, int modelMetadata) {
        this.renderLayer(source, modelMetadata);
    }

    @Override
    public void renderByItem(ItemStack item, float partialTicks) {
        boolean blendEnabled = GL11.glIsEnabled(GL11.GL_BLEND);
        boolean depthEnabled = GL11.glIsEnabled(GL11.GL_DEPTH_TEST);
        GL11.glPushMatrix();
        try {
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            this.renderLayer(item, 2);
            GL11.glPushMatrix();
            double time = Minecraft.getSystemTime();
            if (ItemNBTHelper.getBoolean(item, "isActive", true)) {
                GL11.glTranslated(0.0, Math.cos(time / 650.0) * 0.0075 - 0.05, 0.0);
            } else {
                // Four pixels in the overlay texture equal two base-texture pixels.
                // Move only itemantigravitycharm_1 down by those two base pixels.
                GL11.glTranslated(0.0, -0.125, 0.0);
            }
            this.renderLayerOnTop(item, 3);
            GL11.glPopMatrix();
        } finally {
            if (blendEnabled) {
                GlStateManager.enableBlend();
            } else {
                GlStateManager.disableBlend();
            }
            if (depthEnabled) {
                GlStateManager.enableDepth();
            } else {
                GlStateManager.disableDepth();
            }
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            GL11.glPopMatrix();
        }
    }
}
