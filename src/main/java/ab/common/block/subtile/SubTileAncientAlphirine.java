package ab.common.block.subtile;

import ab.api.AdvancedBotanyAPI;
import ab.api.recipe.RecipeAncientAlphirine;
import ab.common.entity.EntityAlphirinePortal;
import ab.common.lib.register.RecipeListAB;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import vazkii.botania.api.lexicon.LexiconEntry;
import vazkii.botania.api.subtile.RadiusDescriptor;
import vazkii.botania.api.subtile.SubTileFunctional;
import vazkii.botania.common.item.ItemLexicon;

import java.util.List;

public class SubTileAncientAlphirine extends SubTileFunctional {
    protected static int manaRequare = 4500;

    @Override
    public void onUpdate() {
        super.onUpdate();
        float posX = this.supertile.getPos().getX() + 0.5f;
        float posY = this.supertile.getPos().getY();
        float posZ = this.supertile.getPos().getZ() + 0.5f;
        World world = this.supertile.getWorld();
        List<EntityItem> items = world.getEntitiesWithinAABB(EntityItem.class,
                new AxisAlignedBB(posX, posY, posZ, posX + 1.0f, posY + 1.0f, posZ + 1.0f).grow(1.0, 0.0, 1.0));
        if (this.ticksExisted % 10 == 0 && this.mana >= manaRequare) {
            for (EntityItem item : items) {
                if (item.isDead || item.getItem().getCount() < 1) continue;
                for (RecipeAncientAlphirine recipe : AdvancedBotanyAPI.alphirineRecipes) {
                    if (!recipe.matches(item.getItem())) continue;
                    if (world.isRemote) {
                        this.spawnParticle(world, item);
                        return;
                    }
                    if (item.getItem().getCount() > 1) {
                        item.getItem().shrink(1);
                    } else {
                        item.setDead();
                    }
                    if (world.rand.nextInt(111) <= recipe.getChance()) {
                        this.spawnPortal(world, recipe.getOutput().copy(), posX, posY, posZ);
                        this.mana -= manaRequare;
                    } else {
                        this.mana -= manaRequare / 10;
                    }
                    this.sync();
                    return;
                }
                if (!(item.getItem().getItem() instanceof ItemLexicon) || ((ItemLexicon) item.getItem().getItem()).isKnowledgeUnlocked(item.getItem(), RecipeListAB.forgotten))
                    continue;
                if (world.isRemote) {
                    this.spawnParticle(world, item);
                    break;
                }
                if (item.getItem().getCount() > 1) {
                    item.getItem().shrink(1);
                } else {
                    item.setDead();
                }
                ItemStack lexicon = item.getItem().copy();
                ((ItemLexicon) lexicon.getItem()).unlockKnowledge(lexicon, RecipeListAB.forgotten);
                this.mana -= manaRequare;
                this.spawnPortal(world, lexicon, posX, posY, posZ);
                this.sync();
                break;
            }
        }
    }

    @Override
    public RadiusDescriptor getRadius() {
        return new RadiusDescriptor.Square(this.supertile.getPos(), 1);
    }

    private void spawnPortal(World world, ItemStack stack, float posX, float posY, float posZ) {
        EntityAlphirinePortal portal = new EntityAlphirinePortal(world);
        float itemX = (float) ((double) posX + (Math.random() * 2.0 - 1.0));
        float itemY = (float) ((double) (posY + 1.2f) + (Math.random() - 0.5));
        float itemZ = (float) ((double) posZ + (Math.random() * 2.0 - 1.0));
        portal.setPosition(itemX, itemY, itemZ);
        portal.setStack(stack);
        world.spawnEntity(portal);
    }

    private void spawnParticle(World world, EntityItem item) {
        ItemStack stack = item.getItem();
        if (world.isRemote) {
            for (int i = 0; i < 10; i++) {
                float m = 0.2f;
                float mx = (float) (Math.random() - 0.5) * m;
                float my = (float) (Math.random() - 0.5) * m;
                float mz = (float) (Math.random() - 0.5) * m;
                if (stack.getItem() instanceof ItemBlock) {
                    world.spawnParticle(net.minecraft.util.EnumParticleTypes.BLOCK_CRACK, item.posX, item.posY, item.posZ, mx, my, mz, Item.getIdFromItem(stack.getItem()));
                } else {
                    world.spawnParticle(net.minecraft.util.EnumParticleTypes.ITEM_CRACK, item.posX, item.posY, item.posZ, mx, my, mz, Item.getIdFromItem(stack.getItem()));
                }
            }
        }
    }

    @Override
    public int getMaxMana() {
        return 180000;
    }

    @Override
    public int getColor() {
        return 13680472;
    }

    @Override
    public LexiconEntry getEntry() {
        return RecipeListAB.ancientAlphirine;
    }
}
