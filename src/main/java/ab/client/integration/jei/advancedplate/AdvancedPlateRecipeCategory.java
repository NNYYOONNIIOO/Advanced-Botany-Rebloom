package ab.client.integration.jei.advancedplate;

import ab.api.recipe.RecipeAdvancedPlate;
import ab.common.lib.register.BlockListAB;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.List;

public class AdvancedPlateRecipeCategory implements IRecipeCategory<AdvancedPlateRecipeWrapper> {

    public static final String UID = "advanced_botany.advanced_plate";

    private final IDrawable background;
    private final IDrawable overlay;
    private final String localizedName;

    public AdvancedPlateRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createBlankDrawable(114, 104);
        this.overlay = guiHelper.createDrawable(
                new ResourceLocation("botania", "textures/gui/petalOverlay.png"), 17, 11, 114, 82);
        this.localizedName = I18n.format("advanced_botany.jei.advanced_plate");
    }

    @Nonnull
    @Override
    public String getUid() {
        return UID;
    }

    @Nonnull
    @Override
    public String getTitle() {
        return localizedName;
    }

    @Nonnull
    @Override
    public String getModName() {
        return "Advanced Botany";
    }

    @Nonnull
    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public void drawExtras(@Nonnull Minecraft minecraft) {
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        overlay.draw(minecraft, 0, 4);
        GlStateManager.disableBlend();
        GlStateManager.disableAlpha();
    }

    @Override
    public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull AdvancedPlateRecipeWrapper recipeWrapper, @Nonnull IIngredients ingredients) {
        recipeLayout.getItemStacks().init(0, true, 47, 44);
        recipeLayout.getItemStacks().set(0, new ItemStack(BlockListAB.blockABPlate));

        int index = 1;
        double angleBetweenEach = 360.0 / ingredients.getInputs(VanillaTypes.ITEM).size();
        java.awt.Point point = new java.awt.Point(47, 12), center = new java.awt.Point(47, 44);

        for (List<ItemStack> o : ingredients.getInputs(VanillaTypes.ITEM)) {
            recipeLayout.getItemStacks().init(index, true, point.x, point.y);
            recipeLayout.getItemStacks().set(index, o);
            index += 1;
            point = rotatePointAbout(point, center, angleBetweenEach);
        }

        recipeLayout.getItemStacks().init(index, false, 86, 11);
        recipeLayout.getItemStacks().set(index, ingredients.getOutputs(VanillaTypes.ITEM).get(0));
    }

    private java.awt.Point rotatePointAbout(java.awt.Point in, java.awt.Point about, double degrees) {
        double rad = degrees * Math.PI / 180.0;
        double newX = Math.cos(rad) * (in.x - about.x) - Math.sin(rad) * (in.y - about.y) + about.x;
        double newY = Math.sin(rad) * (in.x - about.x) + Math.cos(rad) * (in.y - about.y) + about.y;
        return new java.awt.Point((int) newX, (int) newY);
    }
}
