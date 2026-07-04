package ab.client.render.item;

import ab.common.item.relic.ItemSphereNavigation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

public class RenderItemSphereNavigation extends TileEntityItemStackRenderer {
    @Override
    public void renderByItem(ItemStack stack, float partialTicks) {
        Minecraft mc = Minecraft.getMinecraft();
        GL11.glPushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        mc.getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.NONE);
        double time = Minecraft.getSystemTime();
        if (ItemSphereNavigation.getFindBlock(stack) != null) {
            GL11.glTranslated(0.0, Math.cos(time / 650.0) * 0.0075 - 0.015, 0.0);
        }
        GlStateManager.disableBlend();
        GL11.glPopMatrix();
    }
}
