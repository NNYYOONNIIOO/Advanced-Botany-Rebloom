package ab.common.block.subtile;

import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.subtile.SubTileFunctional;
import vazkii.botania.api.subtile.SubTileGenerating;
import vazkii.botania.api.subtile.signature.SubTileSignature;

import java.util.List;

public class ABSubTileSignature implements SubTileSignature {
    String name;

    public ABSubTileSignature(String name) {
        this.name = name;
    }

    @Override
    public String getUnlocalizedNameForStack(ItemStack stack) {
        return this.unlocalizedName("");
    }

    private String unlocalizedName(String end) {
        return "ab.flower." + this.name + end;
    }

    @Override
    public String getUnlocalizedLoreTextForStack(ItemStack stack) {
        return this.unlocalizedName(".reference");
    }

    public String getType() {
        Class clazz = BotaniaAPI.getSubTileMapping(this.name);
        if (clazz == null) {
            return "uwotm8";
        }
        if (SubTileGenerating.class.isAssignableFrom(clazz)) {
            return "botania.flowerType.generating";
        }
        if (SubTileFunctional.class.isAssignableFrom(clazz)) {
            return "botania.flowerType.functional";
        }
        return "botania.flowerType.misc";
    }

    @Override
    public void addTooltip(ItemStack stack, World world, List<String> tooltip) {
        tooltip.add(TextFormatting.BLUE + net.minecraft.client.resources.I18n.format(this.getType()));
    }
}
