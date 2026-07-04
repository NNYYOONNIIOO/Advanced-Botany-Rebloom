package ab.api.recipe.lexicon;

import ab.api.AdvancedBotanyAPI;
import ab.api.recipe.RecipeAncientAlphirine;
import ab.client.core.ClientHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import vazkii.botania.api.internal.IGuiLexiconEntry;
import vazkii.botania.api.lexicon.LexiconEntry;
import vazkii.botania.api.lexicon.LexiconRecipeMappings;
import vazkii.botania.common.item.block.ItemBlockSpecialFlower;
import vazkii.botania.common.lexicon.page.PageRecipe;

import java.util.ArrayList;
import java.util.List;

public class AlphirineCraftPage extends PageRecipe {
    private static final ResourceLocation alphirineOverlay = new ResourceLocation("botania:textures/gui/pureDaisyOverlay.png");
    private RecipeAncientAlphirine recipe;
    private final ItemStack resultStack;

    public AlphirineCraftPage(LexiconEntry entry, ItemStack stack) {
        this(entry, stack, "");
    }

    public AlphirineCraftPage(LexiconEntry entry, ItemStack stack, String str) {
        super(str);
        this.resultStack = stack;
        this.refreshRecipe(entry, stack);
    }

    public ItemStack getResult() {
        return this.resultStack;
    }

    public void refreshRecipe(LexiconEntry entry, ItemStack stack) {
        RecipeAncientAlphirine rec = null;
        for (RecipeAncientAlphirine recipe : AdvancedBotanyAPI.alphirineRecipes) {
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
        FontRenderer font = Minecraft.getMinecraft().fontRenderer;
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        render.bindTexture(alphirineOverlay);
        ((GuiScreen)gui).drawTexturedModalRect(gui.getLeft() + 40, gui.getTop() + 44, 0, 0, gui.getWidth(), gui.getHeight());
        ItemStack inp = this.recipe.getInput().copy();
        if (inp.getItemDamage() == Short.MAX_VALUE) {
            inp.setItemDamage(0);
        }
        this.renderItem(gui, gui.getLeft() + 34, gui.getTop() + 59, inp, false);
        this.renderItem(gui, gui.getLeft() + 62, gui.getTop() + 57, ItemBlockSpecialFlower.ofType("ancientAlphirine").copy(), false);
        this.renderItem(gui, gui.getLeft() + 93, gui.getTop() + 54, this.recipe.getOutput().copy(), false);
        int x = gui.getLeft() + gui.getWidth() / 2 - 50;
        int y = gui.getTop() + 85;
        String name = TextFormatting.BOLD + I18n.format("ab.name.alpherine.craft");
        boolean unicode = font.getUnicodeFlag();
        font.setUnicodeFlag(true);
        font.drawString(name, x + 50 - font.getStringWidth(name) / 2, y - 65, -1728053248);
        font.setUnicodeFlag(unicode);
        ClientHelper.drawChanceBar(x + 21, y + 14, this.recipe.getChance());
        GlStateManager.disableBlend();
    }

    @Override
    public List<ItemStack> getDisplayedRecipes() {
        ArrayList<ItemStack> list = new ArrayList<ItemStack>();
        list.add(this.recipe.getOutput());
        return list;
    }
}
