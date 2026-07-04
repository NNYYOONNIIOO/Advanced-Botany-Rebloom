package ab.client.integration.jei.alphirine;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import vazkii.botania.common.item.block.ItemBlockSpecialFlower;

import javax.annotation.Nonnull;

public class AlphirineRecipeCategory implements IRecipeCategory<AlphirineRecipeWrapper> {

    public static final String UID = "advanced_botany.alphirine";

    private final IDrawable background;
    private final IDrawable overlay;
    private final String localizedName;

    public AlphirineRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createBlankDrawable(96, 44);
        this.overlay = guiHelper.createDrawable(
                new ResourceLocation("botania", "textures/gui/pureDaisyOverlay.png"), 0, 0, 64, 44);
        this.localizedName = I18n.format("advanced_botany.jei.alphirine");
    }

    @Override
    public String getUid() {
        return UID;
    }

    @Override
    public String getTitle() {
        return localizedName;
    }

    @Override
    public String getModName() {
        return "Advanced Botany";
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, AlphirineRecipeWrapper recipeWrapper, IIngredients ingredients) {
        IGuiItemStackGroup group = recipeLayout.getItemStacks();

        group.init(0, true, 9, 12);
        group.set(0, recipeWrapper.getRecipe().getInput());

        group.init(1, true, 39, 12);
        group.set(1, ItemBlockSpecialFlower.ofType("ancientAlphirine"));

        group.init(2, false, 68, 12);
        group.set(2, recipeWrapper.getRecipe().getOutput());
    }

    @Override
    public void drawExtras(@Nonnull Minecraft minecraft) {
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        overlay.draw(minecraft, 17, 0);
        GlStateManager.disableBlend();
        GlStateManager.disableAlpha();
    }
}
