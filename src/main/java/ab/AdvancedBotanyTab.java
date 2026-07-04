package ab;

import ab.common.lib.register.BlockListAB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class AdvancedBotanyTab extends CreativeTabs {
    public AdvancedBotanyTab(String str) {
        super(str);
        this.setBackgroundImageName("ab.png");
        this.setNoTitle();
    }

    @Override
    public boolean hasSearchBar() {
        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ItemStack createIcon() {
        return new ItemStack(ItemBlock.getItemFromBlock(BlockListAB.blockABSpreader));
    }
}
