package ab.client.render.tile;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class TileRenderer {
    public static void renderCube(double xCenter, double yCenter, double zCenter, double r, TextureAtlasSprite icon, BufferBuilder buffer) {
        TileRenderer.renderCube(xCenter, yCenter, zCenter, r, icon, buffer, 0.0f);
    }

    public static void renderLateralSides(double xCenter, double yCenter, double zCenter, double r, TextureAtlasSprite icon, BufferBuilder buffer) {
        TileRenderer.renderLateralSides(xCenter, yCenter, zCenter, r, icon, buffer, 0.0f);
    }

    public static void renderUpperlSide(double xCenter, double yCenter, double zCenter, double r, TextureAtlasSprite icon, BufferBuilder buffer) {
        TileRenderer.renderUpperlSide(xCenter, yCenter, zCenter, r, icon, buffer, 0.0f);
    }

    public static void renderDownside(double xCenter, double yCenter, double zCenter, double r, TextureAtlasSprite icon, BufferBuilder buffer) {
        TileRenderer.renderUpperlSide(xCenter, yCenter, zCenter, r, icon, buffer, 0.0f);
    }

    public static void renderCube(double xCenter, double yCenter, double zCenter, double r, TextureAtlasSprite icon, BufferBuilder buffer, float sideOffset) {
        TileRenderer.renderLateralSides(xCenter, yCenter, zCenter, r, icon, buffer, sideOffset);
        TileRenderer.renderUpperlSide(xCenter, yCenter, zCenter, r, icon, buffer, sideOffset);
        TileRenderer.renderDownside(xCenter, yCenter, zCenter, r, icon, buffer, sideOffset);
    }

    public static void renderLateralSides(double xCenter, double yCenter, double zCenter, double r, TextureAtlasSprite icon, BufferBuilder buffer, float sideOffset) {
        float maxU = icon.getMaxU();
        float maxV = icon.getMaxV();
        float minU = icon.getMinU();
        float minV = icon.getMinV();
        double minX = xCenter - r;
        double minY = yCenter - r;
        double minZ = zCenter - r;
        double maxX = xCenter + r;
        double maxY = yCenter + r;
        double maxZ = zCenter + r;
        buffer.pos(maxX, minY, maxZ + sideOffset).tex(maxU, maxV).endVertex();
        buffer.pos(maxX, maxY, maxZ + sideOffset).tex(maxU, minV).endVertex();
        buffer.pos(minX, maxY, maxZ + sideOffset).tex(minU, minV).endVertex();
        buffer.pos(minX, minY, maxZ + sideOffset).tex(minU, maxV).endVertex();
        buffer.pos(minX, minY, minZ - sideOffset).tex(maxU, maxV).endVertex();
        buffer.pos(minX, maxY, minZ - sideOffset).tex(maxU, minV).endVertex();
        buffer.pos(maxX, maxY, minZ - sideOffset).tex(minU, minV).endVertex();
        buffer.pos(maxX, minY, minZ - sideOffset).tex(minU, maxV).endVertex();
        buffer.pos(minX - sideOffset, minY, maxZ).tex(maxU, maxV).endVertex();
        buffer.pos(minX - sideOffset, maxY, maxZ).tex(maxU, minV).endVertex();
        buffer.pos(minX - sideOffset, maxY, minZ).tex(minU, minV).endVertex();
        buffer.pos(minX - sideOffset, minY, minZ).tex(minU, maxV).endVertex();
        buffer.pos(maxX + sideOffset, minY, minZ).tex(maxU, maxV).endVertex();
        buffer.pos(maxX + sideOffset, maxY, minZ).tex(maxU, minV).endVertex();
        buffer.pos(maxX + sideOffset, maxY, maxZ).tex(minU, minV).endVertex();
        buffer.pos(maxX + sideOffset, minY, maxZ).tex(minU, maxV).endVertex();
    }

    public static void renderUpperlSide(double xCenter, double yCenter, double zCenter, double r, TextureAtlasSprite icon, BufferBuilder buffer, float sideOffset) {
        float maxU = icon.getMaxU();
        float maxV = icon.getMaxV();
        float minU = icon.getMinU();
        float minV = icon.getMinV();
        double minX = xCenter - r;
        double minY = yCenter - r;
        double minZ = zCenter - r;
        double maxX = xCenter + r;
        double maxY = yCenter + r;
        double maxZ = zCenter + r;
        buffer.pos(maxX, maxY + sideOffset, maxZ).tex(maxU, maxV).endVertex();
        buffer.pos(maxX, maxY + sideOffset, minZ).tex(maxU, minV).endVertex();
        buffer.pos(minX, maxY + sideOffset, minZ).tex(minU, minV).endVertex();
        buffer.pos(minX, maxY + sideOffset, maxZ).tex(minU, maxV).endVertex();
    }

    public static void renderDownside(double xCenter, double yCenter, double zCenter, double r, TextureAtlasSprite icon, BufferBuilder buffer, float sideOffset) {
        float maxU = icon.getMaxU();
        float maxV = icon.getMaxV();
        float minU = icon.getMinU();
        float minV = icon.getMinV();
        double minX = xCenter - r;
        double minY = yCenter - r;
        double minZ = zCenter - r;
        double maxX = xCenter + r;
        double maxY = yCenter + r;
        double maxZ = zCenter + r;
        buffer.pos(minX, minY - sideOffset, minZ).tex(maxU, maxV).endVertex();
        buffer.pos(maxX, minY - sideOffset, minZ).tex(maxU, minV).endVertex();
        buffer.pos(maxX, minY - sideOffset, maxZ).tex(minU, minV).endVertex();
        buffer.pos(minX, minY - sideOffset, maxZ).tex(minU, maxV).endVertex();
    }
}
