package ab.client.core.handler;

import ab.client.core.ClientHelper;
import ab.common.item.equipment.armor.ItemNebulaArmor;
import ab.common.item.equipment.armor.ItemNebulaHelm;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;
import vazkii.botania.api.item.IPhantomInkable;
import vazkii.botania.client.core.handler.ClientTickHandler;

import java.awt.Color;

public class PlayerRendererHandler {
    @SubscribeEvent
    public void onPlayerRender(RenderPlayerEvent.Specials.Post event) {
        EntityPlayer player = event.getEntityPlayer();
        ItemStack item = player.getItemStackFromSlot(net.minecraft.inventory.EntityEquipmentSlot.HEAD);
        if (!item.isEmpty() && item.getItem() instanceof ItemNebulaHelm) {
            boolean hasPhantomInk;
            ItemNebulaHelm helm = (ItemNebulaHelm) item.getItem();
            boolean bl = hasPhantomInk = helm instanceof IPhantomInkable && ((IPhantomInkable) helm).hasPhantomInk(item);
            if (!hasPhantomInk && ItemNebulaArmor.enableEffect(item)) {
                float angelX = event.getRenderer().getMainModel().bipedHead.rotateAngleX * 180.0f / (float) Math.PI;
                float angelY = event.getRenderer().getMainModel().bipedHead.rotateAngleY * 180.0f / (float) Math.PI;
                GlStateManager.pushMatrix();
                if (player.isSneaking()) {
                    GlStateManager.translate(event.getRenderer().getMainModel().bipedHead.offsetX * 0.0625f, event.getRenderer().getMainModel().bipedHead.offsetY * 0.0625f, 0.0f);
                }
                GlStateManager.rotate(angelY, 0.0f, 1.0f, 0.0f);
                GlStateManager.rotate(angelX, 1.0f, 0.0f, 0.0f);
                this.renderCosmicFace();
                GlStateManager.popMatrix();
            }
        }
    }

    private void renderCosmicFace() {
        double worldTime = ClientTickHandler.ticksInGame + ClientTickHandler.partialTicks;
        GlStateManager.pushMatrix();
        GlStateManager.enableTexture2D();
        GlStateManager.disableCull();
        GlStateManager.pushMatrix();
        GlStateManager.scale(0.5f, 0.4f, 0.4f);
        GlStateManager.rotate(90.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.translate(-0.5f, -0.8675f, 0.0f);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        boolean hasFog = GL11.glGetBoolean(GL11.GL_FOG);
        if (hasFog) {
            GlStateManager.disableFog();
        }
        ClientHelper.renderCosmicBackground();
        if (hasFog) {
            GlStateManager.enableFog();
        }
        GlStateManager.popMatrix();
        int light = 0xF000F0;
        int lightmapX = light % 65536;
        int lightmapY = light / 65536;
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lightmapX, lightmapY);
        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        GlStateManager.translate(-0.2f, -0.35f, -0.25f);
        GlStateManager.scale(0.4f, 0.4f, 0.4f);
        TextureAtlasSprite eyes = ItemNebulaArmor.nebulaEyes;
        if (eyes != null) {
            float f = eyes.getMinU();
            float f1 = eyes.getMaxU();
            float f2 = eyes.getMaxV();
            float f3 = eyes.getMinV();
            Color color = Color.getHSBColor((float) (Minecraft.getSystemTime() / 20.0f % 360.0f / 360.0f), 1.0f, 1.0f);
            float r = (float) color.getRed() / 510.0f;
            float g = (float) color.getGreen() / 510.0f;
            float b = (float) color.getBlue() / 510.0f;
            GlStateManager.color(0.5f + r, 0.5f + g, 0.5f + b, 0.685f + (float) Math.sin(worldTime / 50.0) * 0.175f);
            this.renderFaceQuad(eyes, 0.03125f);
        }
        GlStateManager.disableBlend();
        GlStateManager.enableRescaleNormal();
        GlStateManager.popMatrix();
    }

    private void renderFaceQuad(TextureAtlasSprite icon, float thickness) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        float f = icon.getMinU();
        float f1 = icon.getMaxU();
        float f2 = icon.getMaxV();
        float f3 = icon.getMinV();
        buffer.begin(GL11.GL_QUADS, net.minecraft.client.renderer.vertex.DefaultVertexFormats.POSITION_TEX_NORMAL);
        buffer.pos(0.0, 0.0, 0.0).tex(f1, f3).normal(0, 0, 1).endVertex();
        buffer.pos(1.0, 0.0, 0.0).tex(f, f3).normal(0, 0, 1).endVertex();
        buffer.pos(1.0, 1.0, 0.0).tex(f, f2).normal(0, 0, 1).endVertex();
        buffer.pos(0.0, 1.0, 0.0).tex(f1, f2).normal(0, 0, 1).endVertex();
        tessellator.draw();
    }
}
