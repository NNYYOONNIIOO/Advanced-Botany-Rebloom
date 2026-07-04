package ab.client.core.handler;

import ab.AdvancedBotany;
import ab.api.IRankItem;
import ab.common.item.ItemAdvancedSpark;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;
import vazkii.botania.client.core.handler.ClientTickHandler;

import java.awt.Color;
import java.lang.reflect.Field;

public class ClientHandler {
    @SubscribeEvent
    public void clientTickEnd(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            ItemsRemainingRender.tick();
            PlayerItemUsingSound.ClientSoundHandler.tick();
        }
    }

    @SubscribeEvent
    public void onTextureStitchPre(TextureStitchEvent.Pre event) {
        if (event.getMap() instanceof TextureMap) {
            ItemAdvancedSpark.worldIcon = event.getMap().registerSprite(
                    new ResourceLocation("advanced_botany", "items/itemab.spark_0"));
        }
    }

    @SubscribeEvent
    public void onRenderTooltip(RenderTooltipEvent.PostText event) {
        ItemStack stack = event.getStack();
        if (stack.isEmpty() || !(stack.getItem() instanceof IRankItem)) return;

        Minecraft mc = Minecraft.getMinecraft();
        FontRenderer font = mc.fontRenderer;

        int mouseX = event.getX();
        int mouseY = event.getY() - 4; // Position above tooltip, like Terra Shatterer
        int width = event.getWidth();
        int height = 3;

        drawRankItemBar(stack, mouseX, mouseY, width, height, font);
    }

    private static void drawRankItemBar(ItemStack stack, int mouseX, int mouseY, int width, int height, FontRenderer font) {
        IRankItem item = (IRankItem) stack.getItem();
        int level = item.getLevel(stack);
        int max = item.getLevels()[Math.min(item.getLevels().length - 1, level + 1)];
        boolean ss = level >= item.getLevels().length - 1;
        int curr = item.getMana(stack);
        float percent = level == 0 ? 0.0f : (float) curr / (float) max;
        int rainbowWidth = Math.min(width - (ss ? 0 : 1), (int) ((float) width * percent));
        float huePer = width == 0 ? 0.0f : 1.0f / (float) width;
        float hueOff = (ClientTickHandler.ticksInGame + ClientTickHandler.partialTicks) * 0.01f;
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        // Draw bar ABOVE tooltip (mouseY - height to mouseY), like Botania's Terra Shatterer
        Gui.drawRect(mouseX - 1, mouseY - height - 1, mouseX + width + 1, mouseY, 0xFF000000);
        for (int i = 0; i < rainbowWidth; ++i) {
            Gui.drawRect(mouseX + i, mouseY - height, mouseX + i + 1, mouseY, Color.HSBtoRGB(hueOff + huePer * (float) i, 1.0f, 1.0f));
        }
        Gui.drawRect(mouseX + rainbowWidth, mouseY - height, mouseX + width, mouseY, 0xFF555555);
        String rank = I18n.format("botania.rank" + level).replaceAll("&", "\u00a7");
        GL11.glPushAttrib(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_LIGHTING);
        font.drawStringWithShadow(rank, mouseX, mouseY - 12, 0xFFFFFF);
        if (!ss) {
            rank = I18n.format("botania.rank" + (level + 1)).replaceAll("&", "\u00a7");
            font.drawStringWithShadow(rank, mouseX + width - font.getStringWidth(rank), mouseY - 12, 0xFFFFFF);
        }
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glPopAttrib();
    }

    // Creative tab search field offset
    private static final int CREATIVE_SEARCH_OFFSET = 30;
    private static Field creativeSearchFieldField;
    private static Field selectedTabIndexField;

    @SubscribeEvent
    public void onDrawScreenPre(GuiScreenEvent.DrawScreenEvent.Pre event) {
        if (!(event.getGui() instanceof GuiContainerCreative)) return;
        GuiContainerCreative gui = (GuiContainerCreative) event.getGui();
        try {
            if (creativeSearchFieldField == null) {
                creativeSearchFieldField = findField(GuiContainerCreative.class, "searchField", "field_147062_A");
            }
            if (selectedTabIndexField == null) {
                selectedTabIndexField = findField(GuiContainerCreative.class, "selectedTabIndex", "field_147058_w");
            }
            if (creativeSearchFieldField == null || selectedTabIndexField == null) return;

            int selectedIndex = selectedTabIndexField.getInt(null);
            CreativeTabs[] tabs = CreativeTabs.CREATIVE_TAB_ARRAY;
            if (selectedIndex < 0 || selectedIndex >= tabs.length) return;

            GuiTextField searchField = (GuiTextField) creativeSearchFieldField.get(gui);
            if (searchField != null) {
                int baseX = gui.getGuiLeft() + 82;
                if (tabs[selectedIndex] == AdvancedBotany.tabAB) {
                    searchField.x = baseX + CREATIVE_SEARCH_OFFSET;
                } else {
                    searchField.x = baseX;
                }
            }
        } catch (Exception e) {
            // Ignore reflection failures
        }
    }

    private static Field findField(Class<?> clazz, String... names) {
        for (String name : names) {
            try {
                Field f = clazz.getDeclaredField(name);
                f.setAccessible(true);
                return f;
            } catch (NoSuchFieldException e) {
                continue;
            }
        }
        return null;
    }
}
