package ab.common.core.handler;

import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import java.io.File;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class ConfigABHandler {
    public static Configuration config;
    public static final String CATEGORY_BALANCE = "general.balance";
    public static final String CATEGORY_BALANCE_MANA_COST = "general.balance.manacost";
    public static final String CATEGORY_RELICS = "general.relics";
    public static final String CATEGORY_INTEGRATION = "general.integration";
    public static int[] maxContainerMana = new int[]{64000000, 8000000, 64000000};
    public static double protectionFactorNebula = 1.0;
    public static double damageFactorSpaceSword = 1.0;
    public static int nebulaWandCooldownTick = 18;
    public static int spreaderMaxMana = 128000;
    public static int spreaderBurstMana = 32000;
    public static String[] lockWorldNameNebulaRod = new String[0];
    public static int limitXZCoords = 30000000;
    public static int maxDictariusCount = 64;
    public static int sprawlRodMaxArea = 64;
    public static int nebulaRodManaCost = 180;
    public static int sphereNavigationManaCost = 2500;
    public static String[] lockEntityListToHorn = new String[0];
    public static String[] relicNames = new String[]{"infiniteFruit", "kingKey", "flugelEye", "thorRing", "odinRing", "lokiRing", "freyrSlingshot", "keyHiddenRiches", "pocketWardrobe", "sphereNavigation", "hornPlenty"};
    public static boolean[] fateBoardRelicEnables = new boolean[relicNames.length];
    public static boolean hasAutoThaum = true;
    public static boolean useManaChargerAnimation = true;
    public static boolean hasManaCharger = true;

    static {
        for (int i = 0; i < fateBoardRelicEnables.length; ++i) {
            ConfigABHandler.fateBoardRelicEnables[i] = true;
        }
    }

    public static void loadConfig(File configFile) {
        config = new Configuration(configFile);
        config.load();
        ConfigABHandler.loadCategories();
        ConfigABHandler.load();
        FMLCommonHandler.instance().bus().register(new ChangeListener());
    }

    public static void load() {
        String desc = "Maximum mana capacity for Mana Container";
        ConfigABHandler.maxContainerMana[0] = ConfigABHandler.loadPropInt(CATEGORY_BALANCE, "Mana Container Capacity", desc, false, maxContainerMana[0]);
        desc = "Maximum mana capacity for Diluted Mana Container";
        ConfigABHandler.maxContainerMana[1] = ConfigABHandler.loadPropInt(CATEGORY_BALANCE, "Mana Container Capacity (Diluted)", desc, false, maxContainerMana[1]);
        desc = "Maximum mana capacity for Fabulous Mana Container";
        ConfigABHandler.maxContainerMana[2] = ConfigABHandler.loadPropInt(CATEGORY_BALANCE, "Mana Container Capacity (Fabulous)", desc, false, maxContainerMana[2]);
        desc = "Protection factor for nebula armour";
        protectionFactorNebula = ConfigABHandler.loadPropDouble(CATEGORY_BALANCE, "Nebula Armor Protection Factor", desc, false, protectionFactorNebula);
        desc = "Damage factor for Space Blade";
        damageFactorSpaceSword = ConfigABHandler.loadPropDouble(CATEGORY_BALANCE, "Space Sword Damage Factor", desc, false, damageFactorSpaceSword);
        desc = "The number of ticks needed to restore 1 unit of the Rod of Nebula strength.";
        nebulaWandCooldownTick = ConfigABHandler.loadPropInt(CATEGORY_BALANCE, "Rod of Nebula Cooldown", desc, false, nebulaWandCooldownTick);
        desc = "Maximum amount of mana held in a mana spreader";
        spreaderMaxMana = ConfigABHandler.loadPropInt(CATEGORY_BALANCE, "Spreader Max Mana", desc, false, spreaderMaxMana);
        desc = "Amount of Mana in a Mana Burst";
        spreaderBurstMana = ConfigABHandler.loadPropInt(CATEGORY_BALANCE, "Spreader Burst Mana", desc, false, spreaderBurstMana);
        desc = "To block a creature, type it's class name";
        lockWorldNameNebulaRod = ConfigABHandler.loadPropString(CATEGORY_BALANCE, "Locking Worlds for Teleportation with Nebula Rod", desc, false, lockWorldNameNebulaRod);
        desc = "Limitation on X Z coordinates for teleportation, do not increase the default value";
        limitXZCoords = ConfigABHandler.loadPropInt(CATEGORY_BALANCE, "Restriction on X Z coordinates for Rod of Nebula", desc, false, limitXZCoords);
        desc = "Limit the number of flowers next to each other";
        maxDictariusCount = ConfigABHandler.loadPropInt(CATEGORY_BALANCE, "Max Dictarius Count", desc, false, maxDictariusCount);
        desc = "Changes the area of effect of a projectile created with Rod of Sprawl";
        sprawlRodMaxArea = ConfigABHandler.loadPropInt(CATEGORY_BALANCE, "Rod of Sprawl Max Area", desc, false, sprawlRodMaxArea);
        desc = "Mana cost to restore one unit of strength";
        nebulaRodManaCost = ConfigABHandler.loadPropInt(CATEGORY_BALANCE_MANA_COST, "Rod of Nebula repair Mana cost", desc, false, nebulaRodManaCost);
        desc = "Mana cost to try to find the specified block";
        sphereNavigationManaCost = ConfigABHandler.loadPropInt(CATEGORY_BALANCE_MANA_COST, "Sphere of Navigation Mana cost", desc, false, sphereNavigationManaCost);
        lockEntityListToHorn = ConfigABHandler.loadPropString(CATEGORY_RELICS, "Blocked creatures for double drop", desc, false, lockEntityListToHorn);
        desc = "Enable or disable relic drop on the Fate Playing Board";
        for (int i = 0; i < fateBoardRelicEnables.length; ++i) {
            ConfigABHandler.fateBoardRelicEnables[i] = ConfigABHandler.loadPropBool(CATEGORY_RELICS, "Enable relic: " + relicNames[i], desc, false, fateBoardRelicEnables[i]);
        }
        desc = "Activating the charging animation for the Mana Charger";
        useManaChargerAnimation = ConfigABHandler.loadPropBool("general", "Mana Charger lighting", desc, false, useManaChargerAnimation);
        desc = "Switching the Mana Charger on or off in the game";
        hasManaCharger = ConfigABHandler.loadPropBool("general", "Enable Mana Charger", desc, true, hasManaCharger);
        desc = "Switching the Thaumim Crafty Crate on or off in the game";
        hasAutoThaum = ConfigABHandler.loadPropBool(CATEGORY_INTEGRATION, "Enable Thaumim Crafty Crate", desc, true, hasAutoThaum);
        if (config.hasChanged()) {
            config.save();
        }
    }

    public static void loadCategories() {
        ConfigABHandler.addCategory(CATEGORY_BALANCE, "Advanced Botany: balance settings");
        ConfigABHandler.addCategory(CATEGORY_BALANCE_MANA_COST, "Advanced Botany: balance settings (mana cost)");
        ConfigABHandler.addCategory(CATEGORY_RELICS, "Advanced Botany: relics settings");
        ConfigABHandler.addCategory(CATEGORY_INTEGRATION, "Advanced Botany: integration settings");
    }

    public static void addCategory(String name, String comment) {
        config.addCustomCategoryComment(name, comment);
        config.getCategory(name).setLanguageKey(name);
    }

    public static void loadPostInit() {
        if (config.hasChanged()) {
            config.save();
        }
    }

    public static String[] loadPropString(String category, String propName, String desc, boolean hasRestart, String[] default_) {
        Property prop = config.get(category, propName, default_);
        prop.setComment(desc);
        prop.setRequiresMcRestart(hasRestart);
        return prop.getStringList();
    }

    public static boolean loadPropBool(String category, String propName, String desc, boolean hasRestart, boolean default_) {
        Property prop = config.get(category, propName, default_);
        prop.setComment(desc);
        prop.setRequiresMcRestart(hasRestart);
        return prop.getBoolean(default_);
    }

    public static int loadPropInt(String category, String propName, String desc, boolean hasRestart, int default_) {
        Property prop = config.get(category, propName, default_);
        prop.setComment(desc);
        prop.setRequiresMcRestart(hasRestart);
        return prop.getInt(default_);
    }

    public static double loadPropDouble(String category, String propName, String desc, boolean hasRestart, double default_) {
        Property prop = config.get(category, propName, default_);
        prop.setComment(desc);
        prop.setRequiresMcRestart(hasRestart);
        return prop.getDouble(default_);
    }

    public static class ChangeListener {
        @SubscribeEvent
        public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent eventArgs) {
            if (eventArgs.getModID().equals("advanced_botany")) {
                ConfigABHandler.load();
            }
        }
    }
}
