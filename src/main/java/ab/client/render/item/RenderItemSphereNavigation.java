package ab.client.render.item;

import ab.common.item.relic.ItemSphereNavigation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

public class RenderItemSphereNavigation extends TileEntityItemStackRenderer {
    private void renderLayer(ItemStack source, int modelMetadata) {
        ItemStack layer = source.copy();
        layer.setItemDamage(modelMetadata);
        Minecraft.getMinecraft().getRenderItem().renderItem(layer, ItemCameraTransforms.TransformType.NONE);
    }

    private void renderLayerOnTop(ItemStack source, int modelMetadata) {
        GlStateManager.enableDepth();
        GlStateManager.depthFunc(GL11.GL_LEQUAL);
        GlStateManager.enablePolygonOffset();
        GlStateManager.doPolygonOffset(-1.0f, -1.0f);
        try {
            this.renderLayer(source, modelMetadata);
        } finally {
            GlStateManager.doPolygonOffset(0.0f, 0.0f);
            GlStateManager.disablePolygonOffset();
        }
    }

    @Override
    public void renderByItem(ItemStack stack, float partialTicks) {
        GL11.glPushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        this.renderLayer(stack, 2);
        // Metadata 0 is enabled (_4); metadata 1 is disabled (_3).
        this.renderLayerOnTop(stack, stack.getMetadata() == 0 ? 6 : 5);
        GL11.glPushMatrix();
        double time = Minecraft.getSystemTime();
        // The _1 layer is part of the item in both states and floats by +/- 0.5 pixels.
        GL11.glTranslated(0.0, Math.cos(time / 650.0) * 0.03125, 0.0);
        this.renderLayerOnTop(stack, 3);
        GL11.glPopMatrix();
        GlStateManager.disableBlend();
        GL11.glPopMatrix();
    }
}
