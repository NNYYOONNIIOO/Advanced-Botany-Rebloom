package ab.common.item.block;

import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import vazkii.botania.api.BotaniaAPI;

import java.util.List;

public class ItemBlockBoard extends ItemBlockBase {
    public ItemBlockBoard(Block block) {
        super(block);
    }

    @Override
    public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag flag) {
        if (stack.getMetadata() == 1) {
            if (GuiScreen.isShiftKeyDown()) {
                this.addStringToTooltip(I18n.format("abmisc.fateBoard.info0"), list);
                this.addStringToTooltip(I18n.format("abmisc.fateBoard.info1"), list);
            } else {
                this.addStringToTooltip(I18n.format("botaniamisc.shiftinfo"), list);
            }
        }
    }

    public void addStringToTooltip(String s, List<String> tooltip) {
        tooltip.add(s.replaceAll("&", "\u00a7"));
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        if (stack.getMetadata() == 1) {
            return BotaniaAPI.rarityRelic;
        }
        return super.getRarity(stack);
    }
}
