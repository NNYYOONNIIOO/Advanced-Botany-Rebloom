package ab.common.item;

import ab.AdvancedBotany;
import net.minecraft.item.Item;

public class ItemMod extends Item {
    public ItemMod(String name) {
        this.setTranslationKey(AdvancedBotany.modid + "." + name);
        this.setCreativeTab(AdvancedBotany.tabAB);
    }
}
