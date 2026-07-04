package ab.client.integration.jei;

import ab.api.AdvancedBotanyAPI;
import ab.api.recipe.RecipeAdvancedPlate;
import ab.api.recipe.RecipeAncientAlphirine;
import ab.client.integration.jei.advancedplate.AdvancedPlateRecipeCategory;
import ab.client.integration.jei.advancedplate.AdvancedPlateRecipeWrapper;
import ab.client.integration.jei.alphirine.AlphirineRecipeCategory;
import ab.client.integration.jei.alphirine.AlphirineRecipeWrapper;
import ab.common.lib.register.BlockListAB;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.item.ItemStack;
import vazkii.botania.common.item.block.ItemBlockSpecialFlower;

import java.util.ArrayList;
import java.util.List;

@JEIPlugin
public class JEIAdvancedBotanyPlugin implements IModPlugin {

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        registry.addRecipeCategories(new AdvancedPlateRecipeCategory(registry.getJeiHelpers().getGuiHelper()));
        registry.addRecipeCategories(new AlphirineRecipeCategory(registry.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void register(IModRegistry registry) {
        registry.addRecipes(wrapPlateRecipes(), AdvancedPlateRecipeCategory.UID);
        registry.addRecipeCatalyst(new ItemStack(BlockListAB.blockABPlate), AdvancedPlateRecipeCategory.UID);

        registry.addRecipes(wrapAlphirineRecipes(), AlphirineRecipeCategory.UID);
        registry.addRecipeCatalyst(ItemBlockSpecialFlower.ofType("ancientAlphirine"), AlphirineRecipeCategory.UID);
    }

    private static List<AdvancedPlateRecipeWrapper> wrapPlateRecipes() {
        List<AdvancedPlateRecipeWrapper> wrappers = new ArrayList<>();
        for (RecipeAdvancedPlate recipe : AdvancedBotanyAPI.advancedPlateRecipes) {
            wrappers.add(new AdvancedPlateRecipeWrapper(recipe));
        }
        return wrappers;
    }

    private static List<AlphirineRecipeWrapper> wrapAlphirineRecipes() {
        List<AlphirineRecipeWrapper> wrappers = new ArrayList<>();
        for (RecipeAncientAlphirine recipe : AdvancedBotanyAPI.alphirineRecipes) {
            wrappers.add(new AlphirineRecipeWrapper(recipe));
        }
        return wrappers;
    }
}
