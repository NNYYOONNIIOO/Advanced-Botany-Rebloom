package ab.client.integration.jei.advancedplate;

import ab.api.recipe.RecipeAdvancedPlate;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.client.resources.I18n;
import vazkii.botania.client.core.handler.HUDHandler;

import java.util.ArrayList;
import java.util.List;

public class AdvancedPlateRecipeWrapper implements IRecipeWrapper {

    private final RecipeAdvancedPlate recipe;

    public AdvancedPlateRecipeWrapper(RecipeAdvancedPlate recipe) {
        this.recipe = recipe;
    }

    public RecipeAdvancedPlate getRecipe() {
        return recipe;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        List<ItemStack> inputs = new ArrayList<>(recipe.getInputs());
        ingredients.setInputs(ItemStack.class, inputs);
        ingredients.setOutput(ItemStack.class, recipe.getOutput());
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        HUDHandler.renderManaBar(8, 92, 0x0000FF, 0.75f, recipe.getManaUsage(), 100000);
        String manaText = I18n.format("advanced_botany.jei.mana_cost", recipe.getManaUsage());
        minecraft.fontRenderer.drawString(manaText, 8, 97, 0x808080);
    }
}
