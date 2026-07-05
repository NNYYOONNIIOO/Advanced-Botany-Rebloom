package ab.client.core;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import vazkii.botania.client.core.handler.ClientTickHandler;
import vazkii.botania.client.core.handler.HUDHandler;
import vazkii.botania.common.block.ModBlocks;

import java.awt.Color;
import java.nio.FloatBuffer;
import java.util.Random;

public class ClientHelper {
    public static final ResourceLocation miscHuds = new ResourceLocation("advanced_botany", "textures/misc/miscHuds.png");
    private static final ResourceLocation END_SKY = new ResourceLocation("textures/environment/end_sky.png");
    private static final ResourceLocation END_PORTAL = new ResourceLocation("textures/entity/end_portal.png");
    private static final Random rand = new Random(31100L);
    public static Minecraft mc = Minecraft.getMinecraft();
    private static FloatBuffer buffer = net.minecraft.client.renderer.GLAllocation.createDirectFloatBuffer(16);

    public static void renderCosmicBackground() {
        rand.setSeed(31100L);
        float f4 = 0.24f;
        for (int i = 0; i < 16; ++i) {
            GlStateManager.pushMatrix();
            float f5 = 16 - i;
            float f6 = 0.0625f;
            float f7 = 1.0f / (f5 + 1.0f);
            if (i == 0) {
                mc.renderEngine.bindTexture(miscHuds);
                f7 = 0.1f;
                f5 = 65.0f;
                f6 = 0.125f;
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            }
            if (i == 1) {
                mc.renderEngine.bindTexture(END_PORTAL);
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(GL11.GL_ONE, GL11.GL_ONE);
                f6 = 0.5f;
            }
            GlStateManager.translate(0.0f, 1.5f, 0.0f);
            GL11.glTexGeni(GL11.GL_S, GL11.GL_TEXTURE_GEN_MODE, GL11.GL_OBJECT_LINEAR);
            GL11.glTexGeni(GL11.GL_T, GL11.GL_TEXTURE_GEN_MODE, GL11.GL_OBJECT_LINEAR);
            GL11.glTexGeni(GL11.GL_R, GL11.GL_TEXTURE_GEN_MODE, GL11.GL_OBJECT_LINEAR);
            GL11.glTexGeni(GL11.GL_Q, GL11.GL_TEXTURE_GEN_MODE, GL11.GL_EYE_LINEAR);
            GL11.glTexGen(GL11.GL_S, GL11.GL_OBJECT_PLANE, createFloatBuffer(1.0f, 0.0f, 0.0f, 0.0f));
            GL11.glTexGen(GL11.GL_T, GL11.GL_OBJECT_PLANE, createFloatBuffer(0.0f, 0.0f, 1.0f, 0.0f));
            GL11.glTexGen(GL11.GL_R, GL11.GL_OBJECT_PLANE, createFloatBuffer(0.0f, 0.0f, 0.0f, 1.0f));
            GL11.glTexGen(GL11.GL_Q, GL11.GL_EYE_PLANE, createFloatBuffer(0.0f, 1.0f, 0.0f, 0.0f));
            GL11.glEnable(GL11.GL_TEXTURE_GEN_S);
            GL11.glEnable(GL11.GL_TEXTURE_GEN_Q);
            GL11.glEnable(GL11.GL_TEXTURE_GEN_R);
            GL11.glEnable(GL11.GL_TEXTURE_GEN_T);
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(GL11.GL_TEXTURE);
            GlStateManager.pushMatrix();
            GlStateManager.loadIdentity();
            GlStateManager.translate(0.0f, (float) (Minecraft.getSystemTime() % 20000L) / 20000.0f, 0.0f);
            GlStateManager.scale(f6, f6, f6);
            GlStateManager.translate(0.5f, 0.5f, 0.0f);
            GlStateManager.rotate((float) (i * i * 4321 + i * 9) * 2.0f, 0.0f, 0.0f, 1.0f);
            GlStateManager.translate(-0.5f, -0.5f, 0.0f);
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buf = tessellator.getBuffer();
            buf.begin(GL11.GL_QUADS, net.minecraft.client.renderer.vertex.DefaultVertexFormats.POSITION_COLOR);
            Color color = Color.getHSBColor((float) Minecraft.getSystemTime() / 20.0f % 360.0f / 360.0f, 1.0f, 1.0f);
            float f11 = color.getRed() / 255.0f;
            float f12 = color.getGreen() / 255.0f;
            float f13 = color.getBlue() / 255.0f;
            buf.pos(0.0, f4, 0.0).color(f11 * f7, f12 * f7, f13 * f7, 1.0f).endVertex();
            buf.pos(0.0, f4, 1.0).color(f11 * f7, f12 * f7, f13 * f7, 1.0f).endVertex();
            buf.pos(1.0, f4, 1.0).color(f11 * f7, f12 * f7, f13 * f7, 1.0f).endVertex();
            buf.pos(1.0, f4, 0.0).color(f11 * f7, f12 * f7, f13 * f7, 1.0f).endVertex();
            tessellator.draw();
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        }
        GlStateManager.disableBlend();
        GL11.glDisable(GL11.GL_TEXTURE_GEN_S);
        GL11.glDisable(GL11.GL_TEXTURE_GEN_T);
        GL11.glDisable(GL11.GL_TEXTURE_GEN_R);
        GL11.glDisable(GL11.GL_TEXTURE_GEN_Q);
    }

