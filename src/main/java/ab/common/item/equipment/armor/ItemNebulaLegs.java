package ab.common.item.equipment.armor;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;

import java.util.List;
import java.util.UUID;

public class ItemNebulaLegs extends ItemNebulaArmor {

    public ItemNebulaLegs() {
        super(EntityEquipmentSlot.LEGS, "nebulaLegs");
    }

    @Override
    public net.minecraft.util.ActionResult<ItemStack> onItemRightClick(net.minecraft.world.World world, EntityPlayer player, EnumHand hand) {
        return toggleEffect(world, player, hand);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, net.minecraft.world.World world, List<String> list, ITooltipFlag flag) {
        super.addInformation(stack, world, list, flag);
        String effectKey = enableEffect(stack) ? "ab.nebula.effect.speed.on" : "ab.nebula.effect.speed.off";
        addStringToTooltip(I18n.format(effectKey), list);
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
        HashMultimap<String, AttributeModifier> hashMultimap = HashMultimap.create();
        if (slot == EntityEquipmentSlot.LEGS && enableEffect(stack)) {
            UUID uuid = new UUID(this.getRegistryName().hashCode(), 0L);
            float manaRatio = 1.0f - (float) getDamage(stack) / 1000.0f;
            hashMultimap.put(SharedMonsterAttributes.MOVEMENT_SPEED.getName(),
                    new AttributeModifier(uuid, "NebulaLegs modifier", 0.3 * manaRatio, 0));
        }
        if (slot == EntityEquipmentSlot.LEGS) {
            UUID toughnessUuid = new UUID(this.getRegistryName().hashCode() + 1, 0L);
            float manaRatio = 1.0f - (float) getDamage(stack) / 1000.0f;
            hashMultimap.put(SharedMonsterAttributes.ARMOR_TOUGHNESS.getName(),
                    new AttributeModifier(toughnessUuid, "Nebula toughness", 0.3 * manaRatio, 0));
            UUID knockbackUuid = new UUID(this.getRegistryName().hashCode() + 3, 0L);
            hashMultimap.put(SharedMonsterAttributes.KNOCKBACK_RESISTANCE.getName(),
                    new AttributeModifier(knockbackUuid, "Nebula knockback resistance", 0.3, 0));
        }
        return hashMultimap;
    }
}
