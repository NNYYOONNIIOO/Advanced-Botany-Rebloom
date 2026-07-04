package ab.client.render.item;

import ab.client.render.tile.RenderTileManaContainer;
import ab.common.block.tile.TileABSpreader;
import ab.common.block.tile.TileEngineerHopper;
import ab.common.block.tile.TileManaCharger;
import ab.common.block.tile.TileManaContainer;
import ab.common.block.tile.TileManaCrystalCube;
import ab.common.block.tile.TileNidavellirForge;
import ab.common.lib.register.BlockListAB;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class RenderItemTESRBlock extends TileEntityItemStackRenderer {

    @Override
    public void renderByItem(ItemStack stack, float partialTicks) {
        if (stack.getItem() instanceof ItemBlock) {
            Block block = ((ItemBlock) stack.getItem()).getBlock();
            TileEntity te = createTileForBlock(block, stack.getMetadata());
            if (te != null) {
                if (te instanceof TileManaContainer) {
                    RenderTileManaContainer.metadata = stack.getMetadata();
                }
                TileEntityRendererDispatcher.instance.render(te, 0.0, 0.0, 0.0, partialTicks, -1, 1.0f);
            }
        }
    }

    private static TileEntity createTileForBlock(Block block, int meta) {
        // Directly instantiate the correct tile entity based on the block type
        if (block == BlockListAB.blockABSpreader) return new TileABSpreader();
        if (block == BlockListAB.blockABPlate) return new TileNidavellirForge();
        if (block == BlockListAB.blockManaContainer) return new TileManaContainer();
        if (block == BlockListAB.blockManaCrystalCube) return new TileManaCrystalCube();
        if (block == BlockListAB.blockEngineerHopper) return new TileEngineerHopper();
        if (block == BlockListAB.blockManaCharger) return new TileManaCharger();
        return null;
    }
}
