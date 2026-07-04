package ab.common.block;

import ab.AdvancedBotany;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class BlockABStorage extends Block {

    public BlockABStorage() {
        super(Material.IRON);
        this.setCreativeTab(AdvancedBotany.tabAB);
        this.setHardness(3.0f);
        this.setResistance(10.0f);
        this.setSoundType(SoundType.METAL);
        this.setTranslationKey(AdvancedBotany.modid + "." + "abStorage");
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
        for (int i = 0; i < 1; i++) {
            list.add(new ItemStack(this, 1, i));
        }
    }

    @Override
    public int damageDropped(IBlockState state) {
        return this.getMetaFromState(state);
    }

    @Override
    public boolean isBeaconBase(IBlockAccess worldObj, BlockPos pos, BlockPos beaconPos) {
        return true;
    }
}
