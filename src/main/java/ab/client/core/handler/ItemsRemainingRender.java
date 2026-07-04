package ab.client.core.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

public class ItemsRemainingRender {
    private static int maxTicks = 30;
    private static int leaveTicks = 20;
    private static ItemStack stack;
    private static int ticks;
    private static String text;

    @SideOnly(Side.CLIENT)
    public static void render(ScaledResolution resolution, float partTicks) {
        if (ticks > 0 && stack != null && !stack.isEmpty()) {
            int pos = maxTicks - ticks;
            Minecraft mc = Minecraft.getMinecraft();
            int x = resolution.getScaledWidth() / 2 + 10 + Math.max(0, pos - leaveTicks);
            int y = resolution.getScaledHeight() / 2;
            int start = maxTicks - leaveTicks;
            float alpha = ticks + partTicks > start ? 1.0f : (ticks + partTicks) / start;
            GL11.glDisable(GL11.GL_ALPHA_TEST);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glEnable(0x8037);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, alpha);
            RenderHelper.enableGUIStandardItemLighting();
            int xp = x + (int) (16.0f * (1.0f - alpha));
            GL11.glTranslatef(xp, y, 0.0f);
            GL11.glScalef(alpha, 1.0f, 1.0f);
            mc.getRenderItem().renderItemAndEffectIntoGUI(stack, 0, 0);
            GL11.glScalef(1.0f / alpha, 1.0f, 1.0f);
            GL11.glTranslatef(-xp, -y, 0.0f);
            RenderHelper.disableStandardItemLighting();
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            GL11.glEnable(GL11.GL_BLEND);
            int color = 0xFFFFFF | (int) (alpha * 255.0f) << 24;
            mc.fontRenderer.drawStringWithShadow(text, x + 20, y + 6, color);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_ALPHA_TEST);
        }
    }

    @SideOnly(Side.CLIENT)
    public static void tick() {
        if (ticks > 0) {
            --ticks;
        }
    }

    public static void set(ItemStack stack, String text) {
        ItemsRemainingRender.stack = stack;
        ItemsRemainingRender.text = text;
        ticks = stack == null || stack.isEmpty() ? 0 : maxTicks;
    }
}
