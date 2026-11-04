package ab.common.integration.thaumcraft;

import ab.common.lib.register.RecipeListAB;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import vazkii.botania.api.lexicon.LexiconEntry;
import vazkii.botania.api.subtile.RadiusDescriptor;
import vazkii.botania.api.subtile.SubTileFunctional;

public class SubTileAspecolus extends SubTileFunctional {
    private static final int MANA_PER_TICK = 1000;

    @Override
    public void onUpdate() {
        super.onUpdate();
        World world = supertile.getWorld();
        if (world.isRemote || mana < MANA_PER_TICK) return;

        mana -= MANA_PER_TICK;
        supertile.markDirty();
        AxisAlignedBB range = new AxisAlignedBB(supertile.getPos()).grow(3);
        for (EntityPlayerMP player : world.getEntitiesWithinAABB(EntityPlayerMP.class, range)) {
            AspecolusResearchHandler.markActive(player);
        }
    }

    @Override
    public boolean isOvergrowthAffected() {
        return false;
    }

    @Override
    public int getMaxMana() {
        return 15000;
    }

    @Override
    public int getColor() {
        return 0x8A21DC;
    }

    @Override
    public RadiusDescriptor getRadius() {
        return new RadiusDescriptor.Square(supertile.getPos(), 3);
    }

    @Override
    public LexiconEntry getEntry() {
        return RecipeListAB.aspecolus;
    }
}
