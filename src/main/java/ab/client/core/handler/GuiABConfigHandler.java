package ab.client.core.handler;

import ab.common.core.handler.ConfigABHandler;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.common.config.ConfigElement;

public class GuiABConfigHandler extends GuiConfig {
    public GuiABConfigHandler(GuiScreen parentScreen) {
        super(parentScreen, new ConfigElement(ConfigABHandler.config.getCategory("general")).getChildElements(), "advanced_botany", false, false, GuiConfig.getAbridgedConfigPath(ConfigABHandler.config.toString()));
    }
}
