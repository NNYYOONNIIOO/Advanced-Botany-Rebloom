package ab.common.integration.crafttweaker;

import ab.api.AdvancedBotanyAPI;
import ab.api.recipe.RecipeAdvancedPlate;
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
 * CraftTweaker 集成：尼达维里尔铁砧配方管理。
 *
 * CraftTweaker 在其 init() 阶段加载脚本并立即调用 action.apply()，
 * 由于本模组依赖 after:crafttweaker，脚本执行时本模组的 init()（含 RecipeListAB.init()）尚未运行，
 * 原始配方尚未注册。因此所有 action 被延迟到第一个服务器 tick 时执行，
 * 此时所有模组的 init()/postInit() 均已完成，原始配方已注册。
 */
@ZenClass("mods.advancedbotany.AdvancedPlate")
@ZenRegister
@ModOnly("crafttweaker")
public class AdvancedPlateTweaker {

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
                AdvancedPlateTweaker.executeDeferred();
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
                CraftTweakerAPI.logError("[AdvancedBotany] Error executing deferred action: " + action.describe() + " - " + e.getMessage());
            }
        }
        deferredActions.clear();
        // 同步 JEI 显示
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
                        pluginClass.getMethod("refreshPlateRecipes").invoke(null);
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

    @ZenMethod
    public static void addRecipe(IItemStack output, IItemStack[] inputs, int mana, int color) {
        if (output == null) {
            CraftTweakerAPI.logError("[AdvancedBotany] addRecipe: output cannot be null");
            return;
        }
        if (inputs == null || inputs.length == 0) {
            CraftTweakerAPI.logError("[AdvancedBotany] addRecipe: inputs cannot be null or empty");
            return;
        }
        CraftTweakerAPI.apply(new AddRecipeAction(output, inputs, mana, color));
    }

    @ZenMethod
    public static void removeRecipe(IItemStack output) {
        if (output == null) {
            CraftTweakerAPI.logError("[AdvancedBotany] removeRecipe: output cannot be null");
            return;
        }
        CraftTweakerAPI.apply(new RemoveRecipeAction(output));
    }

    @ZenMethod
    public static void removeAll() {
        CraftTweakerAPI.apply(new RemoveAllAction());
    }

    @ZenMethod
    public static void modifyMana(IItemStack output, int newMana) {
        if (output == null) {
            CraftTweakerAPI.logError("[AdvancedBotany] modifyMana: output cannot be null");
            return;
        }
        CraftTweakerAPI.apply(new ModifyManaAction(output, newMana));
    }

    @ZenMethod
    public static void modifyOutput(IItemStack oldOutput, IItemStack newOutput) {
        if (oldOutput == null || newOutput == null) {
            CraftTweakerAPI.logError("[AdvancedBotany] modifyOutput: output cannot be null");
            return;
        }
        CraftTweakerAPI.apply(new ModifyOutputAction(oldOutput, newOutput));
    }

    @ZenMethod
    public static void modifyColor(IItemStack output, int newColor) {
        if (output == null) {
            CraftTweakerAPI.logError("[AdvancedBotany] modifyColor: output cannot be null");
            return;
        }
        CraftTweakerAPI.apply(new ModifyColorAction(output, newColor));
    }

    // ==================== Actions ====================

    private static List<ItemStack> toStackList(IItemStack[] inputs) {
        List<ItemStack> list = new ArrayList<>();
        for (IItemStack input : inputs) {
            if (input != null) {
                list.add(CraftTweakerMC.getItemStack(input));
            }
        }
        return list;
    }

    private static class AddRecipeAction implements IAction {
        private final ItemStack output;
        private final List<ItemStack> inputs;
        private final int mana;
        private final int color;

        AddRecipeAction(IItemStack output, IItemStack[] inputs, int mana, int color) {
            this.output = CraftTweakerMC.getItemStack(output);
            this.inputs = toStackList(inputs);
            this.mana = mana;
            this.color = color;
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
            ItemStack[] inputArray = inputs.toArray(new ItemStack[0]);
            RecipeAdvancedPlate recipe = new RecipeAdvancedPlate(output, mana, color, inputArray);
            AdvancedBotanyAPI.advancedPlateRecipes.add(recipe);
        }

        @Override
        public String describe() {
            return "Adding Advanced Plate recipe for " + output.getDisplayName();
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
            Iterator<RecipeAdvancedPlate> it = AdvancedBotanyAPI.advancedPlateRecipes.iterator();
            while (it.hasNext()) {
                RecipeAdvancedPlate recipe = it.next();
                if (recipe.getOutput().isItemEqual(output)) {
                    it.remove();
                }
            }
        }

        @Override
        public String describe() {
            return "Removing Advanced Plate recipes for " + output.getDisplayName();
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
            AdvancedBotanyAPI.advancedPlateRecipes.clear();
        }

        @Override
        public String describe() {
            return "Removing all Advanced Plate recipes";
        }
    }

    private static class ModifyManaAction implements IAction {
        private final ItemStack output;
        private final int newMana;

        ModifyManaAction(IItemStack output, int newMana) {
            this.output = CraftTweakerMC.getItemStack(output);
            this.newMana = newMana;
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
            for (int i = 0; i < AdvancedBotanyAPI.advancedPlateRecipes.size(); i++) {
                RecipeAdvancedPlate recipe = AdvancedBotanyAPI.advancedPlateRecipes.get(i);
                if (recipe.getOutput().isItemEqual(output)) {
                    ItemStack[] inputArray = recipe.getInputs().toArray(new ItemStack[0]);
                    AdvancedBotanyAPI.advancedPlateRecipes.set(i,
                            new RecipeAdvancedPlate(recipe.getOutput(), newMana, recipe.getColor(), inputArray));
                }
            }
        }

        @Override
        public String describe() {
            return "Modifying mana cost for Advanced Plate recipe: " + output.getDisplayName() + " to " + newMana;
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
            for (int i = 0; i < AdvancedBotanyAPI.advancedPlateRecipes.size(); i++) {
                RecipeAdvancedPlate recipe = AdvancedBotanyAPI.advancedPlateRecipes.get(i);
                if (recipe.getOutput().isItemEqual(oldOutput)) {
                    ItemStack[] inputArray = recipe.getInputs().toArray(new ItemStack[0]);
                    AdvancedBotanyAPI.advancedPlateRecipes.set(i,
                            new RecipeAdvancedPlate(newOutput, recipe.getManaUsage(), recipe.getColor(), inputArray));
                }
            }
        }

        @Override
        public String describe() {
            return "Modifying output of Advanced Plate recipe from " + oldOutput.getDisplayName()
                    + " to " + newOutput.getDisplayName();
        }
    }

    private static class ModifyColorAction implements IAction {
        private final ItemStack output;
        private final int newColor;

        ModifyColorAction(IItemStack output, int newColor) {
            this.output = CraftTweakerMC.getItemStack(output);
            this.newColor = newColor;
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
            for (int i = 0; i < AdvancedBotanyAPI.advancedPlateRecipes.size(); i++) {
                RecipeAdvancedPlate recipe = AdvancedBotanyAPI.advancedPlateRecipes.get(i);
                if (recipe.getOutput().isItemEqual(output)) {
                    ItemStack[] inputArray = recipe.getInputs().toArray(new ItemStack[0]);
                    AdvancedBotanyAPI.advancedPlateRecipes.set(i,
                            new RecipeAdvancedPlate(recipe.getOutput(), recipe.getManaUsage(), newColor, inputArray));
                }
            }
        }

        @Override
        public String describe() {
            return "Modifying color for Advanced Plate recipe: " + output.getDisplayName() + " to " + newColor;
        }
    }
}
