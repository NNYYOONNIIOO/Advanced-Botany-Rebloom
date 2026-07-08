package ab.client.render.entity;

import ab.common.entity.EntityAdvancedSpark;
import ab.common.item.ItemAdvancedSpark;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import vazkii.botania.client.core.handler.MiscellaneousIcons;
import vazkii.botania.client.render.entity.RenderSparkBase;

import javax.annotation.Nonnull;

public class RenderEntityAdvancedSpark extends RenderSparkBase<EntityAdvancedSpark> {

    public RenderEntityAdvancedSpark(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    public TextureAtlasSprite getBaseIcon(EntityAdvancedSpark entity) {
        return ItemAdvancedSpark.worldIcon != null ? (TextureAtlasSprite) ItemAdvancedSpark.worldIcon : MiscellaneousIcons.INSTANCE.sparkWorldIcon;
    }

    @Override
    public TextureAtlasSprite getSpinningIcon(EntityAdvancedSpark entity) {
        int upgrade = entity.getUpgrade().ordinal() - 1;
        return upgrade >= 0 && upgrade < MiscellaneousIcons.INSTANCE.sparkUpgradeIcons.length
                ? MiscellaneousIcons.INSTANCE.sparkUpgradeIcons[upgrade] : null;
    }

    @Nonnull
    @Override
    protected ResourceLocation getEntityTexture(@Nonnull EntityAdvancedSpark entity) {
        return TextureMap.LOCATION_BLOCKS_TEXTURE;
    }

    public static class Factory implements IRenderFactory<EntityAdvancedSpark> {
        @Override
        public RenderEntityAdvancedSpark createRenderFor(RenderManager manager) {
            return new RenderEntityAdvancedSpark(manager);
        }
    }
}
