package ab.client.core.handler;

import ab.api.IRenderHud;
import ab.common.item.equipment.ItemBlackHalo;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.profiler.Profiler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.botania.common.item.ItemLexicon;
import vazkii.botania.common.item.ItemTwigWand;

public class HudRenderHandler {
    @SubscribeEvent
    public void onDrawScreenPost(RenderGameOverlayEvent.Post event) {
        Minecraft mc = Minecraft.getMinecraft();
        Profiler profiler = mc.profiler;
        ItemStack equippedStack = mc.player.getHeldItemMainhand();
        if (event.getType() == RenderGameOverlayEvent.ElementType.ALL) {
            profiler.startSection("advancedBotany-hud");
            RayTraceResult pos = mc.objectMouseOver;
            if (pos != null && pos.typeOfHit == RayTraceResult.Type.BLOCK) {
                boolean canRender;
                TileEntity tile = mc.world.getTileEntity(pos.getBlockPos());
                boolean bl = canRender = !equippedStack.isEmpty() && (equippedStack.getItem() instanceof ItemTwigWand || equippedStack.getItem() instanceof ItemLexicon);
                if (tile != null && tile instanceof IRenderHud && !canRender) {
                    ((IRenderHud) tile).renderHud(mc, event.getResolution());
                }
            }
            if (!equippedStack.isEmpty() && equippedStack.getItem() instanceof ItemBlackHalo) {
                profiler.startSection("blackHalo");
                // TODO: Implement ItemBlackHalo HUD rendering
                profiler.endSection();
            }
            profiler.endStartSection("itemsRemainingAB");
            ItemsRemainingRender.render(event.getResolution(), event.getPartialTicks());
            profiler.endSection();
        }
    }
}
