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

    @Override
    public void renderByItem(ItemStack stack, float partialTicks) {
        GL11.glPushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.translate(0.0F, 0.5F, 0.0F);
        this.renderLayer(stack, 2);
        this.renderLayer(stack, stack.getMetadata() == 0 ? 6 : 5);
        GL11.glPushMatrix();
        double time = Minecraft.getSystemTime();
        if (ItemSphereNavigation.getFindBlock(stack) != null) {
            GL11.glTranslated(0.0, Math.cos(time / 650.0) * 0.03125, 0.0);
            this.renderLayer(stack, 3);
        }
        GL11.glPopMatrix();
        GlStateManager.disableBlend();
        GL11.glPopMatrix();
    }
}
