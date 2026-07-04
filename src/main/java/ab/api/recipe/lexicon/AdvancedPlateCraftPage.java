package ab.api.recipe.lexicon;

import ab.api.AdvancedBotanyAPI;
import ab.api.recipe.RecipeAdvancedPlate;
import ab.client.core.ClientHelper;
import ab.common.lib.register.BlockListAB;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import vazkii.botania.api.internal.IGuiLexiconEntry;
import vazkii.botania.api.lexicon.LexiconEntry;
import vazkii.botania.api.lexicon.LexiconRecipeMappings;
import vazkii.botania.client.core.handler.ClientTickHandler;
import vazkii.botania.common.core.handler.ConfigHandler;
import vazkii.botania.common.lexicon.page.PageRecipe;

import java.util.ArrayList;
import java.util.List;

public class AdvancedPlateCraftPage extends PageRecipe {
    private static final ResourceLocation plateOverlay = new ResourceLocation("botania:textures/gui/petalOverlay.png");
    private RecipeAdvancedPlate recipe;
    private final ItemStack resultStack;
    int ticksElapsed = 0;

    public AdvancedPlateCraftPage(LexiconEntry entry, ItemStack stack) {
        this(entry, stack, "");
    }

    public AdvancedPlateCraftPage(LexiconEntry entry, ItemStack stack, String str) {
        super(str);
        this.resultStack = stack;
        this.refreshRecipe(entry, stack);
    }

    public ItemStack getResult() {
        return this.resultStack;
    }

    public void refreshRecipe(LexiconEntry entry, ItemStack stack) {
        RecipeAdvancedPlate rec = null;
        for (RecipeAdvancedPlate recipe : AdvancedBotanyAPI.advancedPlateRecipes) {
            if (stack == null || recipe.getOutput() == null || !recipe.getOutput().isItemEqual(stack)) continue;
            rec = recipe;
            break;
        }
        if (rec == null) {
            entry.pages.remove(this);
        }
        this.recipe = rec;
    }

    @Override
    public void onPageAdded(LexiconEntry entry, int index) {
        LexiconRecipeMappings.map(this.recipe.getOutput(), entry, index);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void renderRecipe(IGuiLexiconEntry gui, int mx, int my) {
        TextureManager render = Minecraft.getMinecraft().renderEngine;
        this.renderItemAtGridPos(gui, 3, 0, this.recipe.getOutput(), false);
        this.renderItemAtGridPos(gui, 2, 1, new ItemStack(BlockListAB.blockABPlate), false);
        List<ItemStack> inputs = this.recipe.getInputs();
        int degreePerInput = (int)(360.0f / (float)inputs.size());
        float currentDegree = ConfigHandler.lexiconRotatingItems ? (GuiScreen.isShiftKeyDown() ? (float)this.ticksElapsed : (float)this.ticksElapsed + ClientTickHandler.partialTicks) : 0.0f;
        for (ItemStack obj : inputs) {
            ItemStack copy = obj.copy();
            if (copy.getItemDamage() == Short.MAX_VALUE) {
                copy.setItemDamage(0);
            }
            this.renderItemAtAngle(gui, currentDegree, copy);
            currentDegree += (float)degreePerInput;
        }
        this.renderManaBar(gui, mx, my);
        render.bindTexture(plateOverlay);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        ((GuiScreen)gui).drawTexturedModalRect(gui.getLeft(), gui.getTop(), 0, 0, gui.getWidth(), gui.getHeight());
        GlStateManager.disableBlend();
    }

    @SideOnly(Side.CLIENT)
    public void renderManaBar(IGuiLexiconEntry gui, int mx, int my) {
        FontRenderer font = Minecraft.getMinecraft().fontRenderer;
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        String manaUsage = I18n.format("botaniamisc.manaUsage");
        font.drawString(manaUsage, gui.getLeft() + gui.getWidth() / 2 - font.getStringWidth(manaUsage) / 2, gui.getTop() + 105, 0x66000000);
        int x = gui.getLeft() + gui.getWidth() / 2 - 50;
        int y = gui.getTop() + 120;
        String stopStr = I18n.format("botaniamisc.shiftToStopSpin");
        ClientHelper.renderPoolManaBar(x, y - 5, 2334172, 1.0f, this.recipe.getManaUsage());
        boolean unicode = font.getUnicodeFlag();
        font.setUnicodeFlag(true);
        font.drawString(stopStr, x + 49 - font.getStringWidth(stopStr) / 2, y + 18, -1728053248);
        font.setUnicodeFlag(unicode);
        GlStateManager.disableBlend();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void updateScreen() {
        if (GuiScreen.isShiftKeyDown()) {
            return;
        }
        ++this.ticksElapsed;
    }

    @Override
    public List<ItemStack> getDisplayedRecipes() {
        ArrayList<ItemStack> list = new ArrayList<ItemStack>();
        list.add(this.recipe.getOutput());
        return list;
    }
}
