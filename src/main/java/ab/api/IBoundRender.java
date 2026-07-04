package ab.api;

import net.minecraft.util.math.BlockPos;
import vazkii.botania.api.wand.IWandBindable;

public interface IBoundRender extends IWandBindable {
    BlockPos[] getBlocksCoord();
}
