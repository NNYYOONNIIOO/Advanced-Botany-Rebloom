package ab.client.core.handler;

import ab.api.IBoundRender;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;
import vazkii.botania.api.wand.ICoordBoundItem;
import vazkii.botania.api.wand.IWireframeAABBProvider;
import vazkii.botania.client.core.handler.ClientTickHandler;

import java.awt.Color;

public class BoundRenderHandler {
    @SubscribeEvent
    public void onWorldRenderLast(RenderWorldLastEvent event) {
        GlStateManager.pushMatrix();
        GlStateManager.pushAttrib();
        GlStateManager.disableDepth();
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        ItemStack stack = Minecraft.getMinecraft().player.getHeldItemMainhand();
        int color = Color.HSBtoRGB((ClientTickHandler.ticksInGame % 200) / 200.0f, 0.6f, 1.0f);
        if (!stack.isEmpty() && stack.getItem() instanceof ICoordBoundItem) {
            TileEntity tile;
            BlockPos[] coords = null;
            RayTraceResult pos = Minecraft.getMinecraft().objectMouseOver;
            if (pos != null && pos.typeOfHit == RayTraceResult.Type.BLOCK && (tile = Minecraft.getMinecraft().world.getTileEntity(pos.getBlockPos())) != null && tile instanceof IBoundRender) {
                coords = ((IBoundRender) tile).getBlocksCoord();
            }
            if (coords != null) {
                for (int i = 0; i < coords.length; ++i) {
                    if (coords[i].getY() == -1) continue;
                    this.renderBlockOutlineAt(coords[i], color, 1.0f);
                }
            }
        }
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popAttrib();
        GlStateManager.popMatrix();
    }

    private void renderBlockOutlineAt(BlockPos bPos, int color, float thickness) {
        GlStateManager.pushMatrix();
        double renderX = Minecraft.getMinecraft().getRenderManager().viewerPosX;
        double renderY = Minecraft.getMinecraft().getRenderManager().viewerPosY;
        double renderZ = Minecraft.getMinecraft().getRenderManager().viewerPosZ;
        GlStateManager.translate(bPos.getX() - renderX, bPos.getY() - renderY, bPos.getZ() - renderZ + 1.0);
        Color colorRGB = new Color(color);
        GlStateManager.color(colorRGB.getRed() / 255.0f, colorRGB.getGreen() / 255.0f, colorRGB.getBlue() / 255.0f, 1.0f);
        World world = Minecraft.getMinecraft().world;
        Block block = world.getBlockState(bPos).getBlock();
        AxisAlignedBB axis = null;
        if (block instanceof IWireframeAABBProvider) {
            axis = ((IWireframeAABBProvider) block).getWireframeAABB(world, bPos);
        } else {
            axis = block.getBoundingBox(world.getBlockState(bPos), world, bPos);
        }
        if (block != null && axis != null) {
            axis = axis.offset(-bPos.getX(), -bPos.getY(), -(bPos.getZ() + 1));
            GlStateManager.glLineWidth(thickness);
            this.renderBlockOutline(axis);
            GlStateManager.glLineWidth(thickness + 3.0f);
            GlStateManager.color(colorRGB.getRed() / 255.0f, colorRGB.getGreen() / 255.0f, colorRGB.getBlue() / 255.0f, 0.25f);
            this.renderBlockOutline(axis);
        }
        GlStateManager.popMatrix();
    }

    private void renderBlockOutline(AxisAlignedBB aabb) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        double ix = aabb.minX;
        double iy = aabb.minY;
        double iz = aabb.minZ;
        double ax = aabb.maxX;
        double ay = aabb.maxY;
        double az = aabb.maxZ;
        buffer.begin(GL11.GL_LINES, net.minecraft.client.renderer.vertex.DefaultVertexFormats.POSITION);
        buffer.pos(ix, iy, iz).endVertex();
        buffer.pos(ix, ay, iz).endVertex();
        buffer.pos(ix, ay, iz).endVertex();
        buffer.pos(ax, ay, iz).endVertex();
        buffer.pos(ax, ay, iz).endVertex();
        buffer.pos(ax, iy, iz).endVertex();
        buffer.pos(ax, iy, iz).endVertex();
        buffer.pos(ix, iy, iz).endVertex();
        buffer.pos(ix, iy, az).endVertex();
        buffer.pos(ix, ay, az).endVertex();
        buffer.pos(ix, iy, az).endVertex();
        buffer.pos(ax, iy, az).endVertex();
        buffer.pos(ax, iy, az).endVertex();
        buffer.pos(ax, ay, az).endVertex();
        buffer.pos(ix, ay, az).endVertex();
        buffer.pos(ax, ay, az).endVertex();
        buffer.pos(ix, iy, iz).endVertex();
        buffer.pos(ix, iy, az).endVertex();
        buffer.pos(ix, ay, iz).endVertex();
        buffer.pos(ix, ay, az).endVertex();
        buffer.pos(ax, iy, iz).endVertex();
        buffer.pos(ax, iy, az).endVertex();
        buffer.pos(ax, ay, iz).endVertex();
        buffer.pos(ax, ay, az).endVertex();
        tessellator.draw();
    }
}