    private static FloatBuffer createFloatBuffer(float a, float b, float c, float d) {
        buffer.clear();
        buffer.put(a).put(b).put(c).put(d);
        buffer.flip();
        return buffer;
    }

    public static void drawArrow(int x, int y, boolean side) {
        mc.renderEngine.bindTexture(miscHuds);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 0);
        GlStateManager.scale(2.2f, 1.0f, 1.0f);
        vazkii.botania.client.core.helper.RenderHelper.drawTexturedModalRect(0, 0, 0.0f, side ? 0 : 10, 10, 10, 38);
        GlStateManager.popMatrix();
    }

    public static void drawChanceBar(int x, int y, int chance) {
        mc.renderEngine.bindTexture(miscHuds);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        vazkii.botania.client.core.helper.RenderHelper.drawTexturedModalRect(x, y, 0.0f, 0, 0, 57, 6);
        int chancePercentage = Math.max(0, (int) ((double) ((float) chance / 100.0f) * 55.0));
        vazkii.botania.client.core.helper.RenderHelper.drawTexturedModalRect(x + 1, y + 1, 0.0f, 0, 6, 55, 4);
        Color color = new Color(Color.HSBtoRGB(chance / 360.0f, ((float) Math.sin((ClientTickHandler.ticksInGame + ClientTickHandler.partialTicks) * 0.2) + 1.0f) * 0.3f + 0.4f, 1.0f));
        GlStateManager.color(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, 1.0f);
        vazkii.botania.client.core.helper.RenderHelper.drawTexturedModalRect(x + 1, y + 1, 0.0f, 0, 6, Math.min(55, chancePercentage), 4);
    }

    public static void renderPoolManaBar(int x, int y, int color, float alpha, int mana) {
        Minecraft mc = Minecraft.getMinecraft();
        int poolCount = (int) Math.floor((double) mana / 1000000.0);
        if (poolCount < 0) {
            poolCount = 0;
        }
        int onePoolMana = mana - poolCount * 1000000;
        String strPool = poolCount + "x";
        int xc = x - mc.fontRenderer.getStringWidth(strPool) / 2;
        int yc = y;
        GlStateManager.pushMatrix();
        GlStateManager.translate(xc + 42.0f, yc + 5.0f, 0.0f);
        RenderHelper.enableGUIStandardItemLighting();
        mc.getRenderItem().renderItemAndEffectIntoGUI(new ItemStack(ModBlocks.pool), 0, 0);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.translate(18.0f, 5.0f, 300.0f);
        mc.fontRenderer.drawString(strPool, 0, 0, color);
        GlStateManager.popMatrix();
        if (poolCount * 1000000 == mana) {
            onePoolMana = poolCount * 1000000;
        }
        HUDHandler.renderManaBar(x, y, color, alpha, onePoolMana, 1000000);
    }

    public static void drawPoolManaHUD(ScaledResolution res, String name, int mana, int maxMana, int color) {
        Minecraft mc = Minecraft.getMinecraft();
        int poolCount = (int) Math.floor((double) mana / 1000000.0);
        int maxPoolCount = (int) Math.floor((double) maxMana / 1000000.0);
        if (poolCount < 0) {
            poolCount = 0;
        }
        if (maxPoolCount < 0) {
            maxPoolCount = 0;
        }
        int onePoolMana = mana - poolCount * 1000000;
        String strPool = poolCount + "x / " + maxPoolCount + "x";
        int xc = res.getScaledWidth() / 2 - mc.fontRenderer.getStringWidth(strPool) / 2 - 3;
        int yc = res.getScaledHeight() / 2;
        GlStateManager.pushMatrix();
        GlStateManager.translate(xc - 6.0f, yc + 30.0f, 0.0f);
        RenderHelper.enableGUIStandardItemLighting();
        mc.getRenderItem().renderItemAndEffectIntoGUI(new ItemStack(ModBlocks.pool), 0, 0);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.translate(18.0f, 4.5f, 300.0f);
        mc.fontRenderer.drawStringWithShadow(strPool, 0, 0, color);
        GlStateManager.popMatrix();
        if (poolCount * 1000000 == mana) {
            onePoolMana = poolCount * 1000000;
        }
        HUDHandler.drawSimpleManaHUD(color, onePoolMana, 1000000, name, res);
    }

    public static void setLightmapTextureCoords() {
        int light = 0xF000F0;
        int lightmapX = light % 65536;
        int lightmapY = light / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lightmapX, lightmapY);
    }

    public static vazkii.botania.common.core.helper.Vector3 setRotation(float angel, float vX, float vY, float vZ, vazkii.botania.common.core.helper.Vector3 v3) {
        vazkii.botania.common.core.helper.Vector3 rVec = new vazkii.botania.common.core.helper.Vector3(vX, vY, vZ);
        vazkii.botania.common.core.helper.Vector3 rVec1 = new vazkii.botania.common.core.helper.Vector3(v3.x, v3.y, v3.z).normalize();
        double rAngel = Math.toRadians(angel) * 0.5;
        double sin = Math.sin(rAngel);
        double x = rVec.x * sin;
        double y = rVec.y * sin;
        double z = rVec.z * sin;
        rAngel = Math.cos(rAngel);
        double d = -x * rVec1.x - y * rVec1.y - z * rVec1.z;
        double d1 = rAngel * rVec1.x + y * rVec1.z - z * rVec1.y;
        double d2 = rAngel * rVec1.y - x * rVec1.z + z * rVec1.x;
        double d3 = rAngel * rVec1.z + x * rVec1.y - y * rVec1.x;
        return new vazkii.botania.common.core.helper.Vector3(d1 * rAngel - d * x - d2 * z + d3 * y, d2 * rAngel - d * y + d1 * z - d3 * x, d3 * rAngel - d * z - d1 * y + d2 * x);
    }

    public static Color getCorporeaRuneColor(int posX, int posY, int posZ, int meta) {
        double time = ClientTickHandler.ticksInGame + ClientTickHandler.partialTicks;
        float sin = (float) (Math.sin((time += (double) new Random(posX ^ posY ^ posZ).nextInt(360)) / 20.0) * 0.15f) - 0.15f;
        int color = 0;
        switch (meta) {
            case 0: {
                color = Color.HSBtoRGB(0.0f, 0.0f, 0.54f + sin / 1.2f);
                break;
            }
            case 1: {
                color = Color.HSBtoRGB(0.688f, 0.93f, 0.96f + sin - 0.15f);
                break;
            }
            case 2: {
                color = Color.HSBtoRGB(0.983f, 0.99f, 1.0f + sin - 0.15f);
                break;
            }
            case 3: {
                color = Color.HSBtoRGB(0.319f, 0.92f, 0.95f + sin - 0.15f);
                break;
            }
            case 4: {
                color = Color.HSBtoRGB(0.536f, 0.53f, 0.92f + sin - 0.15f);
            }
        }
        return new Color(color);
    }

    public static void renderIcon(TextureAtlasSprite icon, int light) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        float f = icon.getMinU();
        float f1 = icon.getMaxU();
        float f2 = icon.getMaxV();
        float f3 = icon.getMinV();
        float f4 = 1.0f;
        float f5 = 0.5f;
        float f6 = 0.25f;
        buffer.begin(GL11.GL_QUADS, net.minecraft.client.renderer.vertex.DefaultVertexFormats.POSITION_TEX);
        buffer.pos(0.0f - f5, 0.0f - f6, 0.0).tex(f, f3).endVertex();
        buffer.pos(f4 - f5, 0.0f - f6, 0.0).tex(f1, f3).endVertex();
        buffer.pos(f4 - f5, f4 - f6, 0.0).tex(f1, f2).endVertex();
        buffer.pos(0.0f - f5, f4 - f6, 0.0).tex(f, f2).endVertex();
        tessellator.draw();
    }

    public static void renderIconUV(float minU, float minV, float maxU, float maxV) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        float f4 = 1.0f;
        float f5 = 0.5f;
        float f6 = 0.25f;
        buffer.begin(GL11.GL_QUADS, net.minecraft.client.renderer.vertex.DefaultVertexFormats.POSITION_TEX);
        buffer.pos(0.0f - f5, 0.0f - f6, 0.0).tex(minU, minV).endVertex();
        buffer.pos(f4 - f5, 0.0f - f6, 0.0).tex(maxU, minV).endVertex();
        buffer.pos(f4 - f5, f4 - f6, 0.0).tex(maxU, maxV).endVertex();
        buffer.pos(0.0f - f5, f4 - f6, 0.0).tex(minU, maxV).endVertex();
        tessellator.draw();
    }
}
