package ab.common.item.equipment;

import ab.api.AdvancedBotanyAPI;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;

public class ItemNebulaRing extends ItemMithrillRing {

    public ItemNebulaRing(String name) {
        super(name);
        this.setMaxDamage(1000);
        this.setNoRepair();
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return AdvancedBotanyAPI.rarityNebula;
    }

    @Override
    public int getMaxMana(ItemStack stack) {
        return 48000000;
    }
}
