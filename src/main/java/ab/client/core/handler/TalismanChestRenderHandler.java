package ab.client.core.handler;

import ab.common.item.relic.ItemTalismanHiddenRiches;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelChest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;
import vazkii.botania.client.core.handler.ClientTickHandler;

public class TalismanChestRenderHandler {

    private static final ModelChest CHEST_MODEL = new ModelChest();
    private static final ResourceLocation CHEST_TEXTURE = new ResourceLocation("textures/entity/chest/normal.png");
    private static final ResourceLocation GLOW_TEXTURE = new ResourceLocation("advanced_botany", "textures/misc/glow3.png");

    private static final int SEGMENTS = 16;
    private static final int CHEST_COUNT = 11;

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.player;
        if (player == null) return;

        ItemStack mainHand = player.getHeldItem(EnumHand.MAIN_HAND);
        ItemStack offHand = player.getHeldItem(EnumHand.OFF_HAND);
        ItemStack talisman = null;
        if (!mainHand.isEmpty() && mainHand.getItem() instanceof ItemTalismanHiddenRiches) {
            talisman = mainHand;
        } else if (!offHand.isEmpty() && offHand.getItem() instanceof ItemTalismanHiddenRiches) {
            talisman = offHand;
        }
        if (talisman == null || !ItemTalismanHiddenRiches.wasEquipped(talisman)) return;

        float rotationBase = ItemTalismanHiddenRiches.getRotationBase(talisman);
        float partialTicks = event.getPartialTicks();
        int selectedSegment = ItemTalismanHiddenRiches.getSegmentLookedAt(talisman, player);

        float segAngles = 360F / SEGMENTS;
        float shift = rotationBase - segAngles / 2 - (CHEST_COUNT / 2) * segAngles;

        Tessellator tess = Tessellator.getInstance();

        double renderPosX = mc.getRenderManager().viewerPosX;
        double renderPosY = mc.getRenderManager().viewerPosY;
        double renderPosZ = mc.getRenderManager().viewerPosZ;
        double posX = player.prevPosX + (player.posX - player.prevPosX) * partialTicks;
        double posY = player.prevPosY + (player.posY - player.prevPosY) * partialTicks;
        double posZ = player.prevPosZ + (player.posZ - player.prevPosZ) * partialTicks;

        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.translate(posX - renderPosX, posY - renderPosY + player.getDefaultEyeHeight(), posZ - renderPosZ);

        // Background arc band (Botania crafting halo style)
        float alpha = ((float) Math.sin((ClientTickHandler.ticksInGame + partialTicks) * 0.2F) * 0.5F + 0.5F) * 0.4F + 0.3F;
        float u = 1F;
        float v = 0.25F;
        float s = 3F;
        float m = 0.8F;
        float y = v * s * 2;
        float y0 = 0;

        mc.renderEngine.bindTexture(GLOW_TEXTURE);

        for (int seg = 0; seg < CHEST_COUNT; seg++) {
            boolean inside = (seg == selectedSegment);
            GlStateManager.pushMatrix();
            GlStateManager.rotate(180F, 1F, 0F, 0F);

            float a = alpha;
            if (inside) {
                a += 0.3F;
                y0 = -y;
            }

            // Purple theme
            if (seg % 2 == 0) {
                GlStateManager.color(0.5F, 0.2F, 0.7F, a);
            } else {
                GlStateManager.color(0.7F, 0.7F, 0.8F, a);
            }

            GlStateManager.disableCull();
            tess.getBuffer().begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            int subCount = (int) Math.ceil(segAngles);
            for (int i = 0; i < subCount; i++) {
                float ang = i + seg * segAngles + shift;
                double xp = Math.cos(ang * Math.PI / 180F) * s;
                double zp = Math.sin(ang * Math.PI / 180F) * s;

                tess.getBuffer().pos(xp * m, y, zp * m).tex(u, v).endVertex();
                tess.getBuffer().pos(xp, y0, zp).tex(u, 0).endVertex();

                float nextAng = Math.min(i + 1, segAngles) + seg * segAngles + shift;
                xp = Math.cos(nextAng * Math.PI / 180F) * s;
                zp = Math.sin(nextAng * Math.PI / 180F) * s;

                tess.getBuffer().pos(xp, y0, zp).tex(0, 0).endVertex();
                tess.getBuffer().pos(xp * m, y, zp * m).tex(0, v).endVertex();
            }
            y0 = 0;
            tess.draw();
            GlStateManager.enableCull();
            GlStateManager.popMatrix();
        }

        // Chests (Botania style: rotate +180 + rotate +90 = +270 to face center)
        mc.renderEngine.bindTexture(CHEST_TEXTURE);
        for (int i = 0; i < CHEST_COUNT; i++) {
            TileEntityChest chest = ItemTalismanHiddenRiches.getChestForSegment(i);
            if (chest == null) continue;

            float rotationAngle = (i + 0.5F) * segAngles + shift;
            GlStateManager.pushMatrix();
            GlStateManager.rotate(rotationAngle, 0F, 1F, 0F);
            GlStateManager.translate(s * m, -0.75F, 0F);

            float scale = (i == selectedSegment) ? 0.9F : 0.8F;
            GlStateManager.scale(scale, scale, scale);
            // ModelChest Y axis points down (like ModelRenderer default),
            // scale(1,-1,-1) flips Y and Z to match world orientation (TileEntityChestRenderer style).
            GlStateManager.scale(1.0F, -1.0F, -1.0F);
            // Front face defaults to -Z; rotate +90 (after Z flip) to face center.
            GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.translate(-0.5F, -0.5F, -0.5F);

            float lidAngle = chest.prevLidAngle + (chest.lidAngle - chest.prevLidAngle) * partialTicks;
            lidAngle = 1.0f - lidAngle;
            lidAngle = 1.0f - lidAngle * lidAngle * lidAngle;
            CHEST_MODEL.chestLid.rotateAngleX = -(lidAngle * (float) Math.PI / 2.0f);

            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            CHEST_MODEL.renderAll();

            GlStateManager.popMatrix();
        }

        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }
}
