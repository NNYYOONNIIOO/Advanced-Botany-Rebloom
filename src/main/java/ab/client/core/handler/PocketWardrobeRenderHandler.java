package ab.client.core.handler;

import ab.common.item.relic.ItemPocketWardrobe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;
import vazkii.botania.client.core.handler.ClientTickHandler;

public class PocketWardrobeRenderHandler {

    private static final ResourceLocation GLOW_TEXTURE = new ResourceLocation("advanced_botany", "textures/misc/glow1.png");

    private static final int SEGMENTS = 12;
    private static final int ARMOR_COUNT = 5;
    private static final float ARMOR_Y_OFFSET = -1.5F;

    private EntityArmorStand virtualArmorStand;

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.player;
        if (player == null) return;

        ItemStack mainHand = player.getHeldItem(EnumHand.MAIN_HAND);
        ItemStack offHand = player.getHeldItem(EnumHand.OFF_HAND);
        ItemStack wardrobe = null;
        if (!mainHand.isEmpty() && mainHand.getItem() instanceof ItemPocketWardrobe) {
            wardrobe = mainHand;
        } else if (!offHand.isEmpty() && offHand.getItem() instanceof ItemPocketWardrobe) {
            wardrobe = offHand;
        }
        if (wardrobe == null || !ItemPocketWardrobe.wasEquipped(wardrobe)) return;

        float rotationBase = ItemPocketWardrobe.getRotationBase(wardrobe);
        float partialTicks = event.getPartialTicks();
        int selectedSegment = ItemPocketWardrobe.getSegmentLookedAt(wardrobe, player);

        int segAngles = 360 / SEGMENTS;
        float shift = rotationBase - segAngles / 2 - (ARMOR_COUNT / 2) * segAngles;

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

        for (int seg = 0; seg < ARMOR_COUNT; seg++) {
            boolean inside = (seg == selectedSegment);
            GlStateManager.pushMatrix();
            GlStateManager.rotate(180F, 1F, 0F, 0F);

            float a = alpha;
            if (inside) {
                a += 0.3F;
                y0 = -y;
            }

            // Blue theme
            if (seg % 2 == 0) {
                GlStateManager.color(0.3F, 0.5F, 0.8F, a);
            } else {
                GlStateManager.color(0.7F, 0.7F, 0.8F, a);
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

        // Armor stands (renderYawOffset = 90 to face center)
        renderArmorItems(mc, wardrobe, shift, segAngles, selectedSegment, partialTicks, s, m);

        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    private void renderArmorItems(Minecraft mc, ItemStack wardrobe, float shift, int segAngles, int selectedSegment, float partialTicks, float s, float m) {
        if (virtualArmorStand == null || virtualArmorStand.world != mc.world) {
            virtualArmorStand = new EntityArmorStand(mc.world);
            virtualArmorStand.setInvisible(true);
            virtualArmorStand.setNoGravity(true);
            virtualArmorStand.setSilent(true);
        }

        for (int i = 0; i < ARMOR_COUNT; i++) {
            ItemStack[] armorSet = ItemPocketWardrobe.getArmorSet(wardrobe, i);
            if (armorSet == null) continue;

            boolean hasArmor = false;
            for (int j = 0; j < 4; j++) {
                if (armorSet[j] != null && !armorSet[j].isEmpty()) {
                    hasArmor = true;
                    break;
                }
            }
            if (!hasArmor) continue;

            float rotationAngle = (i + 0.5F) * segAngles + shift;
            GlStateManager.pushMatrix();
            GlStateManager.rotate(rotationAngle, 0F, 1F, 0F);
            GlStateManager.translate(s * m, ARMOR_Y_OFFSET, 0F);

            float scale = (i == selectedSegment) ? 0.9F : 0.8F;
            GlStateManager.scale(scale, scale, scale);

            virtualArmorStand.setItemStackToSlot(EntityEquipmentSlot.HEAD, armorSet[3] != null ? armorSet[3] : ItemStack.EMPTY);
            virtualArmorStand.setItemStackToSlot(EntityEquipmentSlot.CHEST, armorSet[2] != null ? armorSet[2] : ItemStack.EMPTY);
            virtualArmorStand.setItemStackToSlot(EntityEquipmentSlot.LEGS, armorSet[1] != null ? armorSet[1] : ItemStack.EMPTY);
            virtualArmorStand.setItemStackToSlot(EntityEquipmentSlot.FEET, armorSet[0] != null ? armorSet[0] : ItemStack.EMPTY);

            // renderYawOffset = 90 makes RenderLivingBase's (180 - renderYawOffset) = 90,
            // combined with scale(-1,-1,1) flip, results in armor stand facing center.
            float yaw = 90F;
            virtualArmorStand.rotationYawHead = yaw;
            virtualArmorStand.prevRotationYawHead = yaw;
            virtualArmorStand.rotationYaw = yaw;
            virtualArmorStand.renderYawOffset = yaw;
            virtualArmorStand.prevRotationYaw = yaw;
            virtualArmorStand.prevRenderYawOffset = yaw;
            virtualArmorStand.setPosition(0, 0, 0);
            virtualArmorStand.prevPosX = 0;
            virtualArmorStand.prevPosY = 0;
            virtualArmorStand.prevPosZ = 0;

            RenderHelper.enableStandardItemLighting();
            mc.getRenderManager().renderEntity(virtualArmorStand, 0.0, 0.0, 0.0, 0.0F, partialTicks, false);
            RenderHelper.disableStandardItemLighting();

            GlStateManager.popMatrix();
        }
    }
}
