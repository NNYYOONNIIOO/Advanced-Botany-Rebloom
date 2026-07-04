package ab.common.item.equipment;

import ab.AdvancedBotany;
import baubles.api.BaubleType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.mana.IManaGivingItem;
import vazkii.botania.api.mana.IManaItem;
import vazkii.botania.api.mana.ManaItemHandler;

public class ItemManaFlower extends ItemBauble implements IManaGivingItem {

    public ItemManaFlower() {
        super("manaFlower");
        this.setCreativeTab(AdvancedBotany.tabAB);
        this.setNoRepair();
    }

    @Override
    public void onWornTick(ItemStack stack, EntityLivingBase player) {
        super.onWornTick(stack, player);
        if (player instanceof EntityPlayer && player.ticksExisted % 2 == 0) {
            EntityPlayer ePlayer = (EntityPlayer) player;
            int manaPerItem = 50;
            dispatchManaToAll(ePlayer, stack, manaPerItem);
        }
    }

    private void dispatchManaToAll(EntityPlayer player, ItemStack source, int manaToSend) {
        for (ItemStack stackInSlot : player.inventory.mainInventory) {
            if (!stackInSlot.isEmpty() && stackInSlot.getItem() instanceof IManaItem && stackInSlot != source) {
                IManaItem manaItem = (IManaItem) stackInSlot.getItem();
                if (manaItem.canReceiveManaFromItem(stackInSlot, source)
                        && manaItem.getMana(stackInSlot) + manaToSend <= manaItem.getMaxMana(stackInSlot)) {
                    manaItem.addMana(stackInSlot, manaToSend);
                }
            }
        }
        for (ItemStack stackInSlot : player.inventory.armorInventory) {
            if (!stackInSlot.isEmpty() && stackInSlot.getItem() instanceof IManaItem && stackInSlot != source) {
                IManaItem manaItem = (IManaItem) stackInSlot.getItem();
                if (manaItem.canReceiveManaFromItem(stackInSlot, source)
                        && manaItem.getMana(stackInSlot) + manaToSend <= manaItem.getMaxMana(stackInSlot)) {
                    manaItem.addMana(stackInSlot, manaToSend);
                }
            }
        }
        IInventory baublesInv = BotaniaAPI.internalHandler.getBaublesInventory(player);
        if (baublesInv != null) {
            for (int i = 0; i < baublesInv.getSizeInventory(); i++) {
                ItemStack stackInSlot = baublesInv.getStackInSlot(i);
                if (!stackInSlot.isEmpty() && stackInSlot.getItem() instanceof IManaItem && stackInSlot != source) {
                    IManaItem manaItem = (IManaItem) stackInSlot.getItem();
                    if (manaItem.canReceiveManaFromItem(stackInSlot, source)
                            && manaItem.getMana(stackInSlot) + manaToSend <= manaItem.getMaxMana(stackInSlot)) {
                        manaItem.addMana(stackInSlot, manaToSend);
                        BotaniaAPI.internalHandler.sendBaubleUpdatePacket(player, i);
                    }
                }
            }
        }
    }

    @Override
    public BaubleType getBaubleType(ItemStack stack) {
        return BaubleType.BELT;
    }
}
