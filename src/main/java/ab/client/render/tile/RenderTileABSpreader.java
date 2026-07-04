package ab.client.render.tile;

import ab.common.block.tile.TileABSpreader;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import vazkii.botania.api.mana.ILens;
import vazkii.botania.client.core.handler.ClientTickHandler;
import vazkii.botania.client.core.proxy.ClientProxy;
import vazkii.botania.client.model.ModelSpreader;

public class RenderTileABSpreader extends TileEntitySpecialRenderer<TileEntity> {
    private static final ResourceLocation texture_0 = new ResourceLocation("advanced_botany", "textures/model/lebethronspreader.png");
    private static final ResourceLocation texture_1 = new ResourceLocation("advanced_botany", "textures/model/lebethronspreader_halloween.png");
    private static final ModelSpreader model = new ModelSpreader();

    @Override
    public void render(TileEntity tileentity, double x, double y, double z, float ticks, int destroyStage, float alpha) {
        TileABSpreader spreader = (TileABSpreader)tileentity;
        GL11.glPushMatrix();
        GL11.glEnable(32826);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glTranslated(x, y, z);
        GL11.glTranslatef(0.5f, 1.5f, 0.5f);
        GL11.glRotatef(spreader.rotationX + 90.0f, 0.0f, 1.0f, 0.0f);
        GL11.glTranslatef(0.0f, -1.0f, 0.0f);
        GL11.glRotatef(spreader.rotationY, 1.0f, 0.0f, 0.0f);
        GL11.glTranslatef(0.0f, 1.0f, 0.0f);
        if (!ClientProxy.dootDoot) {
            Minecraft.getMinecraft().renderEngine.bindTexture(texture_0);
        } else {
            Minecraft.getMinecraft().renderEngine.bindTexture(texture_1);
        }
        GL11.glScalef(1.0f, -1.0f, -1.0f);
        double time = (float)ClientTickHandler.ticksInGame + ticks;
        model.render();
        GL11.glColor3f(1.0f, 1.0f, 1.0f);
        GL11.glPushMatrix();
        double worldTicks = tileentity.getWorld() == null ? 0.0 : time;
        GL11.glRotatef((float)(worldTicks % 360.0f), 0.0f, 1.0f, 0.0f);
        GL11.glTranslatef(0.0f, (float)Math.sin(worldTicks / 20.0) * 0.05f, 0.0f);
        model.renderCube();
        GL11.glPopMatrix();
        GL11.glScalef(1.0f, -1.0f, -1.0f);
        ItemStack stack = spreader.getItemHandler().getStackInSlot(0);
        if (!stack.isEmpty()) {
            Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            GL11.glPushMatrix();
            GL11.glTranslatef(-0.4f, -1.4f, -0.4375f);
            GL11.glScalef(0.8f, 0.8f, 0.8f);
            Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.GROUND);
            GL11.glPopMatrix();
        }
        if (spreader.paddingColor != -1) {
            Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            IBlockState state = Blocks.STAINED_HARDENED_CLAY.getStateFromMeta(spreader.paddingColor);
            BlockRendererDispatcher blockRenderer = Minecraft.getMinecraft().getBlockRendererDispatcher();
            GL11.glTranslatef(0.0f, -0.0625f, 0.0f);
            blockRenderer.renderBlockBrightness(state, 1.0f);
            GL11.glTranslatef(0.0f, -0.9375f, 0.0f);
            blockRenderer.renderBlockBrightness(state, 1.0f);
            GL11.glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
            GL11.glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
            GL11.glPushMatrix();
            GL11.glScalef(0.875f, 1.0f, 1.0f);
            blockRenderer.renderBlockBrightness(state, 1.0f);
            GL11.glPopMatrix();
            GL11.glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
            GL11.glTranslatef(0.0f, 0.0f, -0.03125f);
            GL11.glScalef(0.875f, 1.0f, 0.9375f);
            blockRenderer.renderBlockBrightness(state, 1.0f);
            GL11.glTranslatef(0.0f, 0.9375f, 0.0f);
            blockRenderer.renderBlockBrightness(state, 1.0f);
        }
        GL11.glEnable(32826);
        GL11.glPopMatrix();
    }
}
