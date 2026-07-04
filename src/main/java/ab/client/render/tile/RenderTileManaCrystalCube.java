package ab.client.render.tile;

import ab.common.block.BlockManaCrystalCube;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.entity.RenderEntityItem;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.lwjgl.opengl.GL11;
import vazkii.botania.client.core.handler.ClientTickHandler;
import vazkii.botania.common.item.ModItems;

import javax.annotation.Nonnull;

public class RenderTileManaCrystalCube extends TileEntitySpecialRenderer<TileEntity> {

    private EntityItem entity = null;
    private RenderEntityItem itemRenderer = null;
    private static BlockRendererDispatcher blockRenderer;

    @Override
    public void render(@Nonnull TileEntity tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile.getWorld() == null) return;

        ItemStack stack = ItemStack.EMPTY;
        if (entity == null) {
            entity = new EntityItem(tile.getWorld(), tile.getPos().getX(), tile.getPos().getY(), tile.getPos().getZ(), new ItemStack(ModItems.twigWand));
        }
        if (itemRenderer == null) {
            itemRenderer = new RenderEntityItem(Minecraft.getMinecraft().getRenderManager(), Minecraft.getMinecraft().getRenderItem()) {
                @Override
                public boolean shouldBob() {
                    return false;
                }
            };
        }

        ReflectionHelper.setPrivateValue(EntityItem.class, entity, ClientTickHandler.ticksInGame, "age", "field_70292_b");

        double time = ClientTickHandler.ticksInGame + partialTicks;
        double worldTicks = time;

        // Render floating item
        Minecraft mc = Minecraft.getMinecraft();
        GlStateManager.pushMatrix();
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(1F, 1F, 1F, 1F);
        GlStateManager.translate(x, y, z);
        GlStateManager.translate(0.5F, 1.5F, 0.5F);
        GlStateManager.scale(1F, -1F, -1F);
        GlStateManager.translate(0F, (float) Math.sin(worldTicks / 20.0 * 1.55) * 0.025F, 0F);

        stack = entity.getItem();
        if (!stack.isEmpty()) {
            GlStateManager.pushMatrix();
            float s = stack.getItem() instanceof ItemBlock ? 0.7F : 0.5F;
            GlStateManager.translate(0F, 0.8F, 0F);
            GlStateManager.scale(s, s, s);
            GlStateManager.rotate(180F, 0F, 0F, 1F);
            itemRenderer.doRender(entity, 0, 0, 0, 1F, partialTicks);
            GlStateManager.popMatrix();
        }

        GlStateManager.color(1F, 1F, 1F);
        GlStateManager.scale(1F, -1F, -1F);
        GlStateManager.enableRescaleNormal();
        GlStateManager.popMatrix();

        // Render base model (static)
        renderBlockModel(tile, x, y, z, partialTicks, true, 0F);

        // Render glass model (floating animation, matching Botania's armature)
        // Armature: offset_y oscillates around 0.275 with amplitude 0.025, cycle ~52 ticks
        float floatOffset = 0.275F + (float) Math.sin(time * 2F * (float) Math.PI / 52F) * 0.025F;
        renderBlockModel(tile, x, y, z, partialTicks, false, floatOffset);
    }

    private void renderBlockModel(TileEntity tile, double x, double y, double z, float partialTicks, boolean isStatic, float offsetY) {
        if (blockRenderer == null) blockRenderer = Minecraft.getMinecraft().getBlockRendererDispatcher();
        BlockPos pos = tile.getPos();
        IBlockAccess world = MinecraftForgeClient.getRegionRenderCache(tile.getWorld(), pos);
        IBlockState state = world.getBlockState(pos);

        IBlockState renderState;
        if (isStatic) {
            renderState = state.withProperty(BlockManaCrystalCube.STATIC, true);
        } else {
            renderState = state.withProperty(BlockManaCrystalCube.STATIC, false);
        }

        IBakedModel model = blockRenderer.getBlockModelShapes().getModelForState(renderState);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableBlend();
        GlStateManager.disableCull();

        if (Minecraft.isAmbientOcclusionEnabled()) {
            GlStateManager.shadeModel(GL11.GL_SMOOTH);
        } else {
            GlStateManager.shadeModel(GL11.GL_FLAT);
        }

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
        buffer.setTranslation(x - pos.getX(), y - pos.getY() + offsetY, z - pos.getZ());
        blockRenderer.getBlockModelRenderer().renderModel(world, model, renderState, pos, buffer, false);
        buffer.setTranslation(0, 0, 0);
        tessellator.draw();

        RenderHelper.enableStandardItemLighting();
    }
}
