package ab.common.integration.crafttweaker;

import ab.api.AdvancedBotanyAPI;
import ab.api.recipe.RecipeAncientAlphirine;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * CraftTweaker 集成：古阿尔菲林配方管理。
 *
 * chance 参数为 0.0~1.0 的浮点数，内部转换为 1~100 的整数百分比。
 * 所有 action 延迟到第一个服务器 tick 时执行，确保原始配方已注册。
 */
@ZenClass("mods.advancedbotany.Alphirine")
@ZenRegister
@ModOnly("crafttweaker")
public class AlphirineTweaker {

    private static final List<IAction> deferredActions = new ArrayList<>();
    private static boolean executingDeferred = false;
    private static boolean handlerRegistered = false;

    private static void ensureHandlerRegistered() {
        if (!handlerRegistered) {
            handlerRegistered = true;
            MinecraftForge.EVENT_BUS.register(new DeferredHandler());
        }
    }

    private static class DeferredHandler {
        @SubscribeEvent
        public void onServerTick(TickEvent.ServerTickEvent event) {
            if (event.phase == TickEvent.Phase.START && !deferredActions.isEmpty()) {
                AlphirineTweaker.executeDeferred();
                MinecraftForge.EVENT_BUS.unregister(this);
            }
        }
    }

    private static void defer(IAction action) {
        ensureHandlerRegistered();
        if (!executingDeferred) {
            deferredActions.add(action);
        }
    }

    private static void executeDeferred() {
        if (executingDeferred) return;
        executingDeferred = true;
        for (IAction action : deferredActions) {
            try {
                action.apply();
            } catch (Exception e) {
                CraftTweakerAPI.logError("[AdvancedBotany] Error executing deferred Alphirine action: " + action.describe() + " - " + e.getMessage());
            }
        }
        deferredActions.clear();
        refreshJEI();
    }

    private static void refreshJEI() {
        try {
            if (!net.minecraftforge.fml.common.FMLCommonHandler.instance().getSide().isClient()) return;
            Class<?> mcClass = Class.forName("net.minecraft.client.Minecraft");
            Object mcInstance = mcClass.getMethod("getMinecraft").invoke(null);
            Runnable task = new Runnable() {
                @Override
                public void run() {
                    try {
                        Class<?> pluginClass = Class.forName("ab.client.integration.jei.JEIAdvancedBotanyPlugin");
                        pluginClass.getMethod("refreshAlphirineRecipes").invoke(null);
                    } catch (Exception e) {
                        // JEI 不存在时忽略
                    }
                }
            };
            mcClass.getMethod("addScheduledTask", Runnable.class).invoke(mcInstance, task);
        } catch (Exception e) {
            // 客户端类不存在时忽略（专用服务器）
        }
    }

    /**
     * 添加古阿尔菲林配方。
     * @param output 输出物品
     * @param input 输入物品
     * @param chance 成功概率，0.0~1.0
     */
    @ZenMethod
    public static void addRecipe(IItemStack output, IItemStack input, double chance) {
        if (output == null || input == null) {
            CraftTweakerAPI.logError("[AdvancedBotany] Alphirine addRecipe: output and input cannot be null");
            return;
        }
        if (chance <= 0.0 || chance > 1.0) {
            CraftTweakerAPI.logError("[AdvancedBotany] Alphirine addRecipe: chance must be between 0.0 (exclusive) and 1.0 (inclusive), got " + chance);
            return;
        }
        CraftTweakerAPI.apply(new AddRecipeAction(output, input, chance));
    }

    @ZenMethod
    public static void removeRecipe(IItemStack output) {
        if (output == null) {
            CraftTweakerAPI.logError("[AdvancedBotany] Alphirine removeRecipe: output cannot be null");
            return;
        }
        CraftTweakerAPI.apply(new RemoveRecipeAction(output));
    }

    @ZenMethod
    public static void removeAll() {
        CraftTweakerAPI.apply(new RemoveAllAction());
    }

    @ZenMethod
    public static void modifyChance(IItemStack output, double newChance) {
        if (output == null) {
            CraftTweakerAPI.logError("[AdvancedBotany] Alphirine modifyChance: output cannot be null");
            return;
        }
        if (newChance <= 0.0 || newChance > 1.0) {
            CraftTweakerAPI.logError("[AdvancedBotany] Alphirine modifyChance: chance must be between 0.0 (exclusive) and 1.0 (inclusive), got " + newChance);
            return;
        }
        CraftTweakerAPI.apply(new ModifyChanceAction(output, newChance));
    }

    @ZenMethod
    public static void modifyOutput(IItemStack oldOutput, IItemStack newOutput) {
        if (oldOutput == null || newOutput == null) {
            CraftTweakerAPI.logError("[AdvancedBotany] Alphirine modifyOutput: output cannot be null");
            return;
        }
        CraftTweakerAPI.apply(new ModifyOutputAction(oldOutput, newOutput));
    }

    @ZenMethod
    public static void modifyInput(IItemStack output, IItemStack newInput) {
        if (output == null || newInput == null) {
            CraftTweakerAPI.logError("[AdvancedBotany] Alphirine modifyInput: parameters cannot be null");
            return;
        }
        CraftTweakerAPI.apply(new ModifyInputAction(output, newInput));
    }

    // ==================== Actions ====================

