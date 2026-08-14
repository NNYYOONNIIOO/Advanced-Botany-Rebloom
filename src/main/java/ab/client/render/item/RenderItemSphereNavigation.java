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

    private void renderLayerUnlit(ItemStack source, int modelMetadata) {
        boolean lightingEnabled = GL11.glIsEnabled(GL11.GL_LIGHTING);
        boolean light0Enabled = GL11.glIsEnabled(GL11.GL_LIGHT0);
        boolean light1Enabled = GL11.glIsEnabled(GL11.GL_LIGHT1);
        boolean colorMaterialEnabled = GL11.glIsEnabled(GL11.GL_COLOR_MATERIAL);
        GlStateManager.disableLighting();
        GL11.glDisable(GL11.GL_LIGHT0);
        GL11.glDisable(GL11.GL_LIGHT1);
        GL11.glDisable(GL11.GL_COLOR_MATERIAL);
        try {
            this.renderLayer(source, modelMetadata);
        } finally {
            if (lightingEnabled) GlStateManager.enableLighting();
            else GlStateManager.disableLighting();
            if (light0Enabled) GL11.glEnable(GL11.GL_LIGHT0);
            else GL11.glDisable(GL11.GL_LIGHT0);
            if (light1Enabled) GL11.glEnable(GL11.GL_LIGHT1);
            else GL11.glDisable(GL11.GL_LIGHT1);
            if (colorMaterialEnabled) GL11.glEnable(GL11.GL_COLOR_MATERIAL);
            else GL11.glDisable(GL11.GL_COLOR_MATERIAL);
        }
    }

    private void renderLayerOnTop(ItemStack source, int modelMetadata) {
        this.renderLayerUnlit(source, modelMetadata);
    }

    @Override
    public void renderByItem(ItemStack stack, float partialTicks) {
        GlStateManager.pushMatrix();
        try {
            this.renderLayerUnlit(stack, 2);
            // Metadata 0 is enabled (_4); metadata 1 is disabled (_3).
            GlStateManager.pushMatrix();
            try {
                GlStateManager.translate(0.0, 0.0, 0.0015625);
                this.renderLayerOnTop(stack, stack.getMetadata() == 0 ? 6 : 5);
            } finally {
                GlStateManager.popMatrix();
            }
            GlStateManager.pushMatrix();
            try {
                double time = Minecraft.getSystemTime();
                // Keep spherenavigation_1 above the state layer and prevent z-fighting.
                GlStateManager.translate(0.0, Math.cos(time / 650.0) * 0.03125, 0.003125);
                this.renderLayerOnTop(stack, 3);
            } finally {
                GlStateManager.popMatrix();
            }
        } finally {
            GlStateManager.popMatrix();
        }
    }
}
