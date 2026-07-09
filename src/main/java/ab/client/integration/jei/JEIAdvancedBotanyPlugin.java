package ab.client.integration.jei;

import ab.api.AdvancedBotanyAPI;
import ab.api.recipe.RecipeAdvancedPlate;
import ab.api.recipe.RecipeAncientAlphirine;
import ab.client.integration.jei.advancedplate.AdvancedPlateRecipeCategory;
import ab.client.integration.jei.advancedplate.AdvancedPlateRecipeWrapper;
import ab.client.integration.jei.alphirine.AlphirineRecipeCategory;
import ab.client.integration.jei.alphirine.AlphirineRecipeWrapper;
import ab.common.lib.register.BlockListAB;
import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.IRecipeRegistry;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;
import vazkii.botania.common.item.block.ItemBlockSpecialFlower;

import java.util.ArrayList;
import java.util.List;

@JEIPlugin
public class JEIAdvancedBotanyPlugin implements IModPlugin {

    private static IJeiRuntime jeiRuntime;

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

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        JEIAdvancedBotanyPlugin.jeiRuntime = jeiRuntime;
    }

    /**
     * CraftTweaker 延迟操作执行后调用此方法，同步 JEI 显示。
     * 先隐藏所有旧配方，再添加当前配方列表中的配方。
     */
    public static void refreshPlateRecipes() {
        if (jeiRuntime == null) return;
        IRecipeRegistry registry = jeiRuntime.getRecipeRegistry();
        // 隐藏所有旧的 Advanced Plate 配方
        IRecipeCategory<?> category = registry.getRecipeCategory(AdvancedPlateRecipeCategory.UID);
        if (category != null) {
            List<?> oldWrappers = registry.getRecipeWrappers(category);
            for (Object wrapper : oldWrappers) {
                if (wrapper instanceof AdvancedPlateRecipeWrapper) {
                    registry.hideRecipe((IRecipeWrapper) wrapper, AdvancedPlateRecipeCategory.UID);
                }
            }
        }
        // 添加当前配方列表中的配方
        for (AdvancedPlateRecipeWrapper wrapper : wrapPlateRecipes()) {
            registry.addRecipe(wrapper, AdvancedPlateRecipeCategory.UID);
        }
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

    /**
     * CraftTweaker 延迟操作执行后调用此方法，同步 JEI 显示。
     * 先隐藏所有旧配方，再添加当前配方列表中的配方。
     */
    public static void refreshAlphirineRecipes() {
        if (jeiRuntime == null) return;
        IRecipeRegistry registry = jeiRuntime.getRecipeRegistry();
        // 隐藏所有旧的 Alphirine 配方
        IRecipeCategory<?> category = registry.getRecipeCategory(AlphirineRecipeCategory.UID);
        if (category != null) {
            List<?> oldWrappers = registry.getRecipeWrappers(category);
            for (Object wrapper : oldWrappers) {
                if (wrapper instanceof AlphirineRecipeWrapper) {
                    registry.hideRecipe((IRecipeWrapper) wrapper, AlphirineRecipeCategory.UID);
                }
            }
        }
        // 添加当前配方列表中的配方
        for (AlphirineRecipeWrapper wrapper : wrapAlphirineRecipes()) {
            registry.addRecipe(wrapper, AlphirineRecipeCategory.UID);
        }
    }
}
