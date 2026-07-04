package ab.common.core.proxy;

import ab.AdvancedBotany;
import ab.common.core.handler.ConfigABHandler;
import ab.common.core.handler.GuiHandler;
import ab.common.core.handler.NetworkHandler;
import ab.common.lib.register.AchievementRegister;
import ab.common.lib.register.BlockListAB;
import ab.common.lib.register.EntityListAB;
import ab.common.lib.register.FlowerRegister;
import ab.common.item.equipment.armor.ItemNebulaArmor;
import ab.common.lib.register.ItemListAB;
import ab.common.lib.register.RecipeListAB;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class CommonProxy {
    public void preInit(FMLPreInitializationEvent event) {
        BlockListAB.init();
        ItemListAB.init();
        AchievementRegister.init();
        EntityListAB.init();
        FlowerRegister.init();
        ConfigABHandler.loadConfig(event.getSuggestedConfigurationFile());
        MinecraftForge.EVENT_BUS.register(ItemListAB.itemAntigravityCharm);
        MinecraftForge.EVENT_BUS.register(new ItemNebulaArmor.DamageHandler());
    }

    public void init(FMLInitializationEvent event) {
        NetworkRegistry.INSTANCE.registerGuiHandler(AdvancedBotany.instance, new GuiHandler());
        RecipeListAB.init();
        NetworkHandler.registerPackets();
    }

    public void postInit(FMLPostInitializationEvent event) {
        ConfigABHandler.loadPostInit();
    }
}
