package ab.api.recipe;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class RecipeAdvancedPlate {
    private ItemStack output;
    private int color;
    private List<ItemStack> inputs;
    private int mana;

    public RecipeAdvancedPlate(ItemStack output, int mana, int color, ItemStack... inputs) {
        this.output = output;
        this.mana = mana;
        this.color = color;
        ArrayList<ItemStack> inputsToSet = new ArrayList<ItemStack>();
        for (ItemStack obj : inputs) {
            inputsToSet.add(obj);
        }
        this.inputs = inputsToSet;
    }

    public List<ItemStack> getInputs() {
        return new ArrayList<ItemStack>(this.inputs);
    }

    public ItemStack getOutput() {
        return this.output;
    }

    public int getManaUsage() {
        return this.mana;
    }

    public int getColor() {
        return this.color;
    }

    public boolean matches(IInventory inv) {
        ArrayList<ItemStack> inputsMissing = new ArrayList<ItemStack>(this.inputs);
        for (int i = 1; i < inv.getSizeInventory(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (stack.isEmpty()) continue;
            int stackIndex = -1;
            for (int j = 0; j < inputsMissing.size(); j++) {
                ItemStack input = inputsMissing.get(j);
                if (!this.simpleAreStacksEqual(input.copy(), stack)) continue;
                stackIndex = j;
                break;
            }
            if (stackIndex == -1) {
                return false;
            }
            inputsMissing.remove(stackIndex);
        }
        return inputsMissing.isEmpty();
    }

    boolean simpleAreStacksEqual(ItemStack input, ItemStack stack) {
        if (input.getItemDamage() == Short.MAX_VALUE) {
            input.setItemDamage(stack.getItemDamage());
        }
        return input.getItem() == stack.getItem() && input.getItemDamage() == stack.getItemDamage();
    }
}
