package ab.common.block;

import ab.AdvancedBotany;
import ab.common.block.tile.TileLebethronCore;
import ab.common.lib.register.RecipeListAB;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import vazkii.botania.api.internal.VanillaPacketDispatcher;
import vazkii.botania.api.lexicon.ILexiconable;
import vazkii.botania.api.lexicon.LexiconEntry;

import javax.annotation.Nonnull;

public class BlockLebethronWood extends Block implements ILexiconable, ITileEntityProvider {
    public static TextureAtlasSprite portalIcon;

    public BlockLebethronWood() {
        super(Material.WOOD);
        this.setCreativeTab(AdvancedBotany.tabAB);
        this.setHardness(6.0f);
        this.setSoundType(SoundType.WOOD);
        this.setTranslationKey(AdvancedBotany.modid + "." + "lebethronWood");
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        int meta = this.getMetaFromState(state);
        if (meta == 4) {
            TileEntity te = world.getTileEntity(pos);
            if (!(te instanceof TileLebethronCore)) return false;
            TileLebethronCore core = (TileLebethronCore) te;
            ItemStack heldItem = player.getHeldItem(hand);
            if (heldItem.isEmpty()) {
                return false;
            }
            Block block = Block.getBlockFromItem(heldItem.getItem());
            if (block.getMaterial(block.getDefaultState()) == Material.LEAVES && !heldItem.hasTagCompound()) {
                if (!world.isRemote) {
                    core.updateStructure();
                    if (core.getValidTree() && core.setBlock(player, block, heldItem.getMetadata())) {
                        heldItem.shrink(1);
                        if (heldItem.getCount() == 0) {
                            player.inventory.setInventorySlotContents(player.inventory.currentItem, ItemStack.EMPTY);
                        }
                        VanillaPacketDispatcher.dispatchTEToNearbyPlayers(world, pos);
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        int meta = this.getMetaFromState(state);
        if (!world.isRemote && meta == 4) {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileLebethronCore) {
                TileLebethronCore tile = (TileLebethronCore) te;
                ItemStack stack = new ItemStack(tile.getBlock(), 1, tile.getMeta());
                if (!stack.isEmpty()) {
                    float f = world.rand.nextFloat() * 0.8f + 0.1f;
                    float f1 = world.rand.nextFloat() * 0.8f + 0.1f;
                    float f2 = world.rand.nextFloat() * 0.8f + 0.1f;
                    EntityItem entityitem = new EntityItem(world,
                            pos.getX() + f, pos.getY() + f1, pos.getZ() + f2,
                            stack.copy());
                    float f3 = 0.05f;
                    entityitem.motionX = world.rand.nextGaussian() * f3;
                    entityitem.motionY = world.rand.nextGaussian() * f3 + 0.2f;
                    entityitem.motionZ = world.rand.nextGaussian() * f3;
                    if (stack.hasTagCompound()) {
                        entityitem.getItem().setTagCompound(stack.getTagCompound().copy());
                    }
                    world.spawnEntity(entityitem);
                }
            }
        }
        super.breakBlock(world, pos, state);
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
        for (int i = 0; i < 5; i++) {
            list.add(new ItemStack(this, 1, i));
        }
    }

    @Override
    public int damageDropped(IBlockState state) {
        return this.getMetaFromState(state);
    }

    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
        return this.getMetaFromState(state) == 3 ? 12 : 0;
    }

    @Override
    public boolean canSustainLeaves(IBlockState state, IBlockAccess world, BlockPos pos) {
        return this.getMetaFromState(state) == 0;
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        int meta = this.getMetaFromState(state);
        return new ItemStack(this, 1, meta);
    }

    @Override
    public LexiconEntry getEntry(World world, BlockPos pos, EntityPlayer player, ItemStack lexicon) {
        return RecipeListAB.lebethronWood;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return meta == 4 ? new TileLebethronCore() : null;
    }
}
