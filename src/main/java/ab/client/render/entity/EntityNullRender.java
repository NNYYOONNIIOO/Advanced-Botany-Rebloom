package ab.client.render.entity;

import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;

import javax.annotation.Nonnull;

public class EntityNullRender extends Render<Entity> {

    public EntityNullRender(RenderManager renderManager) {
        super(renderManager);
        this.shadowSize = 0.0F;
    }

    @Override
    public boolean shouldRender(Entity entity, ICamera camera, double camX, double camY, double camZ) {
        return false;
    }

    @Override
    public void doRender(@Nonnull Entity entity, double x, double y, double z, float entityYaw, float partialTicks) {
        // Empty - these entities render via particle effects only
    }

    @Nonnull
    @Override
    protected ResourceLocation getEntityTexture(@Nonnull Entity entity) {
        return TextureMap.LOCATION_BLOCKS_TEXTURE;
    }

    public static class Factory implements IRenderFactory<Entity> {
        @Override
        public Render<? super Entity> createRenderFor(RenderManager manager) {
            return new EntityNullRender(manager);
        }
    }
}
