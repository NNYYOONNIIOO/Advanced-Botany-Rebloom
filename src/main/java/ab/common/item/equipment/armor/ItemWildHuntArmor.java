package ab.common.item.equipment.armor;

import ab.AdvancedBotany;
import ab.api.AdvancedBotanyAPI;
import ab.client.model.armor.ModelArmorWildHunt;
import ab.common.lib.register.ItemListAB;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import vazkii.botania.common.item.equipment.armor.manasteel.ItemManasteelArmor;

import java.util.List;

public class ItemWildHuntArmor extends ItemManasteelArmor {

    static ItemStack[] armorset;

    public ItemWildHuntArmor(EntityEquipmentSlot type, String name) {
        super(type, name, AdvancedBotanyAPI.wildHundArmor);
        this.setCreativeTab(AdvancedBotany.tabAB);
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return AdvancedBotanyAPI.rarityWildHunt;
    }

    @Override
    public ItemStack[] getArmorSetStacks() {
        if (armorset == null) {
            armorset = new ItemStack[]{new ItemStack(ItemListAB.itemWildHuntHelm), new ItemStack(ItemListAB.itemWildHuntChest), new ItemStack(ItemListAB.itemWildHuntLegs), new ItemStack(ItemListAB.itemWildHuntBoots)};
        }
        return armorset;
    }

    @Override
    public boolean hasArmorSetItem(EntityPlayer player, int i) {
        ItemStack stack = player.inventory.armorItemInSlot(3 - i);
        if (stack.isEmpty()) {
            return false;
        }
        switch (i) {
            case 0:
                return stack.getItem() == ItemListAB.itemWildHuntHelm;
            case 1:
                return stack.getItem() == ItemListAB.itemWildHuntChest;
            case 2:
                return stack.getItem() == ItemListAB.itemWildHuntLegs;
            case 3:
                return stack.getItem() == ItemListAB.itemWildHuntBoots;
        }
        return false;
    }

    @Override
    public ModelBiped provideArmorModelForSlot(ItemStack stack, EntityEquipmentSlot slot) {
        this.models.put(slot, new ModelArmorWildHunt(slot));
        return this.models.get(slot);
    }

    @Override
    public String getArmorTextureAfterInk(ItemStack stack, EntityEquipmentSlot slot) {
        return "advanced_botany:textures/model/wildhuntarmor.png";
    }

    @Override
    public void addArmorSetDescription(ItemStack stack, List<String> list) {
        addStringToTooltip(I18n.format("ab.armorset.wildHunt.desc0"), list);
        addStringToTooltip(I18n.format("ab.armorset.wildHunt.desc1"), list);
        addStringToTooltip(I18n.format("ab.armorset.wildHunt.desc2"), list);
    }
}
