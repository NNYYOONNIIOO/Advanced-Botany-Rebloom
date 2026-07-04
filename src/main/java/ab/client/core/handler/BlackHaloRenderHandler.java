package ab.client.core.handler;

import ab.common.item.equipment.ItemBlackHalo;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;
import vazkii.botania.client.core.handler.ClientTickHandler;

public class BlackHaloRenderHandler {

    private static final ResourceLocation GLOW_TEXTURE = new ResourceLocation("advanced_botany", "textures/misc/glow.png");
    private static final int SEGMENTS = 12;

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.player;
        if (player == null) return;

        ItemStack mainHand = player.getHeldItem(EnumHand.MAIN_HAND);
        ItemStack offHand = player.getHeldItem(EnumHand.OFF_HAND);
        ItemStack halo = null;
        if (!mainHand.isEmpty() && mainHand.getItem() instanceof ItemBlackHalo) {
            halo = mainHand;
        } else if (!offHand.isEmpty() && offHand.getItem() instanceof ItemBlackHalo) {
            halo = offHand;
        }
        if (halo == null || !ItemBlackHalo.wasEquipped(halo)) return;

        float rotationBase = ItemBlackHalo.getRotationBase(halo);
        float partialTicks = event.getPartialTicks();
        int selectedSegment = ItemBlackHalo.getSegmentLookedAt(halo, player);

        int segAngles = 360 / SEGMENTS;
        float shift = rotationBase - segAngles / 2;

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

        for (int seg = 0; seg < SEGMENTS; seg++) {
            boolean inside = (seg == selectedSegment);
            GlStateManager.pushMatrix();
            GlStateManager.rotate(180F, 1F, 0F, 0F);

            float a = alpha;
            if (inside) {
                a += 0.3F;
                y0 = -y;
            }

            // Dark purple theme
            if (seg % 2 == 0) {
                GlStateManager.color(0.3F, 0.0F, 0.5F, a);
            } else {
                GlStateManager.color(0.5F, 0.0F, 0.8F, a);
            }

            GlStateManager.disableCull();
            tess.getBuffer().begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            for (int i = 0; i < segAngles; i++) {
                float ang = i + seg * segAngles + shift;
                double xp = Math.cos(ang * Math.PI / 180F) * s;
                double zp = Math.sin(ang * Math.PI / 180F) * s;

                tess.getBuffer().pos(xp * m, y, zp * m).tex(u, v).endVertex();
                tess.getBuffer().pos(xp, y0, zp).tex(u, 0).endVertex();

                xp = Math.cos((ang + 1) * Math.PI / 180F) * s;
                zp = Math.sin((ang + 1) * Math.PI / 180F) * s;

                tess.getBuffer().pos(xp, y0, zp).tex(0, 0).endVertex();
                tess.getBuffer().pos(xp * m, y, zp * m).tex(0, v).endVertex();
            }
            y0 = 0;
            tess.draw();
            GlStateManager.enableCull();
            GlStateManager.popMatrix();
        }

        // Items (Botania style: rotate +180 + rotate +90 = +270 to face center)
        for (int seg = 0; seg < SEGMENTS; seg++) {
            ItemStack talisman = ItemBlackHalo.getItemForSlot(halo, seg);
            if (talisman.isEmpty()) continue;

            Block block = vazkii.botania.common.item.ItemBlackHoleTalisman.getBlock(talisman);
            int meta = vazkii.botania.common.item.ItemBlackHoleTalisman.getBlockMeta(talisman);
            ItemStack renderStack;
            if (block != null && block != Blocks.AIR) {
                renderStack = new ItemStack(block, 1, meta);
            } else {
                renderStack = talisman.copy();
            }

            float rotationAngle = (seg + 0.5F) * segAngles + shift;
            GlStateManager.pushMatrix();
            GlStateManager.rotate(rotationAngle, 0F, 1F, 0F);
            GlStateManager.translate(s * m, -0.75F, 0F);

            float scale = (seg == selectedSegment) ? 0.9F : 0.8F;
            GlStateManager.scale(scale, scale, scale);
            GlStateManager.rotate(180F, 0F, 1F, 0F);
            GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);

            mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            RenderHelper.enableStandardItemLighting();
            mc.getRenderItem().renderItem(renderStack, ItemCameraTransforms.TransformType.GUI);
            RenderHelper.disableStandardItemLighting();

            GlStateManager.popMatrix();
        }

        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }
}
