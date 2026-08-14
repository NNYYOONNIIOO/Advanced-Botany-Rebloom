package ab.client.core.handler;

import ab.common.lib.register.BlockListAB;
import ab.common.lib.register.ItemListAB;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(modid = "advanced_botany", value = Side.CLIENT)
public class ModelRegisterHandler {

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        registerBlockModels();
        registerItemModels();
    }

    private static void registerBlockModels() {
        // Simple blocks (no metadata)
        registerBlockModel(BlockListAB.blockABSpreader, 0, "blockabspreader");
        registerBlockModel(BlockListAB.blockABPlate, 0, "blockabplate");
        registerBlockModel(BlockListAB.blockManaCrystalCube, 0, "blockmanacrystalcube");
        registerBlockModel(BlockListAB.blockTerraFarmland, 0, "blockterrafarmland");
        registerBlockModel(BlockListAB.blockManaCharger, 0, "blockmanacharger");
        registerBlockModel(BlockListAB.blockEngineerHopper, 0, "blockengineerhopper");
        registerBlockModel(BlockListAB.blockFreyrLiana, 0, "blockfreyrliana");
        registerBlockModel(BlockListAB.blockLuminousFreyrLiana, 0, "blockluminousfreyrliana");
        registerBlockModel(BlockListAB.blockABStorage, 0, "blockabstorage");

        // Blocks with metadata
        for (int i = 0; i < 5; i++) {
            registerBlockModel(BlockListAB.blockLebethron, i, "blocklebethron_" + i);
        }
        for (int i = 0; i < 3; i++) {
            registerBlockModel(BlockListAB.blockManaContainer, i, "blockmanacontainer_" + i);
        }
        for (int i = 0; i < 2; i++) {
            registerBlockModel(BlockListAB.blockBoardFate, i, "blockboardfate_" + i);
        }
    }

    private static void registerItemModels() {
        // Simple items (no metadata)
        registerItemModel(ItemListAB.itemMithrillRing, 0, "itemmithrillring");
        registerItemModel(ItemListAB.itemNebulaRing, 0, "itemnebularing");
        registerItemModel(ItemListAB.itemMihrillMultiTool, 0, "itemmihrillmultitool");
        registerItemModel(ItemListAB.itemMihrillMultiTool, 1, "itemmihrillmultitool_1");
        registerItemModel(ItemListAB.itemMithrillSword, 0, "itemmithrillsword");
        registerItemModel(ItemListAB.itemTerraHoe, 0, "itemterrahoe");
        registerItemModel(ItemListAB.itemManaFlower, 0, "itemmanaflower");
        registerItemModel(ItemListAB.itemBlackHalo, 0, "itemblackhalo");
        registerItemModel(ItemListAB.itemNebulaBlaze, 0, "itemnebulablaze");
        registerItemModel(ItemListAB.itemNebulaRod, 0, "itemnebularod");
        registerItemModel(ItemListAB.itemSprawlRod, 0, "itemsprawlrod");
        registerItemModel(ItemListAB.itemAquaSword, 0, "itemaquasword");
        registerItemModel(ItemListAB.itemPocketWardrobe, 0, "itempocketwardrobe");
        registerItemModel(ItemListAB.itemTalismanHiddenRiches, 0, "itemtalismanhiddenriches");
        registerItemModel(ItemListAB.itemFreyrSlingshot, 0, "itemfreyrslingshot");
        registerItemModel(ItemListAB.itemHornPlenty, 0, "itemhornplenty");
        registerItemModel(ItemListAB.itemNebulaHelm, 0, "itemnebulahelm");
        registerItemModel(ItemListAB.itemNebulaChest, 0, "itemnebulachest");
        registerItemModel(ItemListAB.itemNebulaLegs, 0, "itemnebulalegs");
        registerItemModel(ItemListAB.itemNebulaBoots, 0, "itemnebulaboots");
        registerItemModel(ItemListAB.itemWildHuntHelm, 0, "itemwildhunthelm");
        registerItemModel(ItemListAB.itemWildHuntChest, 0, "itemwildhuntchest");
        registerItemModel(ItemListAB.itemWildHuntLegs, 0, "itemwildhuntlegs");
        registerItemModel(ItemListAB.itemWildHuntBoots, 0, "itemwildhuntboots");

        // Items with metadata
        for (int i = 0; i < 7; i++) {
            registerItemModel(ItemListAB.itemABResource, i, "itemabresource_" + i);
        }
        for (int i = 0; i < 2; i++) {
            registerItemModel(ItemListAB.itemAdvancedSpark, i, "itemadvancedspark_" + i);
        }
        registerItemModel(ItemListAB.itemAntigravityCharm, 0, "itemantigravitycharm_active");
        registerItemModel(ItemListAB.itemAntigravityCharm, 1, "itemantigravitycharm_inactive");
        registerItemModel(ItemListAB.itemAntigravityCharm, 2, "itemantigravitycharm_base");
        registerItemModel(ItemListAB.itemAntigravityCharm, 3, "itemantigravitycharm_overlay");

        registerItemModel(ItemListAB.itemSphereNavigation, 0, "itemspherenavigation_active");
        registerItemModel(ItemListAB.itemSphereNavigation, 1, "itemspherenavigation_inactive");
        registerItemModel(ItemListAB.itemSphereNavigation, 2, "itemspherenavigation_base");
        registerItemModel(ItemListAB.itemSphereNavigation, 3, "itemspherenavigation_target");
        registerItemModel(ItemListAB.itemSphereNavigation, 5, "itemspherenavigation_disabled");
        registerItemModel(ItemListAB.itemSphereNavigation, 6, "itemspherenavigation_enabled");
    }

    private static void registerBlockModel(Block block, int meta, String modelPath) {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), meta,
                new ModelResourceLocation("advanced_botany:" + modelPath, "inventory"));
    }

    private static void registerItemModel(Item item, int meta, String modelPath) {
        ModelLoader.setCustomModelResourceLocation(item, meta,
                new ModelResourceLocation("advanced_botany:" + modelPath, "inventory"));
    }
}
