package ab.client.integration.thaumcraft;

import ab.common.integration.thaumcraft.AspecolusResearchHandler;
import java.lang.reflect.Field;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import thaumcraft.api.research.theorycraft.ResearchTableData;
import thaumcraft.client.gui.GuiResearchTable;
import thaumcraft.common.container.ContainerResearchTable;
import thaumcraft.common.tiles.crafting.TileResearchTable;

public class AspecolusClientHandler {
    private static final Field DUMMY_INSPIRATION = findField("dummyInspirationStart");
    private static final Field NEXT_CHECK = findField("nextCheck");
    private static final Field CURRENT_AIDS = findField("currentAids");
    private static boolean active;
    private static int activeWindowId = -1;

    public static void setActive(int windowId, boolean enabled) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player != null && mc.player.openContainer instanceof ContainerResearchTable
                && mc.player.openContainer.windowId == windowId) {
            active = enabled;
            activeWindowId = windowId;
        }
    }

    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event) {
        if (event.getGui() == null) {
            active = false;
            activeWindowId = -1;
        }
    }

    @SubscribeEvent
    public void onDrawGui(GuiScreenEvent.DrawScreenEvent.Pre event) {
        if (!(event.getGui() instanceof GuiResearchTable)
                || DUMMY_INSPIRATION == null || NEXT_CHECK == null || CURRENT_AIDS == null) return;
        GuiContainer gui = (GuiContainer) event.getGui();
        if (!(gui.inventorySlots instanceof ContainerResearchTable)) return;
        TileResearchTable table = ((ContainerResearchTable) gui.inventorySlots).tileEntity;
        EntityPlayer player = Minecraft.getMinecraft().player;
        if (player == null || table.data != null) return;
        try {
            if (NEXT_CHECK.getLong(gui) < player.ticksExisted) {
                CURRENT_AIDS.set(gui, table.checkSurroundingAids());
                NEXT_CHECK.setLong(gui, player.ticksExisted + 100L);
            }
            DUMMY_INSPIRATION.setInt(gui, ResearchTableData.getAvailableInspiration(player)
                    + (active && activeWindowId == gui.inventorySlots.windowId ? AspecolusResearchHandler.BONUS : 0));
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Unable to update Thaumcraft research table", e);
        }
    }

    private static Field findField(String name) {
        try {
            Field field = GuiResearchTable.class.getDeclaredField(name);
            field.setAccessible(true);
            return field;
        } catch (ReflectiveOperationException e) {
            LogManager.getLogger("advanced_botany").error("Could not find Thaumcraft GUI field {}", name, e);
            return null;
        }
    }
}