    private static int chanceToInt(double chance) {
        int intChance = (int) Math.round(chance * 100.0);
        if (intChance <= 0) intChance = 1;
        if (intChance > 100) intChance = 100;
        return intChance;
    }

    private static class AddRecipeAction implements IAction {
        private final ItemStack output;
        private final ItemStack input;
        private final int chance;

        AddRecipeAction(IItemStack output, IItemStack input, double chance) {
            this.output = CraftTweakerMC.getItemStack(output);
            this.input = CraftTweakerMC.getItemStack(input);
            this.chance = chanceToInt(chance);
        }

        @Override
        public void apply() {
            if (executingDeferred) {
                doApply();
            } else {
                defer(this);
            }
        }

        private void doApply() {
            AdvancedBotanyAPI.alphirineRecipes.add(new RecipeAncientAlphirine(output, input, chance));
        }

        @Override
        public String describe() {
            return "Adding Alphirine recipe for " + output.getDisplayName() + " (chance: " + chance + "%)";
        }
    }

    private static class RemoveRecipeAction implements IAction {
        private final ItemStack output;

        RemoveRecipeAction(IItemStack output) {
            this.output = CraftTweakerMC.getItemStack(output);
        }

        @Override
        public void apply() {
            if (executingDeferred) {
                doApply();
            } else {
                defer(this);
            }
        }

        private void doApply() {
            Iterator<RecipeAncientAlphirine> it = AdvancedBotanyAPI.alphirineRecipes.iterator();
            while (it.hasNext()) {
                RecipeAncientAlphirine recipe = it.next();
                if (recipe.getOutput().isItemEqual(output)) {
                    it.remove();
                }
            }
        }

        @Override
        public String describe() {
            return "Removing Alphirine recipes for " + output.getDisplayName();
        }
    }

    private static class RemoveAllAction implements IAction {
        @Override
        public void apply() {
            if (executingDeferred) {
                doApply();
            } else {
                defer(this);
            }
        }

        private void doApply() {
            AdvancedBotanyAPI.alphirineRecipes.clear();
        }

        @Override
        public String describe() {
            return "Removing all Alphirine recipes";
        }
    }

    private static class ModifyChanceAction implements IAction {
        private final ItemStack output;
        private final int newChance;

        ModifyChanceAction(IItemStack output, double newChance) {
            this.output = CraftTweakerMC.getItemStack(output);
            this.newChance = chanceToInt(newChance);
        }

        @Override
        public void apply() {
            if (executingDeferred) {
                doApply();
            } else {
                defer(this);
            }
        }

        private void doApply() {
            for (int i = 0; i < AdvancedBotanyAPI.alphirineRecipes.size(); i++) {
                RecipeAncientAlphirine recipe = AdvancedBotanyAPI.alphirineRecipes.get(i);
                if (recipe.getOutput().isItemEqual(output)) {
                    AdvancedBotanyAPI.alphirineRecipes.set(i,
                            new RecipeAncientAlphirine(recipe.getOutput(), recipe.getInput(), newChance));
                }
            }
        }

        @Override
        public String describe() {
            return "Modifying chance for Alphirine recipe: " + output.getDisplayName() + " to " + newChance + "%";
        }
    }

    private static class ModifyOutputAction implements IAction {
        private final ItemStack oldOutput;
        private final ItemStack newOutput;

        ModifyOutputAction(IItemStack oldOutput, IItemStack newOutput) {
            this.oldOutput = CraftTweakerMC.getItemStack(oldOutput);
            this.newOutput = CraftTweakerMC.getItemStack(newOutput);
        }

        @Override
        public void apply() {
            if (executingDeferred) {
                doApply();
            } else {
                defer(this);
            }
        }

        private void doApply() {
            for (int i = 0; i < AdvancedBotanyAPI.alphirineRecipes.size(); i++) {
                RecipeAncientAlphirine recipe = AdvancedBotanyAPI.alphirineRecipes.get(i);
                if (recipe.getOutput().isItemEqual(oldOutput)) {
                    AdvancedBotanyAPI.alphirineRecipes.set(i,
                            new RecipeAncientAlphirine(newOutput, recipe.getInput(), recipe.getChance()));
                }
            }
        }

        @Override
        public String describe() {
            return "Modifying output of Alphirine recipe from " + oldOutput.getDisplayName()
                    + " to " + newOutput.getDisplayName();
        }
    }

    private static class ModifyInputAction implements IAction {
        private final ItemStack output;
        private final ItemStack newInput;

        ModifyInputAction(IItemStack output, IItemStack newInput) {
            this.output = CraftTweakerMC.getItemStack(output);
            this.newInput = CraftTweakerMC.getItemStack(newInput);
        }

        @Override
        public void apply() {
            if (executingDeferred) {
                doApply();
            } else {
                defer(this);
            }
        }

        private void doApply() {
            for (int i = 0; i < AdvancedBotanyAPI.alphirineRecipes.size(); i++) {
                RecipeAncientAlphirine recipe = AdvancedBotanyAPI.alphirineRecipes.get(i);
                if (recipe.getOutput().isItemEqual(output)) {
                    AdvancedBotanyAPI.alphirineRecipes.set(i,
                            new RecipeAncientAlphirine(recipe.getOutput(), newInput, recipe.getChance()));
                }
            }
        }

        @Override
        public String describe() {
            return "Modifying input of Alphirine recipe for " + output.getDisplayName()
                    + " to " + newInput.getDisplayName();
        }
    }
}
