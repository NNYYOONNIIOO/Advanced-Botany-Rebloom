package ab.client.render.tile;

import ab.client.core.ClientHelper;
import ab.client.model.ModelManaContainer;
import ab.common.block.tile.TileManaContainer;
import java.awt.Color;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;
import vazkii.botania.client.core.handler.ClientTickHandler;
import vazkii.botania.client.core.handler.MiscellaneousIcons;
import vazkii.botania.client.core.helper.ShaderHelper;

public class RenderTileManaContainer extends TileEntitySpecialRenderer<TileEntity> {
    private static final ResourceLocation texture = new ResourceLocation("advanced_botany", "textures/model/mana_container.png");
    private static final ResourceLocation texture1 = new ResourceLocation("advanced_botany", "textures/model/mana_container1.png");
    private static final ModelManaContainer model = new ModelManaContainer();
    public static int metadata;

    @Override
    public void render(TileEntity tileentity, double x, double y, double z, float ticks, int destroyStage, float alpha) {
        boolean dil;
        double worldTime;
        TileManaContainer container = (TileManaContainer)tileentity;
        worldTime = tileentity.getWorld() == null ? 0.0 : (double)((float)ClientTickHandler.ticksInGame + ticks);
        if (tileentity != null) {
            worldTime += (double)new Random(tileentity.getPos().getX() ^ tileentity.getPos().getY() ^ tileentity.getPos().getZ()).nextInt(360);
        }
        dil = tileentity.getWorld() == null ? metadata == 1 : tileentity.getBlockMetadata() == 1;
        boolean fab = tileentity.getWorld() == null ? metadata == 2 : tileentity.getBlockMetadata() == 2;
        GL11.glPushMatrix();
        GL11.glEnable(32826);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glTranslated(x, y, z);
        GL11.glTranslatef(0.5f, 1.075f, 0.5f);
        GL11.glScalef(0.7f, 0.7f, 0.7f);
        GL11.glRotatef((float)worldTime * 0.375f, 0.0f, 1.0f, 0.0f);
        GL11.glTranslated(0.0, Math.sin(worldTime / 80.0) / 20.0 - 0.025, 0.0);
        GL11.glRotatef(180.0f, 1.0f, 0.0f, 1.0f);
        if (fab) {
            float time = (float)ClientTickHandler.ticksInGame + ClientTickHandler.partialTicks;
            if (tileentity != null) {
                time += (float)new Random(tileentity.getPos().getX() ^ tileentity.getPos().getY() ^ tileentity.getPos().getZ()).nextInt(100000);
            }
            Color color = Color.getHSBColor(time * 0.005f, 0.6f, 1.0f);
            GL11.glColor4ub((byte)color.getRed(), (byte)color.getGreen(), (byte)color.getBlue(), (byte)-1);
        }
        Minecraft.getMinecraft().renderEngine.bindTexture(dil ? texture1 : texture);
        model.render();
        this.renderMana(tileentity.getWorld(), container);
        GL11.glEnable(32826);
        GL11.glPopMatrix();
        metadata = 0;
    }

    public void renderMana(World world, TileManaContainer container) {
        if (world != null) {
            Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            int mana = container.getCurrentMana();
            int cap = container.getMaxMana();
            float waterLevel = (float)mana / (float)cap * 0.8f;
            if (waterLevel > 0.0f) {
                float s = 0.5f;
                GL11.glPushMatrix();
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(770, 771);
                GL11.glDisable(3008);
                GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                GL11.glScalef(s, s, s);
                GL11.glTranslatef(0.0f, 1.71f - waterLevel, -0.25f);
                GL11.glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
                ShaderHelper.useShader(ShaderHelper.manaPool);
                ClientHelper.renderIcon(MiscellaneousIcons.INSTANCE.manaWater, 240);
                ShaderHelper.releaseShader();
                GL11.glEnable(3008);
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glPopMatrix();
            }
        }
    }
}
