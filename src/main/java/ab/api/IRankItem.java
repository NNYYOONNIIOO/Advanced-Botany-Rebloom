package ab.api;

import net.minecraft.item.ItemStack;
import vazkii.botania.api.mana.IManaItem;

public interface IRankItem extends IManaItem {
    int getLevel(ItemStack var1);

    int[] getLevels();
}
