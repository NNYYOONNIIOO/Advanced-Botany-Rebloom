package ab.api;

import ab.api.recipe.RecipeAdvancedPlate;
import ab.api.recipe.RecipeAncientAlphirine;
import ab.common.core.handler.ConfigABHandler;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.util.EnumHelper;

public class AdvancedBotanyAPI {
    public static List<RecipeAdvancedPlate> advancedPlateRecipes = new ArrayList<RecipeAdvancedPlate>();
    public static List<RecipeAncientAlphirine> alphirineRecipes = new ArrayList<RecipeAncientAlphirine>();
    public static List<TerraFarmlandList> farmlandList = new ArrayList<TerraFarmlandList>();
    public static List<ItemStack> relicList = new ArrayList<ItemStack>();
    public static List<ItemStack> diceList = new ArrayList<ItemStack>();
    public static Item.ToolMaterial mithrilToolMaterial = EnumHelper.addToolMaterial("MITHRIL", 7, -1, 8.0f, 4.0f, 24);
    public static ItemArmor.ArmorMaterial nebulaArmorMaterial = EnumHelper.addArmorMaterial("NEBULA", "NEBULA", 0, new int[]{(int)(3.0 * ConfigABHandler.protectionFactorNebula), (int)(8.0 * ConfigABHandler.protectionFactorNebula), (int)(6.0 * ConfigABHandler.protectionFactorNebula), (int)(3.0 * ConfigABHandler.protectionFactorNebula)}, 26, SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, 0.0F);
    public static ItemArmor.ArmorMaterial wildHundArmor = EnumHelper.addArmorMaterial("WILD_HUNT_MATERIAL", "WILD_HUNT_MATERIAL", 34, new int[]{7, 8, 3, 2}, 26, SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, 0.0F);
    public static EnumRarity rarityNebula = EnumHelper.addRarity("NEBULA", TextFormatting.LIGHT_PURPLE, "Nebula");
    public static EnumRarity rarityWildHunt = EnumHelper.addRarity("WILD_HUNT", TextFormatting.AQUA, "WildHunt");

    public static RecipeAdvancedPlate registerAdvancedPlateRecipe(ItemStack output, ItemStack input1, ItemStack input2, ItemStack input3, int mana, int color) {
        RecipeAdvancedPlate recipe = new RecipeAdvancedPlate(output, mana, color, input1, input2, input3);
        advancedPlateRecipes.add(recipe);
        return recipe;
    }

    public static RecipeAncientAlphirine registerAlphirineRecipe(ItemStack output, ItemStack input, int chance) {
        RecipeAncientAlphirine recipe = new RecipeAncientAlphirine(output, input, chance);
        alphirineRecipes.add(recipe);
        return recipe;
    }

    public static TerraFarmlandList registerFarmlandSeed(Block block, int meta) {
        TerraFarmlandList seed = new TerraFarmlandList(block, meta);
        farmlandList.add(seed);
        return seed;
    }
}
