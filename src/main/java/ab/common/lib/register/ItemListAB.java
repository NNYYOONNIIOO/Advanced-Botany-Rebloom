package ab.common.lib.register;

import ab.AdvancedBotany;
import ab.common.item.ItemABResource;
import ab.common.item.ItemAdvancedSpark;
import ab.common.item.ItemAntigravityCharm;
import ab.common.item.equipment.ItemAquaSword;
import ab.common.item.equipment.ItemBlackHalo;
import ab.common.item.equipment.ItemManaFlower;
import ab.common.item.equipment.ItemMithrillMultiTool;
import ab.common.item.equipment.ItemMithrillRing;
import ab.common.item.equipment.ItemNebulaBlaze;
import ab.common.item.equipment.ItemNebulaRing;
import ab.common.item.equipment.ItemNebulaRod;
import ab.common.item.equipment.ItemSpaceBlade;
import ab.common.item.equipment.ItemSprawlRod;
import ab.common.item.equipment.ItemTerraHoe;
import ab.common.item.equipment.armor.ItemNebulaBoots;
import ab.common.item.equipment.armor.ItemNebulaChest;
import ab.common.item.equipment.armor.ItemNebulaHelm;
import ab.common.item.equipment.armor.ItemNebulaLegs;
import ab.common.item.equipment.armor.ItemWildHuntBoots;
import ab.common.item.equipment.armor.ItemWildHuntChest;
import ab.common.item.equipment.armor.ItemWildHuntHelm;
import ab.common.item.equipment.armor.ItemWildHuntLegs;
import ab.common.item.relic.ItemFreyrSlingshot;
import ab.common.item.relic.ItemHornPlenty;
import ab.common.item.relic.ItemPocketWardrobe;
import ab.common.item.relic.ItemSphereNavigation;
import ab.common.item.relic.ItemTalismanHiddenRiches;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.lang.reflect.Field;

public class ItemListAB {

    private static final Field REGISTRY_NAME_FIELD = ReflectionHelper.findField(
            net.minecraftforge.registries.IForgeRegistryEntry.Impl.class, "registryName");
    public static Item itemABResource;
    public static Item itemMithrillRing;
    public static Item itemMihrillMultiTool;
    public static Item itemNebulaRing;
    public static Item itemAdvancedSpark;
    public static Item itemMithrillSword;
    public static Item itemTerraHoe;
    public static Item itemManaFlower;
    public static Item itemBlackHalo;
    public static Item itemNebulaBlaze;
    public static Item itemAntigravityCharm;
    public static Item itemNebulaHelm;
    public static Item itemNebulaChest;
    public static Item itemNebulaLegs;
    public static Item itemNebulaBoots;
    public static Item itemFreyrSlingshot;
    public static Item itemNebulaRod;
    public static Item itemSprawlRod;
    public static Item itemPocketWardrobe;
    public static Item itemTalismanHiddenRiches;
    public static Item itemHornPlenty;
    public static Item itemSphereNavigation;
    public static Item itemAquaSword;
    public static Item itemWildHuntHelm;
    public static Item itemWildHuntChest;
    public static Item itemWildHuntLegs;
    public static Item itemWildHuntBoots;

    public static void init() {
        itemABResource = new ItemABResource();
        registerItem(itemABResource, "itemABResource");
        itemMithrillRing = new ItemMithrillRing("mithrillManaRing");
        registerItem(itemMithrillRing, "itemMithrillRing");
        itemNebulaRing = new ItemNebulaRing("nebulaManaRing");
        registerItem(itemNebulaRing, "itemNebulaRing");
        itemMihrillMultiTool = new ItemMithrillMultiTool();
        registerItem(itemMihrillMultiTool, "itemMihrillMultiTool");
        itemAdvancedSpark = new ItemAdvancedSpark();
        registerItem(itemAdvancedSpark, "itemAdvancedSpark");
        itemMithrillSword = new ItemSpaceBlade();
        registerItem(itemMithrillSword, "itemMithrillSword");
        itemTerraHoe = new ItemTerraHoe();
        registerItem(itemTerraHoe, "itemTerraHoe");
        itemManaFlower = new ItemManaFlower();
        registerItem(itemManaFlower, "itemManaFlower");
        itemBlackHalo = new ItemBlackHalo();
        registerItem(itemBlackHalo, "itemBlackHalo");
        itemNebulaBlaze = new ItemNebulaBlaze();
        registerItem(itemNebulaBlaze, "itemNebulaBlaze");
        itemAntigravityCharm = new ItemAntigravityCharm();
        registerItem(itemAntigravityCharm, "itemAntigravityCharm");
        itemNebulaRod = new ItemNebulaRod();
        registerItem(itemNebulaRod, "itemNebulaRod");
        itemSprawlRod = new ItemSprawlRod();
        registerItem(itemSprawlRod, "itemSprawlRod");
        itemAquaSword = new ItemAquaSword();
        registerItem(itemAquaSword, "itemAquaSword");
        itemPocketWardrobe = new ItemPocketWardrobe();
        registerItem(itemPocketWardrobe, "itemPocketWardrobe");
        itemTalismanHiddenRiches = new ItemTalismanHiddenRiches();
        registerItem(itemTalismanHiddenRiches, "itemTalismanHiddenRiches");
        itemFreyrSlingshot = new ItemFreyrSlingshot();
        registerItem(itemFreyrSlingshot, "itemFreyrSlingshot");
        itemHornPlenty = new ItemHornPlenty();
        registerItem(itemHornPlenty, "itemHornPlenty");
        itemSphereNavigation = new ItemSphereNavigation();
        registerItem(itemSphereNavigation, "itemSphereNavigation");
        itemNebulaHelm = new ItemNebulaHelm();
        registerItem(itemNebulaHelm, "itemNebulaHelm");
        itemNebulaChest = new ItemNebulaChest();
        registerItem(itemNebulaChest, "itemNebulaChest");
        itemNebulaLegs = new ItemNebulaLegs();
        registerItem(itemNebulaLegs, "itemNebulaLegs");
        itemNebulaBoots = new ItemNebulaBoots();
        registerItem(itemNebulaBoots, "itemNebulaBoots");
        itemWildHuntHelm = new ItemWildHuntHelm();
        registerItem(itemWildHuntHelm, "itemWildHuntHelm");
        itemWildHuntChest = new ItemWildHuntChest();
        registerItem(itemWildHuntChest, "itemWildHuntChest");
        itemWildHuntLegs = new ItemWildHuntLegs();
        registerItem(itemWildHuntLegs, "itemWildHuntLegs");
        itemWildHuntBoots = new ItemWildHuntBoots();
        registerItem(itemWildHuntBoots, "itemWildHuntBoots");
    }

    private static void registerItem(Item item, String name) {
        ResourceLocation existing = item.getRegistryName();
        if (existing != null && !AdvancedBotany.modid.equals(existing.getNamespace())) {
            // Item was given a registry name from a parent mod (e.g. Botania's ItemManasteelArmor sets "botania:" prefix)
            // Clear it via reflection so setRegistryName can succeed, then set our own modid
            try {
                REGISTRY_NAME_FIELD.set(item, null);
            } catch (Exception e) {
                // Failed to clear registry name via reflection, fall through
            }
        }
        if (item.getRegistryName() == null) {
            item.setRegistryName(name);
        }
        ForgeRegistries.ITEMS.register(item);
    }
}
