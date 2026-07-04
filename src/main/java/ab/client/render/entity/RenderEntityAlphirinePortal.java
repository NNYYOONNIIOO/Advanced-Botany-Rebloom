package ab.client.render.entity;

import ab.client.core.ClientHelper;
import ab.common.block.BlockLebethronWood;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import vazkii.botania.client.core.handler.ClientTickHandler;

import javax.annotation.Nullable;

public class RenderEntityAlphirinePortal extends Render<Entity> {
    public RenderEntityAlphirinePortal(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float fl, float fl1) {
        double worldTime = entity.world == null ? 0.0 : (double)((float)ClientTickHandler.ticksInGame + fl1);
        if (entity != null) {
            worldTime += (double)new Random((int)entity.posX ^ (int)entity.posY ^ (int)entity.posZ).nextInt(360);
        }
        Minecraft mc = Minecraft.getMinecraft();
        float burn = Math.min(1.0f, (float)entity.ticksExisted * 0.0561f);
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(770, 771);
        burn = Math.max(0.0f, (float)((double)burn + Math.sin(worldTime / 3.2) / 9.0));
        GL11.glScalef(burn / 3.15f, burn / 3.15f, burn / 3.15f);
        GL11.glRotatef(180.0f - this.renderManager.playerViewY, 0.0f, 1.0f, 0.0f);
        GL11.glRotatef(-this.renderManager.playerViewX, 1.0f, 0.0f, 0.0f);
        GL11.glDisable(GL11.GL_CULL_FACE);
        mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        ClientHelper.setLightmapTextureCoords();
        ClientHelper.renderIcon(BlockLebethronWood.portalIcon, 220);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return TextureMap.LOCATION_BLOCKS_TEXTURE;
    }

    public static class Factory implements net.minecraftforge.fml.client.registry.IRenderFactory<Entity> {
        @Override
        public Render<? super Entity> createRenderFor(RenderManager manager) {
            return new RenderEntityAlphirinePortal(manager);
        }
    }
}
