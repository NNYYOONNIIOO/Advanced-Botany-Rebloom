package ab.client.render.tile;

import ab.client.model.ModelNidavellirForge;
import ab.common.block.tile.TileNidavellirForge;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.lwjgl.opengl.GL11;
import vazkii.botania.client.core.handler.ClientTickHandler;

public class RenderTileNidavellirForge extends TileEntitySpecialRenderer<TileEntity> {
    private List<EntityItem> entityList = null;
    private static final ResourceLocation texture = new ResourceLocation("advanced_botany", "textures/model/nidavellirforge.png");
    private static final ModelNidavellirForge model = new ModelNidavellirForge();

    @Override
    public void render(TileEntity tileentity, double x, double y, double z, float f, int destroyStage, float alpha) {
        TileNidavellirForge tile = (TileNidavellirForge)tileentity;
        double worldTime = 0.0;
        int meta = 2;
        float invRender = 0.0f;
        if (tileentity != null && tileentity.getWorld() != null) {
            worldTime = (float)ClientTickHandler.ticksInGame + f + (float)new Random(tileentity.getPos().getX() ^ tileentity.getPos().getY() ^ tileentity.getPos().getZ()).nextInt(360);
            meta = tileentity.getWorld().getBlockState(tileentity.getPos()).getBlock().getMetaFromState(tileentity.getWorld().getBlockState(tileentity.getPos()));
        } else {
            invRender = 0.0875f;
        }
        float indetY = (float)(Math.sin(worldTime / 18.0) / 24.0);
        GL11.glPushMatrix();
        GL11.glEnable(32826);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glTranslated(x - invRender, y, z);
        GL11.glTranslatef(0.5f, 1.5f, 0.5f);
        GL11.glRotatef(180.0f, 1.0f, 0.0f, 1.0f);
        GL11.glRotatef(90.0f * meta, 0.0f, 1.0f, 0.0f);
        Minecraft.getMinecraft().renderEngine.bindTexture(texture);
        model.renderBottom();
        GL11.glTranslatef(0.0f, indetY, 0.0f);
        model.renderTop();
        GL11.glEnable(32826);
        GL11.glPopMatrix();
        if (this.entityList == null) {
            ArrayList<EntityItem> list = new ArrayList<EntityItem>();
            net.minecraft.world.World entityWorld = tile.getWorld() != null ? tile.getWorld() : Minecraft.getMinecraft().world;
            double ex = tile.getPos() != null ? tile.getPos().getX() : 0;
            double ey = tile.getPos() != null ? tile.getPos().getY() : 0;
            double ez = tile.getPos() != null ? tile.getPos().getZ() : 0;
            for (int i = 0; i < tile.getSizeInventory(); ++i) {
                list.add(new EntityItem(entityWorld, ex, ey, ez));
            }
            this.entityList = list;
        }
        GL11.glPushMatrix();
        GL11.glTranslated(x + 0.5, y + 0.675 - indetY, z + 0.5);
        GL11.glScalef(0.2f, 0.225f, 0.225f);
        for (int i = 1; i < this.entityList.size(); ++i) {
            GL11.glPushMatrix();
            ItemStack stack = tile.getStackInSlot(i);
            if (!stack.isEmpty()) {
                switch (i) {
                    case 1:
                        GL11.glTranslated(0.15f, 0.0, 0.0);
                        break;
                    case 2:
                        GL11.glTranslated(-0.15f, 0.0, -0.15f);
                        break;
                    case 3:
                        GL11.glTranslated(-0.15f, 0.0, 0.15f);
                        break;
                }
                this.entityList.get(i).setItem(stack);
                ReflectionHelper.setPrivateValue(EntityItem.class, this.entityList.get(i), ClientTickHandler.ticksInGame, "age", "field_70292_b");
                Minecraft.getMinecraft().getRenderManager().renderEntity(this.entityList.get(i), 0.0, 0.0, 0.0, 0.0f, f, false);
            }
            GL11.glPopMatrix();
        }
        GL11.glPopMatrix();
        if (!tile.getStackInSlot(0).isEmpty()) {
            GL11.glPushMatrix();
            GL11.glTranslated(x + 0.5, y + 0.915 - indetY, z + 0.5);
            GL11.glScalef(0.45f, 0.45f, 0.45f);
            this.entityList.get(0).setItem(tile.getStackInSlot(0));
            ReflectionHelper.setPrivateValue(EntityItem.class, this.entityList.get(0), ClientTickHandler.ticksInGame, "age", "field_70292_b");
            Minecraft.getMinecraft().getRenderManager().renderEntity(this.entityList.get(0), 0.0, 0.0, 0.0, 0.0f, f, false);
            GL11.glPopMatrix();
        }
    }
}
