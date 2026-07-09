package ab;

import ab.common.core.proxy.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.creativetab.CreativeTabs;

@Mod(modid = AdvancedBotany.modid, name = "Advanced Botany", version = "1.3.1.7", dependencies = "required-after:botania;after:crafttweaker", guiFactory = "ab.client.core.handler.GuiABFactory")
public class AdvancedBotany {
    public static final String modid = "advanced_botany";
    public static final String version = "1.3.1.7";
    @Mod.Instance(value = "advanced_botany")
    public static AdvancedBotany instance;
    @SidedProxy(clientSide = "ab.client.core.proxy.ClientProxy", serverSide = "ab.common.core.proxy.CommonProxy")
    public static CommonProxy proxy;
    public static final CreativeTabs tabAB = new AdvancedBotanyTab("tabAB");

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }
}
