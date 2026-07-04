package ab.client.integration.jei.alphirine;

import ab.api.recipe.RecipeAncientAlphirine;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

import java.util.Collections;

public class AlphirineRecipeWrapper implements IRecipeWrapper {

    private final RecipeAncientAlphirine recipe;

    public AlphirineRecipeWrapper(RecipeAncientAlphirine recipe) {
        this.recipe = recipe;
    }

    public RecipeAncientAlphirine getRecipe() {
        return recipe;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setInput(ItemStack.class, recipe.getInput());
        ingredients.setOutput(ItemStack.class, recipe.getOutput());
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        int chance = recipe.getChance();
        String text = chance + "%";
        int textWidth = minecraft.fontRenderer.getStringWidth(text);
        minecraft.fontRenderer.drawString(text, 39 - textWidth / 2, 28, 0x808080);
    }
}
